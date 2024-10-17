package com.legendarylan.dj.vdj.data;

public class PlaylistQueue extends Playlist {
    private boolean dbOutdated;
    private double currentTrackDurationSec;
    private double currentTrackProgress;
    private int currentTrackRemainingMs;

    public PlaylistQueue(boolean dbOutdated, double currentTrackDurationSec, double currentTrackProgress, int currentTrackRemainingMs) {
        super();
        this.dbOutdated = dbOutdated;
        this.currentTrackDurationSec = currentTrackDurationSec;
        this.currentTrackProgress = currentTrackProgress;
        this.currentTrackRemainingMs = currentTrackRemainingMs;
    }

    /* Getters & Setters */

    public boolean isDbOutdated() {
        return dbOutdated;
    }

    public void setDbOutdated(boolean dbOutdated) {
        this.dbOutdated = dbOutdated;
    }

    public double getCurrentTrackProgress() {
        return currentTrackProgress;
    }

    public void setCurrentTrackProgress(double currentTrackProgress) {
        this.currentTrackProgress = currentTrackProgress;
    }

    public double getCurrentTrackDurationSec() {
        return currentTrackDurationSec;
    }

    public void setCurrentTrackDurationSec(double currentTrackDurationSec) {
        this.currentTrackDurationSec = currentTrackDurationSec;
    }

    public int getCurrentTrackRemainingMs() {
        return currentTrackRemainingMs;
    }

    public void setCurrentTrackRemainingMs(int currentTrackRemainingMs) {
        this.currentTrackRemainingMs = currentTrackRemainingMs;
    }
}
