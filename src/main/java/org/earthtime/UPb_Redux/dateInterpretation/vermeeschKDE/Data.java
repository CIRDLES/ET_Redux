package org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE;

import java.io.*;
import java.util.*;

/**
 *
 * @author James F. Bowring
 */
public abstract class Data implements Iterator, Iterable {
    
    /**
     *
     * @param fn
     * @param preferences
     */
    public Data(String fn, Preferences preferences) {
        X = new ArrayList<Double>();
        Y = new ArrayList<Double>();
        Z = new ArrayList<Double>();
        z = new ArrayList<Double>();
        sigma = new ArrayList<Double>();
        C = new ArrayList<Double>();
        radialX = new ArrayList<Double>(); 
        radialY = new ArrayList<Double>();
        setFileName(fn);
        this.preferences = preferences;
    }
    
    /**
     *
     * @param X
     * @param Y
     * @param Z
     * @param preferences
     */
    public Data(ArrayList<Double> X, ArrayList<Double> Y, ArrayList<Double> Z, Preferences preferences){
        this("reduxData", preferences);
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    /**
     *
     * @return
     */
    public Preferences preferences(){
        return preferences;
    }
    
    /* check to see if the input file contains -as its second entry-
     * "F" (Fission tracks = 0), "L" (Luminescence = 1) or "O" (Other = 2).
     * If not, return -1
     */

    /**
     *
     * @param fn
     * @return
     * @throws Exception
     */
    
    static public boolean isFissionTrackFile(String fn) throws Exception {
        String aLine;
        char datatype = 'F';
        try {
            //System.out.println("Loading: " + fn);
            FileInputStream fin = new FileInputStream(fn);
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            // extract sample name
            aLine = br.readLine();
            StringTokenizer stokenizer = new StringTokenizer(aLine, ",");
            int numtokens = stokenizer.countTokens();
            stokenizer.nextToken();
            if (numtokens == 1) {
                return false;
            } else if (numtokens == 2){
                datatype = stokenizer.nextToken().charAt(0);
            }
            if (datatype=='F'){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            System.err.println("Problem with Data.getDataType()");
            return false;
        }
    }

    /**
     *
     * @throws Exception
     */
    public void readData() throws Exception {
        String aLine;
        if (filename.equals("")){return;}
        try {
            //System.out.println("Loading: " + filename);
            FileInputStream fin = new FileInputStream(getFileName());
            BufferedReader br = new BufferedReader(new InputStreamReader(fin));
            // extract sample name
            aLine = br.readLine();
            StringTokenizer stokenizer = new StringTokenizer(aLine, ",");
            int numtokens = stokenizer.countTokens();
            samplename = stokenizer.nextToken();
            if (numtokens == 2){
                stokenizer.nextToken();
            }  
            readHeaderAndBody(br);
            br.close();
        } catch (FileNotFoundException e){
            System.err.println("Couldn't find file: " + filename);
            this.setFileName("");
        } catch (Exception e) {
            System.err.println("Problem in Data:readData()");
        }
    }

    int getRowNumber(int samplenumber) throws Exception {
        int sn = 0;
        for (Iterator i = iterator(); i.hasNext(); i.next()) {
            if (sn >= samplenumber){
                break;
            } else {
                sn++;
            }
        }
        return ii;
    }
    
    abstract void readHeaderAndBody(BufferedReader br) throws Exception;
    
    /**
     *
     * @param br
     * @throws Exception
     */
    public void readBody(BufferedReader br) throws Exception {
        String aLine;
        int numtokens = 1;
        try {
            // extract the X, (optional) Y and (optional) Z data
            for (int i=0; (aLine = br.readLine()) != null ; i++) {
                StringTokenizer st = new StringTokenizer(aLine, ",");
                numtokens = st.countTokens();
                this.setX(Double.parseDouble(st.nextToken()),i);
                if (numtokens > 1) {
                    this.setY(Double.parseDouble(st.nextToken()),i);
                } else {
                    this.setY(NAN, i);
                }
                if (numtokens == 3) {
                    this.setZ(Double.parseDouble(st.nextToken()),i);
                } else {
                    this.setZ(NAN, i);
                }
            }
        } catch (Exception e){
            System.err.println("Error in Data.readBody(), error reading: " + filename);
        }
    }
    
    abstract void data2rxry(boolean fixedAxes) throws Exception ;
    
    abstract void writeOutput(String filepath) throws Exception ;
    
    /**
     *
     * @param out
     * @throws Exception
     */
    protected void writeBody(BufferedWriter out) throws Exception {
        String nl = System.getProperties().getProperty("line.separator");
        try {
            double[] XYZ;
            double c;
            for (Iterator i= this.iterator(); i.hasNext(); ) {
                XYZ = (double[]) i.next();
                c = XYZ[2];
                this.writeXY(out, XYZ[0], XYZ[1]);
                if (c >=0) {
                    out.write("," + c);
                }
                out.write(nl);
            }
            out.close();
        } catch (IOException e) {
            System.err.println("Problem in Data.writeBody()");
        }
    }
    
    /**
     *
     * @param out
     * @param X
     * @param Y
     * @throws Exception
     */
    protected void writeXY(BufferedWriter out, double X, double Y) throws Exception{
        out.write(X + "," + Y);
    }
    
    abstract double z02t(double z0) throws Exception ;
    
    /* returns 3-element array of central age, its standard error and age dispersion*/
    abstract double[] getCentralAge() throws Exception ;

    abstract double getPooledAge() throws Exception;

    abstract double[] getArithmeticMean() throws Exception;

    /* convert an age t (in years) to the corresponding radial plot parameter z
     * you should run data2rxry(plotoption,fixedaxes) before this function
     */
    abstract double t2z(double t) throws Exception ;

    /**
     *
     * @param doLog
     * @return
     * @throws Exception
     */
    public double[] getMinMaxAgeErr(boolean doLog) throws Exception {
        double[][] dea = this.getDataErrArray(doLog);
        double[] minmaxterr = {MINT,MAXT, 0, 0}; // initialise to some ridiculous values
        for (int i=0; i<dea[0].length; i++){
            if (dea[0][i]<minmaxterr[0]){
                minmaxterr[0] = dea[0][i];
                minmaxterr[2] = dea[1][i];
            }
            if (dea[0][i]>minmaxterr[1]){
                minmaxterr[1] = dea[0][i];
                minmaxterr[3] = dea[1][i];
            }
        }
        return minmaxterr;
    };

    double[] getMinMaxAge() throws Exception{
        double[] minmaxterr = getMinMaxAgeErr(false),
                 out = {minmaxterr[0],minmaxterr[1]};
        return out;
    }

    double getMinAge() throws Exception {
        double[] mM = getMinMaxAge();
        return mM[0];
    }

    double getMaxAge() throws Exception {
        double[] mM = getMinMaxAge();
        return mM[1];
    }
    
    double[] getMinMaxC() throws Exception {
        double c;
        double[] minmaxc = {1e10,1e-10};// initialize to some ridiculous values
        double[] XYZ;
        for (Iterator i= iterator(); i.hasNext(); ) {
            XYZ = (double[]) i.next();
            c = XYZ[2];
            if (c<minmaxc[0] && c!= Data.NAN){
                minmaxc[0] = c;
            } 
            if (c>minmaxc[1] && c!= Data.NAN){
                minmaxc[1] = c;
            }
        }
        return minmaxc;    
    };   
    
    /**
     *
     * @param index
     * @return
     * @throws Exception
     */
    public double getC(int index) throws Exception {
        return C.get(index);
    }
    
//    Reloads the data from filename if you have changed it

    /**
     *
     * @throws Exception
     */
        public void reload() throws Exception {
        this.clear();
        readData();
    }

    /**
     *
     * @throws Exception
     */
    public void clear() throws Exception {
        X.clear();
        Y.clear();
        Z.clear();
        z.clear();
        sigma.clear();
        C.clear();
        radialX.clear();
        radialY.clear();
    }
    
    /**
     *
     * @return
     */
    public int getSize(){
        return X.size();
    }

    /**
     *
     * @param index
     * @return
     */
    public double getY(int index) {
        return Y.get(index);
    }

    /**
     *
     * @param value
     * @param index
     */
    public void setY(double value, int index) {
        // recursively extend the ArrayList if necessary
        if (index<Y.size()){
            Y.set(index, value);
        } else {
            X.add(NAN);
            Y.add(NAN);
            Z.add(NAN);
            setY(value,index);
        }
    }
    
    /**
     *
     * @param index
     * @return
     */
    public double getX(int index) {
        return X.get(index);
    }
    
    /**
     *
     * @param value
     * @param index
     */
    public void setX(double value, int index) {
        // recursively extend the ArrayList if necessary
        if (index<X.size()){
            X.set(index, value);
        } else {
            X.add(NAN);
            Y.add(NAN);
            Z.add(NAN);
            setX(value,index);
        }
    }

    /**
     *
     * @param index
     * @return
     */
    public double getZ(int index) {
        return Z.get(index);
    }
    
    /**
     *
     * @param value
     * @param index
     */
    public void setZ(double value, int index) {
        // recursively extend the ArrayList if necessary
        if (index<Z.size()){
            Z.set(index, value);
        } else {
            X.add(NAN);
            Y.add(NAN);
            Z.add(NAN);
            setZ(value,index);
        }
    }    
    
    /**
     *
     * @return
     */
    public String getSampleName(){
        return samplename;
    }
    
    /**
     *
     * @param samplename
     */
    public void setSampleName(String samplename){
        this.samplename = samplename;
    }  
    
    /**
     *
     * @param x
     * @param y
     * @param index
     * @throws Exception
     */
    public void insertEntry(double x, double y, int index) throws Exception {
        this.insertEntry(x, y, NAN, index);
    }

    /**
     *
     * @param x
     * @param y
     * @param c
     * @param index
     * @throws Exception
     */
    public void insertEntry(double x, double y, double c, int index) throws Exception {
        X.add(index, x);
        Y.add(index, y);
        Z.add(index, c);
    }
    
    /**
     *
     * @param index
     * @throws Exception
     */
    public void removeEntry(int index) throws Exception {
        Y.remove(index);
        X.remove(index);
        Z.remove(index);
    }
    
    /**
     *
     * @param fn
     */
    public void setFileName(String fn){
        filename = fn;
    }
    
    /**
     *
     * @return
     */
    public String getFileName(){
        return filename;
    }
    
    /* checks to see if there are any instances where X = 0
     * (to be used to automatically assign arcsin transformation)
     */

    /**
     *
     * @return
     * @throws Exception
     */
    
    public boolean hasZeros() throws Exception {
        double xyj;
        double[] XY;
        for (Iterator i= this.iterator(); i.hasNext(); ){
            XY = (double[]) i.next();
            xyj = XY[0];
            if (xyj == 0){
                return true;
            }
        }
        return false;
    }

    abstract double[][] getDataErrArray(boolean doLog) throws Exception;

    abstract double[] zs2ts(double z, double s) throws Exception ;
    
    /**
     *
     * @param z
     * @param sigma
     * @return
     * @throws Exception
     */
    public double[] zs2rxry(double z, double sigma) throws Exception {
        double[] xy = new double[2];
        xy[0] = 1/sigma;
        xy[1] = (z-z0)/sigma;
        return xy;
    }

    /**
     *
     * @param rx
     * @param ry
     * @return
     * @throws Exception
     */
    public double[] rxry2zs(double rx, double ry) throws Exception {
        double[] zs = new double[2];
        zs[1] = 1/rx;
        zs[0] = z0 + zs[1]*ry;
        return zs;
    }    
    
    /**
     *
     * @return
     */
    public ArrayList<Double> getRadialX(){
        return this.radialX;
    }

    /**
     *
     * @return
     */
    public ArrayList<Double> getRadialY(){
        return this.radialY;
    }

    /* get the radial parameter z (not to be confused with the colour parameter Z!)*/

    /**
     *
     * @return
     */
    
    public ArrayList<Double> get_z(){
        return this.z;
    }

    /**
     *
     * @return
     */
    public ArrayList<Double> get_sigma(){
        return this.sigma;
    }

    /**
     *
     * @return
     */
    public double get_z0(){
        return this.z0;
    }    

    /**
     *
     * @param z0
     */
    public void set_z0(double z0){
        this.z0 = z0;
    }
    
    /* checks to see if there are at least 2 instances where the third
     * data column !=0, in order to see if a colour scale should be plotted
     */

    /**
     *
     * @return
     * @throws Exception
     */
    
    public boolean hasColour() throws Exception {
        double zj;
        int numGTzero = 0; // number of instances where z>=0
        double[] XYZ;
        for (Iterator i= this.iterator(); i.hasNext(); ){
            XYZ = (double[]) i.next();
            zj = XYZ[2];
            if (zj >= 0){
                numGTzero++;
            }
            if (numGTzero>=2){return true;}
        }
        return false;
    }

    /**
     *
     * @return
     */
    public int length(){
       int j = 0;
       for (Iterator i = this.iterator(); i.hasNext(); j++){ i.next(); }
       return j;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public int getNumPeaks() throws Exception {
        return numpeaks;
    }

    /**
     *
     * @param numpeaks
     * @throws Exception
     */
    public void setNumPeaks(int numpeaks) throws Exception {
        this.numpeaks = numpeaks;
    }

    /**
     *
     * @return
     * @throws Exception
     */
    public double getUnit() throws Exception {
        return this.unit;
    }

    /**
     *
     * @param unit
     * @throws Exception
     */
    public void setUnit(double unit) throws Exception {
        this.unit = unit;
    }

    /**
     *
     * @param data
     */
    public void copy(Data data){
        this.samplename = data.samplename;
        double[] XYZ;
        int j = 0;
        for (Iterator i= data.iterator(); i.hasNext(); ) {
            XYZ = (double[]) i.next();
            this.setX(XYZ[0], j);
            this.setY(XYZ[1], j);
            this.setZ(XYZ[2], j);
            j++;
        }
    }

    @Override
    public Iterator iterator() {
        ii = 0;
        // if the X, Y arrays are empty, return immediately
        if (this.X.size()==0){return this;}
        // if the first row is empty, find the first non-empty row
        if (this.getX(ii)==NAN){
            do {
            ii++;
            } while (hasNext() && (this.getX(ii)==NAN));
            return this;
        } else {
            return this;
        }
    }

    @Override
    public boolean hasNext() { return ii < this.getSize(); }

    @Override
    public double[] next() {
        double xx = (double) this.getX(ii);
        double yy = (double) this.getY(ii);
        double zz = this.getZ(ii);
        double[] XYZ = {xx,yy,zz};
        // put iterator in right position for next request
        do {
            ii++;
        } while (hasNext() && (this.getX(ii)==NAN));
        return XYZ;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("remove() in Data.java not implemented");
    }
   
   protected String filename,

    /**
     *
     */
    samplename = "";
   protected ArrayList<Double> Y,    

    /**
     *
     */
    X,    

    /**
     *
     */
    Z,    

    /**
     *
     */
    C,    

    /**
     *
     */
    z,    

    /**
     *
     */
    sigma,    

    /**
     *
     */
    radialX,    

    /**
     *
     */
    radialY;    

    /**
     *
     */
    protected int ii; // iterator index

    /**
     *
     */
    protected double z0 = 0.0;
   static final double NAN = -999.9, MINT = Double.MAX_VALUE, MAXT = 0d;
   static final boolean debugmode = true;

    /**
     *
     */
    public Preferences preferences;

    /**
     *
     */
    protected int numpeaks = 0;
   final static double GA = 1e9, MA = 1e6, KA = 1e3, A = 1;

    /**
     *
     */
    protected double unit = A;

    /**
     * @param Y the Y to set
     */
    public void setY ( ArrayList<Double> Y ) {
        this.Y = Y;
    }

    /**
     * @param X the X to set
     */
    public void setX ( ArrayList<Double> X ) {
        this.X = X;
    }

    /**
     * @param Z the Z to set
     */
    public void setZ ( ArrayList<Double> Z ) {
        this.Z = Z;
    }

}