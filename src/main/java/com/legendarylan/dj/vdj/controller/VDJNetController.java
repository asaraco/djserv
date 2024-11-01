package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.PlaylistSong;
import com.legendarylan.dj.vdj.data.SongRequest;
import com.legendarylan.dj.vdj.data.Track;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXParseException;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class containing REST endpoints
 * providing access to the VirtualDJ "Network Control" plugin.
 */
@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200", "http://${app.legendarydj.localhost-ip}:80", "http://localhost:80"})
public class VDJNetController {
    private static Logger logger = LogManager.getLogger(VDJNetController.class);
    private final XmlController xmlController;

    //private static List<SongRequest> requestQueue = new ArrayList<>();

    @Value("${app.legendarydj.localhost-ip:localhost}")
    private String localhostIp;
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
        this.baseUri = "http://"+localhostIp+":8082";
        logger.debug("INIT: localhostIp={}",localhostIp);
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
        logger.info("REQUESTED: " + newRequest.toString());
        /*
        // REQUEST QUEUE MANAGEMENT
        // AMS 10/29/2024 - Changing this; it used to check play history, but that isn't necessarily accurate
        //                  because if a song that was already played gets requested, it'll do a false positive.
        //                  Checking if it's in the *upcoming* songs should be both more accurate and less expensive.
        //                  This DOES depend on my "queue length" code being correct, however.
        // Check request queue to see which songs from it are still upcoming in Automix.
        // ***AMS*** TODO: Make sure this works even if you request something that already exists way ahead in the queue
        // Remove them if necessary, so we can correctly assess the length of the request queue.
        logger.debug("{}: Request queue BEFORE:\n{}", method, requestQueue);
        List<SongRequest> refreshedQueue = new ArrayList<>();   // temporary list of just whatever from the queue is still upcoming
        if (!requestQueue.isEmpty()) {
            //List<PlaylistSong> alreadyPlayed = xmlController.getPlayHistory().getPlaylistTracks();
            List<PlaylistSong> upcoming;
            try {
                upcoming = xmlController.getQueue().getPlaylistTracks();
                boolean stillThere = false;
                for (SongRequest sr : requestQueue) {
                    for (PlaylistSong ps : upcoming) {
                        if (sr.getFilePath().equals(ps.getPath())) {
                            stillThere = true;
                            break;
                        }
                    }
                    if (stillThere) {
                        refreshedQueue.add(sr);
                    }
                }
                // after loop determining what's still there, rebuild the queue with what we've learned
                requestQueue.clear();
                requestQueue.addAll(refreshedQueue);
            } catch (SAXParseException e) {
                logger.error("{}: Exception occurred while parsing queue - {}", method, e.getMessage());
            }
            xmlController.setAdditionalQueueSize(requestQueue.size());
        }
        // Add request to queue
        requestQueue.add(newRequest);
        logger.debug("{}: Request queue AFTER:\n{}", method, requestQueue);
         */
        xmlController.addRequestToQueue(newRequest);
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
        //String sanitizedScript = VDJNetworkControlInterface.sanitizeScript(scriptBody);
        String result = VDJNetworkControlInterface.doScriptExec(baseUri, scriptBody, token);
        // If it was a previously unrated song AND not online sourced (new upload), refresh database to move it into the main area
        if (!newRequest.isRated() && !sanitizedPath.contains("netsearch")) {
            //boolean reload = this.xmlController.forceReloadDatabase();
            //logger.debug("{}: Reload complete", method);
            // AMS 10/27/2024 - Trying to force an update server-side before trying to get data client-side
            ResponseEntity<String> res2 = (ResponseEntity<String>) refreshSongBrowser();
            if (res2!=null && res2.getBody().equalsIgnoreCase("true")) {
                logger.info("{}: Finished VDJ browser refresh script", method);
                this.xmlController.forceReloadDatabase();
                logger.info("{}: Finished VDJ database reload", method);
            } else {
                logger.info("{}: Refresh didn't work, sorry", method);
            }
        }
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