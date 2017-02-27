package com.engure.util;

import java.util.List;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Utils {
    private final static int DEC_PRECISION = 100000;
    
    public static void removeArrFromList(List<Integer> list,
                                          int arr[]) {
        for (int i = 0; i < arr.length; i++) {
            list.remove(new Integer(arr[i]));
        }
    }

    public static void removeIntFromArr(int arr[], int len,
                                        int elem) {
        boolean found = false;
        for (int i = 0; i < len - 1; i++)
            if (found)
                arr[i] = arr[i + 1];
            else if (arr[i] == elem) {
                found = true;
                arr[i] = arr[i + 1];                
            } 
    }

    public static boolean isInArr(int arr[], int len, int elem) {
        for (int i = 0; i < len; i++) {
            if (arr[i] == elem)
                return true;
        }
        return false;
    }

    public static boolean includes(int a0, int w, int x) {
        return x >= a0 && x < a0 + w;
    }

    public static int[] concatArr(int a[], int b[]) {
        int res[] = new int[a.length + b.length];
        int z = 0;
        for (int i = 0; i < a.length; i++)
            res[z++] = a[i];
        for (int i = 0; i < b.length; i++)
            res[z++] = b[i];
        return res;
    }

    public static void getDecimalFromFloat(float f, int res[]) {
        float a = 1f / f;
        res[0] = DEC_PRECISION;
        res[1] = (int)(DEC_PRECISION * a);
    }

    public static void readBytes(DataInputStream dis,
                                 int arrLen, int arr[])
    throws IOException {

        for (int i = 0; i < arrLen; i++)
            arr[i] = dis.readInt();
    }

    public static void writeBytes(DataOutputStream dos, int arr[])
    throws IOException {
        if (arr == null) {
            dos.writeInt(0);
        } else {
            dos.writeInt(arr.length);
            for (int i = 0; i < arr.length; i++)
                dos.writeInt(arr[i]);
        }
    }

    public static float distance(float x0, float y0,
                                 float x1, float y1) {
        double dx = x0 - x1;
        double dy = y0 - y1;
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    public static double distance(double x0, double y0,
                                  double x1, double y1) {
        double dx = x0 - x1;
        double dy = y0 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static int periodicFunction(int period, int t) {
        int a = (int)(t / period);
        return (a % 2 == 0) ? 1 : -1;
    }

    public static void uassert(boolean val, String msg) {
        if (!val)
            throw new Error(msg);
    }

}
