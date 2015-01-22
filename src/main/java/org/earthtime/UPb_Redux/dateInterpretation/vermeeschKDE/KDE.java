package org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE;

import java.util.Arrays;

// based on Zdravko Botev's kde.m Matlab function from MATLAB Central

/**
 *
 * @author James F. Bowring
 */
public class KDE {

    /**
     *
     */
    public void KDE(){
        this.reset();
    }

    /**
     *
     */
    public void reset(){
        this.bandwidth = Data.NAN;
    }

    /**
     *
     * @param data
     * @param xmesh
     * @param auto
     * @return
     * @throws Exception
     */
    public double[] pdf(double[] data, double[] xmesh, boolean auto) throws Exception {
        int n = xmesh.length; // must be power of 2!
        int N = unique(data); // number of unique data points
        double R = xmesh[xmesh.length-1]-xmesh[0];
        double[] initial_data = arrayDiv(histc(data,xmesh),data.length);
        double[] a = dct1d(initial_data);
        double tstar = auto ? fsolve(a,N) : (bandwidth*bandwidth)/(R*R);
        double[] a_t = get_a_t(a,n,tstar);
        double[] density = idct1d(a_t);
        density = arrayDiv(density,R);
        bandwidth = Math.sqrt(tstar)*R;
        return density;
    }

    /**
     *
     * @return
     */
    public double getBandwidth(){
        return this.bandwidth;
    }

    /**
     *
     * @param bandwidth
     */
    public void setBandwidth(double bandwidth){
        this.bandwidth = bandwidth;
    }

    private static Complex[] real2complex(double[] in) throws Exception {
        Complex[] out = new Complex[in.length];
        for (int i=0; i<in.length; i++){
            out[i] = new Complex(in[i],0);
        }
        return out;
    }

    private static double[] get_a_t(double[] a, int n, double tstar) throws Exception {
        double[] a_t = new double[n];
        for (int i=0; i<n; i++){
            a_t[i] = a[i]*Math.exp(-i*i*Math.PI*Math.PI*tstar/2);
        }
        return a_t;
    }

    private static int unique(double[] data) throws Exception {
        int n = data.length;
        int N = 1;
        double[] sortedData = data.clone();
        Arrays.sort(sortedData);
        for (int i=1; i<n; i++){
            if (sortedData[i]!=sortedData[i-1]) {
                N++;
            }
        }
        return N;
    }

    private static double[] arrayDiv(double[] data, double N) throws Exception {
        int n = data.length;
        double[] out = new double[n];
        for (int i=0; i<n; i++){
            out[i] = data[i]/N;
        }
        return out;
    }

    private static Complex[] arrayMultiply(Complex[] array1, Complex[] array2) throws Exception {
        Complex[] out = new Complex[array1.length];
        for (int i=0; i<array1.length; i++){
            out[i] = array1[i].times(array2[i]);
        }
        return out;
    }

    private static double[] arrayReal(Complex[] array) throws Exception {
        int n = array.length;
        double[] out = new double[n];
        for (int i=0; i<n; i++){
            out[i] = array[i].re();
        }
        return out;
    }

    private static double[] histc(double[] data, double[] xmesh) throws Exception {
        int ii, n = xmesh.length;
        double min = xmesh[0], max = xmesh[n-1];
        double[] out = new double[n];
        for (int i=0; i<data.length; i++){
            if (data[i]>=min & data[i]<=max){
                ii = (int)Math.floor((n-1)*(data[i]-min)/(max-min));
                out[ii]++;
            }
        }
        return out;
    }

    private static double[] geta2(double[] a) throws Exception {
        int n = a.length;
        double[] a2 = new double[n-1];
        for (int i=1; i<n; i++){
            a2[i-1] = a[i]*a[i]/4;
        }
        return a2;
    }

    private static double[] getI(int n) throws Exception {
        double[] I = new double[n-1];
        for (int i=1; i<n; i++){
            I[i-1] = i*i;
        }
        return I;
    }

    private static double fsolve(double[] a, int N) {
        double t_star = 0d;
        try {
            double[] I = getI(a.length);
            double[] a2 = geta2(a);
            t_star = binarySearch(0,0.1,N,I,a2);
        } catch (Exception e) {
            t_star = 0.28*Math.pow(N,-2d/5d);
            System.err.println("KDE: binary search for t_star failed.");
        } finally {
            return t_star;
        }
    }

    private static double binarySearch(double m, double M, int N, double[] I, double[] a2) throws Exception {
        double t, misfit, min=m, max=M;
        // for loop and not recursive to prevent infinite loops
        for (int i=0; i<100; i++){
            t = min + 0.5*(max-min);
            misfit = fixed_point(t,(double)N,I,a2);
            if (Math.abs(misfit/t)<1e-3){
                return t;
            } else if (misfit>0d) {
                max = t;
            } else {
                min = t;
            }
        }
        return 0.28*Math.pow(N,-2d/5d);
    }

    private static double fixed_point(double t, double N, double[] I, double[] a2) throws Exception {
        // this implements the function t-zeta*gamma^[l](t)
        int l = 7;
        double K0, cnst, time;
        double f = getf(I,l,a2,t);
        for (int s=l-1; s>1; s--){
            K0 = getK0(s);
            cnst = (1+Math.pow(0.5,s+0.5))/3;
            time = Math.pow(2d*cnst*K0/N/f,2d/(3d+2d*s));
            f = getf(I,s,a2,time);
        }
        return (t-Math.pow(2d*N*Math.sqrt(Math.PI)*f,-2d/5d));
    }

    private static double getK0(int s) throws Exception {
        double K0 = 1d;
        for (int i=1; i<2*s; i=i+2){
            K0 *= i;
        }
        return K0/Math.sqrt(2*Math.PI);
    }

    private static double getf(double[] I, int l, double[] a2, double t) throws Exception {
        int n = I.length;
        double sum = 0d;
        for (int i=0; i<n; i++){
            sum += Math.pow(I[i],l)*a2[i]*Math.exp(-I[i]*Math.PI*Math.PI*t);
        }
        return 2*Math.pow(Math.PI,2*l)*sum;
    }

    private static double[] dct1d(double[] data) throws Exception {
        // computes the discrete cosine transform of the column vector data
        int n = data.length;
        double gamma = 0d;
        Complex[] data2 = new Complex[n];
        // Compute weights to multiply DFT coefficients
        Complex[] weight = new Complex[n];
        weight[0] = new Complex(1,0);
        for (int i=1; i<n; i++){
            gamma = -i*Math.PI/(2*n);
            weight[i] = new Complex(2*Math.cos(gamma),2*Math.sin(gamma));
        }
        // Re-order the elements of the columns of x
        for (int i=0; i<n/2; i++){
            data2[i] = new Complex(data[2*i],0);
            data2[n/2+i] = new Complex(data[n-1-2*i],0);
        }
        // Multiply FFT by weights:
        Complex[] fft = FFT.fft(data2);
        Complex[] weightedData = arrayMultiply(weight,fft);
        return arrayReal(weightedData);
    }

    private static double[] idct1d(double[] data) throws Exception {
        // computes the inverse discrete cosine transform
        // Reference: A. K. Jain, "Fundamentals of Digital Image Processing", pp. 150-153
        int n = data.length;
        double gamma = 0d;
        // Compute weights
        Complex[] weight = new Complex[n];
        for (int i=0; i<n; i++){
            gamma = i*Math.PI/(2*n);
            weight[i] = new Complex(n*Math.cos(gamma),n*Math.sin(gamma));
        }
        // Compute x tilde using equation (5.93) in Jain
        Complex[] data2 = real2complex(data);
        Complex[] weightsXdata = arrayMultiply(weight,data2);
        Complex[] ifft = FFT.ifft(weightsXdata);
        for (int i=0; i<n; i++){
            data[i] = ifft[i].re();
        }
        // Re-order elements of each column according to equations (5.93) and (5.94) in Jain
        double[] out = new double[n];
        for (int i=0; i<n/2; i++){
            out[2*i] = data[i];
            out[1+2*i] = data[n-i-1];
        }
        return out;
    }

    private double bandwidth;

}
