package com.example.quantitativedetect.domain;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//TODO 好像是特征Line对象的转换，将concentration和gray进行bias转换，
public class Stripe implements Serializable {

    private List<Line> lineList;
    private Line maxGrayLine;
//    B:本stripe的maxGrayLine的Gray值与本stripe所在Mark的CLine的maxGrayLine的Gray值之比
    private float B = 0;


    private float tLineAndeCLineGrayRatio = 1;//用于存放对应的特征点在所在的Mark中的T/C的值
    private int indexOfFeatureLineList;//ID: index of featureLine
//    lines表示本archive中包含的mark中的特征行列表
//    private List<Line> lines = new ArrayList<>();

    public Stripe(){
        lineList = new ArrayList<>();
    }

    public Stripe(int indexOfFeatureLineList){
        this.indexOfFeatureLineList = indexOfFeatureLineList;
    }

    public float gettLineAndeCLineGrayRatio() {
        return tLineAndeCLineGrayRatio;
    }

    public void settLineAndeCLineGrayRatio(float tLineAndeCLineGrayRatio) {
        this.tLineAndeCLineGrayRatio = tLineAndeCLineGrayRatio;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }

    public Line getMaxGrayLine() {
        return maxGrayLine;
    }

    public void setMaxGrayLine(Line maxGrayLine) {
        this.maxGrayLine = maxGrayLine;
    }

    public float getB() {
        return B;
    }

    public void setB(float b) {
        B = b;
    }
}
