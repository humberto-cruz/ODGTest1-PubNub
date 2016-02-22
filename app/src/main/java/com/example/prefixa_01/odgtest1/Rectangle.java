package com.example.prefixa_01.odgtest1;

import android.content.Context;

/**
 * Created by Humberto on 22/02/2016.
 */
public class Rectangle {

    private float mXini;
    private float mYini;
    private float mXend;
    private float mYend;
    private static Rectangle rectangle;

    public static Rectangle get(Context context){
        if(rectangle == null){
            rectangle = new Rectangle(context);
        }
        return rectangle;
    }


    private Rectangle(Context context){
    }


    public float getXini() {
        return mXini;
    }

    public void setXini(float xini) {
        mXini = xini;
    }

    public float getYini() {
        return mYini;
    }

    public void setYini(float yini) {
        mYini = yini;
    }

    public float getXend() {
        return mXend;
    }

    public void setXend(float xend) {
        mXend = xend;
    }

    public float getYend() {
        return mYend;
    }

    public void setYend(float yend) {
        mYend = yend;
    }


}
