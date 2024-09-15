package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.*;
import jakarta.annotation.PostConstruct;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.*;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200"})
public class XmlController {
    private static Logger logger = LogManager.getLogger(XmlController.class);
    @Autowired
    private Jaxb2Marshaller marshaller;

    private static List<Track> allTracks = null;

    private static File xmlDatabase = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\database.xml");
    private static File automixQueue = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\Sideview\\automix.vdjfolder");
    private static File historyPath = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\History\\");
    private static File historyPlaylistFile;

    static {
        if (historyPath.isDirectory()) {
            File[] dirFiles = historyPath.listFiles((FileFilter) FileFilterUtils.fileFileFilter());
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
        //Jaxb2Marshaller marshaller2 = new Jaxb2Marshaller();
        //marshaller.setClassesToBeBound(VirtualDJDatabase.class, Track.class, Tags.class, VirtualFolder.class, PlaylistSong.class);
        //if (fulldb==null) fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
    }

    public static List<Track> getFulldbSongs() {
        return allTracks;
    }

    @PostConstruct
    @Cacheable("vdjDatabase")
    @GetMapping("/getAllTracks")
    public List<Track> getAllTracks() throws FileNotFoundException {
        //if (allTracks==null) {
            VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
            allTracks = fulldb.getSongs();
        //}
        return allTracks;
    }

    @GetMapping("/getRatedTracks")
    public List<Track> getRatedTracks() throws FileNotFoundException {
        //if (allTracks==null) {
            VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
            allTracks = fulldb.getSongs();
        //}
        return allTracks.stream().filter(e -> e.getRating()>0).toList();
    }

    @GetMapping("/getRatedLocalTracks")
    public List<Track> getRatedLocalTracks() throws FileNotFoundException {
        //if (allTracks==null) {
            VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
            allTracks = fulldb.getSongs();
        //}
        return allTracks.stream().filter(e -> e.getRating()>0 && !e.getFilePath().contains("netsearch")).toList();
    }

    @GetMapping("/getUnratedLocalTracks")
    public List<Track> getUnratedLocalTracks() throws FileNotFoundException {
        //if (allTracks==null) {
            VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
            allTracks = fulldb.getSongs();
        //}
        return allTracks.stream().filter(e -> e.getRating()==0 && e.getFilePath().contains("LANtrax")).toList();
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
                //logger.debug("EXTVDJ - " + line);
                PlaylistSong ps = new PlaylistSong();
                int iArtistStart = line.indexOf("<artist>");
                int iArtistEnd = line.indexOf("</artist");
                String sArtist = line.substring(iArtistStart, iArtistEnd);
                int iTitleStart = line.indexOf("<title>");
                int iTitleEnd = line.indexOf("</title>");
                String sTitle = line.substring(iTitleStart, iTitleEnd);
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
