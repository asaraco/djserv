package com.legendarylan.dj.vdj.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name="VirtualFolder")
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualFolder {
    @XmlAttribute(name="noDuplicates")
    @JsonIgnore
    public String noDuplicates;
    @XmlAttribute(name="ordered")
    @JsonIgnore
    public String ordered;

    @XmlElement(name="song")
    public List<PlaylistSong> songs;

    public List<PlaylistSong> getSongs() {
        return songs;
    }
    public void setSongs(List<PlaylistSong> tracks) {
        this.songs = songs;
    }
}

