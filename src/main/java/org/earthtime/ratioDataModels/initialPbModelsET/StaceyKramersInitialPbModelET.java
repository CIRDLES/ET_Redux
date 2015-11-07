/*
 * StaceyKramersInitialPbModelET.java
 *
 *
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
package org.earthtime.ratioDataModels.initialPbModelsET;

import java.math.BigDecimal;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.exceptions.ETException;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public final class StaceyKramersInitialPbModelET extends InitialPbModelET {

    // class variables
    private static final long serialVersionUID = 8869581043057627575L;
    // instance variable
    // april 2014 the uncertainty for r207_206c is provided here temporarily for use in
    // initialPb corrections while Noah figures out how he wants to handle
    // this issue for InitialPbModels in general
    private BigDecimal r207_206cUnctPCT;
    // sept 2014 to handle common lead updates
    private TripoliFraction tripoliFraction;

    /**
     *
     */
    public StaceyKramersInitialPbModelET() {
        super("StaceyKramers", //
                1, 0,//
                "EARTHTIME",//
                DateHelpers.defaultEarthTimeDateString(),//
                "Stacey, J. C. and Kramers, J., 1975. EPSL 26, pp. 207:221",//
                "Model calculates initial Pb from fraction's est Date and uncertainty.");

        this.initializeModel();
    }

    /**
     *
     * @param tripoliFraction
     */
    public StaceyKramersInitialPbModelET(TripoliFraction tripoliFraction) {
        super("StaceyKramers", //
                1, 0,//
                "EARTHTIME",//
                DateHelpers.defaultEarthTimeDateString(),//
                "Stacey, J. C. and Kramers, J., 1975. EPSL 26, pp. 207:221",//
                "Model calculates initial Pb from fraction's est Date and uncertainty.");

        this.tripoliFraction = tripoliFraction;
        resetModelFromTripoliFraction();
        //this.initializeModel();
    }

    /**
     *
     * @param initialPbModelMatrix the value of initialPbModelMatrix
     * @param showAbsUnct the value of showAbsUnct
     */
    public void saveSKParametersToFraction(AbstractMatrixModel initialPbModelMatrix, boolean showAbsUnct) {
        // first row of displayed model is  editable, so use values here
        // check for abs or pct toggle
        double value = initialPbModelMatrix.getMatrix().get(0, 0);
        double oneSigmaVar = initialPbModelMatrix.getMatrix().get(0, 1);
        double oneSigmaSys = initialPbModelMatrix.getMatrix().get(0, 2);
        double oneSigmaVarPCT = oneSigmaVar;
        double oneSigmaSysPCT = oneSigmaSys;
        if (showAbsUnct) {
            // recalculate the percents
            oneSigmaVarPCT = (oneSigmaVar / value) * 100.0;
            oneSigmaSysPCT = (oneSigmaSys / value) * 100.0;
        }
        tripoliFraction.setSkOneSigmaVarUnctPct(new BigDecimal(oneSigmaVarPCT));
        tripoliFraction.setSkOneSigmaSysUnctPct(new BigDecimal(oneSigmaSysPCT));

    }

    /**
     *
     * @param correlationVarUnctMatrixView
     * @param correlationSysUnctMatrixView
     */
    public void saveSKRhoVarSysToFraction(//
            AbstractMatrixModel correlationVarUnctMatrixView, AbstractMatrixModel correlationSysUnctMatrixView) {
        // use upper left editable cell to populate rho
        try {
            tripoliFraction.setSkRhoVarUnct(new BigDecimal(correlationVarUnctMatrixView.getMatrix().get(0, 1)));
        } catch (Exception e) {
            tripoliFraction.setSkRhoVarUnct(BigDecimal.ZERO);
        }
        try {
            tripoliFraction.setSkRhoSysUnct(new BigDecimal(correlationSysUnctMatrixView.getMatrix().get(0, 1)));
        } catch (Exception e) {
            tripoliFraction.setSkRhoSysUnct(BigDecimal.ZERO);
        }
    }

    /**
     *
     * @param estDate
     */
    public void saveSKEstimatedDateToFraction(Double estDate) {
        tripoliFraction.setSkEstimatedDate(new BigDecimal(estDate));
    }

    /**
     *
     * @return
     */
    public BigDecimal getSKEstimatedDateFromFraction() {
        return tripoliFraction.getSkEstimatedDate();
    }

    /**
     *
     */
    public void resetModelFromTripoliFraction() {
        AbstractRatiosDataModel physicalConstantsModel
                = PhysicalConstantsModel.getMostRecentEARTHTIMEPhysicalConstantsModel();

        BigDecimal lambda238 = physicalConstantsModel.getDatumByName(Lambdas.lambda238.getName()).getValue();
        BigDecimal lambda235 = physicalConstantsModel.getDatumByName(Lambdas.lambda235.getName()).getValue();
        BigDecimal lambda232 = physicalConstantsModel.getDatumByName(Lambdas.lambda232.getName()).getValue();

        calculateRatios(tripoliFraction.getSkEstimatedDate(), lambda238, lambda235, lambda232);
        calculateUncertaintiesAndRhos(//
                tripoliFraction.getSkOneSigmaVarUnctPct(), tripoliFraction.getSkRhoVarUnct(), //
                tripoliFraction.getSkOneSigmaSysUnctPct(), tripoliFraction.getSkRhoSysUnct());

        tripoliFraction.setInitialPbSchemeA_r207_206c(calculateR207_206c());

        tripoliFraction.setInitialPbSchemeB_R206_204c(getDatumByName("r206_204c"));
        tripoliFraction.setInitialPbSchemeB_R207_204c(getDatumByName("r207_204c"));
        tripoliFraction.setInitialPbSchemeB_R208_204c(getDatumByName("r208_204c"));

    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel copyModel() {

        // don't copy stacey kramers, but convert it
        AbstractRatiosDataModel myModel = InitialPbModelET.createNewInstance();
        myModel.setModelName("Generated-PleaseRename");
        myModel.setComment("Generated using Stacey-Kramers.");

        myModel.initializeModel(cloneData(), cloneRhosVarUnct(), null);

        return myModel;
    }

    @Override
    public void saveEdits(boolean checkCovarianceValidity) throws ETException {
        super.saveEdits(checkCovarianceValidity); //To change body of generated methods, choose Tools | Templates.

    }

//    /**
//     *
//     * @param value the value of value
//     * @return
//     */
//    @Override
//    protected BigDecimal calculateR207_206cVarUnctPCT(double value) {
//        return r207_206cUnctPCT;
//    }
    /**
     *
     * @param estimatedAgeInMA
     * @param lambda238
     * @param lambda235
     * @param lambda232
     */
    public void calculateRatios(
            BigDecimal estimatedAgeInMA,
            BigDecimal lambda238,
            BigDecimal lambda235,
            BigDecimal lambda232) {

        if (estimatedAgeInMA.compareTo(new BigDecimal("3700.0")) == 1) {
            BigDecimal ageConst = new BigDecimal("4.57E9");

            getDatumByName("r206_204c").
                    setValue(new BigDecimal("9.307").//
                            add(new BigDecimal("7.19").//
                                    multiply(calcExponentialTerm(lambda238, ageConst, estimatedAgeInMA),//
                                            ReduxConstants.mathContext15)));

            getDatumByName("r207_204c").
                    setValue(new BigDecimal("10.294").//
                            add(new BigDecimal(Double.toString(7.19 / 137.88)).//
                                    multiply(calcExponentialTerm(lambda235, ageConst, estimatedAgeInMA),//
                                            ReduxConstants.mathContext15)));

            getDatumByName("r208_204c").
                    setValue(new BigDecimal("29.487").//
                            add(new BigDecimal("33.21").//
                                    multiply(calcExponentialTerm(lambda232, ageConst, estimatedAgeInMA),//
                                            ReduxConstants.mathContext15)));

        } else {
            BigDecimal ageConst = new BigDecimal("3.7E9");

            getDatumByName("r206_204c").
                    setValue(new BigDecimal("11.152").//
                            add(new BigDecimal("9.74").//
                                    multiply(calcExponentialTerm(lambda238, ageConst, estimatedAgeInMA),//
                                            ReduxConstants.mathContext15)));

            getDatumByName("r207_204c").
                    setValue(new BigDecimal("12.998").//
                            add(new BigDecimal(Double.toString(9.74 / 137.88)).//
                                    multiply(calcExponentialTerm(lambda235, ageConst, estimatedAgeInMA),//
                                            ReduxConstants.mathContext15)));

            getDatumByName("r208_204c").
                    setValue(new BigDecimal("31.23").//
                            add(new BigDecimal("36.84").//
                                    multiply(calcExponentialTerm(lambda232, ageConst, estimatedAgeInMA),//
                                            ReduxConstants.mathContext15)));

        }
    }

    /**
     *
     * @param oneSigmaVarPct
     * @param skRhoVarUnct
     * @param oneSigmaSysPct the value of oneSigmaSysPct
     * @param skRhoSysUnct the value of skRhoSysUnct
     */
    public void calculateUncertaintiesAndRhos(BigDecimal oneSigmaVarPct, BigDecimal skRhoVarUnct, BigDecimal oneSigmaSysPct, BigDecimal skRhoSysUnct) {

        getDatumByName("r206_204c").setUncertaintyType("PCT");
        getDatumByName("r206_204c").setOneSigma(oneSigmaVarPct);
        getDatumByName("r206_204c").setOneSigmaSys(oneSigmaSysPct);

        getDatumByName("r207_204c").setUncertaintyType("PCT");
        getDatumByName("r207_204c").setOneSigma(oneSigmaVarPct);
        getDatumByName("r207_204c").setOneSigmaSys(oneSigmaSysPct);

        getDatumByName("r208_204c").setUncertaintyType("PCT");
        getDatumByName("r208_204c").setOneSigma(oneSigmaVarPct);
        getDatumByName("r208_204c").setOneSigmaSys(oneSigmaSysPct);

        setRhos(skRhoVarUnct, skRhoSysUnct);

        initializeModel();
    }

    public void providePbcVarUncertaintiesOneSigmaAbs(double r206_204cVarUnct, double r207_204cVarUnct, double r208_204cVarUnct) {
        getDatumByName("r206_204c").setUncertaintyType("ABS");
        getDatumByName("r206_204c").setOneSigma(r206_204cVarUnct);

        getDatumByName("r207_204c").setUncertaintyType("ABS");
        getDatumByName("r207_204c").setOneSigma(r207_204cVarUnct);

        getDatumByName("r208_204c").setUncertaintyType("ABS");
        getDatumByName("r208_204c").setOneSigma(r208_204cVarUnct);
    }

    /**
     *
     * @param skRhoVarUnct
     * @param skRhoSysUnct
     */
    public void setRhos(BigDecimal skRhoVarUnct, BigDecimal skRhoSysUnct) {
        rhos.put("rhoR206_204c__r207_204c", skRhoVarUnct);
        rhos.put("rhoR207_204c__r208_204c", skRhoVarUnct);
        rhos.put("rhoR206_204c__r208_204c", skRhoVarUnct);

        rhosSysUnct.put("rhoR206_204c__r207_204c", skRhoSysUnct);
        rhosSysUnct.put("rhoR207_204c__r208_204c", skRhoSysUnct);
        rhosSysUnct.put("rhoR206_204c__r208_204c", skRhoSysUnct);
    }

    private BigDecimal calcExponentialTerm(
            BigDecimal lambda,
            BigDecimal ageConst,
            BigDecimal commonPbAge) {
        Double term1 = 1.0 + Math.expm1((lambda.multiply(ageConst)).doubleValue());
        Double term2 = 1.0 + Math.expm1((lambda.multiply(
                commonPbAge.multiply(new BigDecimal("1.0E6")))).doubleValue());

        return new BigDecimal(Double.toString(term1 - term2), ReduxConstants.mathContext15);
    }

    /**
     * @param r207_206cUnctPCT the r207_206cUnctPCT to set
     */
    public void setR207_206cUnctPCT(BigDecimal r207_206cUnctPCT) {
        this.r207_206cUnctPCT = r207_206cUnctPCT;
    }

    /**
     * @param tripoliFraction the tripoliFraction to set
     */
    public void setTripoliFraction(TripoliFraction tripoliFraction) {
        this.tripoliFraction = tripoliFraction;
    }

//    private void readObject (
//            ObjectInputStream stream )
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName( StaceyKramersInitialPbModelET.class.getCanonicalName() ) );
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println( "Customized De-serialization of StaceyKramersInitialPbModelET " + theSUID );
//    }
    /**
     * @return the tripoliFraction
     */
    public TripoliFraction getTripoliFraction() {
        return tripoliFraction;
    }
}
