package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.DeezerResultSimple;
import com.legendarylan.dj.vdj.data.DeezerSearchResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class containing REST endpoints
 * providing access to the Deezer API.
 */
@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200", "http://${app.legendarydj.localhost-ip}:80", "http://localhost:80"})
public class DeezerController {
    private static Logger logger = LogManager.getLogger(DeezerController.class);

    /**
     * Makes an external call to the Deezer API using the specified search term.
     * This is a non-specific query that will find the term anywhere in the song metadata,
     * not limited to Artist, Title, etc.
     * @param query - String
     * @return
     */
    @RequestMapping(path="deezerSearch", method= RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> deezerSearch(@RequestParam(value="query") String query) {
        logger.debug("deezerSearch({})", query);
        int limit = 100;    // Limit # of search results; if not set, it defaults to 25. Cannot exceed 100.
        // Call Deezer search API
        RestTemplate restTemplate = new RestTemplate();
        String queryUrl = "https://api.deezer.com/search?q="+query+"&output=json&limit="+limit;
        //ResponseEntity<DeezerSearchResult[]> result = restTemplate.getForEntity(queryUrl, DeezerSearchResult[].class);
        DeezerSearchResult result = restTemplate.getForObject(queryUrl, DeezerSearchResult.class);
        System.out.println(result);

        List<DeezerResultSimple> songList = new ArrayList<>();
        for (DeezerSearchResult.DeezerSong d : result.getData()) {
            songList.add(new DeezerResultSimple(d));
        }

        return ResponseEntity.ok(songList);
    }
}
