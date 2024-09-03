package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.Track;
import com.legendarylan.dj.vdj.data.VirtualDJDatabase;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static File xmlDatabase = new File("C:\\Users\\lemmh\\AppData\\Local\\VirtualDJ\\database.xml");;

    @GetMapping("/getAllTracks")
    public List<Track> getAllTracks() throws FileNotFoundException {
        VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
        return fulldb.getSongs();
    }

    @GetMapping("/getRatedTracks")
    public List<Track> getRatedTracks() throws FileNotFoundException {
        VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
        List<Track> allTracks = fulldb.getSongs();
        return allTracks.stream().filter(e -> e.getRating()>0).toList();
    }

    @GetMapping("/getRatedLocalTracks")
    public List<Track> getRatedLocalTracks() throws FileNotFoundException {
        VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
        List<Track> allTracks = fulldb.getSongs();
        return allTracks.stream().filter(e -> e.getRating()>0 && !e.getFilePath().contains("netsearch")).toList();
    }

    @GetMapping("/getUnratedLocalTracks")
    public List<Track> getUnratedLocalTracks() throws FileNotFoundException {
        VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
        List<Track> allTracks = fulldb.getSongs();
        return allTracks.stream().filter(e -> e.getRating()==0 && e.getFilePath().contains("LANtrax")).toList();
    }
}
