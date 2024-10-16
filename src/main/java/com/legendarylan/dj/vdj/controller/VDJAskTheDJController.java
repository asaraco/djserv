package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.Track;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Controller class containing REST endpoints
 * providing access to the VirtualDJ "Ask the DJ" API.
 */
@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200", "http://${app.legendarydj.localhost-ip}:80", "http://localhost:80"})
public class VDJAskTheDJController {
    private static Logger logger = LogManager.getLogger(VDJAskTheDJController.class);
    private final XmlController xmlController;

    /**
     * Constructor.
     * @param xmlController - Injected to provide access to the other controller class
     */
    public VDJAskTheDJController(@Autowired XmlController xmlController) {
        this.xmlController = xmlController;
    }

    /**
     * Given an ID (#), find the song in the database
     * and then use its metadata to create an "Ask the DJ" request,
     * and send the request.
     * NOTE: The song ID's are generated at runtime and not backed by any database.
     * Therefore, they may be unreliable and we may not want to use this.
     * @param id - String - song ID #
     * @return
     */
    @RequestMapping(path="findSongByIdForAskTheDJ", method= RequestMethod.POST)
    @ResponseBody
    ResponseEntity<?> requestSong(String id) {
        List<Track> allTracks = XmlController.getFullDbSongs();

        List<Track> highlander = (List<Track>) allTracks.stream().filter(e -> e.getId()==Integer.parseInt(id)).toList();
        logger.debug("Highlander({}): {}", id, highlander);
        if (!highlander.isEmpty()) {
            Track t = highlander.get(0);
            String sendToAskTheDJ = t.getArtist() + " - " + t.getTitle();
            // Use this string in "Ask The DJ" request
            ResponseEntity<?> result = askTheDJ(sendToAskTheDJ);

            System.out.println(result.getBody());

            return ResponseEntity.ok(result.getStatusCode());
        } else {
            return null;
        }

    }

    /**
     * Sends the specified message string to the "Ask the DJ" API.
     * The message is typically an artist & title of a song, but could be something else.
     * @param message - String
     * @return
     */
    @RequestMapping(path="requestAskTheDJ", method=RequestMethod.POST)
    @ResponseBody
    ResponseEntity<?> askTheDJ(String message) {
        // Call VDJ 'Ask The DJ' API
        String uri = "https://virtualdj.com/ask/Legendary__LAN";
        RestTemplate restTemplate = new RestTemplate();
        //String reqBody = "{'name':'sirlemmingRequest', 'message':'"+sendToAskTheDJ+"'}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        MultiValueMap<String,String> parms = new LinkedMultiValueMap<>();
        parms.add("name","sirlemmingRequest");
        parms.add("message",message);
        logger.debug(parms);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(parms, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(uri, entity, String.class);

        System.out.println(result.getBody());

        return ResponseEntity.ok(result.getStatusCode());
    }
}
