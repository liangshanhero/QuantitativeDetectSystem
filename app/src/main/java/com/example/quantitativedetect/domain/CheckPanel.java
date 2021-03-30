package com.example.quantitativedetect.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CheckPanel implements Serializable {
    private List<Mark> markList = new ArrayList<>();
    private List<Feature> featureList = new ArrayList<>();
//    private List<Stripe> stripeList = new ArrayList<>();
//    private int markGap;
    private int stripeQuantity;
    private int markQuantity;

    public List<Mark> getMarkList() {
        return markList;
    }

    public void setMarkList(List<Mark> markList) {
        this.markList = markList;
    }

//    public List<Stripe> getStripeList() {
//        return stripeList;
//    }
//
//    public void setStripeList(List<Stripe> stripeList) {
//        this.stripeList = stripeList;
//    }
//
//    public int getStripeQuantity() {
//        return stripeQuantity;
//    }
//
//    public void setStripeQuantity(int stripeQuantity) {
//        this.stripeQuantity = stripeQuantity;
//    }

    public double getBias(int number) {
        int gray0 = this.markList.get(0).getStripeList().get(0).getMaxGrayLine().getGray();
        int gray = this.markList.get(0).getStripeList().get(number).getMaxGrayLine().getGray();
        return (double)gray/gray0;
    }

    public List<Feature> getFeatureList() {
        return featureList;
    }

    public void setFeatureList(List<Feature> featureList) {
        this.featureList = featureList;
    }

    public int getStripeQuantity() {
        return stripeQuantity;
    }

    public void setStripeQuantity(int stripeQuantity) {
        this.stripeQuantity = stripeQuantity;
    }

    public int getMarkQuantity() {
        return markQuantity;
    }

    public void setMarkQuantity(int markQuantity) {
        this.markQuantity = markQuantity;
    }
}
