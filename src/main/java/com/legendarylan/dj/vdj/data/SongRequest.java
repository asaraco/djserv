package com.legendarylan.dj.vdj.data;

public class SongRequest {
    String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "{'filePath': '" + this.getFilePath() + "'}";
    }
}
