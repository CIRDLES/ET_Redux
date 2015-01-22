package org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE;

import java.util.Iterator;


class FT{

    static double getFTage(double zeta, double rhoD, double Ns, double Ni) throws Exception {
        return (1/LAMBDA) * ToolBox.log(1 + LAMBDA*(Ns/Ni)*g*rhoD*zeta);
    }
    
    /* returns the *relative* error */
    static double getFTageErr(double zeta, double zetaErr, double rhoD, double rhoD_Err, 
                              double Ns, double Ni) throws Exception {
        double relerr = Math.sqrt(1/Ns + 1/Ni);
        return getFTage(zeta,rhoD,Ns,Ni)*relerr;
    }
    
    // returns a 3-element array with the central age, its error and the age dispersion
    static double[] getCentralAgeErrDisp(FTdata data) throws Exception {
        double theta = 0.0, sigma = 0.15, theta0, sigma0, wj, pj, nums, numt, sum_wj;
        double[] ts = {0,0,0};
        double[] NsNi;
        double Nsj, Nij, mj;
        theta = initializeTheta(data);
        do {
            sigma0 = sigma;
            theta0 = theta;
            nums = 0.0;
            sum_wj = 0.0;
            numt = 0.0;
            // update sigma and theta
            for (Iterator i= data.iterator(); i.hasNext(); ) {
                NsNi = (double[]) i.next();
                Nsj = NsNi[0];
                Nij = NsNi[1];
                mj = Nsj + Nij;
                pj = Nsj/mj;
                wj = mj/(theta*(1-theta)+(mj-1)*theta*theta*(1-theta)*(1-theta)*sigma*sigma);
                nums += wj*wj*(pj-theta)*(pj-theta);
                sum_wj += wj;
                numt += wj*pj;
            }
            sigma *= Math.sqrt(nums/sum_wj);
            theta = numt/sum_wj;
        }while((1e-4 < Math.abs(sigma-sigma0)/sigma) || (1e-4 < Math.abs(theta-theta0)/theta));
        ts[0] = ToolBox.log(1+0.5*LAMBDA*data.getZeta()*data.getRhoD()*theta/(1-theta))/LAMBDA;
        ts[1] = ts[0] * Math.sqrt(1/(theta*theta*(1-theta)*(1-theta)*sum_wj +
                data.getZeta_Err()*data.getZeta_Err()/(data.getZeta()*data.getZeta())));
        ts[2] = sigma<0.00001 ? 0 : sigma;
        return ts;
    }
    
    /* returns z = log(Ns/Ni) from age t*/
    public static double getz(FTdata data, double t) throws Exception {
        return ToolBox.log((Math.exp(LAMBDA*t)-1)/(LAMBDA*g*data.getRhoD()*data.getZeta()));
    }
    
    public static double getPX2(FTdata data) throws Exception {
        double X2 = getX2(data);
        return 1 - Stat.chiSquareCDF(X2, data.length()-1);
    }
    
    private static double getX2(FTdata data) throws Exception {
        double[] NsNi;
        double X2 = 0d, Nsj, Nij, Ns = 0, Ni = 0;
        for (Iterator i = data.iterator(); i.hasNext(); ) {
            NsNi = (double[]) i.next();
            Ns += (int) NsNi[0];
            Ni += (int) NsNi[1];
        }
        for (Iterator i = data.iterator(); i.hasNext(); ) {
            NsNi = (double[]) i.next();
            Nsj = NsNi[0];
            Nij = NsNi[1];
            X2 += (Nsj*Ni-Nij*Ns)*(Nsj*Ni-Nij*Ns)/((Nsj + Nij)*Ns*Ni);
        }
        return X2;        
    }
    
    private static double initializeTheta(FTdata data) throws Exception {
        double[] NsNi;
        int Nsj, Nij, mj, num = 0, denom = 0;
        for (Iterator i= data.iterator(); i.hasNext(); ) {
            NsNi = (double[]) i.next();
            Nsj = (int) NsNi[0];
            Nij = (int) NsNi[1];
            mj = Nsj + Nij;
            num += Nsj;
            denom += mj;
        }
        return ((double)num)/((double)denom);
    }    

    // returns a two-element arry with the pooled age and its relative error
    static double[] getPooledAgeAndErr(FTdata data) throws Exception {
        double[] pe = new double[2];
        double[] NsNi;
        int Ns=0, Ni=0;
        for (Iterator i= data.iterator(); i.hasNext(); ) {
            NsNi = (double[]) i.next();
            Ns += (int) NsNi[0];
            Ni += (int) NsNi[1];
        }
        pe[0] = getFTage(data.getZeta(), data.getRhoD(), Ns, Ni);
        pe[1] = getFTageErr(data.getZeta(), data.getZeta_Err(), data.getRhoD(), data.getRhoD_Err(), Ns, Ni);
        return pe;
    }
    
    final static double LAMBDA = 1.55125e-10;
    final static double g = 0.5;

}