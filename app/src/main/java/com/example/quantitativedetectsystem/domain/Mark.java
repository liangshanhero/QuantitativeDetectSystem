package com.example.quantitativedetectsystem.domain;

import java.io.Serializable;
import java.util.List;

//原始名称：VirginPoints,表示的是试纸条。
public class Mark implements Serializable {
    public static final int FLAG_INPUTTED = 1,FLAG_NOT_INPUT = 0;
    private int mode;//原来是ID，表示胶体金或者荧光啥的？？？

    private List<Line> lineList;//所有的行对象列表

    private List<Line> featureLineList;//所有的特征行对象列表

    private int flag =0;




    private int lineWidthPixelQuantity;//line的宽度，每行有几个点。

    public int getLineWidthPixelQuantity() {
        return lineWidthPixelQuantity;
    }

    public void setLineWidthPixelQuantity(int lineWidthPixelQuantity) {
        this.lineWidthPixelQuantity = lineWidthPixelQuantity;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }

    public List<Line> getFeatureLineList() {
        return featureLineList;
    }

    public void setFeatureLineList(List<Line> featureLineList) {
        this.featureLineList = featureLineList;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

//  2021-0130  不知道是什么字段，暂时放在这里，弄清楚后去除
    private float trc;
    public float getTrC(int i) {
        float obj = 0;
        return obj;
    }
}
