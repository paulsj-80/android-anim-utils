package com.engure.util;

import android.util.Log;
import com.engure.util.Utils;

public class CommandQueue {

    private final static int QUEUE_SIZE = 1000;
    public final static int CMD_SIZE = 10;
    public final static int WAIT_FOREVER = -1;

    private int queue[][] = new int[QUEUE_SIZE][CMD_SIZE];
    private int first = 0;
    private int last = 0;

    public CommandQueue() {
    }

    private int uiCmd[] = new int[CMD_SIZE];
    public void postCmd(int code) {
        uiCmd[0] = 2;
        uiCmd[1] = code;
        postCmd(uiCmd);
    }
    public void postCmd(int code, int p1) {
        uiCmd[0] = 3;
        uiCmd[1] = code;
        uiCmd[2] = p1;
        postCmd(uiCmd);
    }
    public void postCmd(int code, int p1, int p2) {
        uiCmd[0] = 4;
        uiCmd[1] = code;
        uiCmd[2] = p1;
        uiCmd[3] = p2;
        postCmd(uiCmd);
    }
    public void postCmd(int code, int p1, int p2, int p3,
                         int p4, int p5) {
        uiCmd[0] = 7;
        uiCmd[1] = code;
        uiCmd[2] = p1;
        uiCmd[3] = p2;
        uiCmd[4] = p3;
        uiCmd[5] = p4;
        uiCmd[6] = p5;
        postCmd(uiCmd);
    }
    public void postCmd(int cmd[]) {
        int z = 0;
        synchronized (this) {
            int cmdSize = cmd[0];
            System.arraycopy(cmd, 0, queue[last], 0, cmdSize);
            incLast();
            notify();
        }
    }

    private void incFirst() {
        Utils.uassert(last != first, "queue size is zero");
        first++;
        if (first == QUEUE_SIZE)
            first = 0;
    }

    private void incLast() {
        last++;
        if (last == QUEUE_SIZE)
            last = 0;
        Utils.uassert(last != first, "queue is full");
    }


    private boolean filter(int cmd[], int inclusive_filter[],
                           int pos) {
        boolean res = false;
        int alen = inclusive_filter.length;

        while (last != first) {
            int cmdSize = queue[first][0];
            if (cmdSize > pos &&
                Utils.isInArr
                (inclusive_filter, alen, queue[first][pos])) {
                System.arraycopy(queue[first], 0,
                                 cmd, 0, cmdSize);
                incFirst();
                break;
            }
            incFirst();
        }

        return res;
    }

    public boolean getCommand(int cmd[], long waitTimeMs,
                              int inclusive_filter[], int pos) {
        boolean res;
        long timeout = waitTimeMs;
        long t1, t2;

        synchronized (this) {
            res = filter(cmd, inclusive_filter, pos);

            while (!res && (waitTimeMs == WAIT_FOREVER ||
                            timeout > 0)) {

                try {
                    if (waitTimeMs == WAIT_FOREVER)
                        wait();
                    else {
                        t1 = System.currentTimeMillis();
                        wait(timeout);
                        t2 = System.currentTimeMillis();
                        timeout -= t2 - t1;
                    }
                } catch (InterruptedException e) {
                    Log.w("CommandQueue", "Thread was interrupted");
                    throw new Error("Thread was interrupted");
                }
                res = filter(cmd, inclusive_filter, pos);
            }

            if (!res)
                res = filter(cmd, inclusive_filter, pos);
        }

        return res;
    }

    // waitTimeMs may not be honored due to spurious wakeup
    public boolean getCommand(int cmd[], long waitTimeMs) {
        boolean res = false;

        synchronized (this) {
            
            if (last == first) {
                try {
                    if (waitTimeMs == WAIT_FOREVER)
                        wait();
                    else
                        wait(waitTimeMs);
                } catch (InterruptedException e) {
                    Log.w("CommandQueue", "Thread was interrupted");
                    throw new Error("Thread was interrupted");
                }
            }

            if (last != first) {
                int cmdSize = queue[first][0];
                System.arraycopy(queue[first], 0,
                                 cmd, 0, cmdSize);
                incFirst();
                res = true;
            }
        }

        return res;
    }
}
