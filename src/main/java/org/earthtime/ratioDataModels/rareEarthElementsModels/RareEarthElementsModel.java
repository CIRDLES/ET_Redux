/*
 * RareEarthElementsModel.java
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
package org.earthtime.ratioDataModels.rareEarthElementsModels;

import com.thoughtworks.xstream.XStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataList;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.dataDictionaries.TraceElements;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public final class RareEarthElementsModel extends AbstractRatiosDataModel {

    // class variables
    private static final long serialVersionUID = -2967979507667928974L;
    private static Map<String, AbstractRatiosDataModel> modelInstances = //
            new HashMap<String, AbstractRatiosDataModel>();
    private static final AbstractRatiosDataModel noneModel = //
            new RareEarthElementsModel( //
                    ReduxConstants.NONE, //
                    1, 0,//
                    "No Lab", //
                    "2000-01-01", //
                    "Placeholder model", "Placeholder model");
    private static final ValueModel[] myRatios = new ValueModel[14];
    private static final Map<String, BigDecimal> correlations = new HashMap<String, BigDecimal>();

    // per Matt Rioux October 2014
    // start EARTHTIMESriLankaStandardModel
    static {
        myRatios[0] = valueModelFactory(TraceElements.La.getName(), 0.237);
        myRatios[1] = valueModelFactory(TraceElements.Ce.getName(), 0.613);
        myRatios[2] = valueModelFactory(TraceElements.Pr.getName(), 0.0928);
        myRatios[3] = valueModelFactory(TraceElements.Nd.getName(), 0.457);
        myRatios[4] = valueModelFactory(TraceElements.Sm.getName(), 0.148);
        myRatios[5] = valueModelFactory(TraceElements.Eu.getName(), 0.0563);
        myRatios[6] = valueModelFactory(TraceElements.Gd.getName(), 0.199);
        myRatios[7] = valueModelFactory(TraceElements.Tb.getName(), 0.0361);
        myRatios[8] = valueModelFactory(TraceElements.Dy.getName(), 0.246);
        myRatios[9] = valueModelFactory(TraceElements.Ho.getName(), 0.0546);
        myRatios[10] = valueModelFactory(TraceElements.Er.getName(), 0.16);
        myRatios[11] = valueModelFactory(TraceElements.Tm.getName(), 0.0247);
        myRatios[12] = valueModelFactory(TraceElements.Yb.getName(), 0.161);
        myRatios[13] = valueModelFactory(TraceElements.Lu.getName(), 0.0246);
    }

    private static final AbstractRatiosDataModel EARTHTIME_McDonoughSunREEModel = //
            createInstance("EARTHTIME McDonough and Sun REE Model",
                    1, 0,//
                    "EARTHTIME",//
                    "2012-04-01",
                    "C1 carbonaceous chondite meteorite concentrations (McDonough and Sun, Chemical Geology, 1995)",//
                    "EARTHTIME-supplied model.",//
                    myRatios, //
                    correlations);

    private static final String classNameAliasForXML = "RareEarthElementsModel";

    private static ValueModel valueModelFactory(String name, double value) {
        return new ValueModel(//
                //
                //
                //
                name, //
                new BigDecimal(value), //
                ReduxConstants.NONE, //
                BigDecimal.ZERO,
                BigDecimal.ZERO);
    }

    private RareEarthElementsModel(//
            String modelName, //
            int versionNumber,//
            int minorVersionNumber, //
            String labName, //
            String dateCertified,//
            String reference,//
            String comment) {

        super(modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment);

        initializeNewRatiosAndRhos(false);

    }

    /**
     *
     * @param updateOnly
     */
    @Override
    public final void initializeNewRatiosAndRhos(boolean updateOnly) {
        ArrayList<ValueModel> holdValues = new ArrayList<ValueModel>();
        for (TraceElements traceElement : TraceElements.values()) {
            if (traceElement.isRareEarthElement()) {
                holdValues.add(//
                        new ValueModel(traceElement.getName(),
                                BigDecimal.ZERO,
                                ReduxConstants.NONE,
                                BigDecimal.ZERO,
                                BigDecimal.ZERO));
            }
        }

        ratios = holdValues.toArray(new ValueModel[holdValues.size()]);

        Arrays.sort(ratios, new DataValueModelNameComparator());

    }

    /**
     *
     * @param modelName
     * @param versionNumber
     * @param minorVersionNumber
     * @return
     */
    public static AbstractRatiosDataModel getInstance(String modelName, int versionNumber, int minorVersionNumber) {
        return modelInstances.get(makeNameAndVersion(modelName, versionNumber, minorVersionNumber));
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
     * @return the org.earthtime.ratioDataModels.AbstractRatiosDataModel
     */
    public static AbstractRatiosDataModel createInstance(//
            String modelName, int versionNumber, int minorVersionNumber, String labName, String dateCertified, String reference, String comment, ValueModel[] ratios, Map<String, BigDecimal> rhos) {

        AbstractRatiosDataModel myModel = modelInstances.get(makeNameAndVersion(modelName, versionNumber, minorVersionNumber));

        if (myModel == null) {
            myModel = new RareEarthElementsModel(//
                    modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment);

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

        AbstractRatiosDataModel myModel = new RareEarthElementsModel(//
                "New Rare Earth Element Model", //
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
    public static AbstractRatiosDataModel getEARTHTIME_McDonoughSunREEModelInstance() {
        // guarantee final model
        modelInstances.put(EARTHTIME_McDonoughSunREEModel.getNameAndVersion(), EARTHTIME_McDonoughSunREEModel);
        EARTHTIME_McDonoughSunREEModel.setImmutable(true);
        return EARTHTIME_McDonoughSunREEModel;
    }

    /**
     *
     * @return
     */
    public static ArrayList<AbstractRatiosDataModel> getArrayListOfModels() {

        // guarantee final models
        getNoneInstance();
        getEARTHTIME_McDonoughSunREEModelInstance();

        ArrayList<AbstractRatiosDataModel> arrayListOfModels = new ReduxLabDataList<AbstractRatiosDataModel>("Rare Earth Element Model");
        Iterator<String> modelsKeyInterator = modelInstances.keySet().iterator();
        while (modelsKeyInterator.hasNext()) {
            arrayListOfModels.add(modelInstances.get(modelsKeyInterator.next()));
        }

        Collections.sort(arrayListOfModels);

        return arrayListOfModels;
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

        return getInstance(modelName, versionNumber, minorVersionNumber);
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel cloneModel() {

        AbstractRatiosDataModel myModel = new RareEarthElementsModel(//
                this.modelName, //
                this.versionNumber,
                this.minorVersionNumber, //
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
        modelInstances.remove(this.getReduxLabDataElementName());
    }

    @Override
    public void initializeModel() {
        super.initializeModel();
    }

    /**
     *
     */
    @Override
    protected void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ValueModelXMLConverter());
    //    xstream.registerConverter(new MineralStandardUPbModelXMLConverter());

        xstream.alias("RareEarthElementsModel", RareEarthElementsModel.class);
        xstream.alias("ValueModel", ValueModel.class);

        setClassXMLSchemaURL("URI_RareEarthElementsModelXMLSchemaURL");
    }

    @Override
    public String getClassNameAliasForXML() {
        return classNameAliasForXML;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        ETSerializer.SerializeObjectToFile(sriLanka1, "MineralStandardUPbModelTEST.ser");
//        AbstractRatiosDataModel sriLanka2 = //
//                (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile("MineralStandardUPbModelTEST.ser");

    }
    
//    private void readObject(
//            ObjectInputStream stream)
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName(RareEarthElementsModel.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println("Customized De-serialization of RareEarthElementsModel " + theSUID);
//    }
}
