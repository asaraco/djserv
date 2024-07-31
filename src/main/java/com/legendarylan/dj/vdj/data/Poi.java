package com.legendarylan.dj.vdj.data;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

public class Poi {
    private double pos;
    private String type;
    private String point;
    private String name;
    private int num;
    /* Unused attributes present in the XML
    private double bpm;
    private int size;
    private String color;
    private String slot;
     */

    @XmlAttribute(name="Pos")
    public double getPos() {
        return pos;
    }
    public void setPos(double pos) {
        this.pos = pos;
    }

    @XmlAttribute(name="Type")
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute(name="Point")
    public String getPoint() {
        return point;
    }
    public void setPoint(String point) {
        this.point = point;
    }

    @XmlAttribute(name="Name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="Num")
    public int getNum() {
        return num;
    }
    public void setNum(int num) {
        this.num = num;
    }
}
