/*
 * InitialPbModelET.java
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

import Jama.Matrix;
import com.thoughtworks.xstream.XStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataList;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public class InitialPbModelET extends AbstractRatiosDataModel {

    // class variables
    private static final long serialVersionUID = 4304335957265082527L;
    private static final String classNameAliasForXML = "InitialPbModelET";
    private static final Map<String, AbstractRatiosDataModel> modelInstances = //
            new HashMap<>();
    private static final AbstractRatiosDataModel placeholderModel =//
            new PlaceholderInitialPbModel();
    private static final AbstractRatiosDataModel placeholderModel76 =//
            new PlaceholderInitialPb76Model();
    private static final AbstractRatiosDataModel noneModel = //
            new InitialPbModelET( //
                    ReduxConstants.NONE, //
                    1, 0, //
                    "No Lab",//
                    "2000-01-01",//
                    "empty model",//
                    "empty model");
    private static final AbstractRatiosDataModel staceyKramersModel = //
            new StaceyKramersInitialPbModelET();
    private static final ValueModel[] myRatios;
    private static final Map<String, BigDecimal> correlations;

    static {
        myRatios = new ValueModel[3];
        myRatios[0] = new ValueModel(//
                "r206_204c", //
                new BigDecimal("17.811"), //
                "ABS", //
                new BigDecimal("0.178"), BigDecimal.ZERO);
        myRatios[1] = new ValueModel(//
                "r207_204c", //
                new BigDecimal("15.576"), //
                "ABS", //
                new BigDecimal("0.156"), BigDecimal.ZERO);
        myRatios[2] = new ValueModel(//
                "r208_204c", //
                new BigDecimal("37.566"), //
                "ABS", //
                new BigDecimal("0.376"), BigDecimal.ZERO);

        correlations = new HashMap<String, BigDecimal>();
        correlations.put("rhoR206_204c__r207_204c", new BigDecimal("0.5"));
        correlations.put("rhoR206_204c__r208_204c", new BigDecimal("0.5"));
        correlations.put("rhoR207_204c__r208_204c", new BigDecimal("0.5"));
    }
    private static final AbstractRatiosDataModel EARTHTIMESriLankaInitialPbModel = //
            createInstance(//
                    "EARTHTIME SriLanka InitialPb",
                    1, 0,//
                    "EARTHTIME",//
                    "2012-04-01",//
                    "No reference",//
                    "EARTHTIME-supplied model",//
                    myRatios, //
                    correlations);

    /**
     *
     *
     * @param modelName
     * @param versionNumber
     * @param minorVersionNumber the value of minorVersionNumber
     * @param labName the value of labName
     * @param dateCertified the value of dateCertified
     * @param reference
     * @param comment
     */
    protected InitialPbModelET(//
            String modelName, int versionNumber, int minorVersionNumber, String labName, String dateCertified, String reference, String comment) {

        super(modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment);

        initializeNewRatiosAndRhos(false);
    }

    /**
     *
     * @param updateOnly
     */
    @Override
    public void initializeNewRatiosAndRhos(boolean updateOnly) {
        // initial pb model has a defined set of ratios 
        this.ratios = new ValueModel[DataDictionary.earthTimeInitialPbModelRatioNames.length];
        for (int i = 0; i < DataDictionary.earthTimeInitialPbModelRatioNames.length; i++) {
            this.ratios[i]
                    = new ValueModel(DataDictionary.getEarthTimeInitialPbModelRatioNames(i),
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        Arrays.sort(ratios, new DataValueModelNameComparator());

        buildRhosMap();
        buildRhosSysUnctMap();

    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel getPlaceholderInstance() {
        // guarantee final model
        modelInstances.put(placeholderModel.getNameAndVersion(), placeholderModel);
        placeholderModel.setImmutable(true);
        return placeholderModel;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel getPlaceholder76Instance() {
        // guarantee final model
        modelInstances.put(placeholderModel76.getNameAndVersion(), placeholderModel76);
        placeholderModel76.setImmutable(true);
        return placeholderModel76;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel getNoneInstance() {
        // guarantee final model
        modelInstances.put(noneModel.getNameAndVersion(), noneModel);
        noneModel.setImmutable(true);
        return noneModel;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel getStaceyKramersInstance() {
        // guarantee final model
        modelInstances.put(staceyKramersModel.getNameAndVersion(), staceyKramersModel);
        staceyKramersModel.setImmutable(true);
        return staceyKramersModel;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel getEARTHTIMESriLankaInitialPbModel() {
        // guarantee final model
        modelInstances.put(EARTHTIMESriLankaInitialPbModel.getNameAndVersion(), EARTHTIMESriLankaInitialPbModel);
        EARTHTIMESriLankaInitialPbModel.setImmutable(true);
        return EARTHTIMESriLankaInitialPbModel;
    }

    /**
     *
     *
     * @param modelName
     * @param versionNumber
     * @param minorVersionNumber
     * @param labName
     * @param dateCertified
     * @param reference
     * @param comment
     * @param ratios
     * @param rhos
     * @return
     */
    public static AbstractRatiosDataModel createInstance(//
            String modelName, //
            int versionNumber, //
            int minorVersionNumber, //
            String labName,//
            String dateCertified,//
            String reference, //
            String comment, //
            ValueModel[] ratios, //
            Map<String, BigDecimal> rhos) {

        AbstractRatiosDataModel myModel = modelInstances.get(makeNameAndVersion(modelName, versionNumber, minorVersionNumber));

        if (myModel == null) {
            myModel = new InitialPbModelET(//
                    modelName, //
                    versionNumber, minorVersionNumber,//
                    labName, //
                    dateCertified, //
                    reference, //
                    comment);

            myModel.initializeModel(ratios, rhos, null);

            modelInstances.put(makeNameAndVersion(modelName, versionNumber, minorVersionNumber), myModel);
        }

        return myModel;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel createNewInstance() {

        AbstractRatiosDataModel myModel = new InitialPbModelET(//
                "New InitialPb Model", //
                1, 0, //
                "No Lab", //
                DateHelpers.defaultEarthTimeDateString(), //
                "No reference", //
                "No comment");

        return myModel;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel cloneModel() {

        AbstractRatiosDataModel myModel = new InitialPbModelET(//
                this.modelName, //
                this.versionNumber, //
                this.minorVersionNumber,//
                this.labName, //
                this.dateCertified, //
                this.reference, //
                this.comment);

        myModel.initializeModel(cloneData(), cloneRhosVarUnct(), null);

        return myModel;
    }

    /**
     *
     */
    @Override
    public void removeSelf() {
        modelInstances.remove(this.getNameAndVersion());//((AbstractRatiosDataModel)model).getNameAndVersion() );
    }

    // used for deserialization
    /**
     *
     * @return
     */
    protected Object readResolve() {

        if (!modelInstances.containsKey(makeNameAndVersion(modelName, versionNumber, minorVersionNumber))) {

            this.initializeModel();
            modelInstances.put(makeNameAndVersion(modelName, versionNumber, minorVersionNumber), this);
        }

        return modelInstances.get(makeNameAndVersion(modelName, versionNumber, minorVersionNumber));
    }

    /**
     *
     * @return
     */
    public static ArrayList<AbstractRatiosDataModel> getArrayListOfModels() {

        // guarantee final models
        getPlaceholderInstance();
        getPlaceholder76Instance();
        getNoneInstance();
        getStaceyKramersInstance();
        getEARTHTIMESriLankaInitialPbModel();

        ArrayList<AbstractRatiosDataModel> arrayListOfModels = new ReduxLabDataList<>("Initial Pb Model");
        Iterator<String> modelsKeyInterator = modelInstances.keySet().iterator();
        while (modelsKeyInterator.hasNext()) {
            arrayListOfModels.add(modelInstances.get(modelsKeyInterator.next()));
        }

        Collections.sort(arrayListOfModels);

        return arrayListOfModels;
    }

    @Override
    protected void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new InitialPbModelETXMLConverter());

        xstream.alias("InitialPbModelET", InitialPbModelET.class);
        xstream.alias("ValueModel", ValueModel.class);

        setClassXMLSchemaURL("URI_InitialPbModelETXMLSchema");
    }

    @Override
    public String getClassNameAliasForXML() {
        return classNameAliasForXML;
    }

    /**
     *
     * @return
     */
    public ValueModel calculateR207_206c() {
        ValueModel r207_206c = new ValueModel("r207_206c", "PCT");

        BigDecimal value;

        try {
            value = getDatumByName("r207_204c").getValue()//
                    .divide(getDatumByName("r206_204c").getValue(), ReduxConstants.mathContext10);
        } catch (Exception e) {
            value = BigDecimal.ZERO;
        }

        r207_206c.setValue(value);
        r207_206c.setOneSigma(calculateR207_206cVarUnctPCT(value.doubleValue()));
        r207_206c.setOneSigmaSys(calculateR207_206cSysUnctPCT(value.doubleValue()));

//        System.out.println("CALC 207/6 " + r207_206c.formatValueAndOneSigmaABSForTesting());
        return r207_206c;
    }

    /**
     *
     * @param value the value of value
     * @return
     */
    protected BigDecimal calculateR207_206cVarUnctPCT(double value) {
        // june 2 2014 email from Noah McLean
        /*To calculate the uncertainty in the r207_206c from the current 'Initial Pb' model in Redux, 
         Extract the first two rows and columns of the Initial Pb covariance matrix (corresponding to the 206/204 and 207/204), 
         and call this 2-by-2 matrix covc.  Next, create the matrix J76_6474, which has one row and two columns.  
         The matrix elements are,

         J76_6474(row1, column1) = -r207_204c/(r206_204c^2)
         J76_6474(row1, column2) = 1/r206_204c

         The variance (one-sigma uncertainty, squared) for r207_206c is now

         J76_6474 * covc * transpose(J76_6474)

         where the * is matrix multiplication.*/

//        System.out.println(dataCovariancesVarUnct.ToStringWithLabels());
//        System.out.println(dataCovariancesVarUnct.getMatrix().get(0, 0));
        double r207_204c = getDatumByName("r207_204c").getValue().doubleValue();
        double r206_204c = getDatumByName("r206_204c").getValue().doubleValue();

        BigDecimal retVal;

        try {
            Matrix covc = dataCovariancesVarUnct.getMatrix().getMatrix(0, 1, 0, 1);
            Matrix J76_6474 = new Matrix(1, 2);
            J76_6474.set(0, 0, -r207_204c / (r206_204c * r206_204c));
            J76_6474.set(0, 1, 1.0 / r206_204c);

            double varc = J76_6474.times(covc).times(J76_6474.transpose()).get(0, 0);

            retVal = new BigDecimal(Math.sqrt(varc) / value * 100.0);
        } catch (Exception e) {
            retVal = BigDecimal.ZERO;
        }

        return retVal;

        /* by email 12 september 2014
         There are two forms of the uncertainty propagation equation for division, one using a covariance and one using a correlation coefficient.  
         For z = x/y

         z_oneSigmaAbs = z*sqrt( x_oneSigmaAbs^2/x^2 + y_oneSigmaAbs^2/y^2 - 2*covXY/(x*y) )
         or
         z_oneSigmaAbs = z*sqrt( x_oneSigmaAbs^2/x^2 + y_oneSigmaAbs^2/y^2 - 2*rhoXY*(x_oneSigmaAbs/x)*(y_oneSigmaAbs/y) )

         where 
         x_oneSigmaAbs and y_oneSigmaAbs and the one-sigma absolute uncertainties in x and y, and
         covXY and rhoXY are the covariance between x and y and the correlation coefficient between x and y, respectively.

         Note that the order of operations implied above is correct -- square the numerator and denominator of the first two terms on the RHS of each equation before doing division.  

         So for the 207/206, x = 207/204 and y = 206/204.

         As always, let me know if you have questions!*/
    }

    /**
     *
     * @param value
     * @return
     */
    protected BigDecimal calculateR207_206cSysUnctPCT(double value) {
        double r207_204c = getDatumByName("r207_204c").getValue().doubleValue();
        double r206_204c = getDatumByName("r206_204c").getValue().doubleValue();

        BigDecimal retVal;

        try {
            Matrix covc = dataCovariancesSysUnct.getMatrix().getMatrix(0, 1, 0, 1);
            Matrix J76_6474 = new Matrix(1, 2);
            J76_6474.set(0, 0, -r207_204c / (r206_204c * r206_204c));
            J76_6474.set(0, 1, 1.0 / r206_204c);

            double varc = J76_6474.times(covc).times(J76_6474.transpose()).get(0, 0);

            retVal = new BigDecimal(Math.sqrt(varc) / value * 100.0);
        } catch (Exception e) {
            retVal = BigDecimal.ZERO;
        }

        return retVal;
    }
    
    
}
