package com.legendarylan.dj.vdj.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.util.List;

public class DeezerSearchResult {
    private List<DeezerSong> data;

    public List<DeezerSong> getData() {
        return data;
    }
    public void setData(List<DeezerSong> data) {
        this.data = data;
    }

    public static class DeezerSong {
        private BigInteger id;
        @JsonIgnore
        private boolean readable;
        private String title;
        @JsonIgnore
        private String title_short;
        @JsonIgnore
        private String title_version;
        @JsonIgnore
        private String link;
        private int duration;
        @JsonIgnore
        private int rank;
        private boolean explicit_lyrics;
        private int explicit_content_lyrics;
        private int explicit_content_cover;
        @JsonIgnore
        private String preview;
        @JsonIgnore
        private String md5_image;
        private DeezerArtist artist;
        private DeezerAlbum album;
        @JsonIgnore
        private String type;

        /* Getters & Setters */

        public BigInteger getId() {
            return id;
        }
        public void setId(BigInteger id) {
            this.id = id;
        }

        public boolean isReadable() {
            return readable;
        }
        public void setReadable(boolean readable) {
            this.readable = readable;
        }

        public String getTitle() {
            return title;
        }
        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle_short() {
            return title_short;
        }
        public void setTitle_short(String title_short) {
            this.title_short = title_short;
        }

        public String getTitle_version() {
            return title_version;
        }
        public void setTitle_version(String title_version) {
            this.title_version = title_version;
        }

        public String getLink() {
            return link;
        }
        public void setLink(String link) {
            this.link = link;
        }

        public int getDuration() {
            return duration;
        }
        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getRank() {
            return rank;
        }
        public void setRank(int rank) {
            this.rank = rank;
        }

        public boolean isExplicit_lyrics() {
            return explicit_lyrics;
        }
        public void setExplicit_lyrics(boolean explicit_lyrics) {
            this.explicit_lyrics = explicit_lyrics;
        }

        public int getExplicit_content_lyrics() {
            return explicit_content_lyrics;
        }
        public void setExplicit_content_lyrics(int explicit_content_lyrics) {
            this.explicit_content_lyrics = explicit_content_lyrics;
        }

        public int getExplicit_content_cover() {
            return explicit_content_cover;
        }
        public void setExplicit_content_cover(int explicit_content_cover) {
            this.explicit_content_cover = explicit_content_cover;
        }

        public String getPreview() {
            return preview;
        }
        public void setPreview(String preview) {
            this.preview = preview;
        }

        public String getMd5_image() {
            return md5_image;
        }
        public void setMd5_image(String md5_image) {
            this.md5_image = md5_image;
        }

        public DeezerArtist getArtist() {
            return artist;
        }
        public void setArtist(DeezerArtist artist) {
            this.artist = artist;
        }

        public DeezerAlbum getAlbum() {
            return album;
        }
        public void setAlbum(DeezerAlbum album) {
            this.album = album;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }

        public static class DeezerArtist {
            @JsonIgnore
            private int id;
            private String name;
            @JsonIgnore
            private String link;
            @JsonIgnore
            private String picture;
            @JsonIgnore
            private String picture_small;
            @JsonIgnore
            private String picture_medium;
            @JsonIgnore
            private String picture_big;
            @JsonIgnore
            private String picture_xl;
            @JsonIgnore
            private String tracklist;
            @JsonIgnore
            private String type;

            /* Getters & Setters */

            public int getId() {
                return id;
            }
            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }

            public String getLink() {
                return link;
            }
            public void setLink(String link) {
                this.link = link;
            }

            public String getPicture() {
                return picture;
            }
            public void setPicture(String picture) {
                this.picture = picture;
            }

            public String getPicture_small() {
                return picture_small;
            }
            public void setPicture_small(String picture_small) {
                this.picture_small = picture_small;
            }

            public String getPicture_medium() {
                return picture_medium;
            }
            public void setPicture_medium(String picture_medium) {
                this.picture_medium = picture_medium;
            }

            public String getPicture_big() {
                return picture_big;
            }
            public void setPicture_big(String picture_big) {
                this.picture_big = picture_big;
            }

            public String getPicture_xl() {
                return picture_xl;
            }
            public void setPicture_xl(String picture_xl) {
                this.picture_xl = picture_xl;
            }

            public String getTracklist() {
                return tracklist;
            }
            public void setTracklist(String tracklist) {
                this.tracklist = tracklist;
            }

            public String getType() {
                return type;
            }
            public void setType(String type) {
                this.type = type;
            }
        }

        public static class DeezerAlbum {
            @JsonIgnore
            private int id;
            private String title;
            @JsonIgnore
            private String cover;
            @JsonIgnore
            private String cover_small;
            @JsonIgnore
            private String cover_medium;
            @JsonIgnore
            private String cover_big;
            @JsonIgnore
            private String cover_xl;
            @JsonIgnore
            private String md5_image;
            @JsonIgnore
            private String tracklist;
            @JsonIgnore
            private String type;

            /* Getters & Setters */

            public int getId() {
                return id;
            }
            public void setId(int id) {
                this.id = id;
            }

            public String getTitle() {
                return title;
            }
            public void setTitle(String title) {
                this.title = title;
            }

            public String getCover() {
                return cover;
            }
            public void setCover(String cover) {
                this.cover = cover;
            }

            public String getCover_small() {
                return cover_small;
            }
            public void setCover_small(String cover_small) {
                this.cover_small = cover_small;
            }

            public String getCover_medium() {
                return cover_medium;
            }
            public void setCover_medium(String cover_medium) {
                this.cover_medium = cover_medium;
            }

            public String getCover_big() {
                return cover_big;
            }
            public void setCover_big(String cover_big) {
                this.cover_big = cover_big;
            }

            public String getCover_xl() {
                return cover_xl;
            }
            public void setCover_xl(String cover_xl) {
                this.cover_xl = cover_xl;
            }

            public String getMd5_image() {
                return md5_image;
            }
            public void setMd5_image(String md5_image) {
                this.md5_image = md5_image;
            }

            public String getTracklist() {
                return tracklist;
            }
            public void setTracklist(String tracklist) {
                this.tracklist = tracklist;
            }

            public String getType() {
                return type;
            }
            public void setType(String type) {
                this.type = type;
            }
        }
    }
}
