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
//           TODO 2021-0402  不知道选哪个

//        x轴的坐标值数组
        double[] logOfConcentrations = new double[feature.getStripeList().size()-1];
//        y轴的坐标值数组
        double[] ratiosByGrays = new double[feature.getStripeList().size()-1];
//        浓度值数组(排除CLine)
        float[] concentrations = new float[feature.getStripeList().size()-1];
//        灰度值数组(排除CLine)
        float[] grays = new float[feature.getStripeList().size()-1];
        for (int i = 1; i < feature.getStripeList().size(); i++) {
//           TODO 2021-0402  不知道选哪个
            concentrations[i-1] = feature.getStripeList().get(i).getMaxGrayLine().getConcentration();
            grays[i-1] = feature.getStripeList().get(i).getMaxGrayLine().getGray();

            logOfConcentrations[i-1] = Math.log10(feature.getStripeList().get(i).getMaxGrayLine().getConcentration());
            ratiosByGrays[i-1] = (feature.getStripeList().get(i).getB())/feature.getB0();

        }
//       TODO 2021-0402     不知道选哪个
        float averageConc = getAver(concentrations);
        float averageGray = getAver(grays);
        double averageX = getAver(logOfConcentrations);
        double averageY = getAver(ratiosByGrays);


        float numerator = 0;
        float denominator = 0;
//        int i = 0;
//        int num = 1;
        for(int i = 0;i < logOfConcentrations.length;i++){
//           TODO 2021-0402  不知道选哪个

//            numerator += (concentrations[i] - averageConc)*(grays[i] - averageGray);
//            denominator += (grays[i] - averageGray)*(grays[i] - averageGray);
            denominator += (concentrations[i] - averageConc)*(grays[i] - averageGray);
            numerator += (grays[i] - averageGray)*(grays[i] - averageGray);

//            numerator += (logOfConcentrations[i] - averageX)*(ratiosByGrays[i] - averageY);
//            denominator += (ratiosByGrays[i] - averageY)*(ratiosByGrays[i] - averageY);
//            denominator += (logOfConcentrations[i] - averageX)*(ratiosByGrays[i] - averageY);
//            numerator += (ratiosByGrays[i] - averageY)*(ratiosByGrays[i] - averageY);



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
//        TODO 不知道选哪个

        offset = averageGray - slope * averageConc;
//        offset = averageConc - slope*averageGray;

//        offset = averageY - slope*averageX;
//        offset = averageX - slope*averageY;

        LinearRegressionModel linearRegressionModel = new LinearRegressionModel();
        linearRegressionModel.setSlope(slope);
        linearRegressionModel.setOffset(offset);
        return linearRegressionModel;
    }

    public static double calculateConcentration(LinearRegressionModel linearRegressionModel, double gray){

        double Bn = (double)gray / (double)linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray();
        double offset = linearRegressionModel.getOffset();
        double slope = linearRegressionModel.getSlope();
//        TODO 不知道选哪个

//        return gray * linearRegressionModel.getSlope() + linearRegressionModel.getOffset();
        return ( gray - linearRegressionModel.getOffset()) / linearRegressionModel.getSlope();

//        return Math.pow(10,(Bn/linearRegressionModel.getBias() - offset)/slope);
//        return (Bn/linearRegressionModel.getBias() - offset)/slope;
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
