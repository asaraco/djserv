package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="song")
public class PlaylistSong {
    // Fields that exist in automix.vdjfolder
    private int idx;
    private String path;
    private String artist;
    private String title;
    private double songlength;
    private double bpm;
    private String remix;
    // Unused fields
    /*
    String remix;
    String netsearchId;
    String key;
    int size;
     */

    @XmlAttribute(name="idx")
    public int getIdx() {        return idx;    }
    public void setIdx(int idx) {        this.idx = idx;    }

    @XmlAttribute(name="path")
    public String getPath() {        return path;    }
    public void setPath(String path) {        this.path = path;    }

    @XmlAttribute(name="artist")
    public String getArtist() {        return artist;    }
    public void setArtist(String artist) {        this.artist = artist;    }

    @XmlAttribute(name="title")
    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;    }

    @XmlAttribute(name="songlength")
    public double getSonglength() {        return songlength;    }
    public void setSonglength(double songlength) {        this.songlength = songlength;    }

    @XmlAttribute(name="bpm")
    public double getBpm() {        return bpm;    }
    public void setBpm(double bpm) {        this.bpm = bpm;    }

    @XmlAttribute(name="remix")
    public String getRemix() {        return remix;    }
    public void setRemix(String remix) {        this.remix = remix;    }
}
