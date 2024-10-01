package com.legendarylan.dj.vdj.data;

import java.math.BigInteger;

public class DeezerResultSimple {
    private BigInteger id;
    private String title;
    private String artist;
    private String album;
    private int duration;
    private boolean explicit;

    public DeezerResultSimple(DeezerSearchResult.DeezerSong d) {
        this.setId(d.getId());
        this.setTitle(d.getTitle());
        this.setAlbum(d.getAlbum().getTitle());
        this.setArtist(d.getArtist().getName());
        this.setDuration(d.getDuration());
        this.setExplicit(d.isExplicit_lyrics());
    }

    /* Getters & Setters */

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
