package com.example.quantitativedetect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.quantitativedetect.domain.Mark;


public class GrayCurve extends BaseCoordinate {
    private int[] points;//所有的点的灰度值
    private int[] featureIndex;//特征点在points这个数组里面的位置
    //第一个特征的点？？？？？
    private float CL = 1;
    private float zoom = 1;
    private int flag = 0;
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

    public GrayCurve(Context context, int width, Mark mark, float zoom, int flag){
        super(context);
        super.init(width,width/2,40);
        points = new int[mark.getLineList().size()];
        featureIndex = new int[mark.getFeatureLineList().size()];
        for (int i=0; i<mark.getLineList().size();i++){
            points[i]=mark.getLineList().get(i).getGray();
        }
        this.zoom = zoom*(width/2-2*pad);//width/3为View的高度，而width/3-2*pad才是Y轴的长度
        for (int i=0; i<mark.getFeatureLineList().size();i++){
            featureIndex[i]=mark.getLineList().indexOf(mark.getFeatureLineList().get(i));
        }
//        this.points = points;
//        this.features = features;
        //TODO CL是偏移量，但不知道具体含义，CL值增大能使得缩略图（AbbreviationCurve）位置降低
        // （或者说是使整个Y轴的范围（0~maxValue）更大，导致曲线的上下边界被压缩至可见，即使曲线的振幅变小？）
        this.CL = points[featureIndex[0]]+100;
        this.flag = flag;
        if(flag == 0){
            this.zoom = zoom*(width/3-2*pad);
            super.init(width,width/3,40);
        }
    }





    public void setFeatureIndex(int[] featureIndex){
        this.featureIndex = featureIndex;
        invalidate();
    }
    public void setWid(int wid){
        super.init(wid,wid/3,25);
        invalidate();
    }
    public void setFlag(int flag){
        this.flag = flag;
        if(this.flag ==1)
        invalidate();
    }
    @Override
    public void invalidate(){
        super.invalidate();
    }

    public void bigDraw(Canvas canvas){
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        canvas.drawLine(pad,pad,wid-pad,pad,paint);//上X轴
        canvas.drawLine(wid-pad,pad,wid-pad,hei-pad,paint);//右Y轴
        paint.setStrokeWidth(1);
        int interval = ( hei - 2*pad )/5;
        for(int i = 1;i <= 5;i++){
            paint.setStrokeWidth(1);
            canvas.drawLine(pad,pad+i*interval,wid-pad,pad+i*interval,paint);
        }
        paint.setTextSize(20);
        canvas.drawText("T/C",pad-30,pad,paint);
        canvas.drawText(String.valueOf(0.8),pad-30,pad+interval,paint);
        canvas.drawText(String.valueOf(0.6),pad-30,pad+2*interval,paint);
        canvas.drawText(String.valueOf(0.4),pad-30,pad+3*interval,paint);
        canvas.drawText(String.valueOf(0.2),pad-30,pad+4*interval,paint);
        canvas.drawText(String.valueOf(0),pad-30,pad+5*interval,paint);

        paint.setStrokeWidth(3);
        for(int i = 0;i < points.length-1;i++){
            paint.setColor(Color.rgb(255,165,0));
            float sx = (float)i/points.length * (wid-2*pad) + pad;
            float sy = hei - pad - ((float)points[i])/CL*zoom;
            float ex = (float)(i+1)/points.length * (wid-2*pad) + pad;
            float ey = hei - pad - ((float)points[i+1])/CL*zoom;
            canvas.drawLine(sx,sy,ex,ey,paint);
        }
        paint.setColor(Color.RED);
        paint.setStrokeWidth(9);
        for(int i = 0; i < featureIndex.length; i++){
            paint.setColor(Color.RED);
            if(i == 0)
                paint.setColor(Color.GREEN);
            float x = (float) featureIndex[i]/points.length * (wid - 2*pad) + pad;
            float y = hei - pad - (float)points[featureIndex[i]]/CL * zoom;
            canvas.drawPoint(x,y,paint);
        }
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(flag == 0){
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            //画曲线
            for(int i = 0;i < points.length-1;i++){
                float sx = (float)i/points.length * (wid-2*pad) + pad;
                float sy = hei - pad - ((float)points[i])/CL*zoom;
                float ex = (float)(i+1)/points.length * (wid-2*pad) + pad;
                float ey = hei - pad - ((float)points[i+1])/CL*zoom;
                canvas.drawLine(sx,sy,ex,ey,paint);
            }
            paint.setColor(Color.RED);
            paint.setStrokeWidth(4);
            for(int i = 1; i < featureIndex.length; i++){
                float x = (float) featureIndex[i]/points.length * (wid - 2*pad) + pad;
                float y = hei - pad - (float)points[featureIndex[i]]/CL * zoom;
                canvas.drawPoint(x,y,paint);
            }
        }
        else if(flag == 1)
            bigDraw(canvas);
    }
}
