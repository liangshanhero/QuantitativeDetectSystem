package com.example.quantitativedetectsystem.service;

import com.example.quantitativedetectsystem.domain.Rule;

public class FunctionService {

    public static Rule fitFunction(float[] concentrations, float[] grey) {
        if(concentrations.length < 2||grey.length < 2||concentrations.length != grey.length)
            return null;
        float offset = 0;
        float slope = 0;
        float Aconc = getAver(concentrations);
        float Agrey = getAver(grey);
        float numerator = 0;
        float denominator = 0;
//        int i = 0;
//        int num = 1;
        for(int i = 0;i < concentrations.length;i++){
            numerator += (concentrations[i] - Aconc)*(grey[i] - Agrey);
            denominator += (grey[i] - Agrey)*(grey[i] - Agrey);
//            float slope,offset;
//            if(concentrations[i] == concentrations[j])
//                continue;
//            slope = (concentrations[i] - concentrations[j])/(grey[i] - grey[j]);
//            offset = concentrations[i] -  grey[i]*slope;
//            Aslope += (slope - Aslope)/num;
//            Aoffset += (offset - Aoffset)/num;
//            num++;
        }
        slope = numerator / denominator;
        offset = Aconc - slope*Agrey;
        Rule rule = new Rule();
        rule.setSlope(slope);
        rule.setOffset(offset);
        return rule;
    }

    public static double calConc(Rule rule, double grey){
        return grey* rule.getSlope() + rule.getOffset();
    }
    public static double calGrey(Rule rule, double conc){
        return (conc - rule.getOffset())/ rule.getSlope();
    }

    public static float getAver(float[] array){
        if(array.length <= 0)
            return 0;
        float num = 0;
        for(float n:array)
            num += n;
        return num / array.length;
    }
}
