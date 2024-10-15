package com.legendarylan.dj.vdj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.legendarylan.dj.vdj.data.*;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.coyote.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200", "http://${app.legendarydj.localhost-ip}:80", "http://localhost:80"})
public class XmlController {
    private static Logger logger = LogManager.getLogger(XmlController.class);
    @Autowired
    private Jaxb2Marshaller marshaller;

    private static List<Track> allTracks = null;

    //TODO: Default to some kind of empty list if files don't exist
    private static File vdjDatabaseC = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\database.xml");
    private static File vdjDatabaseL = new File("L:\\VirtualDJ\\database.xml");
    private static File automixQueue = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\Sideview\\automix.vdjfolder");
    private static File historyPath = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\History\\");
    private static File historyPlaylistFile;

    /**
     * Initialize or force a reload of the entire database
     */
    @PostConstruct
    @GetMapping("/forceReloadDatabase")
    private void reloadDatabase() {
        logger.debug("Reloading database");
        VirtualDJDatabase dbL = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(vdjDatabaseL));
        allTracks = dbL.getSongs();
        VirtualDJDatabase dbC = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(vdjDatabaseC));
        allTracks.addAll(dbC.getSongs());
    }

    /**
     * Initialize or force a reload of the play queue/history
     */
    @PostConstruct
    @GetMapping("/forceReloadQueue")
    private void reloadQueue() {
        logger.debug("Reloading play history and queue");
        if (historyPath.isDirectory()) {
            File[] dirFiles = historyPath.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".m3u"));
            if (dirFiles!=null && dirFiles.length>0) {
                Arrays.sort(dirFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                historyPlaylistFile = dirFiles[0];
                logger.debug(historyPlaylistFile);
            }
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        System.out.println("hello world, I have just started up");
        reloadDatabase();
        reloadQueue();
        //Jaxb2Marshaller marshaller2 = new Jaxb2Marshaller();
        //marshaller.setClassesToBeBound(VirtualDJDatabase.class, Track.class, Tags.class, VirtualFolder.class, PlaylistSong.class);
        //if (fulldb==null) fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
    }

    public static List<Track> getFulldbSongs() {
        return allTracks;
    }

    @GetMapping("/getAllTracks")
    public List<Track> getAllTracks() throws FileNotFoundException {
        reloadDatabase();
        return allTracks;
    }

    @Cacheable("vdjDatabase")
    @GetMapping("/getRatedTracks")
    public List<Track> getRatedTracks() throws FileNotFoundException {
        if (allTracks==null) {
            reloadDatabase();
        }
        return allTracks.stream().filter(e -> e.getRating()>0).toList();
    }

    @GetMapping("/getRatedLocalTracks")
    public List<Track> getRatedLocalTracks() throws FileNotFoundException {
        if (allTracks==null) {
            reloadDatabase();
        }
        return allTracks.stream().filter(e -> e.getRating()>0 && !e.getFilePath().contains("netsearch")).toList();
    }

    @GetMapping("/getUnratedLocalTracks")
    public List<Track> getUnratedLocalTracks() throws FileNotFoundException {
        if (allTracks==null) {
            reloadDatabase();
        }
        return allTracks.stream().filter(e -> e.getRating()==0 && e.getFilePath().contains("LANtrax")).toList();
    }

    @GetMapping("/getOnlineTracks")
    public List<Track> getOnlineTracks() {
        VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(vdjDatabaseC));
        return fulldb.getSongs().stream().filter(e -> e.getFilePath().contains("netsearch")).toList();
    }

    @GetMapping("/getRatedRecentTracks")
    public List<Track> getRatedRecentTracks() throws FileNotFoundException {
        int thisYear = LocalDate.now().getYear();
        if (allTracks==null) {
            reloadDatabase();
        }
        return allTracks.stream().filter(e -> e.getRating()>0).filter(e -> e.getYear()>=(thisYear-1)).toList();
    }

    @GetMapping("/getQueue")
    public Playlist getQueue() {
        Playlist p = new Playlist();
        VirtualFolder vdjfolder = (VirtualFolder) marshaller.unmarshal(new StreamSource(automixQueue));
        p.setName("Automix Queue");
        p.setPlaylistTracks(vdjfolder.getSongs());
        return p;
    }

    @GetMapping("/getPlayHistory")
    public Playlist getPlayHistory() throws IOException {
        Playlist p = new Playlist();
        List<String> fileLines = Files.readAllLines(Paths.get(historyPlaylistFile.toURI()));
        //logger.debug(fileLines);
        List<PlaylistSong> pSongs = new ArrayList<>();
        PlaylistSong currentPS = new PlaylistSong();
        for (String line : fileLines) {
            if (line.startsWith("#EXTVDJ:")) {
                String sArtist = "";
                String sTitle = "";
                //logger.debug("EXTVDJ - " + line);
                PlaylistSong ps = new PlaylistSong();
                int iArtistStart = line.indexOf("<artist>");
                int iArtistEnd = line.indexOf("</artist");
                if (iArtistStart > -1 && iArtistEnd > -1) sArtist = line.substring(iArtistStart+8, iArtistEnd);
                int iTitleStart = line.indexOf("<title>");
                int iTitleEnd = line.indexOf("</title>");
                if (iTitleStart > -1 && iTitleEnd > -1) sTitle = line.substring(iTitleStart+7, iTitleEnd);
                ps.setArtist(sArtist);
                ps.setTitle(sTitle);
                currentPS = ps;
            } else {
                //logger.debug("NOT EXTVDJ - " + line);
                currentPS.setPath(line);
                pSongs.add(currentPS);
            }
        }
        p.setName("Last Played");
        p.setPlaylistTracks(pSongs);
        return p;
    }

    @RequestMapping(path="findSongByIdForAskTheDJ", method=RequestMethod.POST)
    @ResponseBody
    ResponseEntity<?> requestSong(String id) {
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

    @RequestMapping(path="requestDirect", method=RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    ResponseEntity<?> requestDirect(@RequestBody SongRequest sr) {
        logger.debug("requestDirect: ENTER");
        // Call VDJ Network Control Plugin
        String uri = "http://localhost:8082/execute"; // ***AMS*** TODO: Replace with parameter
        RestTemplate restTemplate = new RestTemplate();
        String scriptBody = "automix_add_next \"" + sr.getFilePath() + "\" & browser_window automix & browser_scroll top & browser_scroll +1 & browser_move +1";
        logger.debug(scriptBody);

        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.TEXT_PLAIN);
        //HttpEntity<String> entity = new HttpEntity<>(scriptBody, headers);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setOrigin("*");
        MultiValueMap<String,String> parms = new LinkedMultiValueMap<>();
        //parms.add("script",scriptBody);
        parms.add("script","get_clock");
        parms.add("bearer","legendary"); // ***AMS*** TODO: Replace with parameter
        logger.debug(parms);
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(parms, headers);
        //ResponseEntity<String> result = restTemplate.postForEntity(uri, entity, String.class);
        uri+="?script="+scriptBody+"&bearer=legendary";
        ResponseEntity<String> result = restTemplate.getForEntity(uri, String.class);
        //ResponseEntity<String> result = restTemplate.postForObject(uri, entity, String.class);

        System.out.println(result.getBody());

        return ResponseEntity.ok(result.getStatusCode());
    }

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

    @RequestMapping(path="deezerSearch", method=RequestMethod.GET)
    @ResponseBody
    ResponseEntity<?> deezerSearch(@RequestParam(value="query") String query) {
        logger.debug("deezerSearch({})", query);
        int limit = 100;    // Limit # of search results; if not set, it defaults to 25
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
