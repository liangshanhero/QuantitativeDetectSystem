package com.example.quantitativedetect.domain;

import java.io.Serializable;

public class Line implements Serializable, Cloneable{

    private int gray;//行的平均灰度
    private float concentration;//浓度
//    private boolean isFeaturte;//是否是特征点
    /*
        isValid:作用于featureLineList,只有同时位于lineList和featureLineList的Line的isValid值才可能是true,
        当位于featureLineList中的Line的isValid==false时,
        表示该条Line在输入浓度值时被舍弃(grayConcentrationSwitch的isChecked状态为false.
    */
    private boolean isValid;
    private int adaptedY;//用于存放Line在bitmap中的位置


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

//    public boolean isFeaturte() {
//        return isFeaturte;
//    }
//
//    public void setFeaturte(boolean featurte) {
//        isFeaturte = featurte;
//    }

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

    public int getAdaptedY() {
        return adaptedY;
    }

    public void setAdaptedY(int adaptedY) {
        this.adaptedY = adaptedY;
    }
}
