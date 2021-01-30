package com.example.quantitativedetect.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//TODO 好像是特征Line对象的转换，将concentration和gray进行bias转换，
public class Archive implements Serializable {
    private int[] points;
    private float conc0 = 1;
    private float gray0 = 1;//
    private int ID;
    private List<Stripe> stripes = new ArrayList<>();

    public Archive(int ID){
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public int length(){
        return stripes.size();
    }

    public Stripe getFeature(int index){
        return stripes.get(index);
    }
    public void addFeature(Stripe stripe){
        stripes.add(stripe);
    }
    public float[] getGreys(){
        float[] grays = new float[stripes.size()];
        for(int i = 0; i < stripes.size(); i++){
            grays[i] = stripes.get(i).getGray();
        }
        return grays;
    }
    public float[] getConc(){
        float[] conc = new float[stripes.size()-1];
        for(int i = 0; i < stripes.size(); i++){
            conc[i] = (float) stripes.get(i).getConcentration();
        }
        return conc;
    }

    public float getConc0() {
        return conc0;
    }

    public float getGray0() {
        return gray0;
    }

    public void setConc0(float conc0) {
        this.conc0 = conc0;
    }

    public void setGray0(float gray0) {
        this.gray0 = gray0;
    }
}
