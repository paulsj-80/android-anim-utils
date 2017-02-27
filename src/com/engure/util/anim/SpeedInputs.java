package com.engure.util.anim;

import android.util.Log;


import com.engure.util.anim.ifaces.ISpeedInput;

public class SpeedInputs {
    
    public static class Linear implements ISpeedInput {
        private double theSpeed;
        
        public Linear(double s) {
            theSpeed = s;
        }

        public double getSpeed(double pos) {
            return theSpeed;
        }
    };

    public static class ExactEnd implements ISpeedInput {
        private ISpeedInput speedInput;
        public ExactEnd(ISpeedInput si) {
            speedInput = si;
        }

        public double getSpeed(double pos) {
            double v = speedInput.getSpeed(pos);
            if (pos + v > 1.0) {
                v = 1.0 - pos + 0.00001;
            }
            return v;
        }
    };


    public static class ConstantAccel implements ISpeedInput {
        private double s0, s1, d;
        
        public ConstantAccel(double s0, double s1) {
            this.s0 = s0;
            this.s1 = s1;
            d = s1 - s0;
        }

        public double getSpeed(double pos) {
            return 0;
            
        }
    };


    public static class EaseSin implements ISpeedInput {
        private double factor, base;
        public EaseSin(double f, double base) {
            factor = f;
            this.base = base;
        }
        public double getSpeed(double pos) {
            return Math.sin(pos * Math.PI) * factor + base;
        }
    };

    public static class WaitBeforeStart implements ISpeedInput {
        private ISpeedInput speedInput;
        private int ticksToWait;
        public WaitBeforeStart(ISpeedInput si, int callsToWait) {
            speedInput = si;
            ticksToWait = callsToWait;
        }

        public double getSpeed(double pos) {
            if (ticksToWait == 0) {
                return speedInput.getSpeed(pos);
            } else {
                ticksToWait--;
                return 0.0;
            }
        }
    };


    
}
