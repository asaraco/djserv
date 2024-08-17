package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Tags")
public class Tags {
    private String artist;
    private String title;
    private String genre;
    private String album;
    private String composer;
    private int year;
    private int flag;
    private String remix;
    private String remixer;
    private String grouping;
    private String crates;          // This is "User1" renamed
    private String searchTerms;     // This is "User2" renamed
    /* Unused attributes present in the XML
    private String trackNumber;
    private int bpm;
    private String key;
    private String label;
    private int stars;
    */

    @XmlAttribute(name="Author")
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
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

    @XmlAttribute(name="Remix")
    public String getRemix() {
        return remix;
    }
    public void setRemix(String remix) {
        this.remix = remix;
    }

    @XmlAttribute(name="Remixer")
    public String getRemixer() {
        return remixer;
    }
    public void setRemixer(String remixer) {
        this.remixer = remixer;
    }

    @XmlAttribute(name="Grouping")
    public String getGrouping() {
        return grouping;
    }
    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    @XmlAttribute(name="User1")
    public String getCrates() {
        return crates;
    }
    public void setCrates(String crates) {
        this.crates = crates;
    }

    @XmlAttribute(name="User2")
    public String getSearchTerms() {
        return searchTerms;
    }
    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }
}


