package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Tags")
public class Tags {
    private String author;
    private String title;
    private String genre;
    private String album;
    private String composer;
    private int year;
    private int flag;
    private String trackNumber;

    @XmlAttribute(name="Author")
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    @XmlAttribute(name="Title")
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @XmlAttribute(name="Genre")
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @XmlAttribute(name="Album")
    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }

    @XmlAttribute(name="Composer")
    public String getComposer() {
        return composer;
    }
    public void setComposer(String composer) {
        this.composer = composer;
    }

    @XmlAttribute(name="Year")
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    @XmlAttribute(name="Flag")
    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }

    @XmlAttribute(name="TrackNumber")
    public String getTrackNumber() {
        return trackNumber;
    }
    public void setTrackNumber(String trackNumber) {
        this.trackNumber = trackNumber;
    }
}


