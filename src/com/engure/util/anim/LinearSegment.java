package com.engure.util.anim;

import com.engure.util.anim.ifaces.ISegment;

public class LinearSegment implements ISegment {
    double x0, y0, x1, y1, xx, yy;
    public LinearSegment(double x0, double y0,
                         double x1, double y1) {
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        xx = x1 - x0;
        yy = y1 - y0;
    }
        
    @Override
    public double getDist() {
        return Math.sqrt(xx * xx + yy * yy);
    }
        
    @Override
    public void getCoords(double pos, double result[]) {
        result[0] = xx * pos + x0;
        result[1] = yy * pos + y0;
    }
};
