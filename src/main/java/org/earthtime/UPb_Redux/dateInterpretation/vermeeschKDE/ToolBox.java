package org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 *
 * @author James F. Bowring
 */
public class ToolBox {

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str)
      {
        try
        {
          double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
          return false;
        }
        return true;
      }

    /**
     *
     * @param arraylist
     * @return
     * @throws Exception
     */
    static public double getMax(ArrayList<Double> arraylist) throws Exception {
        double x, max = arraylist.get(0);
        for (int i=0;i<arraylist.size();i++){
            x = arraylist.get(i);
            if (x>max){
                max = x;
            }
        }
        return max;
    }

    /**
     *
     * @param num
     * @return
     * @throws Exception
     */
    static public double log(double num) throws Exception {
        double result = Math.log(num);
        if (!Double.isFinite(result)){
            throw new ArithmeticException("Error: log of negative number.");
        }
        return result;
    }

    /**
     *
     * @param num
     * @return
     * @throws Exception
     */
    static public double log10(double num) throws Exception {
        double result = Math.log10(num);
        if (!Double.isFinite(result)){
            throw new ArithmeticException("log of negative number");
        }
        return result;
    }

    /**
     *
     * @param arraylist
     * @return
     * @throws Exception
     */
    static public double getMin(ArrayList<Double> arraylist) throws Exception {
        double x, min = arraylist.get(0);
        for (int i=0;i<arraylist.size();i++){
            x = arraylist.get(i);
            if (x<min){
                min = x;
            }
        }
        return min;
    }

    // converts a double t to a string with n significant digits

    /**
     *
     * @param t
     * @param n
     * @return
     * @throws Exception
     */
        static public String num2string(double t, int n) throws Exception {
        DecimalFormat formatter = sigdig(t,n);
        return formatter.format(t);
    }

    /**
     *
     * @param x
     * @param xerr
     * @param n
     * @return
     * @throws Exception
     */
    static public String[] num2string(double x, double xerr, int n) throws Exception {
        String[] out = new String[2];
        DecimalFormat formatter = sigdig(xerr,n);
        out[0] = formatter.format(x);
        out[1] = formatter.format(xerr);
        return out;
    }

    static DecimalFormat sigdig(double t, int n) throws Exception {
        int numdecimals = (t>0) ?(int) Math.floor(ToolBox.log10(t)) : 0;
        String fmt = (numdecimals < n) ? "#." : "#";
        for (int i=0; i<n-numdecimals; i++){ fmt += "#";}
        return new DecimalFormat(fmt);
    }

    /**
     *
     * @param str
     * @return
     */
    public static String superscript(String str) {
       
        return str;
    }

    /**
     *
     * @param str
     * @return
     */
    public static String subscript(String str) {
       
        return str;
    }

}