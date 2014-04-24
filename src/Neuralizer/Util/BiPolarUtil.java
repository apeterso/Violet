package Neuralizer.Util;

import java.util.Arrays;

/**
 * Created by Matt Levine on 4/9/14.
 */
public class BiPolarUtil {

    /** Converts a Boolean bipolar to a double. For example, true is converted
     to 1.
    **/
    public static double bipolar2double(final boolean b){return b ? 1 : -1;}

    /** Converts a Boolean bipolar to a double. For example, true is converted
     to 1.
     **/
    public static double[] bipolar2double(final boolean b[]){
        double[] result = new double[b.length];
        for (int i = 0; i < b.length; i++) result[i] = b[i]?1:-1;
        return result;
    }

    /** Converts a Boolean bipolar to a double. For example, true is converted
     to 1. Assumes non-zero length **/
    public static double[][] bipolar2double(final boolean b[][]){
        double[][] result = new double[b[0].length][b.length];
        for (int i = 0; i < b.length; i++) result[i] = bipolar2double(b[i]);
        return result;
    }

    /** Converts a double value to a bipolar Boolean. For example, -1 is
    converted to false. **/
    public static boolean double2bipolar(final double d){return d==1;}

    /** Converts a double value to a bipolar Boolean. For example, -1 is
     converted to false. **/
    public static boolean[] double2bipolar(final double d[]){
        boolean[] result = new boolean[d.length];
        for (int i = 0; i < d.length; i++) result[i] = d[i]==1;
        return result;
    }

    /** Converts a double value to a bipolar Boolean. For example, -1 is
     converted to false. Assumes non-zero length **/
    public static boolean[][] double2bipolar(final double d[][]){
        boolean[][] result = new boolean[d[0].length][d.length];
        for (int i = 0; i < d.length; i++) result[i] = double2bipolar(d[i]);
        return result;
    }

}
