package com.example.quantitativedetect.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
//TODO 好像是特征Line对象的转换，将concentration和gray进行bias转换，
public class Archive implements Serializable {
    private int[] points;
//    private float conc0 = 1;
    private float gray0 = 1;//
    private int ID;//ID: index of featureLine
//    lines表示本archive中包含的mark中的特征行列表
    private List<Line> lines = new ArrayList<>();

    public Archive(int ID){
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public int length(){
        return lines.size();
    }

    public Line getLine(int index){
        return lines.get(index);
    }
    public void addLine(Line line){
        lines.add(line);
    }
    public float[] getGrays(){
        float[] grays = new float[lines.size()];
        for(int i = 0; i < lines.size(); i++){
            grays[i] = lines.get(i).getGray();
        }
        return grays;
    }
    public float[] getConc(){
        float[] conc = new float[lines.size()-1];
        for(int i = 0; i < lines.size(); i++){
            conc[i] = (float) lines.get(i).getConcentration();
        }
        return conc;
    }

//    public float getConc0() {
//        return conc0;
//    }

    public float getGray0() {
        return gray0;
    }

//    public void setConc0(float conc0) {
//        this.conc0 = conc0;
//    }

    public void setGray0(float gray0) {
        this.gray0 = gray0;
    }
}
