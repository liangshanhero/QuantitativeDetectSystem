package com.example.quantitativedetect.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Feature implements Serializable {
//    stripeList是本组的stripe,controlStripeList是CLine的stripe
    private List<Stripe> stripeList = new ArrayList<>();

//    private List<Stripe> controlStripeList = new ArrayList<>();
//    B0:本feature的stripeList中的第一个stripe的maxGrayLine的Gray值与第一个Mark中的CLine的maxGrayLine的Gray值之比
    private double B0=0;

    public List<Stripe> getStripeList() {
        return stripeList;
    }

    public void setStripeList(List<Stripe> stripeList) {
        this.stripeList = stripeList;
    }

//    public List<Stripe> getControlStripeList() {
//        return controlStripeList;
//    }
//
//    public void setControlStripeList(List<Stripe> controlStripeList) {
//        this.controlStripeList = controlStripeList;
//    }

    public double getB0() {
        return B0;
    }

    public void setB0(double b0) {
        B0 = b0;
    }
}
