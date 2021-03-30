package com.example.quantitativedetect.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Feature implements Serializable {
    private List<Stripe> stripeList = new ArrayList<>();

    public List<Stripe> getStripeList() {
        return stripeList;
    }

    public void setStripeList(List<Stripe> stripeList) {
        this.stripeList = stripeList;
    }
}
