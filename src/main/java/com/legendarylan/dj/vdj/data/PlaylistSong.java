package com.legendarylan.dj.vdj.data;

import com.legendarylan.dj.vdj.controller.XmlController;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.List;

@XmlRootElement(name="song")
public class PlaylistSong {
    private static Logger logger = LogManager.getLogger(PlaylistSong.class);
    // Fields that exist in automix.vdjfolder
    private int position;
    private String path;
    private String artist;
    private String title;
    private double songlength;
    private double bpm;
    private String remix;
    // Unused fields
    /*
    String remix;
    String netsearchId;
    String key;
    */
    int size;

    // Custom fields
    private Track track;

    public Track getTrack() throws FileNotFoundException {
        List<Track> allTracks = XmlController.getFullDbSongs();
        for (Track t: allTracks) {
            int pFilenameIndex = this.getPath().lastIndexOf("\\");
            int tFilenameIndex = t.getFilePath().lastIndexOf("\\");
            if ((pFilenameIndex>=0) && (tFilenameIndex>=0)) {
                if (t.getFilePath().substring(tFilenameIndex).equals(this.getPath().substring(pFilenameIndex))) this.track = t;
            } else {
                if (t.getFilePath().equals(this.getPath())) this.track = t;
            }
        }
        if (this.track == null) {
            Track t = new Track();
            Tags tt = new Tags();
            Infos i = new Infos();
            t.setFilePath(this.path);
            tt.setArtist(this.artist);
            tt.setTitle(this.title);
            tt.setYear(0);
            tt.setAlbum("");
            i.setSongLength(this.songlength);
            t.setTags(tt);
            t.setInfos(i);
            this.track = t;
        }
        return this.track;
    }

    /* Getters & Setters */

    @XmlAttribute(name="idx")
    public int getPosition() {        return position;    }
    public void setPosition(int position) {        this.position = position;    }

    @XmlAttribute(name="path")
    public String getPath() {        return path;    }
    public void setPath(String path) {        this.path = path;    }

    @XmlAttribute(name="artist")
    public String getArtist() {        return artist;    }
    public void setArtist(String artist) {        this.artist = artist;    }

    @XmlAttribute(name="title")
    public String getTitle() {        return title;    }
    public void setTitle(String title) {        this.title = title;    }

    @XmlAttribute(name="songlength")
    public double getSonglength() {        return songlength;    }
    public void setSonglength(double songlength) {        this.songlength = songlength;    }

    @XmlAttribute(name="bpm")
    public double getBpm() {        return bpm;    }
    public void setBpm(double bpm) {        this.bpm = bpm;    }

    @XmlAttribute(name="remix")
    public String getRemix() {        return remix;    }
    public void setRemix(String remix) {        this.remix = remix;    }

    @XmlAttribute(name="size")
    public int getSize() {  return size;    }
    public void setSize(int size) { this.size = size;   }
}
