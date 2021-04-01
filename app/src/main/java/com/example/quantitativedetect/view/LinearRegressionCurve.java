package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;

import com.example.quantitativedetect.Activity.FunctionFormulaActivity;
import com.example.quantitativedetect.domain.LinearRegressionModel;
import com.example.quantitativedetect.domain.Stripe;
import com.example.quantitativedetect.service.FunctionService;

import java.util.List;

public class LinearRegressionCurve extends BaseCoordinate {
    private LinearRegressionModel linearRegressionModel;
    private int flag = 1;
    private float conc, gray;
//    x轴的最大值和最小值
    double maxConcentrationInTable, minConcentrationInTable;

    public LinearRegressionCurve(Context context, LinearRegressionModel linearRegressionModel, int width){
        super(context);
        this.linearRegressionModel = linearRegressionModel;
        super.init(width,width,80);
        if(linearRegressionModel.getSlope() > 0){
            maxConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray());
            minConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,0);
        }
        else {
            maxConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,0);
            minConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray());
        }
    }
    public void setArchive(Stripe stripe1, Stripe stripe2, LinearRegressionModel linearRegressionModel){
//        this.stripe1 = stripe1;
//        this.stripe2 = stripe2;
        this.linearRegressionModel = linearRegressionModel;
        if(linearRegressionModel.getSlope() > 0){
            maxConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray());
            minConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,0);
        }
        else {
            maxConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,0);
            minConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray());
        }
        this.flag = 2;
        invalidate();
    }

    public void setArchive(LinearRegressionModel linearRegressionModel) {
//        this.stripe1 = stripe1;
//        this.stripe2 = stripe2;
        this.linearRegressionModel = linearRegressionModel;
        if(linearRegressionModel.getSlope() > 0){
            maxConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray());
            minConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,0);
        }
        else {
//            函数图像仅gray∈[0,1]的区间上画出,因此可能画点的时候不会画出特征点
//            max = FunctionService.calculateConcentration(linearRegressionModel,0);
//            min = FunctionService.calculateConcentration(linearRegressionModel,1);
            maxConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,0);
            minConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray());
        }
        this.flag = 2;
        invalidate();
    }

    public void formula(Canvas canvas){
        Paint paint = new Paint();
        //画点
        List<Stripe> stripeList = linearRegressionModel.getFeature().getStripeList();
        for(int i = 1; i < stripeList.size(); i++){
//        for(int i = 0; i < stripe1.length(); i++){
            paint.setStrokeWidth(15);
            float Bn = (float)stripeList.get(i).getMaxGrayLine().getGray()/(float)linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray();
            float y1 = coordinateHeight - coordinatePad - (float) (Bn/linearRegressionModel.getBias())*(coordinateHeight -2 * coordinatePad);
//            float x1 =
//            float x1 = (float)(conc1- minConcentrationInTable)/(float) (maxConcentrationInTable - minConcentrationInTable)*(coordinateWidth -2* coordinatePad);
            float x1 = ((float) (stripeList.get(i).getMaxGrayLine().getConcentration() - minConcentrationInTable) / (float) (maxConcentrationInTable - minConcentrationInTable)) * (coordinateWidth - (2 * coordinatePad));
//            float x1 = (float)(stripeList.get(i).getMaxGrayLine().getConcentration()-min)/(float)(max-min)*(wid-2*pad);
//            float x1 = (float)(stripe1.getLine(i).getConcentration()-min)/(float)(max-min)*(wid-2*pad);
//            float y1 = hei-pad-
//            float y1 = coordinateHeight - coordinatePad - stripeList.get(i).getMaxGrayLine().getGray()*(coordinateHeight -2* coordinatePad);
//            float y1 = hei-pad- stripe1.getLine(i).getGray()*(hei-2*pad);

            paint.setColor(Color.RED);
            canvas.drawPoint(x1 + coordinatePad,y1,paint);
//            TODO 待重构
            if(FunctionFormulaActivity.ONE_TWO == FunctionFormulaActivity.TWO){
//                float x2 = (float)(stripe2.getLine(i).getConcentration()-min)/(float)(max-min)*(wid-2*pad);
//                float y2 = hei-pad- stripe2.getLine(i).getGray()*(hei-2*pad);
                paint.setColor(Color.GREEN);
//                canvas.drawPoint(x2+pad,y2,paint);

                paint.setStrokeWidth(1);
                paint.setColor(Color.BLACK);

//                canvas.drawLine(x1+pad,y1,x2+pad,y2,paint);
            }
        }
    }

    public void setPoint(float conc, float gray, LinearRegressionModel linearRegressionModel){
        this.conc = conc;
        this.gray = gray/(float) linearRegressionModel.getBias();
        this.linearRegressionModel = linearRegressionModel;
        if(linearRegressionModel.getSlope() > 0){
            maxConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray());
            minConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,0);
        }
        else {
            maxConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,0);
            minConcentrationInTable = FunctionService.calculateConcentration(linearRegressionModel,linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray());
        }
        this.flag = 3;
        invalidate();
    }

    public void drawResult(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        float x = (float)(conc - minConcentrationInTable)/(float)(maxConcentrationInTable - minConcentrationInTable)*(coordinateWidth -2* coordinatePad);
        float y = coordinateHeight - coordinatePad - gray *(coordinateHeight -2* coordinatePad);
        canvas.drawPoint(x+ coordinatePad,y,paint);

        paint.setStrokeWidth(1);
        paint.setColor(Color.GREEN);
        for(int i = coordinatePad; i < x+ coordinatePad; i += 10)
            canvas.drawLine(i,y,i+5,y,paint);
        for(int i = coordinateHeight - coordinatePad; i > y; i -= 10)
            canvas.drawLine(x+ coordinatePad,i,x+ coordinatePad,i-5,paint);
    }


    @Override
    public void invalidate(){
        super.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawLine(coordinatePad, coordinateHeight - coordinatePad, coordinateWidth - coordinatePad, coordinateHeight - coordinatePad,paint);//X轴
        canvas.drawLine(coordinatePad, coordinatePad, coordinatePad, coordinateHeight - coordinatePad,paint);//Y轴
        canvas.drawLine(coordinatePad, coordinatePad, coordinateWidth - coordinatePad, coordinatePad,paint);//上X轴
        canvas.drawLine(coordinateWidth - coordinatePad, coordinatePad, coordinateWidth - coordinatePad, coordinateHeight - coordinatePad,paint);//右Y轴
        paint.setStrokeWidth(1);
        int interval = ( coordinateHeight - 2 * coordinatePad)/5;
        for(int i = 1;i <= 5;i++){
            paint.setStrokeWidth(1);
            canvas.drawLine(coordinatePad, coordinatePad +i*interval, coordinateWidth - coordinatePad, coordinatePad +i*interval,paint);
        }
        paint.setTextSize(50);
        canvas.drawText("BN/B0", coordinatePad/4, coordinatePad,paint);
        paint.setTextSize(35);
        canvas.drawText(String.valueOf(0.8), coordinatePad/4, coordinatePad + interval,paint);
        canvas.drawText(String.valueOf(0.6), coordinatePad/4, coordinatePad + 2*interval,paint);
        canvas.drawText(String.valueOf(0.4), coordinatePad/4, coordinatePad + 3*interval,paint);
        canvas.drawText(String.valueOf(0.2), coordinatePad/4, coordinatePad + 4*interval,paint);
        canvas.drawText(String.valueOf(0), coordinatePad/4, coordinatePad + 5*interval,paint);

        interval = (coordinateWidth - 2 * coordinatePad)/5;
        for(int i = 1;i < 5;i++){
            paint.setStrokeWidth(1);
            canvas.drawLine(coordinatePad +i*interval, coordinatePad, coordinatePad +i*interval, coordinateHeight - coordinatePad,paint);
        }

        double target = (maxConcentrationInTable - minConcentrationInTable)/5;
        for(int i = 1;i < 5;i++){
            if (i%2==0){
                canvas.drawText(String.format("%.4f", minConcentrationInTable +target*i),i*interval+ coordinatePad/4, coordinateHeight + coordinatePad/4,paint);
            }else {
                canvas.drawText(String.format("%.4f", minConcentrationInTable +target*i),i*interval+ coordinatePad/4, coordinateHeight - coordinatePad/4,paint);
            }
        }
        canvas.drawText(String.format("%.4f", minConcentrationInTable), coordinatePad/4, coordinateHeight - coordinatePad/4,paint);
        canvas.drawText(String.format("%.4f", maxConcentrationInTable), coordinateWidth - 2*coordinatePad, coordinateHeight - coordinatePad/4,paint);
        paint.setTextSize(50);
//        canvas.drawText("lg[concentration]", coordinateWidth/3, coordinateHeight + coordinatePad,paint);
        canvas.drawText(" concentration ", coordinateWidth/3, coordinateHeight + coordinatePad,paint);

        //画方程
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0f);
        paint.setColor(Color.rgb(255,165,0));
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

//        Point[] points = new Point[coordinateHeight - coordinatePad*2];

//        int index = 0;
//        Path path = new Path();
        double maxConc = 0,minConc = 100;
        for(float i = coordinatePad; i < coordinateHeight - coordinatePad - 2; i++){
            int cLineGray = linearRegressionModel.getFeature().getStripeList().get(0).getMaxGrayLine().getGray();
            int gray1 = (int) ((coordinateHeight - coordinatePad -i)/(coordinateHeight -2* coordinatePad)*linearRegressionModel.getBias()*cLineGray);
            int gray2 = (int) ((coordinateHeight - coordinatePad -i-1)/(coordinateHeight -2* coordinatePad)*linearRegressionModel.getBias()*cLineGray);
            double conc1 = FunctionService.calculateConcentration(linearRegressionModel,gray1);
            maxConc = conc1>maxConc?conc1:maxConc;
            minConc = conc1<minConc?conc1:minConc;
            double conc2 = FunctionService.calculateConcentration(linearRegressionModel,gray2);
            maxConc = conc2>maxConc?conc2:maxConc;
            minConc = conc2<minConc?conc2:minConc;
            float x1 = (float)(conc1- minConcentrationInTable)/(float) (maxConcentrationInTable - minConcentrationInTable)*(coordinateWidth -2* coordinatePad);
            float x2 = (float)(conc2- minConcentrationInTable)/(float) (maxConcentrationInTable - minConcentrationInTable)*(coordinateWidth -2* coordinatePad);
            if(x1 < 0)
                continue;
//            points[index] = new Point((int)x1,(int)i);
//            path.moveTo(x1+coordinatePad,i);
//            path.quadTo((x1+coordinatePad+x2+coordinatePad)/2,(i*2+1)/2,x2+coordinatePad,i+1);
//            if (i==coordinateHeight - coordinatePad - 3){
//                path.setLastPoint(x2,i+1);
//            }
//            canvas.drawPath(path,paint);
            canvas.drawLine(x1+ coordinatePad,i,x2+ coordinatePad,i+1,paint);
        }
        Log.d("TAG","maxConc = "+maxConc);
        Log.d("TAG","minConc = "+minConc);




        if(flag == 2)
            formula(canvas);
        else if(flag == 3)
            drawResult(canvas);
    }
    private static double distance(Point p1, Point p2) {
        return Math.sqrt(((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }
    private static Point ratioPointConvert(Point p1, Point p2, double ratio) {
        Point p = new Point();
        p.x = (int) (ratio * (p1.x - p2.x) + p2.x);
        p.y = (int) (ratio * (p1.y - p2.y) + p2.y);
        return p;
    }

}
