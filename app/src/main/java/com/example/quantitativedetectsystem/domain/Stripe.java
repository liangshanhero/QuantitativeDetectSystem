package com.example.quantitativedetectsystem.domain;

import java.io.Serializable;


public class Stripe implements Serializable {
    private float conc;
    private float gray;
    private int x;

    public Stripe(float conc, float gray){
        this.conc = conc;
        this.gray = gray;
    }

    public void setConc(float conc) {
        this.conc = conc;
    }

    public float getConc() {
        return conc;
    }

    public float getGray() {
        return gray;
    }

    public int getX() {
        return x;
    }
}
