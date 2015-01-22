package org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE;

import java.io.*;
import java.util.*;

/**
 *
 * @author James F. Bowring
 */
public class FTdata extends Data implements Iterator, Iterable {
    
    /**
     *
     * @param preferences
     */
    public FTdata(Preferences preferences) {
        this("", preferences);
    }
    
    /**
     *
     * @param fn
     * @param preferences
     */
    public FTdata(String fn, Preferences preferences) {
        super(fn, preferences);
        try {
            this.readData();
        } catch (Exception e){
            if (debugmode){e.printStackTrace();}
        }
    }
    
    /**
     *
     * @param olddata
     * @return
     * @throws Exception
     */
    public static FTdata cast(Data olddata) throws Exception {
        FTdata newdata = new FTdata(olddata.preferences);
        newdata.copy(olddata);
        return newdata;
   }
    
    /**
     *
     * @param br
     */
    @Override
    protected void readHeaderAndBody(BufferedReader br){
        String aLine;
        try {
            // extract zeta and its (optional) error
            aLine = br.readLine();
            StringTokenizer ztokenizer = new StringTokenizer(aLine, ",");
            int numtokens = ztokenizer.countTokens();
            zeta = Double.parseDouble(ztokenizer.nextToken());
            if (numtokens == 1) {
                zeta_err = 0d;
            } else if (numtokens == 2){
                zeta_err = Double.parseDouble(ztokenizer.nextToken());
            }
            // extract rhoD and its (optional) error
            aLine = br.readLine();
            StringTokenizer rtokenizer = new StringTokenizer(aLine, ",");
            numtokens = rtokenizer.countTokens();
            rhoD = Double.parseDouble(rtokenizer.nextToken());
            if (numtokens == 1) {
                rhoD_err = 0d;
            } else if (numtokens == 2){
                rhoD_err = Double.parseDouble(rtokenizer.nextToken());
            }
            super.readBody(br);
        } catch (Exception e){
            if (debugmode){e.printStackTrace();}
        }
    }

    /**
     *
     * @param filepath
     */
    @Override
    public void writeOutput(String filepath){
        String nl = System.getProperties().getProperty("line.separator");
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filepath));
            out.write(samplename + "," + "F" + nl);
            out.write(this.getZeta() + "," + this.getZeta_Err() + nl);
            out.write(this.getRhoD() + "," + this.getRhoD_Err() + nl);
            super.writeBody(out);
        } catch (Exception e) {
            if (debugmode){e.printStackTrace();}
        }
    }
    
    /* populates the radialX, radialY, zout, sigma, and C ArrayLists with data*/

    /**
     *
     * @param fixedAxes
     */
    
    @Override
    public void data2rxry(boolean fixedAxes){
        try{
            radialX.clear();
            radialY.clear();
            C.clear();
            z.clear();
            sigma.clear();
            double zj = 0, sigmaj = 0, num = 0, denom = 0,
                   nsj = 0, nij = 0, nsplus = 0, niplus = 0;
            double[] XYZ;
            // calculate the zout and sigma values for each grain
            for (Iterator i= this.iterator(); i.hasNext(); ) {
                XYZ = (double[]) i.next();
                nsj = XYZ[0];
                nij = XYZ[1];
                C.add(XYZ[2]);
                nsplus += nsj;
                niplus += nij;
                if (preferences.linear()){
                    zj = FT.getFTage(this.getZeta(), this.getRhoD(), nsj, nij);
                    z.add(zj);
                    sigmaj = zj*Math.sqrt(1/nsj + 1/nij);/* +
                             Math.pow(this.getRhoD_Err()/this.getRhoD(),2)*
                             Math.pow(this.getZeta_Err()/this.getZeta(),2));*/
                    sigma.add(sigmaj);
                    num += zj/(sigmaj*sigmaj);
                    denom += 1/(sigmaj*sigmaj);
                } else if (preferences.logarithmic()){
                    z.add(ToolBox.log(this.getZeta()*FT.g*FT.LAMBDA*this.getRhoD()*nsj/nij));
                    sigma.add(Math.sqrt(1/nsj + 1/nij));
                } else if (preferences.arcsin()){
                    z.add(Math.asin(Math.sqrt((nsj+3/8)/(nsj+nij+3/4))));
                    sigma.add(0.5/Math.sqrt(nsj + nij));
                }
            }
            // calculate the central value (z0)
            if (preferences.linear()){
                if(!fixedAxes){
                    this.set_z0(num/denom);
                }
            } else if (preferences.logarithmic()){
                if(!fixedAxes){
                    this.set_z0(ToolBox.log(this.getZeta()*FT.g*FT.LAMBDA*this.getRhoD()*nsplus/niplus));
                }
            } else if (preferences.arcsin()){
                if(!fixedAxes){
                    this.set_z0(Math.asin(Math.sqrt(nsplus/(nsplus+niplus))));
                }
            }
            // calculate and add the corresponding rx and ry values
            for (int i=0;i<z.size();i++){
                double[] rxry = zs2rxry(z.get(i),sigma.get(i));
                radialX.add(rxry[0]);
                radialY.add(rxry[1]);
            }
        } catch (Exception e){
            if (debugmode){e.printStackTrace();}
        }
    }
    
    /**
     *
     * @param z
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    protected double[] zs2ts(double z, double s) throws Exception {
        double[] ts = new double[2];
        if (preferences.linear()){
            ts[0] = z;
            ts[1] = s;
        } else if (preferences.logarithmic()){
            ts[0] = (1/FT.LAMBDA)*ToolBox.log(1+Math.exp(z));
            ts[1] = ts[0]*s;
        } else if (preferences.arcsin()){
            double ns = (1/(4*s*s) + 3/4)*Math.sin(z)*Math.sin(z) - 3/8;
            double ni = 1/(4*s*s) - ns;
            ts[0] = FT.getFTage(this.getZeta(), this.getRhoD(), ns, ni);
            ts[1] = FT.getFTageErr(this.getZeta(), this.getZeta_Err(),
                this.getRhoD(), this.getRhoD_Err(), ns, ni);
        }
        return ts;
    }

    /**
     *
     * @param z0
     * @return
     * @throws Exception
     */
    @Override
    protected double z02t(double z0) throws Exception {
        double t = 0, NsNi = 1, Ni = 100;
        if (preferences.linear()){
            t = z0;
            return t;
        } else if (preferences.logarithmic()){
            NsNi = Math.exp(z0)/(this.getZeta()*FT.g*FT.LAMBDA*this.getRhoD());
        } else if (preferences.arcsin()){
            NsNi = Math.sin(z0)*Math.sin(z0)/(1-Math.sin(z0)*Math.sin(z0));
        }
        t = FT.getFTage(this.getZeta(), this.getRhoD(), NsNi*Ni, Ni);
        return t;
    } 

    /**
     *
     * @param t
     * @return
     * @throws Exception
     */
    @Override
    public double t2z(double t) throws Exception {
        double zout = 0.0;
        if (preferences.linear()){
            zout = t;
        } else if (preferences.logarithmic()){
            zout = ToolBox.log(Math.exp(FT.LAMBDA*t) - 1);
        } else if (preferences.arcsin()){
            zout = Math.asin(1/Math.sqrt(1 + FT.LAMBDA*this.getZeta()*FT.g*this.getRhoD() /
                   (Math.exp(FT.LAMBDA*t) - 1)));
        }
        return zout;
    }
    
    @Override
    double[] getCentralAge() throws Exception {
        return FT.getCentralAgeErrDisp(this);
    }

    @Override
    double getPooledAge() throws Exception {
        double[] te = FT.getPooledAgeAndErr(this);
        return te[0];
    }

    @Override
    double[] getArithmeticMean() throws Exception {
        double Age, Err, Var;
        double[] NsNi, MuXi, out = new double[3];
        ArrayList<double[]> tst = new ArrayList<double[]>();
        for (Iterator i= iterator(); i.hasNext(); ) {
            NsNi = (double[]) i.next();
            Age = FT.getFTage(zeta, rhoD, NsNi[0], NsNi[1]);
            Err = FT.getFTageErr(zeta, zeta_err, rhoD, rhoD_err, NsNi[0], NsNi[1]);
            double[] AgeErr = {Age, Err};
            tst.add(AgeErr);
        }
        MuXi = Newton.solveMuXi(tst);
        Var = Newton.gettv(tst,MuXi[1]);
        out[0] = MuXi[0];
        out[1] = Math.sqrt(Var);
        out[2] = Math.sqrt(MuXi[1])/MuXi[0];
        return out;
    }

    /**
     *
     * @param index
     * @return
     * @throws Exception
     */
    public double getNi(int index) throws Exception {
        return super.getY(index);
    }
    
    /**
     *
     * @param value
     * @param index
     * @throws Exception
     */
    public void setNi(int value, int index) throws Exception {
        super.setY((double) value, index);
    }
    
    /**
     *
     * @param index
     * @return
     * @throws Exception
     */
    public double getNs(int index) throws Exception {
        return super.getX(index);
    }
    
    /**
     *
     * @param value
     * @param index
     * @throws Exception
     */
    public void setNs(int value, int index) throws Exception {
        super.setX((double)value, index);
    }
    
    /**
     *
     * @return
     */
    public double getZeta(){
        return zeta;
    }
    
    /**
     *
     * @param zeta
     */
    public void setZeta(double zeta){
        this.zeta = zeta;
    }
    
    /**
     *
     * @return
     */
    public double getZeta_Err(){
        return zeta_err;
    }
    
    /**
     *
     * @param zeta_err
     */
    public void setZeta_Err(double zeta_err){
        this.zeta_err = zeta_err;
    }
    
    /**
     *
     * @return
     */
    public double getRhoD(){
        return rhoD;
    }
    
    /**
     *
     * @param rhoD
     */
    public void setRhoD(double rhoD){
        this.rhoD = rhoD;
    }
    
    /**
     *
     * @return
     */
    public double getRhoD_Err(){
        return rhoD_err;
    }
    
    /**
     *
     * @param rhoD_err
     */
    public void setRhoD_Err(double rhoD_err){
        this.rhoD_err = rhoD_err;
    }    
    
    /**
     *
     * @param ns
     * @param ni
     * @param index
     * @throws Exception
     */
    public void insertEntry(int ns, int ni, int index) throws Exception {
        super.insertEntry((double)ns, (double)ni, NAN, index);
    }

    /**
     *
     * @param ns
     * @param ni
     * @param c
     * @param index
     * @throws Exception
     */
    public void insertEntry(int ns, int ni, double c, int index) throws Exception {
        super.insertEntry((double) ns, (double) ni, c, index);
    }

    /**
     *
     * @param out
     * @param X
     * @param Y
     * @throws Exception
     */
    @Override
    protected void writeXY(BufferedWriter out, double X, double Y) throws Exception {
        out.write((int)X + "," + (int)Y);
    }

    /**
     *
     * @param doLog
     * @return
     * @throws Exception
     */
    @Override
    protected double[][] getDataErrArray(boolean doLog) throws Exception {
        double[] NsNi = new double[3];
        double[][] ages = new double[2][this.length()],
                   logages = new double[2][this.length()], out;
        int j = 0;
        double buff = this.hasZeros() ? 0.5 : 0;
        for (Iterator i = iterator(); i.hasNext(); j++) {
            NsNi = (double[]) i.next();
            ages[0][j] = FT.getFTage(zeta, rhoD, NsNi[0]+buff, NsNi[1]+buff);
            ages[1][j] = FT.getFTageErr(zeta, zeta_err, rhoD, rhoD_err, NsNi[0]+buff, NsNi[1]+buff);
            logages[0][j] = ToolBox.log(ages[0][j]);
            logages[1][j] = ages[1][j]/ages[0][j];
        }
        out = doLog ? logages : ages;
        return out;
    }

    private double zeta = 0, zeta_err = 0, rhoD = 0, rhoD_err = 0;
   
}