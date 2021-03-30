package com.example.quantitativedetect.domain;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//TODO 好像是特征Line对象的转换，将concentration和gray进行bias转换，
public class Stripe implements Serializable {

    private List<Line> lineList;
    private Line maxGrayLine;

//    private List<Line> cLineList;//存放第一条特征线,用于计算T/C
    //用于存放对应横行的特征线,用于计算阴性T/C(即第一条Mark的featureLine/CLine)和阳性T/C
//    private List<Line> featureLineList;
//    论文中用B表示阳性特征行(除第一条mark)的灰度值与所在mark的CLine的灰度值的比值,应该有多个
//    private List<Float> BList;
    //    论文中用B0表示阴性特征行(第一条mark)的灰度值与所在mark的CLine的灰度值的比值
//    private float B0;

//    private int[] points;
//    private float conc0 = 1;
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

//    public float getB0() {
//        return B0;
//    }
//
//    public void setB0(float b0) {
//        B0 = b0;
//    }
//
//    public List<Float> getBList() {
//        return BList;
//    }
//
//    public void setBList(List<Float> BList) {
//        this.BList = BList;
//    }
//
//    public List<Line> getFeatureLineList() {
//        return featureLineList;
//    }
//
//    public void setFeatureLineList(List<Line> featureLineList) {
//        this.featureLineList = featureLineList;
//    }

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
}
