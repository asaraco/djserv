package com.legendarylan.dj.vdj.data;

import java.util.List;

public class Playlist {
    private String name;
    private List<PlaylistSong> playlistTracks;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<PlaylistSong> getPlaylistTracks() {
        return playlistTracks;
    }
    public void setPlaylistTracks(List<PlaylistSong> playlistTracks) {
        this.playlistTracks = playlistTracks;
    }
}
