package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.quantitativedetect.domain.Mark;
import com.example.quantitativedetect.domain.Stripe;


public class GrayCurve extends BaseCoordinate {

    public static final int TEXT_SPINNER_SWITCH_CURVE = 0;

    private float[] points;//所有点的trc值
//    private int[] points;//所有的点的灰度值
    private int[] maxGrayIndexInPoints;//特征点在points这个数组里面的位置
    private int cLineIndex;//第一个特征点
    private int selectedPointIndex;//目前选中的输入框对应的点
//    private int focusPoint = -1;//上一次选中的点,初始为-1,即没有点被选中
    //第一个特征的点？？？？？
    private float CL = 1;
    private float zoom = 1;
    private int curveType = 0;
//    public AbbreviationCurve(Context context,int width,int[] points,float zoom,int[] features,int flag){
//        super(context);
//        super.init(width,width/2,40);
//        this.points = points;
//        this.zoom = zoom*(width/2-2*pad);//width/3为View的高度，而width/3-2*pad才是Y轴的长度
//        this.features = features;
//        //this.CL = points[features[0]];
//        this.flag = flag;
//        if(flag == 0){
//            this.zoom = zoom*(width/3-2*pad);
//            super.init(width,width/3,40);
//        }
//    }

    public GrayCurve(Context context, int width, Mark mark, float zoom, int curveType){
        super(context);
        super.init(width,width/2,40);
        int lineQuantity = 0;
        for (int i = 0; i < mark.getStripeList().size(); i++) {
            lineQuantity+=mark.getStripeList().get(i).getLineList().size();
        }
        points = new float[lineQuantity];
//        points = new int[mark.getLineList().size()];
        maxGrayIndexInPoints = new int[mark.getStripeList().size()];
        int pointsIndex = 0;
        for (int i = 0; i < mark.getStripeList().size(); i++) {
            Stripe stripe = mark.getStripeList().get(i);
            for (int j = 0; j < stripe.getLineList().size(); j++) {
                points[pointsIndex++]=mark.getNormalLineTrC(i,j);
            }
        }
//        for (int i=0; i<lineQuantity;i++){
////            points[i]=mark.getLineList().get(i).getGray();
//            points[i] = mark.getTrC(i);
//        }
        this.zoom = zoom*(width/2-2* coordinatePad);//width/3为View的高度，而width/3-2*pad才是Y轴的长度
        int maxGrayIndex = 0;
        for (int i = 0; i < mark.getStripeList().size(); i++) {
            Stripe stripe = mark.getStripeList().get(i);
            maxGrayIndexInPoints[i] = maxGrayIndex + stripe.getLineList().indexOf(stripe.getMaxGrayLine());
            maxGrayIndex += stripe.getLineList().size();

        }

//        for (int i=0; i<mark.getStripeList().size();i++){
//            maxGrayIndexInPoints[i]=mark.getLineList().indexOf(mark.getFeatureLineList().get(i));
//        }
        this.cLineIndex= maxGrayIndexInPoints[0];
//        this.points = points;
//        this.features = features;
        //TODO CL是偏移量，但不知道具体含义，CL值增大能使得缩略图（grayCurve）位置降低
        // （或者说是使整个Y轴的范围（0~maxValue）更大，导致曲线的上下边界被压缩至可见，即使曲线的振幅变小？）
        this.CL = points[maxGrayIndexInPoints[0]];
//        this.CL = points[featureIndex[0]];
        this.curveType = curveType;
        if(curveType == 0){
            this.zoom = zoom*(width/3-2* coordinatePad);
            super.init(width,width/3,40);
        }
    }


    public int[] getMaxGrayIndexInPoints() {
        return maxGrayIndexInPoints;
    }

    public void setMaxGrayIndexInPoints(int[] maxGrayIndexInPoints){
        this.maxGrayIndexInPoints = maxGrayIndexInPoints;
        boolean isFirstPositive = false;
        for (int i = 0; i < maxGrayIndexInPoints.length && !isFirstPositive; i++) {
            if (maxGrayIndexInPoints[i]>=0){
                this.cLineIndex = maxGrayIndexInPoints[i];
                isFirstPositive = true;
            }
        }
        invalidate();
    }
    public void setWid(int wid){
        super.init(wid,wid/3,25);
        invalidate();
    }
    public void setCurveType(int curveType){
        this.curveType = curveType;
        if(this.curveType ==1)
        invalidate();
    }
    @Override
    public void invalidate(){
        super.invalidate();
    }

    public void bigDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        canvas.drawLine(coordinatePad, coordinatePad, coordinateWidth - coordinatePad, coordinatePad,paint);//上X轴
        canvas.drawLine(coordinateWidth - coordinatePad, coordinatePad, coordinateWidth - coordinatePad, coordinateHeight - coordinatePad,paint);//右Y轴
        paint.setStrokeWidth(1);
        int interval = ( coordinateHeight - 2* coordinatePad)/5;
        for(int i = 1;i <= 5;i++){
            paint.setStrokeWidth(1);
            canvas.drawLine(coordinatePad, coordinatePad +i*interval, coordinateWidth - coordinatePad, coordinatePad +i*interval,paint);
        }
        paint.setTextSize(20);
        canvas.drawText("T/C", coordinatePad -30, coordinatePad,paint);
        canvas.drawText(String.valueOf(0.8), coordinatePad -30, coordinatePad +interval,paint);
        canvas.drawText(String.valueOf(0.6), coordinatePad -30, coordinatePad +2*interval,paint);
        canvas.drawText(String.valueOf(0.4), coordinatePad -30, coordinatePad +3*interval,paint);
        canvas.drawText(String.valueOf(0.2), coordinatePad -30, coordinatePad +4*interval,paint);
        canvas.drawText(String.valueOf(0), coordinatePad -30, coordinatePad +5*interval,paint);

        paint.setStrokeWidth(3);
        for(int i = 0;i < points.length-1;i++){
            paint.setColor(Color.rgb(255,165,0));
            float sx = (float)i/points.length * (coordinateWidth -2* coordinatePad) + coordinatePad;
            float sy = coordinateHeight - coordinatePad - ((float)points[i])/CL*zoom;
            float ex = (float)(i+1)/points.length * (coordinateWidth -2* coordinatePad) + coordinatePad;
            float ey = coordinateHeight - coordinatePad - ((float)points[i+1])/CL*zoom;
            canvas.drawLine(sx,sy,ex,ey,paint);
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(9);
        for(int i = 0; i < maxGrayIndexInPoints.length; i++){
            if (maxGrayIndexInPoints[i]<=-1){
                continue;
            }
            paint.setColor(Color.RED);
            paint.setStrokeWidth(9);
            if(maxGrayIndexInPoints[i] == cLineIndex) {
                paint.setColor(Color.GREEN);
            }
            if(maxGrayIndexInPoints[i] == selectedPointIndex){
                paint.setColor(Color.BLUE);
                paint.setStrokeWidth(18);
            }
            float x = (float) maxGrayIndexInPoints[i]/points.length * (coordinateWidth - 2* coordinatePad) + coordinatePad;
            float y = coordinateHeight - coordinatePad - (float)points[maxGrayIndexInPoints[i]]/CL * zoom;
            canvas.drawPoint(x,y,paint);
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(curveType == 0){
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            //画曲线
            for(int i = 0;i < points.length-1;i++){
                float sx = (float)i/points.length * (coordinateWidth -2* coordinatePad) + coordinatePad;
                float sy = coordinateHeight - coordinatePad - ((float)points[i])/CL*zoom;
                float ex = (float)(i+1)/points.length * (coordinateWidth -2* coordinatePad) + coordinatePad;
                float ey = coordinateHeight - coordinatePad - ((float)points[i+1])/CL*zoom;
                canvas.drawLine(sx,sy,ex,ey,paint);
            }
            paint.setColor(Color.RED);
            paint.setStrokeWidth(4);
            for(int i = 1; i < maxGrayIndexInPoints.length; i++){
                float x = (float) maxGrayIndexInPoints[i]/points.length * (coordinateWidth - 2* coordinatePad) + coordinatePad;
                float y = coordinateHeight - coordinatePad - (float)points[maxGrayIndexInPoints[i]]/CL * zoom;
                canvas.drawPoint(x,y,paint);
            }
        }
        else if(curveType == 1)
            bigDraw(canvas);
    }

    public int getcLineIndex() {
        return cLineIndex;
    }

    public void setcLineIndex(int cLineIndex) {
        this.cLineIndex = cLineIndex;
    }

    public int getSelectedPointIndex() {
        return selectedPointIndex;
    }

    public void setSelectedPointIndex(int selectedPointIndex) {
        this.selectedPointIndex = selectedPointIndex;
    }

//    public int getFocusPoint() {
//        return focusPoint;
//    }
//
//    public void setFocusPoint(int focusPoint) {
//        this.focusPoint = focusPoint;
//    }
}
