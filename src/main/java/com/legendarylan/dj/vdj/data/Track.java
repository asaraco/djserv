package com.legendarylan.dj.vdj.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Track {
    // Fields that exist in database.xml
    @XmlAttribute(name="FilePath")
    public String filePath;
    @XmlAttribute(name="FileSize")
    @JsonIgnore
    public String fileSize;
    @XmlElement(name="Tags")
    @JsonIgnore
    public Tags tags;
    @XmlAttribute(name="Flag")
    @JsonIgnore
    public int flag;
    @JsonIgnore
    @XmlElement(name="Infos")
    public Infos infos;
    @JsonIgnore
    @XmlElement(name="Comment")
    public String comment;
    @XmlElement(name="Poi")
    @JsonIgnore
    public List<Poi> pois;
    // Generated fields
    private int id;
    private static int idCounter = 0;
    private List<String> crates;

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

    public String getSubtitle() {
        return this.tags.getRemix();
    }

    public String getAlbum() {
        return this.tags.getAlbum();
    }

    public int getYear() {
        return this.tags.getYear();
    }

    @JsonIgnore
    public Integer getRating() {
        return this.tags.getStars();
    }

    public String getGrouping() {
        if (this.filePath.contains("netsearch")) {
            return "[ONLINE DATABASE]";
        }
        else if (this.tags.getGrouping()!=null && !this.tags.getGrouping().isBlank()) {
            return this.tags.getGrouping();
        } else if (this.tags.getArtist()==null || this.tags.getArtist().isBlank()) {
            return "(no artist)";
        } else {
            return this.tags.getArtist();
        }
    }

    public String getSortArtist() {
        if (this.tags.getGrouping()!=null && !this.tags.getGrouping().isBlank()) {
            String lGroup = this.tags.getGrouping().toLowerCase();
            if (lGroup.startsWith("the ")) {
                return lGroup.substring(4);
            } else {
                return lGroup;
            }
        } else if (this.tags.getArtist()!=null && !this.tags.getArtist().isBlank()) {
            String lArtist = this.tags.getArtist().toLowerCase();
            if (lArtist.startsWith("the ")) {
                return lArtist.substring(4);
            } else {
                return lArtist;
            }
        } else {
            return "(no artist)";
        }
    }

    public double getDuration() {
        if (this.infos==null) {
            return 0d;
        } else {
            return this.infos.getSongLength();
        }
    }

    public List<String> getCrates() {
        if (this.crates == null) {
            if (this.tags.getCrates()!=null) {
                // Split the string into an array via the hashtags (and leading space)
                List<String> crateList = Arrays.asList(this.tags.getCrates().split(" #"));
                // First one will retain its hashtag, but prepend it back onto the other ones
                for (int i=1; i<crateList.size(); i++) {
                    String s = crateList.get(i);
                    s = "#" + s;
                    crateList.set(i, s);
                }
                // Set the value
                this.setCrates(crateList);
            } else {
                this.setCrates(new ArrayList<String>());
            }
        }
        return this.crates;
    }

    public void setCrates(List<String> crates) {
        this.crates = crates;
    }
}
