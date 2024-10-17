package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.Playlist;
import com.legendarylan.dj.vdj.data.PlaylistSong;
import com.legendarylan.dj.vdj.data.SongRequest;
import com.legendarylan.dj.vdj.data.Track;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller class containing REST endpoints
 * providing access to the VirtualDJ "Network Control" plugin.
 */
@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200", "http://${app.legendarydj.localhost-ip}:80", "http://localhost:80"})
public class VDJNetworkControl {
    private static Logger logger = LogManager.getLogger(VDJNetworkControl.class);
    private final XmlController xmlController;

    private static List<SongRequest> requestQueue = new ArrayList<>();
    private static String vdjScriptExecUri = "http://localhost:8082/execute?script={script}&bearer={bearer}"; // ***AMS*** TODO: Replace with parameter
    private static String vdjScriptQueryUri = "http://localhost:8082/query?script={script}&bearer={bearer}"; // ***AMS*** TODO: Replace with parameter

    /**
     * Constructor.
     * @param xmlController - Injected to provide access to the other controller class
     */
    public VDJNetworkControl(XmlController xmlController) {
        this.xmlController = xmlController;
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
        // REQUEST QUEUE MANAGEMENT
        // Check request queue to see if any songs from it have already been played.
        // Remove them if necessary, so we can correctly assess the length of the request queue.
        if (!requestQueue.isEmpty()) {
            List<PlaylistSong> alreadyPlayed = xmlController.getPlayHistory().getPlaylistTracks();
            for (PlaylistSong ps : alreadyPlayed) {
                requestQueue.removeIf(sr -> sr.getFilePath().equalsIgnoreCase(ps.getPath()));
            }
        }
        logger.debug("{}: Request queue:\n{}", method, requestQueue);
        // Add request to queue
        requestQueue.add(newRequest);
        // Call VDJ Network Control Plugin
        RestTemplate restTemplate = new RestTemplate();
        String scriptBody = "automix_add_next \"" + newRequest.getFilePath()+ "\" & browser_window automix & browser_scroll top & browser_scroll +1 & browser_move +" + requestQueue.size();
        logger.debug(scriptBody);
        // Prepare URL params
        Map<String,String> params = new HashMap<>();
        params.put("script",scriptBody);
        params.put("bearer","legendary");
        // Do the call
        String result = restTemplate.getForObject(vdjScriptExecUri, String.class, params);
        System.out.println(result);
        // Finish
        return ResponseEntity.ok(result);
    }

    @RequestMapping(path="getTimeRemaining", method=RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> getTimeRemaining() {
        // Call VDJ Network Control Plugin
        RestTemplate restTemplate = new RestTemplate();
        String scriptBody = "deck active get_time remain";
        logger.debug(scriptBody);
        // Prepare URL params
        Map<String,String> params = new HashMap<>();
        params.put("script",scriptBody);
        params.put("bearer","legendary");
        // Do the call
        String result = restTemplate.getForObject(vdjScriptQueryUri, String.class, params);
        System.out.println(result);
        // Finish
        return ResponseEntity.ok(result);
    }

}
