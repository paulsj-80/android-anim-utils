package com.engure.util.anim;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.util.Log;
import android.graphics.Paint;

import com.engure.util.anim.ifaces.IDrawer;
import com.engure.util.anim.ifaces.IPathProjector;


public class SimpleDrawer implements IDrawer {

    private IPathProjector pathProjector;
    private Bitmap bitmap;
    private double coords[];
    private Canvas canvas;
    private double hw, hh;
    private Paint p;

    
    public SimpleDrawer(IPathProjector pp, Bitmap b) {
        pathProjector = pp;
        bitmap = b;
        coords = new double[2];
        hw = b.getWidth() / 2.0;
        hh = b.getHeight() / 2.0;

        p = new Paint();
        p.setAntiAlias(true);
        p.setFilterBitmap(true);
    }
        
    public void doDraw(double pos) {
        pathProjector.getCoords(pos, coords);
        canvas.drawBitmap(bitmap, (float)(coords[0] - hw),
                          (float)(coords[1] - hh), p);
    }

    public void setCanvas(Canvas c) {
        canvas = c;
    }

    public void getLastCoords(double[] coords) {
        coords[0] = this.coords[0];
        coords[1] = this.coords[1];
    }

}
