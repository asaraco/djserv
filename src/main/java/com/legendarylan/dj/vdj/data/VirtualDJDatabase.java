package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name="VirtualDJ_Database")
@XmlAccessorType(XmlAccessType.FIELD)
public class VirtualDJDatabase {
    @XmlAttribute(name="Version")
    public int version;
    @XmlElement(name="Song")
    public List<Song> songs;

    public int getVersion() {
        return version;
    }
    public void setVersion(int version) {
        this.version = version;
    }

    public List<Song> getSongs() {
        return songs;
    }
    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
