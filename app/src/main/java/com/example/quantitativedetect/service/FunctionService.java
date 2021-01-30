package com.example.quantitativedetect.service;

import com.example.quantitativedetect.domain.Rule;

public class FunctionService {

//    拟合灰度和浓度的规则，rule
    public static Rule fit(float[] concentrations, float[] gray) {
        if(concentrations.length < 2||gray.length < 2||concentrations.length != gray.length)
            return null;
        float offset = 0;
        float slope = 0;
        float Aconc = getAver(concentrations);
        float Agrey = getAver(gray);
        float numerator = 0;
        float denominator = 0;
//        int i = 0;
//        int num = 1;
        for(int i = 0;i < concentrations.length;i++){
            numerator += (concentrations[i] - Aconc)*(gray[i] - Agrey);
            denominator += (gray[i] - Agrey)*(gray[i] - Agrey);
//            float slope,offset;
//            if(concentrations[i] == concentrations[j])
//                continue;
//            slope = (concentrations[i] - concentrations[j])/(gray[i] - gray[j]);
//            offset = concentrations[i] -  gray[i]*slope;
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
