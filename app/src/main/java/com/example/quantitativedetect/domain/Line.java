package com.example.quantitativedetect.domain;

import java.io.Serializable;

public class Line implements Serializable, Cloneable{

    private int gray;//行的平均灰度
    private float concentration;//浓度
    private boolean isValid;


    public int getGray() {
        return gray;
    }

    public Line(){}


    public Line(float concentration, int gray){
        this.concentration = concentration;
        this.gray = gray;
    }

    public void setGray(int gray) {
        this.gray = gray;
    }

    public float getConcentration() {
        return concentration;
    }

    public void setConcentration(float concentration) {
        this.concentration = concentration;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

}
