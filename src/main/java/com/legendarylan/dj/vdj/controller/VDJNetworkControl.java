package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.SongRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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
    private static List<SongRequest> requestQueue = new ArrayList<>();

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
     * @param sr - SongRequest object containing the basic data of the song being requested
     * @return
     */
    @RequestMapping(path="requestDirect", method= RequestMethod.POST, consumes= MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<?> requestDirect(@RequestBody SongRequest sr) {
        logger.debug("requestDirect: ENTER");
        // Add request to statically managed queue
        requestQueue.add(sr);
        // Call VDJ Network Control Plugin
        String uri = "http://localhost:8082/execute?script={script}&bearer={bearer}"; // ***AMS*** TODO: Replace with parameter
        RestTemplate restTemplate = new RestTemplate();
        String scriptBody = "automix_add_next \"" + sr.getFilePath()+ "\" & browser_window automix & browser_scroll top & browser_scroll +1 & browser_move +" + requestQueue.size();
        logger.debug(scriptBody);

        //String uriWithParams = UriComponentsBuilder.fromHttpUrl(uri).queryParam("script", scriptBody).queryParam("bearer","legendary").encode().toUriString();
        Map<String,String> params = new HashMap<>();
        params.put("script",scriptBody);
        params.put("bearer","legendary");

        //String scriptEncoded = UriUtils.encodeQueryParam(scriptBody, StandardCharsets.UTF_8);
        //logger.debug(uriWithParams);
        //uri+="?script="+scriptEncoded+"&bearer=legendary"; // ***AMS*** TODO: Replace password with parameter
        String result = restTemplate.getForObject(uri, String.class, params);
        System.out.println(result);

        return ResponseEntity.ok(result);
    }

}
