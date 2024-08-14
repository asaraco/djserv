package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Infos")
public class Infos {
    private double songLength;
    private int lastPlay;
    private int playCount;
    /* Unused attributes present in the XML
    private int bitRate;
    private int cover;
    private String color;
    private int firstSeen;
    private int firstPlay;
    private String corrupted;
    private double gain;
    private String userColor;
     */

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
}