package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Track {
    // Fields that exist in database.xml
    @XmlAttribute(name="FilePath")
    public String filePath;
    @XmlAttribute(name="FileSize")
    public String fileSize;
    @XmlElement(name="Tags")
    public Tags tags;
    @XmlAttribute(name="Flag")
    public int flag;
    @XmlElement(name="Infos")
    public Infos infos;
    @XmlElement(name="Comment")
    public String comment;
    @XmlElement(name="Poi")
    public List<Poi> pois;
    // Generated fields
    private int id;
    private static int idCounter = 0;

    public Track() {
        this.id = idCounter;
        idCounter++;
    }

    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public String getFileSize() {
        return fileSize;
    }
    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }


    public Tags getTags() {
        return tags;
    }
    public void setTags(Tags tags) {
        this.tags = tags;
    }


    public int getFlag() {
        return flag;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }


    public Infos getInfos() {
        return infos;
    }
    public void setInfos(Infos infos) {
        this.infos = infos;
    }


    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }


    public List<Poi> getPois() {
        return pois;
    }
    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }

    public int getId() {
        return id;
    }

    // Fields derived from tags

    public String getArtist() {
        return this.tags.getArtist();
    }

    public String getTitle() {
        return this.tags.getTitle();
    }

    public String getAlbum() {
        return this.tags.getAlbum();
    }

    public Integer getRating() {
        return this.tags.getStars();
    }

    public String getGrouping() {
        if (this.filePath.contains("netsearch")) {
            return "[ONLINE DATABASE]";
        }
        else if (this.tags.getGrouping()!=null && !this.tags.getGrouping().isBlank()) {
            return this.tags.getGrouping();
        } else {
            return this.tags.getArtist();
        }
    }
}
