package com.example.quantitativedetect.domain;

import java.io.Serializable;

public class Line implements Serializable, Cloneable{

    private int gray;//行的平均灰度
    private float concentration;//浓度
    private boolean isFeaturte;//是否是特征点


    public int getGray() {
        return gray;
    }

    public void setGray(int gray) {
        this.gray = gray;
    }

    public boolean isFeaturte() {
        return isFeaturte;
    }

    public void setFeaturte(boolean featurte) {
        isFeaturte = featurte;
    }

    public float getConcentration() {
        return concentration;
    }

    public void setConcentration(float concentration) {
        this.concentration = concentration;
    }
}
