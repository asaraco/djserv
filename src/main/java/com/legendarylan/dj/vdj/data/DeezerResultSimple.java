package com.legendarylan.dj.vdj.data;

import org.apache.commons.text.WordUtils;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

public class DeezerResultSimple {
    private BigInteger id;
    private String title;
    private String artist;
    private String album;
    private int duration;
    private boolean explicit;

    private static ArrayList<String> fourLetterWords = new ArrayList<>(Arrays.asList("Bullshit","Shit","Fuck","Motherfuck","Ass ","Nigg","Cunt","Bitch"));

    public DeezerResultSimple(DeezerSearchResult.DeezerSong d) {
        this.setId(d.getId());
        this.setTitle(d.getTitle());
        this.setAlbum(d.getAlbum().getTitle());
        this.setArtist(d.getArtist().getName());
        this.setDuration(d.getDuration());
        this.setExplicit(d.isExplicit_lyrics());
    }

    private String cleanText(String input) {
        //System.out.println("cleanText(" + input + ")");
        String capitalizedInput = WordUtils.capitalizeFully(input.toLowerCase());    // Beginning of each word will be capitalized; reduces false positives
        String output = input;
        for (String word : fourLetterWords) {
            while (capitalizedInput.contains(word)) {
                int i = capitalizedInput.indexOf(word);
                String part1 = input.substring(0, i);
                String part2 = input.substring(i+word.length());
                output = part1 + word.replaceAll(".", "*") + part2;
                input = output;
                capitalizedInput = WordUtils.capitalizeFully(output);
            }
        }
        return output;
    }

    /* Getters & Setters */

    public String getTitle() {
        return cleanText(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return cleanText(artist);
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return cleanText(album);
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
