package com.example.quantitativedetect.domain;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//TODO 好像是特征Line对象的转换，将concentration和gray进行bias转换，
public class Stripe implements Serializable {
    private Bitmap bitmap;

    private int[] points;
//    private float conc0 = 1;
    private float tLineAndeCLineGrayRatio = 1;//用于存放对应的特征点在所在的Mark中的T/C的值
    private int indexOfFeatureLineList;//ID: index of featureLine
//    lines表示本archive中包含的mark中的特征行列表
    private List<Line> lines = new ArrayList<>();

    public Stripe(int indexOfFeatureLineList){
        this.indexOfFeatureLineList = indexOfFeatureLineList;
    }

    public int getIndexOfFeatureLineList() {
        return indexOfFeatureLineList;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public int length(){
        return lines.size();
    }

    public Line getLine(int index){
        return lines.get(index);
    }
    public void addLine(Line line){
        lines.add(line);
    }
    public float[] getGrays(){
        float[] grays = new float[lines.size()];
        for(int i = 0; i < lines.size(); i++){
            grays[i] = lines.get(i).getGray();
        }
        return grays;
    }
    public float[] getConc(){
        float[] conc = new float[lines.size()-1];
        for(int i = 0; i < lines.size(); i++){
            conc[i] = (float) lines.get(i).getConcentration();
        }
        return conc;
    }

//    public float getConc0() {
//        return conc0;
//    }

    public float gettLineAndeCLineGrayRatio() {
        return tLineAndeCLineGrayRatio;
    }

//    public void setConc0(float conc0) {
//        this.conc0 = conc0;
//    }

    public void settLineAndeCLineGrayRatio(float tLineAndeCLineGrayRatio) {
        this.tLineAndeCLineGrayRatio = tLineAndeCLineGrayRatio;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
