package com.example.quantitativedetect.domain;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

//原来的名字是Formula，公式
public class Rule extends DataSupport implements Serializable {

    private double slope=0,offset=0;//斜率，偏移
    private double B0 = 1;//
    private String name;
    public Rule(){

    }
    public Rule(double slope, double offset, String name){
        this.slope = slope;
        this.offset = offset;
        this.name = name;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSlope() {
        return slope;
    }

    public double getOffset() {
        return offset;
    }

    public String getName() {
        return name;
    }

    public void setB0(double b0) {
        B0 = b0;
    }

    public double getB0() {
        return B0;
    }
}
