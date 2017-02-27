package com.engure.util.anim;

import android.util.Log;


import com.engure.util.anim.ifaces.IDrawer;
import com.engure.util.anim.ifaces.ISpeedInput;
import com.engure.util.anim.ifaces.ITween;

public class Tween implements ITween {

    // idea behind all this framework is to separate:
    // - trajectory
    // - speed
    // - object drawing

    private ISpeedInput speedInput;
    private IDrawer drawer;
    private double currPos;
    
    public Tween(ISpeedInput si, IDrawer d) {
        speedInput = si;
        drawer = d;
        currPos = -1.0;
    }

    public boolean doStep() {
        if (currPos == -1.0) {
            currPos = 0.0;
        } else if (currPos < 1.0) {
            double speed = speedInput.getSpeed(currPos);
            currPos += speed;
            if (currPos > 1.0) {
                currPos = 1.0;
            }
        }

        drawer.doDraw(currPos);
        return currPos == 1.0;
    }


}
