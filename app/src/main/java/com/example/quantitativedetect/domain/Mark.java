package com.example.quantitativedetect.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//原始名称：VirginPoints,表示的是试纸条。
public class Mark implements Serializable {


    public static final int FLAG_INPUTTED = 1,FLAG_NOT_INPUT = 0;
    //凭感觉flag要改变时，将FLAG_INPUTTED的值赋给flag
    private int isConcentrationInputted =0;
    private int detectMethodPlusID;//原来是ID，表示胶体金或者荧光啥的？？？   检测方式

//    private List<Line> lineList = new ArrayList<>();//所有的行对象列表
    private List<Stripe> stripeList = new ArrayList<>();
    int stripeQuantity;

//    private List<Line> featureLineList = new ArrayList<>();//所有的特征行对象列表    存峰值的行 的数组




    //  TODO 2021-0130 暂时固定设置5个，似乎没用上
    private int lineWidthPixelQuantity = 5;//line的宽度，每行有几个点。CL可能时基准线





    public int getLineWidthPixelQuantity() {
        return lineWidthPixelQuantity;
    }

    public void setLineWidthPixelQuantity(int lineWidthPixelQuantity) {
        this.lineWidthPixelQuantity = lineWidthPixelQuantity;
    }

    public int getDetectMethodPlusID() {
        return detectMethodPlusID;
    }

    public void setDetectMethodPlusID(int detectMethodPlusID) {
        this.detectMethodPlusID = detectMethodPlusID;
    }

//    public List<Line> getLineList() {
//        return lineList;
//    }
//
//    public void setLineList(List<Line> lineList) {
//        this.lineList = lineList;
//    }
//
//    public List<Line> getFeatureLineList() {
//        return featureLineList;
//    }
//
//    public void setFeatureLineList(List<Line> featureLineList) {
//        this.featureLineList = featureLineList;
//    }

    public int getIsConcentrationInputted() {
        return isConcentrationInputted;
    }

    public void setIsConcentrationInputted(int isConcentrationInputted) {
        this.isConcentrationInputted = isConcentrationInputted;
    }

//  2021-0130  不知道是什么字段，暂时放在这里，弄清楚后去除
//    TrC：T/C：T 线的灰度与 C 线的灰度之比，       线：feature line，C线：第一条feature line
    private float trc;
    public float getMaxGrayLineTrC(int i) {
        return (float)this.getStripeList().get(i).getMaxGrayLine().getGray()/(float)this.getStripeList().get(0).getMaxGrayLine().getGray();
//        return (float)this.getFeatureLineList().get(i).getGray()/(float)this.getFeatureLineList().get(0).getGray();
    }
    public float getNormalLineTrC(int stripeIndex, int lineIndexInStripe){
     return (float)this.getStripeList().get(stripeIndex).getLineList().get(lineIndexInStripe).getGray()/(float)this.getStripeList().get(0).getMaxGrayLine().getGray();
    }

//    public void setConcentrations(int[] ids, float[] concs) {
//    }
//
    public void setFlag(int flag) {
    }

    public List<Stripe> getStripeList() {
        return stripeList;
    }

    public void setStripeList(List<Stripe> stripeList) {
        this.stripeList = stripeList;
    }

    public void setStripeQuantity(int stripeQuantity) {
        this.stripeQuantity = stripeQuantity;
    }

    public int getStripeQuantity() {
        return stripeQuantity;
    }


//    原始getTrC,需要修改加以利用
//    public float getTrC(int index){
//        return ((float) dotrowAvgGrays[featureIndexOnDotrowIndex[index+1]])/((float)CLine);
//    }
}
