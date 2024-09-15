package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.*;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200"})
public class XmlController {
    @Autowired
    private Jaxb2Marshaller marshaller;

    private static List<Track> allTracks = null;

    private static File xmlDatabase = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\database.xml");
    private static File automixQueue = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\Sideview\\automix.vdjfolder");;

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
}
