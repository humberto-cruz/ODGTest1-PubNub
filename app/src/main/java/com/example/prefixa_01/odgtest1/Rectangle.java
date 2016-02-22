package com.example.prefixa_01.odgtest1;

import android.content.Context;

/**
 * Created by Humberto on 22/02/2016.
 */
public class Rectangle {

    private double mXini;
    private double mYini;
    private double mXend;
    private double mYend;
    private static Rectangle rectangle;

    public static Rectangle get(Context context){
        if(rectangle == null){
            rectangle = new Rectangle(context);
        }
        return rectangle;
    }


    private Rectangle(Context context){
    }


    public double getXini() {
        return mXini;
    }

    public void setXini(double xini) {
        mXini = xini;
    }

    public double getYini() {
        return mYini;
    }

    public void setYini(double yini) {
        mYini = yini;
    }

    public double getXend() {
        return mXend;
    }

    public void setXend(double xend) {
        mXend = xend;
    }

    public double getYend() {
        return mYend;
    }

    public void setYend(double yend) {
        mYend = yend;
    }


}
