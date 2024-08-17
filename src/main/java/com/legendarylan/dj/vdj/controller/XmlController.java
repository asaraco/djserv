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

@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200"})
public class XmlController {
    @Autowired
    private Jaxb2Marshaller marshaller;

    @PostMapping("/unmarshalSong")
    public Track unmarshalSong(@RequestBody String xmlData) {
        return (Track) marshaller.unmarshal(new StringSource(xmlData));
    }

    @GetMapping("/getSongs")
    public List<Track> getSongs() throws FileNotFoundException {
        File xmlDatabase = new File("C:\\Users\\lemmh\\_VDJ_backup\\database.xml");
        VirtualDJDatabase fulldb = (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
        return fulldb.getSongs();
    }
}
