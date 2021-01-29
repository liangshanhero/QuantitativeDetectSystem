package com.example.quantitativedetectsystem.domain;

import java.io.Serializable;

//原始名称：VirginPoints,表示的是试纸条。
public class Mark implements Serializable {
    public static final int FLAG_INPUTTED = 1,FLAG_NOT_INPUT = 0;
    private int mode;//原来是ID，表示胶体金或者荧光啥的？？？

    private int[] dotrowAvgGrays;//试制区域中点所对应行的平均灰度的数组，所有行的灰度
    private int[] featureIndex;//再指定试制区域中检测出的峰值点的编号数组
    private int[] featureIndexOnDotrowIndex;//峰值点再points中的位置
    private float[] featureIndexConcentrations;

    private int flag = 0;

    private int CLine;

    public Mark(int[] dotrowAvgGrays, int[] featureIndexOnDotrowIndex){
        this.dotrowAvgGrays = dotrowAvgGrays;
        this.featureIndexOnDotrowIndex = featureIndexOnDotrowIndex;
        this.setCLine(featureIndexOnDotrowIndex[0]);
    }
    public Mark(int[] dotrowAvgGrays){
        this.dotrowAvgGrays = dotrowAvgGrays;
    }

    public void setIaC(int[] IDs,float[] conc){
        this.featureIndex = IDs;
        this.featureIndexConcentrations = conc;
    }

    public int[] getFeatureIndex() {
        return featureIndex;
    }

    public float[] getFeatureIndexConcentrations() {
        return featureIndexConcentrations;
    }

    public int getCLine() {
        return CLine;
    }

    public void setCLine(int CLine) {
        this.CLine = CLine;
    }

    public int[] getFeatureIndexOnDotrowIndex() {
        return featureIndexOnDotrowIndex;
    }

    public int[] getDotrowAvgGrays() {
        return dotrowAvgGrays;
    }

    public void setFeatureIndexOnDotrowIndex(int[] featureIndexOnDotrowIndex) {
        this.featureIndexOnDotrowIndex = featureIndexOnDotrowIndex;
        this.setCLine(dotrowAvgGrays[featureIndexOnDotrowIndex[0]]);
    }

    public void setDotrowAvgGrays(int[] dotrowAvgGrays) {
        this.dotrowAvgGrays = dotrowAvgGrays;
    }

    public float getTrC(int index){
        return ((float) dotrowAvgGrays[featureIndexOnDotrowIndex[index+1]])/((float)CLine);
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
