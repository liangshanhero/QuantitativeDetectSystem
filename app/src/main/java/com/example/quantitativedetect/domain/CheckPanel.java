package com.example.quantitativedetect.domain;

import java.util.List;

public class CheckPanel {
    private List<Mark> markList;
    private List<Stripe> stripeList;
    private int markGap;
    private int stripeQuantity;
    private int markQuantity;

    public List<Mark> getMarkList() {
        return markList;
    }

    public void setMarkList(List<Mark> markList) {
        this.markList = markList;
    }

    public List<Stripe> getStripeList() {
        return stripeList;
    }

    public void setStripeList(List<Stripe> stripeList) {
        this.stripeList = stripeList;
    }

    public int getMarkGap() {
        return markGap;
    }

    public void setMarkGap(int markGap) {
        this.markGap = markGap;
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
