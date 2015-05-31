/*
 * AbstractFunctionOfX.java
 *
 * Created Jul 1, 2011
 * 
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.Tripoli.fitFunctions;

import Jama.Matrix;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractFunctionOfX implements Serializable {

    // Class variables
    private static final long serialVersionUID = -7726468650291746155L;
    //
    /**
     *
     */
    protected double a;
    /**
     *
     */
    protected double b;
    /**
     *
     */
    protected double c;
    /**
     *
     */
    protected int countOfParameters;
    /**
     *
     */
    protected int iterations;
    /**
     *
     */
    protected Matrix fitParameterCovarianceMatrix;
    /**
     *
     */
    protected double chiSquared;

    /**
     *
     */
    protected double MSWD;

    /**
     *
     */
    protected double BIC;

    /**
     *
     */
    protected double negativeLogLikelihood;

    /**
     *
     */
    protected int countOfActiveData;
    /**
     *
     */
    protected String description;
    /**
     *
     */
    protected FitFunctionTypeEnum shortName;

    /**
     *
     */
    protected Matrix matrixJ11;

    /**
     *
     */
    protected Matrix hatMatrix;

    /**
     *
     */
    protected boolean overDispersionSelected;

    /**
     *
     */
    protected double overDispersion;

    /**
     *
     */
    protected double logRoughness;

    /**
     *
     */
    protected Matrix matrixJyp;

    /**
     *
     */
    protected double dLrInt_dDt;

    /**
     *
     */
    protected Matrix matrixSf;

    // nov 2014
    protected Matrix matrixJacobianYInterceptLogRatioXY;
    protected double[] diagonalOfSessionUnknownsAnalyticalCovarianceSu;
    private double[] diagonalOfSessionStandardsAnalyticalCovarianceSu;

    /**
     *
     */
    public AbstractFunctionOfX() {
    }

    /**
     *
     * @param a
     * @param b
     * @param c
     */
    public AbstractFunctionOfX(//
            double a,//
            double b, //
            double c) {

        if (Double.isNaN(a)) {
            this.a = 0.0;
        } else {
            this.a = a;
        }
        if (Double.isNaN(b)) {
            this.b = 0.0;
        } else {
            this.b = b;
        }
        if (Double.isNaN(c)) {
            this.c = 0.0;
        } else {
            this.c = c;
        }
        this.countOfParameters = 0;
        this.iterations = 0;
        this.fitParameterCovarianceMatrix = null;
        this.chiSquared = 0.0;
        this.description = "";
        this.shortName = null;
        this.countOfActiveData = 0;
        this.hatMatrix = null;
        this.overDispersionSelected = false;
        this.overDispersion = 0.0;
        this.matrixJyp = null;
        this.matrixSf = null;
    }

    /**
     *
     * @param x
     * @return
     */
    public abstract double f(double x);

    /**
     *
     * @param Jf
     * @param timesForMatrix
     * @return
     */
    public abstract double[] calculateInterpolatedVariances(Matrix Jf, double[] timesForMatrix);

    /**
     *
     * @param copyFrom
     */
    public void copyValuesFrom(AbstractFunctionOfX copyFrom) {
        setCountOfParameters(copyFrom.getCountOfParameters());
        setShortName(copyFrom.getShortName());
        setDescription(copyFrom.getDescription());
        a = copyFrom.getA();
        b = copyFrom.getB();
        c = copyFrom.getC();
        try {
            setMatrixJ11(copyFrom.getMatrixJ11().copy());
        } catch (Exception e) {
        }
        setIterations(copyFrom.getIterations());
        setFitParameterCovarianceMatrix(copyFrom.getFitParameterCovarianceMatrix());
        setChiSquared(copyFrom.getChiSquared());
        setCountOfActiveData(copyFrom.getCountOfActiveData());
        setHatMatrix(copyFrom.getHatMatrix());
        setMSWD(copyFrom.getMSWD());
        setBIC(copyFrom.getBIC());
        setNegativeLogLikelihood(copyFrom.getNegativeLogLikelihood());
        setOverDispersionSelected(copyFrom.isOverDispersionSelected());
        setOverDispersion(copyFrom.getOverDispersion());
        try {
            setMatrixJyp(copyFrom.getMatrixJyp().copy());
        } catch (Exception e) {
        }
        try {
            setMatrixSf(copyFrom.getMatrixSf().copy());
        } catch (Exception e) {
        }

    }

    /**
     *
     * @param tInt
     * @param time
     * @return
     */
    public static int calculateTimeLeftBracketIndex(double tInt, ArrayList<Double> time) {
        // bracket x in time
        int index = Collections.binarySearch(time, tInt);
        // determine index of time just previous
        if (index == -1) {
            index = 0;
        }
        if (index < 0) {
            index = Math.abs(index) - 2;
        }
        if (index >= (time.size() - 1)) {
            index = time.size() - 2;
        }

        return index;
    }

    /**
     *
     * @param SlrXY
     * @param SlogRatioX_Y
     * @return
     */
    public Matrix assembleMatrixJIntp(Matrix SlrXY) {
        Matrix Jabc = null;
        
        try {
            Matrix Jpy = (matrixJyp.transpose().times(SlrXY.solve(matrixJyp)).solve(matrixJyp.transpose().solveTranspose(SlrXY)));
            Jabc = extractMatrixJIntpFromJpy(Jpy);
        } catch (Exception e) {
            System.out.println(this.shortName + " matrix mismatch where MatrixJyp is " + matrixJyp.getRowDimension()//
                    + " x " + matrixJyp.getColumnDimension() + "  SlrXY is " + SlrXY.getRowDimension() + " x " + SlrXY.getColumnDimension());
        }
 
        return Jabc;
    }

    /**
     *
     * @param Jpy
     * @return
     */
    protected abstract Matrix extractMatrixJIntpFromJpy(Matrix Jpy);

    /**
     * @return the a
     */
    public double getA() {
        return a;
    }

    /**
     * @return the b
     */
    public double getB() {
        return b;
    }

    /**
     * @return the c
     */
    public double getC() {
        return c;
    }

    /**
     *
     * @param a
     */
    public void setA(double a) {
        this.a = a;
    }

    /**
     *
     * @param b
     */
    public void setB(double b) {
        this.b = b;
    }

    /**
     *
     * @param c
     */
    public void setC(double c) {
        this.c = c;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @return
     */
    public int getIterations() {
        return iterations;
    }

    /**
     *
     * @param iterations
     */
    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    /**
     * @return the fitParameterCovarianceMatrix
     */
    public Matrix getFitParameterCovarianceMatrix() {
        return fitParameterCovarianceMatrix;
    }

    /**
     * @param fitParameterCovarianceMatrix the fitParameterCovarianceMatrix to
     * set
     */
    public void setFitParameterCovarianceMatrix(Matrix fitParameterCovarianceMatrix) {
        this.fitParameterCovarianceMatrix = fitParameterCovarianceMatrix;
    }

    /**
     * @return the shortName
     */
    public String getShortNameString() {
        return shortName.getName();
    }

    /**
     *
     * @return
     */
    public boolean verifyPositiveVariances() {
        boolean retVal = true;
        for (int i = 0; i < fitParameterCovarianceMatrix.getRowDimension(); i++) {
            if ((Double.isNaN(fitParameterCovarianceMatrix.get(i, i))) || (fitParameterCovarianceMatrix.get(i, i) < 0.0)) {
                retVal = false;
            }
        }

        return retVal;
    }

    /**
     *
     * @return
     */
    public double getChiSquared() {
        return chiSquared;
    }

    /**
     *
     * @param chiSquared
     */
    public void setChiSquared(double chiSquared) {
        this.chiSquared = chiSquared;
    }

    //jan 2012 more explicit
    /**
     *
     * @return
     */
    public double getBIC() {
        // Bayesian Information Criterion
        return BIC;
    }

    /**
     *
     * @return
     */
    public double getMSWD() {
        //Mean Squared Weighted Deviation
        return MSWD;
    }

    /**
     * @return the countOfParameters
     */
    public int getCountOfParameters() {
        return countOfParameters;
    }

    /**
     * @return the shortName
     */
    public FitFunctionTypeEnum getShortName() {
        return shortName;
    }

    /**
     *
     * @return
     */
    public abstract double getYIntercept();

    /**
     *
     * @return
     */
    public abstract double getYInterceptVariance();

    /**
     *
     * @return
     */
    public abstract double getYInterceptStdErr();

    /**
     *
     * @return
     */
    public abstract double getStdErrOfA();

    /**
     *
     * @return
     */
    public abstract double getStdErrOfB();

    /**
     *
     * @return
     */
    public abstract double getStdErrOfC();

    /**
     *
     * @return
     */
    public abstract double getVarOfA();

    /**
     *
     * @return
     */
    public abstract double getVarOfB();

    /**
     *
     * @return
     */
    public abstract double getVarOfC();

    /**
     *
     * @return
     */
    public abstract boolean fitFound();

    /**
     *
     * @return
     */
    public String showStats() {

        NumberFormat formatter = new DecimalFormat("0.0000000000E0");

        String retVal = "Not implemented for this function yet ...";

        try {
            retVal = shortName + "  " + formatter.format(getA()) + "\t" + formatter.format(getB())//
                    + "\t" + formatter.format(getC()) + "\t" + formatter.format(getVarOfA()) //
                    + "\t" + formatter.format(getVarOfB()) + "\t" + formatter.format(getVarOfC());
        } catch (Exception e) {
        }

        return retVal;
    }

    /**
     *
     * @return
     */
    public String showParameters() {

        NumberFormat formatter = new DecimalFormat("0.0000000E0");
        String retval = "";

        retval += "fit function type is : " + getShortNameString() + ((String) (isOverDispersionSelected() ? "-OD" : "-no od")) + "\n";
        retval += "\t a = " + a + "\n";
        if (countOfParameters > 1) {
            retval += "\t b = " + b + "\n";
        }
        if (countOfParameters > 2) {
            retval += "\t c = " + c + "\n";
        }
        retval += "\n\tOD = " + overDispersion + "\n";
        retval += "\n\tMSWD = " + MSWD + "\n";
        retval += "\tBIC = " + BIC + "\n";
        retval += "\nfitParameterCovarianceMatrix:\n";
        for (int i = 0; i < fitParameterCovarianceMatrix.getRowDimension(); i++) {
            for (int j = 0; j < fitParameterCovarianceMatrix.getRowDimension(); j++) {
                retval += "\t" + formatter.format(fitParameterCovarianceMatrix.get(i, j));
            }
            retval += "\n";
        }

        return retval;
    }

    /**
     *
     * @return
     */
    public String showYInterceptData() {
        NumberFormat formatter = new DecimalFormat("0.0000000E0");
        String retval = "";

        retval += "fit function type is : " + getShortNameString() + "\n";

        retval += "Y-intercept (log ratio) = " + getYIntercept() + "\n";
        retval += "Y-intercept sigma (log ratio) = " + getYInterceptStdErr() + "\n";
        retval += "Y-intercept sigma * sqrt MSWD (log ratio) = " + getYInterceptStdErr() * Math.sqrt(getMSWD()) + "\n";

        return retval;
    }

//    public String showYInterceptDataInTermsOfAlpha ( RawRatioDataModel rr ) {
//        NumberFormat formatter = new DecimalFormat( "0.0000000E0" );
//        String retval = "";
//
//        retval += "fit function type is : " + getShortNameString() + "\n";
//
//        retval += "Y-intercept (alpha using standard = " + rr.getStandardValue() //
//                + ") = " + rr.calculateAlpha( getYIntercept()) + "\n";
//        retval += "Y-intercept sigma (alpha using standard = " + rr.getStandardValue() //
//                + ") = " + rr.calculateAlpha( getYInterceptStdErr()) + "\n";
//
//        return retval;
//    }
    /**
     * @return the matrixJ11
     */
    public Matrix getMatrixJ11() {
        return matrixJ11;
    }

    /**
     * @param matrixJ11 the matrixJ11 to set
     */
    public void setMatrixJ11(Matrix matrixJ11) {
        this.matrixJ11 = matrixJ11;
    }

    /**
     *
     * @param countOfActiveData
     * @param dataActiveMap
     * @param neededValues
     * @return
     */
    public abstract Matrix makeMatrixJ21(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues);

    /**
     *
     * @param countOfActiveData
     * @param dataActiveMap
     * @param neededValues
     * @return
     */
    public abstract Matrix makeMatrixJ22(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues);

    /**
     * @return the countOfActiveData
     */
    public int getCountOfActiveData() {
        return countOfActiveData;
    }

    /**
     * @param countOfActiveData the countOfActiveData to set
     */
    public void setCountOfActiveData(int countOfActiveData) {
        this.countOfActiveData = countOfActiveData;
    }

    /**
     * @return the hatMatrix
     */
    public Matrix getHatMatrix() {
        return hatMatrix;
    }

    /**
     * @param hatMatrix the hatMatrix to set
     */
    public void setHatMatrix(Matrix hatMatrix) {
        this.hatMatrix = hatMatrix;
    }

    /**
     * @param countOfParameters the countOfParameters to set
     */
    public void setCountOfParameters(int countOfParameters) {
        this.countOfParameters = countOfParameters;
    }

    /**
     * @param shortName the shortName to set
     */
    public void setShortName(FitFunctionTypeEnum shortName) {
        this.shortName = shortName;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param MSWD the MSWD to set
     */
    public void setMSWD(double MSWD) {
        this.MSWD = MSWD;
    }

    /**
     * @param BIC the BIC to set
     */
    public void setBIC(double BIC) {
        this.BIC = BIC;
    }

    /**
     * @return the negativeLogLikelihood
     */
    public double getNegativeLogLikelihood() {
        return negativeLogLikelihood;
    }

    /**
     * @param negativeLogLikelihood the negativeLogLikelihood to set
     */
    public void setNegativeLogLikelihood(double negativeLogLikelihood) {
        this.negativeLogLikelihood = negativeLogLikelihood;
    }

    /**
     * @return the overDispersionSelected
     */
    public boolean isOverDispersionSelected() {
        return overDispersionSelected;
    }

    /**
     * @param overDispersionSelected the overDispersionSelected to set
     */
    public void setOverDispersionSelected(boolean overDispersionSelected) {
        this.overDispersionSelected = overDispersionSelected;
    }

    /**
     * @return the overDispersion
     */
    public double getOverDispersion() {
        return overDispersion;
    }

    /**
     * @param overDispersion the overDispersion to set
     */
    public void setOverDispersion(double overDispersion) {
        this.overDispersion = overDispersion;
    }

    /**
     * @return the logRoughness
     */
    public double getLogRoughness() {
        return logRoughness;
    }

    /**
     * @param logRoughness the logRoughness to set
     */
    public void setLogRoughness(double logRoughness) {
        this.logRoughness = logRoughness;
    }

    /**
     * @param matrixJyp the matrixJyp to set
     */
    public void setMatrixJyp(Matrix matrixJyp) {
        this.matrixJyp = matrixJyp;
    }

    /**
     * @return the matrixJyp
     */
    public Matrix getMatrixJyp() {
        return matrixJyp;
    }

    /**
     * @return the dLrInt_dDt
     */
    public double getdLrInt_dDt() {
        return dLrInt_dDt;
    }

    /**
     * @param dLrInt_dDt the dLrInt_dDt to set
     */
    public void setdLrInt_dDt(double dLrInt_dDt) {
        this.dLrInt_dDt = dLrInt_dDt;
    }

    /**
     * @return the matrixSf
     */
    public Matrix getMatrixSf() {
        return matrixSf;
    }

    /**
     * @param matrixSf the matrixSf to set
     */
    public void setMatrixSf(Matrix matrixSf) {
        this.matrixSf = matrixSf;
    }

    /**
     * @return the matrixJacobianYInterceptLogRatioXY
     */
    public Matrix getMatrixJacobianYInterceptLogRatioXY() {
        return matrixJacobianYInterceptLogRatioXY;
    }

    /**
     * @param matrixJacobianYInterceptLogRatioXY the
     * matrixJacobianYInterceptLogRatioXY to set
     */
    public void setMatrixJacobianYInterceptLogRatioXY(Matrix matrixJacobianYInterceptLogRatioXY) {
        this.matrixJacobianYInterceptLogRatioXY = matrixJacobianYInterceptLogRatioXY;
    }

    /**
     * @return the diagonalOfSessionUnknownsAnalyticalCovarianceSu
     */
    public double[] getDiagonalOfSessionUnknownsAnalyticalCovarianceSu() {
        return diagonalOfSessionUnknownsAnalyticalCovarianceSu;
    }

    /**
     * @param diagonalOfSessionUnknownsAnalyticalCovarianceSu the diagonalOfSessionUnknownsAnalyticalCovarianceSu to set
     */
    public void setDiagonalOfSessionUnknownsAnalyticalCovarianceSu(double[] diagonalOfSessionUnknownsAnalyticalCovarianceSu) {
        this.diagonalOfSessionUnknownsAnalyticalCovarianceSu = diagonalOfSessionUnknownsAnalyticalCovarianceSu;
    }

    /**
     * @return the diagonalOfSessionStandardsAnalyticalCovarianceSu
     */
    public double[] getDiagonalOfSessionStandardsAnalyticalCovarianceSu() {
        return diagonalOfSessionStandardsAnalyticalCovarianceSu;
    }

    /**
     * @param diagonalOfSessionStandardsAnalyticalCovarianceSu the diagonalOfSessionStandardsAnalyticalCovarianceSu to set
     */
    public void setDiagonalOfSessionStandardsAnalyticalCovarianceSu(double[] diagonalOfSessionStandardsAnalyticalCovarianceSu) {
        this.diagonalOfSessionStandardsAnalyticalCovarianceSu = diagonalOfSessionStandardsAnalyticalCovarianceSu;
    }
}
