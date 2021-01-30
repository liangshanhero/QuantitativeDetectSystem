package com.example.quantitativedetect.domain;

import org.litepal.crud.DataSupport;

public class Result extends DataSupport {

    private double concentration;
    private String name;

    public Result(){

    }
    public Result(double concentration, String name){
        this.concentration = concentration;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getConcentration() {
        return concentration;
    }

    public void setConcentration(double concentration) {
        this.concentration = concentration;
    }

    public void setName(String name) {
        this.name = name;
    }
}
