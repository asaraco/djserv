package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.*;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamSource;
import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Controller class with REST endpoints pertaining to
 * VirtualDJ database & playlist access.
 */
@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200", "http://${app.legendarydj.localhost-ip}:80", "http://localhost:80"})
public class XmlController {
    private static Logger logger = LogManager.getLogger(XmlController.class);
    @Autowired
    private Jaxb2Marshaller marshaller;

    private static List<Track> allTracks = null;
    // AMS 10/29/2024 - Trying to move this from VDJNetController to see if it helps
    private static List<SongRequest> requestQueue = new ArrayList<>();
    private static List<PlaylistSong> truncatedAutomixQueue = new ArrayList<>();

    private boolean dbOutdated = false;

    @Value("${app.legendarydj.localhost-ip:localhost}")
    private String localhostIp;
    @Value("${app.vdj.networkcontrol.token}")
    private String token;
    private String baseUri;
    @Value("${app.legendarydj.newdays:1}")
    private int newDays;
    @Value("${app.vdj.truncate-queue:5}")
    private int truncateQueueBase;
    private static int truncateQueueSize;

    //TODO: Default to some kind of empty list if files don't exist
    private static File vdjDatabaseC = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\database.xml");
    private static File vdjDatabaseL = new File("L:\\VirtualDJ\\database.xml");
    private static File automixQueue = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\Sideview\\automix.vdjfolder");
    private static File historyPath = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\History\\");
    private static File historyPlaylistFile;

    @PostConstruct
    private void initialize() {
        this.baseUri = "http://"+this.localhostIp+":8082";
        truncateQueueSize = this.truncateQueueBase;
        logger.debug("INIT: localhostIp={}",localhostIp);
        logger.debug("INIT: baseUri={}", baseUri);
        logger.debug("INIT: token={}", token);
        logger.debug("INIT: newDays={}", newDays);
        logger.debug("INIT: truncateQueueSize={}", truncateQueueSize);
    }
/*
    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        System.out.println("XML Controller starting up");
        reloadDatabase();
        reloadQueue();
    }
*/
    /**
     * Initialize or force a reload of the entire database
     */
    private void reloadDatabase() {
        this.dbOutdated = true;
        logger.debug("Reloading database");
        VirtualDJDatabase dbL = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(vdjDatabaseL));
        allTracks = dbL.getSongs();
        VirtualDJDatabase dbC = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(vdjDatabaseC));
        allTracks.addAll(dbC.getSongs());
        logger.debug("Database reloaded: {} tracks", allTracks.size());
    }

    public List<SongRequest> getRequestQueue() {
        return requestQueue;
    }

    // REQUEST QUEUE MANAGEMENT
    // AMS 10/29/2024 - Changing this; it used to check play history, but that isn't necessarily accurate
    //                  because if a song that was already played gets requested, it'll do a false positive.
    //                  Checking if it's in the *upcoming* songs should be both more accurate and less expensive.
    //                  This DOES depend on my "queue length" code being correct, however.
    // Check request queue to see which songs from it are still upcoming in Automix.
    // Remove them if necessary, so we can correctly assess the length of the request queue.
    public void checkReqQueueForCompletedRequests() {
        String method = "checkReqQueueForCompletedRequests";
        logger.info("{}: Request queue BEFORE:\n{}", method, requestQueue);
        List<SongRequest> refreshedQueue = new ArrayList<>();   // temporary list of just whatever from the queue is still upcoming
        //List<PlaylistSong> alreadyPlayed = xmlController.getPlayHistory().getPlaylistTracks();
        logger.info("{}: Truncated automix queue:\n{}", method, truncatedAutomixQueue);
        boolean stillThere = false;
        for (SongRequest sr : requestQueue) {
            for (PlaylistSong ps : truncatedAutomixQueue) {
                logger.info("{}: {} == {} ?", method, sr.getFilePath(), ps.getPath());
                if (sr.getFilePath().equals(ps.getPath())) {
                    stillThere = true;
                    logger.info("{}: Song {} is still there.", method, ps.getPath());
                    break;
                }
            }
            if (stillThere) {
                logger.info("Still there.");
                refreshedQueue.add(sr);
            }
        }
        // after loop determining what's still there, rebuild the queue with what we've learned
        requestQueue.clear();
        requestQueue.addAll(refreshedQueue);
        logger.info("{}: Request queue after cleanup:\n{}", method, requestQueue);
    }

    /**
     * Set the amount of songs to display in the queue
     * IN ADDITION TO the base amount configured.
     * Actual "Automix Queue" stored by VDJ is much longer,
     * but we truncate it to the base amount + requests.
     */
    public void setAutomixQueueSize(int additionalSongs) {
        truncateQueueSize = this.truncateQueueBase + additionalSongs;
        logger.info("ADDING TO QUEUE SIZE: {} + {} = {}", this.truncateQueueBase, additionalSongs, truncateQueueSize);
    }

    public void addRequestToReqQueue(SongRequest newRequest) {
        String method = "addRequestToReqQueue";
        logger.info("{}: REQUESTED: {}", method, newRequest.toString());
        // REQUEST QUEUE MANAGEMENT
        checkReqQueueForCompletedRequests();
        logger.info("{}: CLEANED QUEUE, SIZE IS: {}", method, requestQueue.size());
        // Add request to queue
        requestQueue.add(newRequest);
        logger.info("{}: ADDED TO REQUEST QUEUE, NEW SIZE IS: {}", method, requestQueue.size());
        this.setAutomixQueueSize(requestQueue.size());
    }

    @PostConstruct
    @GetMapping("/forceReloadDatabase")
    @CacheEvict(value="vdjDatabase", allEntries = true)
    public boolean forceReloadDatabase() {
        logger.debug("Forcing reload of database");
        this.reloadDatabase();
        //this.cacheManagerVDJ.getCache("vdjDatabase").clear();
        return true;
    }

    /**
     * Initialize or force a reload of the play queue/history
     */
    @PostConstruct
    @GetMapping("/forceReloadQueue")
    private boolean reloadQueue() {
        logger.debug("Reloading play history and queue");
        if (historyPath.isDirectory()) {
            File[] dirFiles = historyPath.listFiles((FileFilter) FileFilterUtils.suffixFileFilter(".m3u"));
            if (dirFiles!=null && dirFiles.length>0) {
                Arrays.sort(dirFiles, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                historyPlaylistFile = dirFiles[0];
                logger.debug(historyPlaylistFile);
            }
        }
        return true;
    }

    /**
     * Returns the full list of songs (for internal use)
     * @return List<Track>
     */
    public static List<Track> getFullDbSongs() {
        return allTracks;
    }

    /**
     * Reloads the database and returns the full list of songs
     * @return List<Track>
     * @throws FileNotFoundException - This should never happen if configured correctly
     */
    @Cacheable("vdjDatabase")
    @GetMapping("/getAllTracks")
    public List<Track> getAllTracks() throws FileNotFoundException {
        reloadDatabase();
        return allTracks;
    }

    /**
     * Returns a filtered list of tracks that are rated 1 star or higher.
     * @return List<Track>
     * @throws FileNotFoundException - This should never happen if configured correctly
     */
    @GetMapping("/getRatedTracks")
    public List<Track> getRatedTracks() throws FileNotFoundException {
        if (allTracks==null) {
            reloadDatabase();
        }
        return allTracks.stream().filter(e -> e.getRating()>0).toList();
    }

    /**
     * Returns a filtered list of tracks that are rated 1 star or higher,
     * and are not sourced from an online service.
     * @return List<Track>
     * @throws FileNotFoundException - This should never happen if configured correctly
     */
    @GetMapping("/getRatedLocalTracks")
    public List<Track> getRatedLocalTracks() throws FileNotFoundException {
        if (allTracks==null) {
            reloadDatabase();
        }
        return allTracks.stream().filter(e -> e.getRating()>0 && !e.getFilePath().contains("netsearch")).toList();
    }

    /**
     * Returns a filtered list of tracks that are rated 0 stars only.
     * These would be non-song audio files or songs otherwise meant to be hidden from the user.
     * @return List<Track>
     * @throws FileNotFoundException - This should never happen if configured correctly
     */
    @GetMapping("/getUnratedLocalTracks")
    public List<Track> getUnratedLocalTracks() throws FileNotFoundException {
        if (allTracks==null) {
            reloadDatabase();
        }
        logger.info("newDays = {}", newDays);
        return allTracks.stream().filter(
                e -> e.getRating()==0
                        && e.getFilePath().contains("LANtrax")
                        && e.getFirstSeen().isAfter(LocalDateTime.now().minusDays(newDays))
                ).toList();
    }

    /**
     * Returns a filtered list of only the tracks that come from online databases.
     * @return List<Track>
     */
    @GetMapping("/getOnlineTracks")
    public List<Track> getOnlineTracks() {
        VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(vdjDatabaseC));
        return fulldb.getSongs().stream().filter(e -> e.getFilePath().contains("netsearch")).toList();
    }

    /**
     * Returns a filtered list of tracks that have been released within the last 2 years, and are rated 1 star or higher.
     * @return List<Track>
     * @throws FileNotFoundException - This should never happen if configured correctly
     */
    @GetMapping("/getRatedRecentTracks")
    public List<Track> getRatedRecentTracks() throws FileNotFoundException {
        int thisYear = LocalDate.now().getYear();
        if (allTracks==null) {
            reloadDatabase();
        }
        return allTracks.stream().filter(e -> e.getRating()>0).filter(e -> e.getYear()>=(thisYear-1)).toList();
    }

    /**
     * Returns the "Automix Queue" as a PlaylistQueue object.
     * Invoke other webservices to fill in information about current track and database state.
     * @return PlaylistQueue
     */
    @PostConstruct
    @GetMapping("/getQueue")
    public PlaylistQueue getQueue() throws SAXParseException, IOException {
        logger.info("GET AUTOMIX QUEUE");
        // Get queue from file
        logger.debug("Getting Queue");

        FileReader reader = new FileReader(automixQueue);
        StreamSource aq = new StreamSource(reader);
        logger.debug(aq.getReader());
        VirtualFolder vdjfolder = (VirtualFolder) marshaller.unmarshal(aq);
        reader.close();

        //VirtualFolder vdjfolder = (VirtualFolder) marshaller.unmarshal(new StreamSource(automixQueue));
        logger.debug("Got Queue");
        // Truncate to just the ones we want to display
        logger.debug("Truncating Queue to {}", truncateQueueSize);
        List<PlaylistSong> shortList = vdjfolder.getSongs().subList(0,truncateQueueSize);
        // Get current track duration
        double duration = shortList.get(0).getSonglength();
        logger.debug("Current track: {} ({})", shortList.get(0).getTitle(), duration);
        truncatedAutomixQueue = shortList;    // AMS 10/31/2024 save this off for easy access by other methods
        // Get current track time data from webservices
        int timeRemaining = VDJNetworkControlInterface.getTimeRemaining(baseUri,token);
        double trackProgress = VDJNetworkControlInterface.getSongPosition(baseUri,token);
        PlaylistQueue pq = new PlaylistQueue(this.dbOutdated, duration, trackProgress, timeRemaining);
        pq.setName("Automix Queue");
        pq.setPlaylistTracks(shortList);
        // Request Queue management - see if any requests are still in Automix queue and adjust "request queue" length as needed
        checkReqQueueForCompletedRequests();
        return pq;
    }

    /**
     * Parses the most recent .m3u playlist file and returns it as a Playlist object.
     * @return Playlist
     * @throws IOException - This should never happen if configured correctly
     */
    @GetMapping("/getPlayHistory")
    public Playlist getPlayHistory() throws IOException {
        Playlist p = new Playlist();
        //logger.debug("Getting History");
        List<String> fileLines = Files.readAllLines(Paths.get(historyPlaylistFile.toURI()));
        //logger.debug("Got History");
        List<PlaylistSong> pSongs = new ArrayList<>();
        PlaylistSong currentPS = new PlaylistSong();
        for (String line : fileLines) {
            //logger.debug("HISTORY FILE LINE - {}", line);
            if (line.startsWith("#EXTVDJ:")) {
                String sArtist = "";
                String sTitle = "";
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
                currentPS.setPath(line);
                pSongs.add(currentPS);
            }
        }
        p.setName("Last Played");
        p.setPlaylistTracks(pSongs);
        return p;
    }

}
