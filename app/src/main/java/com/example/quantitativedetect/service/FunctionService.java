package com.example.quantitativedetect.service;

import com.example.quantitativedetect.domain.Feature;
import com.example.quantitativedetect.domain.LinearRegressionModel;

public class FunctionService {

//    拟合灰度和浓度的规则，rule
    public static LinearRegressionModel fit(float[] concentrations, float[] gray) {
        if(concentrations.length < 2||gray.length < 2||concentrations.length != gray.length)
            return null;
        float offset = 0;
        float slope = 0;
        float averageConcentration = getAver(concentrations);
        float averageGray = getAver(gray);
        float numerator = 0;
        float denominator = 0;
//        int i = 0;
//        int num = 1;
        for(int i = 0;i < concentrations.length;i++){
            numerator += (concentrations[i] - averageConcentration)*(gray[i] - averageGray);
            denominator += (gray[i] - averageGray)*(gray[i] - averageGray);
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
        offset = averageConcentration - slope*averageGray;
        LinearRegressionModel linearRegressionModel = new LinearRegressionModel();
        linearRegressionModel.setSlope(slope);
        linearRegressionModel.setOffset(offset);
        return linearRegressionModel;
    }

    public static LinearRegressionModel fluorescentMicrosphereFit(Feature feature) {

        double offset = 0;
        float slope = 0;
        float averageBDividedB0;
//        x轴的坐标值数组
        double[] logOfConcentrations = new double[feature.getStripeList().size()-1];
//        y轴的坐标值数组
        double[] ratiosByGrays = new double[feature.getStripeList().size()-1];
        for (int i = 1; i < feature.getStripeList().size(); i++) {
            logOfConcentrations[i-1] = Math.log10(feature.getStripeList().get(i).getMaxGrayLine().getConcentration());
            ratiosByGrays[i-1] = (feature.getStripeList().get(i).getB())/feature.getB0();
        }


        double averageX = getAver(logOfConcentrations);
        double averageY = getAver(ratiosByGrays);
        float numerator = 0;
        float denominator = 0;
//        int i = 0;
//        int num = 1;
        for(int i = 0;i < logOfConcentrations.length;i++){
            numerator += (logOfConcentrations[i] - averageX)*(ratiosByGrays[i] - averageY);
            denominator += (ratiosByGrays[i] - averageY)*(ratiosByGrays[i] - averageY);
//            float slope,offset;
//            if(logOfConcentrations[i] == logOfConcentrations[j])
//                continue;
//            slope = (logOfConcentrations[i] - logOfConcentrations[j])/(ratiosByGrays[i] - ratiosByGrays[j]);
//            offset = logOfConcentrations[i] -  ratiosByGrays[i]*slope;
//            Aslope += (slope - Aslope)/num;
//            Aoffset += (offset - Aoffset)/num;
//            num++;
        }
        slope = numerator / denominator;
        offset = averageX - slope*averageY;
        LinearRegressionModel linearRegressionModel = new LinearRegressionModel();
        linearRegressionModel.setSlope(slope);
        linearRegressionModel.setOffset(offset);
        return linearRegressionModel;
    }

    public static double calculateConcentration(LinearRegressionModel linearRegressionModel, double grey){
        return grey* linearRegressionModel.getSlope() + linearRegressionModel.getOffset();
    }
    public static double calGrey(LinearRegressionModel linearRegressionModel, double conc){
        return (conc - linearRegressionModel.getOffset())/ linearRegressionModel.getSlope();
    }

    public static float getAver(float[] array){
        if(array.length <= 0)
            return 0;
        float num = 0;
        for(float n:array)
            num += n;
        return num / array.length;
    }
    public static double getAver(double[] array){
        if(array.length <= 0)
            return 0;
        float num = 0;
        for(double n:array)
            num += n;
        return num / array.length;
    }
}
