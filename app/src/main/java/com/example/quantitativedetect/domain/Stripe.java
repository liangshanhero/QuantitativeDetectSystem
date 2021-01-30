package com.example.quantitativedetect.domain;

import java.io.Serializable;


public class Stripe implements Serializable {
    private float concentration;
    private float gray;
//    private int x;

    public Stripe(float concentration, float gray){
        this.concentration = concentration;
        this.gray = gray;
    }

    public void setConcentration(float concentration) {
        this.concentration = concentration;
    }

    public float getConcentration() {
        return concentration;
    }

    public float getGray() {
        return gray;
    }

//    public int getX() {
//        return x;
//    }
}
