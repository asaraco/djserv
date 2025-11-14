package com.legendarylan.dj.vdj.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.xml.bind.annotation.*;

import java.time.LocalDateTime;
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
    @XmlElement(name="Link")
    @JsonIgnore
    public Link link;
    // Generated fields
    private int id;
    private static int idCounter = 0;
    private List<String> crates;
    private String searchTerms;
    private String composer;
    private double duration;
    private String grouping;
    private String sortArtist;
    private boolean onlineSource;
    private LocalDateTime firstSeen;
    private boolean alreadyPlayed;

    public Track() {
        this.id = idCounter;
        idCounter++;
        if (getLink()!=null) {
            System.out.println(getLink());
        }
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

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
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

    public Integer getRating() {
        return this.tags.getStars();
    }

    public String getGrouping() {
        if (this.grouping==null) {
            /*
            if (this.filePath.contains("netsearch")) {
                this.grouping = "[ONLINE DATABASE]";
            }
             */
            if (this.tags.getGrouping()!=null && !this.tags.getGrouping().isBlank()) {
                this.grouping = this.tags.getGrouping();
            } else if (this.tags.getArtist()==null || this.tags.getArtist().isBlank()) {
                this.grouping = "(no artist)";
            } else {
                this.grouping = this.tags.getArtist();
            }
        }
        return this.grouping;
    }

    public String getSortArtist() {
        if (this.sortArtist==null) {
            if (this.tags.getGrouping()!=null && !this.tags.getGrouping().isBlank()) {
                String lGroup = this.tags.getGrouping().toLowerCase();
                if (lGroup.startsWith("the ")) {
                    this.sortArtist = lGroup.substring(4);
                } else {
                    this.sortArtist = lGroup;
                }
            } else if (this.tags.getArtist()!=null && !this.tags.getArtist().isBlank()) {
                String lArtist = this.tags.getArtist().toLowerCase();
                if (lArtist.startsWith("the ")) {
                    this.sortArtist = lArtist.substring(4);
                } else {
                    this.sortArtist = lArtist;
                }
            } else {
                this.sortArtist = "(no artist)";
            }
        }
        return this.sortArtist;
    }

    public double getDuration() {
        if (this.duration==0d) {
            if (this.infos==null && this.pois==null) {
                this.duration = 0d;
            } else {
                this.duration = this.infos.getSongLength();
                if (this.pois != null) {   // Use POIs to more accurately determine greatest possible duration of song playback
                    double startPoint = this.duration;  // Highest possible start point, keep checking for lower
                    double endPoint = 0d;               // Lowest possible end point, keep checking for higher
                    for (Poi p : this.pois) {
                        String pSubtype = p.getPoint();
                        if (p.getPoint()!=null) { // some POIs are beatgrid markers and won't have this, should be skipped
                            double pPos = p.getPos();
                            if (pSubtype.equalsIgnoreCase("cutStart") || pSubtype.equalsIgnoreCase("fadeStart") || pSubtype.equalsIgnoreCase("tempoStart")) {
                                if (pPos < startPoint) startPoint = pPos;
                            }
                            if (pSubtype.equalsIgnoreCase("cutEnd") || pSubtype.equalsIgnoreCase("fadeEnd") || pSubtype.equalsIgnoreCase("tempoEnd")) {
                                if (pPos > endPoint) endPoint = pPos;
                            }
                        }
                    }
                    // Negative or less than true duration should probably never happen, but just in case...
                    double calculatedDuration = endPoint - startPoint;
                    if (calculatedDuration > 0d && calculatedDuration < this.duration) {
                        this.duration = calculatedDuration;
                    }
                }
            }
        }
        return this.duration;
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

    public boolean isOnlineSource() {
        return this.getLink()!=null;
    }

    public LocalDateTime getFirstSeen() {
        return this.getInfos().getFirstSeenDateTime();
    }

    public void setFirstSeen(LocalDateTime firstSeen) {
        this.firstSeen = firstSeen;
    }

    public String getSearchTerms() {
        return this.getTags().getSearchTerms();
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public String getComposer() {
        return this.tags.getComposer();
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public boolean isAlreadyPlayed() {
        return alreadyPlayed;
    }

    public void setAlreadyPlayed(boolean alreadyPlayed) {
        this.alreadyPlayed = alreadyPlayed;
    }
}
