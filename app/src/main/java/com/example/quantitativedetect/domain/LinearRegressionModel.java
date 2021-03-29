package com.example.quantitativedetect.domain;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.List;

//原来的名字是Formula，公式
public class LinearRegressionModel extends DataSupport implements Serializable {

    private double slope=0,offset=0;//斜率，偏移
    private double bias = 1;//以前是B0
    private String name;

    private List<Line> stripeList;

    public LinearRegressionModel(){

    }
    public LinearRegressionModel(double slope, double offset, String name){
        this.slope = slope;
        this.offset = offset;
        this.name = name;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public void setOffset(double offset) {
        this.offset = offset;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSlope() {
        return slope;
    }

    public double getOffset() {
        return offset;
    }

    public String getName() {
        return name;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double getBias() {
        return bias;
    }

    public List<Line> getStripeList() {
        return stripeList;
    }

    public void setStripeList(List<Line> stripeList) {
        this.stripeList = stripeList;
    }
}
