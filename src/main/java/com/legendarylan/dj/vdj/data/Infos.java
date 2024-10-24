package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.cglib.core.Local;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@XmlRootElement(name="Infos")
public class Infos {
    private double songLength;
    private int lastPlay;
    private int playCount;
    private long firstSeen;
    private long firstPlay;
    /* Unused attributes present in the XML
    private int bitRate;
    private int cover;
    private String color;
    private String corrupted;
    private double gain;
    private String userColor;
     */

    public LocalDateTime getFirstSeenDateTime() {
        return Instant.ofEpochSecond(this.getFirstSeen()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @XmlAttribute(name="SongLength")
    public double getSongLength() {
        return songLength;
    }
    public void setSongLength(double songLength) {
        this.songLength = songLength;
    }

    @XmlAttribute(name="LastPlay")
    public int getLastPlay() {
        return lastPlay;
    }
    public void setLastPlay(int lastPlay) {
        this.lastPlay = lastPlay;
    }

    @XmlAttribute(name="PlayCount")
    public int getPlayCount() {
        return playCount;
    }
    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    @XmlAttribute(name="FirstSeen")
    public long getFirstSeen() {        return firstSeen;    }
    public void setFirstSeen(long firstSeen) {        this.firstSeen = firstSeen;    }

    @XmlAttribute(name="FirstPlay")
    public long getFirstPlay() {        return firstPlay;    }
    public void setFirstPlay(long firstPlay) {        this.firstPlay = firstPlay;    }
}
