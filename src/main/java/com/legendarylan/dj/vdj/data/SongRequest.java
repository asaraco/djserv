package com.legendarylan.dj.vdj.data;

public class SongRequest {
    String filePath;
    String artist;
    String title;
    boolean rated;

    @Override
    public String toString() {
        return "{'filePath': '" + this.getFilePath() + "'}";
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRated() {
        return rated;
    }

    public void setRated(boolean rated) {
        this.rated = rated;
    }
}
