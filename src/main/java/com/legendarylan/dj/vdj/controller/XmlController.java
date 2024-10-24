package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.*;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.*;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

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

    private boolean dbOutdated = false;

    private static String vdjScriptQueryUri = "http://localhost:8082/query?script={script}&bearer={bearer}"; // ***AMS*** TODO: Replace with parameter
    //TODO: Default to some kind of empty list if files don't exist
    private static File vdjDatabaseC = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\database.xml");
    private static File vdjDatabaseL = new File("L:\\VirtualDJ\\database.xml");
    private static File automixQueue = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\Sideview\\automix.vdjfolder");
    private static File historyPath = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\History\\");
    private static File historyPlaylistFile;

    /**
     * Initialize or force a reload of the entire database
     */
    //@PostConstruct
    //@GetMapping("/forceReloadDatabase")
    private void reloadDatabase() {
        this.dbOutdated = true;
        logger.debug("Reloading database");
        VirtualDJDatabase dbL = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(vdjDatabaseL));
        allTracks = dbL.getSongs();
        VirtualDJDatabase dbC = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(vdjDatabaseC));
        allTracks.addAll(dbC.getSongs());
    }

    @PostConstruct
    @GetMapping("/forceReloadDatabase")
    @CacheEvict(value="vdjDatabase", allEntries = true)
    public boolean forceReloadDatabase() {
        logger.debug("Forcing reload of database");
        this.reloadDatabase();
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

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        System.out.println("XML Controller starting up");
        reloadDatabase();
        reloadQueue();
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
    @Cacheable("vdjDatabase")
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
        return allTracks.stream().filter(
                e -> e.getRating()==0
                        && e.getFilePath().contains("LANtrax")
                        && e.getFirstSeen().isAfter(LocalDateTime.now().minusDays(1))
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
    @GetMapping("/getQueue")
    public PlaylistQueue getQueue() {
        // Get queue from file
        VirtualFolder vdjfolder = (VirtualFolder) marshaller.unmarshal(new StreamSource(automixQueue));
        // Get current track duration
        double duration = vdjfolder.getSongs().get(0).getSonglength();
        logger.debug("Current track: {} ({})", vdjfolder.getSongs().get(0).getTitle(), duration);
        // Get current track time data from webservices
        int timeRemaining = VDJNetworkControl.vdjGetTimeRemaining();
        double trackProgress = VDJNetworkControl.vdjGetSongPosition();
        PlaylistQueue pq = new PlaylistQueue(this.dbOutdated, duration, trackProgress, timeRemaining);
        pq.setName("Automix Queue");
        pq.setPlaylistTracks(vdjfolder.getSongs());
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

}
