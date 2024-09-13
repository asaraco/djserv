package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name="VirtualFolder")
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualFolder {
    @XmlElement(name="song")
    public List<PlaylistSong> songs;

    public List<PlaylistSong> getSongs() {
        return songs;
    }
    public void setSongs(List<PlaylistSong> tracks) {
        this.songs = songs;
    }
}

