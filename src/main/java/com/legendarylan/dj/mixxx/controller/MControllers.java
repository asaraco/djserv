package com.legendarylan.dj.mixxx.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin({"http://${app.legendarydj.vdj-ip}:8080", "http://${app.legendarydj.vdj-ip}:4200", "http://localhost:4200"})
public class MControllers {
    private static Logger logger = LogManager.getLogger(MControllers.class);

    /**
     * Manually evict the library from the cache so that the data reloads
     * (whenever the user triggers something on the page that will fetch it again)
     * @return ResponseEntity
     */
    @RequestMapping(path="forceLibraryRefresh", method=RequestMethod.POST)
    @ResponseBody
    @CacheEvict(value="library", allEntries=true)
    ResponseEntity<?> forceLibraryRefresh() {
        logger.info("Evicting library from cache");
        return ResponseEntity.ok("Success: library has been evicted from cache");
    }
}
