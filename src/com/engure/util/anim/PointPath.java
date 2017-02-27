package com.engure.util.anim;

import android.util.Log;
import java.util.Random;

import com.engure.util.anim.ifaces.IPathProjector;
import com.engure.util.anim.ifaces.ISpeedInput;

import com.engure.util.anim.SpeedInputs;
import com.engure.util.Utils;

public class PointPath implements IPathProjector, ISpeedInput {

    // 0-1 systems:
    // - for IPoint (A)
    // - for PointPath (B)

    public interface IPoint extends IPathProjector, ISpeedInput {
        public double getDistToNext();

        // last point in chain should return null
        public IPoint getNext();
        public void getCoords(double pos, double result[]);
        public double getSpeed(double pos);
    };

    public interface ISegment extends IPathProjector {
        public double getDist();
        public void getCoords(double pos, double result[]);
    };

    public interface ISegmentFactory {
        public ISegment create(double x0, double y0,
                               double x1, double y1);
    };

    public static class LinearSegment implements ISegment {
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

    private static int[][] ANCHORS = {
        {-1, -1},   {0, -1},   {1, -1},
        {-1, 0},    {0, 0},    {1, 0},
        {-1, 1},    {0, 1},    {1, 1}
    };

    public static class Point implements IPoint {
        private IPoint next;
        private double baseX, baseY;
        private ISegmentFactory segmentFactory;
        private ISegment segment;
        private double coords[];
        private ISpeedInput speedInput;
        
        public Point(int anchor, double x, double y,
                     double offsetX, double offsetY,
                     double objectW, double objectH,
                     ISegmentFactory sf, ISpeedInput si) {
            coords = new double[2];
            speedInput = si;
            segmentFactory = sf;

            double hw = objectW / 2.0;
            double hh = objectH / 2.0;
            baseX = x + ANCHORS[anchor][0] * hw * -1.0 + offsetX;
            baseY = y + ANCHORS[anchor][1] * hh * -1.0 + offsetY;
        }

        // if distance between points is 0, segment is ignored
        // (the tween continues on next segment with non-zero
        // length)
        public void setNext(IPoint p) {
            next = p;
            next.getCoords(0.0, coords);
            segment = segmentFactory.create(baseX, baseY,
                                            coords[0], coords[1]);
        }

        @Override
        public double getDistToNext() {
            if (next == null)
                return 0.0;
            return segment.getDist();
        }

        @Override
        public IPoint getNext() {
            return next;
        }
        @Override
        public void getCoords(double pos, double result[]) {
            if (pos == 0.0) {
                result[0] = baseX;
                result[1] = baseY;
            } else 
                segment.getCoords(pos, result);
        }
        @Override
        public double getSpeed(double pos) {
            return speedInput.getSpeed(pos);
        }
    };

    public static IPoint createHoverPath(double x,
                                         double y,
                                         int objectW,
                                         int objectH,
                                         double maxSpeed,
                                         double minSpeed,
                                         int minStops,
                                         int maxStops,
                                         double radius,
                                         IPoint last) {
        Random rnd = new Random(System.currentTimeMillis());

        PointPath.ISegmentFactory sf =
            new PointPath.ISegmentFactory() {
                public PointPath.ISegment create
                    (double x0, double y0,
                     double x1, double y1) {
                    return new PointPath.LinearSegment
                        (x0, y0, x1, y1);
                }
            };

        int stops = minStops;
        if (maxStops - minStops > 0)
            stops += rnd.nextInt(maxStops - minStops);
        IPoint first = last;
        double px = x;
        double py = y;
        for (int i = 0; i < stops; i++) {
            double r = rnd.nextDouble();
            double a = rnd.nextDouble() * Math.PI * 2.0;
            double xx = (Math.sin(a) * r) * radius + x;
            double yy = (Math.cos(a) * r) * radius + y;

            double d = Utils.distance(px, py, xx, yy);
            Log.w("shit00000000000", String.valueOf(d));
            double s = d / maxSpeed;
            double sb = d / minSpeed;
            //            ISpeedInput si = new SpeedInputs.EaseSin(s, sb);
            ISpeedInput si = new SpeedInputs.Linear(s);

            px = xx;
            py = yy;
            Point next = new Point(4, xx, yy,
                                   0, 0, objectW, objectH,
                                   sf, si);
            next.setNext(first);
            first = next;
        }
        return first;
    }

    private IPoint currPoint;
    private double startPos[];
    private int currPointIndex;
    private double lastPos;          // in (B)
    private double pointPos;         // in (A)


    // the trajectory cannot be too long (millions of pixels)
    // otherwise precision suffers
    public PointPath(IPoint p) {
        currPoint = p;
        currPointIndex = 0;
        lastPos = 0.0;
        pointPos = 0.0;
        double overallLength = 0.0;
        int pointCount = 0;
        IPoint p2 = p;
        while (p2 != null) {
            pointCount++;
            overallLength += p2.getDistToNext();
            p2 = p2.getNext();
        }
        
        startPos = new double[pointCount];
        p2 = p;
        startPos[0] = 0.0;
        for (int i = 1; i < pointCount; i++) {
            startPos[i] = startPos[i - 1] + p2.getDistToNext() /
                overallLength;
            p2 = p2.getNext();
        }
        startPos[pointCount - 1] = 1.0;
    }

    public void getCoords(double pos, double result[]) {
        // to have O(n) algorithm we don't allow other pos
        if (lastPos > pos)
            throw new Error("cannot check backward position");

        lastPos = pos;
        
        while (currPointIndex < startPos.length - 1 &&
               startPos[currPointIndex+1] < pos) {
            currPointIndex++;
            currPoint = currPoint.getNext();
        }

        if (currPointIndex == startPos.length - 1) {
            pointPos = 0.0;
        } else {
            double ref = startPos[currPointIndex + 1] -
                startPos[currPointIndex];
            pointPos = (pos - startPos[currPointIndex]) / ref;
            if (pointPos > 1.0)
                pointPos = 1.0; // safety
        }

        currPoint.getCoords(pointPos, result);
    }

    public double getSpeed(double pos) {
        // to have O(n) algorithm we don't allow other pos
        if (lastPos != pos)
            throw new Error("cannot get speed for another position");
        return currPoint.getSpeed(pointPos);
    }

}
