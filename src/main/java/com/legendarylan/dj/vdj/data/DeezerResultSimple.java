package com.legendarylan.dj.vdj.data;

public class DeezerResultSimple {
    private String title;
    private String artist;
    private String album;
    private boolean explicit;

    public DeezerResultSimple(DeezerSearchResult.DeezerSong d) {
        this.setTitle(d.getTitle());
        this.setAlbum(d.getAlbum().getTitle());
        this.setArtist(d.getArtist().getName());
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
}
