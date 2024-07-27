package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.*;

public class Song {
    private String filePath;
    private String fileSize;
    private Tags tags;

    @XmlAttribute(name="FilePath")
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @XmlAttribute(name="FileSize")
    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @XmlElement(name="Tags")
    public Tags getTags() {
        return tags;
    }

    public void setTags(Tags tags) {
        this.tags = tags;
    }
}
