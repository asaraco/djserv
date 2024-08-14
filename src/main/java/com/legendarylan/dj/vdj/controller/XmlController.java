package com.legendarylan.dj.vdj.controller;

import com.legendarylan.dj.vdj.data.Song;
import com.legendarylan.dj.vdj.data.VirtualDJDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;

@RestController
public class XmlController {
    @Autowired
    private Jaxb2Marshaller marshaller;

    @PostMapping("/unmarshalSong")
    public Song unmarshalSong(@RequestBody String xmlData) {
        return (Song) marshaller.unmarshal(new StringSource(xmlData));
    }

    @GetMapping("/getSongs")
    public VirtualDJDatabase getSongs() throws FileNotFoundException {
        File xmlDatabase = new File("C:\\Users\\lemmh\\_VDJ_backup\\database.xml");
        return (VirtualDJDatabase) marshaller.unmarshal(new StreamSource(xmlDatabase));
    }
}
