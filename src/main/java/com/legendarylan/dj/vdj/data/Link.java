package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Link")
public class Link {
    private String netSearch;
    private String drm;

    @XmlAttribute(name="NetSearch")
    public String getNetSearch() {
        return netSearch;
    }
    public void setNetSearch(String netSearch) {
        this.netSearch = netSearch;
    }

    @XmlAttribute(name="DRM")
    public String getDrm() {
        return drm;
    }
    public void setDrm(String drm) {
        this.drm = drm;
    }
}
