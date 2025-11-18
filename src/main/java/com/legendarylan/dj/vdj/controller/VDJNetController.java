package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.SongRequest;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;

/**
 * Controller class containing REST endpoints
 * providing access to the VirtualDJ "Network Control" plugin.
 */
@RestController
@CrossOrigin({"http://${app.legendarydj.vdj-ip}:8080", "http://${app.legendarydj.vdj-ip}:4200", "http://${app.legendarydj.vdj-ip}:80",
                "http://${app.legendarydj.webserver-ip}:80", "http://${app.legendarydj.webserver-ip}:4200",
                "http://localhost:80", "http://localhost:4200" })
public class VDJNetController {
    private static Logger logger = LogManager.getLogger(VDJNetController.class);
    private final XmlController xmlController;

    //private static List<SongRequest> requestQueue = new ArrayList<>();

    @Value("${app.legendarydj.vdj-ip:localhost}")
    private String vdjIp;
    @Value("${app.legendarydj.file-path:L:\\LANtrax}")
    private String filePath;
    @Value("${app.vdj.networkcontrol.token}")
    private String token;
    private String baseUri;

    /**
     * Constructor.
     * @param xmlController - Injected to provide access to the other controller class
     */
    public VDJNetController(XmlController xmlController) {
        this.xmlController = xmlController;
    }

    @PostConstruct
    private void initialize() {
        this.baseUri = "http://"+ vdjIp +":8082";
        logger.debug("INIT: vdjIp={}", vdjIp);
        logger.debug("INIT: filePath={}",filePath);
        logger.debug("INIT: baseUri={}", baseUri);
        logger.debug("INIT: token={}", token);
    }

    /**
     * Sends a custom script to VDJ that has the effect of adding a song to the Automix queue,
     * offset to allow both songs currently on deck to play out before the request comes up.
     * Also offset by a "request queue length" parameter managed in this class.
     * Anatomy of the VDJ script:
     * - automix_add_next "{filePath}" - adds the song to the queue; by default, it adds it only 1 song down the queue
     * - browser_window automix - makes "Automix" the active browser window in VDJ
     * - browser_scroll top - positions the cursor at the top of the Automix queue
     * - browser_scroll +1 - positions the cursor 1 song down (to select the requested song)
     * - browser_move +{n} - moves the selected song down by {n} spaces
     * @param newRequest - SongRequest object containing the basic data of the song being requested
     * @return
     */
    @RequestMapping(path="requestDirect", method= RequestMethod.POST, consumes= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<?> requestDirect(@RequestBody SongRequest newRequest) throws IOException {
        String method = "requestDirect";
        logger.trace("{}: ENTER", method);
        logger.info("REQUESTED: {}", newRequest.toString());
        // Send request to queue manager
        xmlController.addRequestToReqQueue(newRequest);
        // Prepare strings
        String decodedPath = URLDecoder.decode(newRequest.getFilePath(), Charset.defaultCharset());
        String sanitizedPath = VDJNetworkControlInterface.sanitizePath(decodedPath);
        String scriptBody = "";
        // If Deezer result, tell VDJ to do a search on it first, which will hopefully force it to get the online track metadata for its database
        // (Otherwise, the request still works but it shows as a blank track)
        if (sanitizedPath.contains("netsearch")) {
            scriptBody += "clear_search & wait 500ms & search_add \"" + newRequest.getArtist() + " " + newRequest.getTitle() + "\" & wait 3000ms & ";
        }
        // AMS 10/29/2024 - Get request queue size from XmlController
        int queueSize = xmlController.getRequestQueue().size();
        // Main request script
        scriptBody += "automix_add_next \"" + sanitizedPath + "\" & browser_window automix & browser_scroll top & browser_scroll +1 & browser_move +" + queueSize;
        // If song isn't rated yet, rate it (this is important for new uploads to get into the main DB)
        if (!newRequest.isRated()) {
            scriptBody += " & browsed_song 'rating' 1";
        }
        // AMS 10/31/2024 Add a delay after all of the above to hopefully avoid returning the data before VDJ actually knows what it did
        scriptBody += " & wait 5000ms & browser_window automix";    // I have to add a dummy command at the end for "wait" to work
        logger.info("{}: Finished script:\n{}", method, scriptBody);
        String result = VDJNetworkControlInterface.doScriptExec(baseUri, scriptBody, token);
        // If it was a previously unrated song AND not online sourced (new upload), refresh database to move it into the main area
        //if (!newRequest.isRated() && !sanitizedPath.contains("netsearch")) {  // AMS 11/15/2025 Want to try this for netsearches too
        if (!newRequest.isRated()) {
            // AMS 10/27/2024 - Trying to force an update server-side before trying to get data client-side
            ResponseEntity<String> res2 = (ResponseEntity<String>) refreshSongBrowser();
            if (res2!=null && res2.getBody().equalsIgnoreCase("true")) {
                logger.debug("{}: Finished VDJ browser refresh script", method);
                this.xmlController.forceReloadDatabase();
                logger.debug("{}: Finished VDJ database reload", method);
            } else {
                logger.warn("{}: Refresh didn't work, sorry", method);
            }
        }
        logger.info("{} Response: {}", method, result);
        // Finish
        return ResponseEntity.ok(result);
    }

    @RequestMapping(path="getTimeRemaining", method=RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getTimeRemaining() {
        return ResponseEntity.ok(VDJNetworkControlInterface.getTimeRemaining(baseUri, token));
    }

    @RequestMapping(path="getSongPosition", method=RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getSongPosition() {
        return ResponseEntity.ok(VDJNetworkControlInterface.getSongPosition(baseUri, token));
    }

    @RequestMapping(path="refreshSongBrowser", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> refreshSongBrowser() {
        String sanitizedPath = VDJNetworkControlInterface.sanitizePath(filePath);
        String scriptBody = "browser_gotofolder \"" + sanitizedPath + "\" & browser_sort \"-First Seen\" & browser_window 'songs' & browser_scroll top & browsed_file_analyze & wait 50ms & goto_last_folder";
        String sanitizedScript = VDJNetworkControlInterface.sanitizeScript(scriptBody);
        String result = VDJNetworkControlInterface.doScriptExec(baseUri, sanitizedScript, token);
        // Refresh cached database
        this.xmlController.forceReloadDatabase();
        // Return
        return ResponseEntity.ok(result);
    }
}