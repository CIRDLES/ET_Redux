/*
 * PhysicalConstantsModel.java
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
package org.earthtime.ratioDataModels.physicalConstantsModels;

import com.thoughtworks.xstream.XStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferencedXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.PhysicalConstantsDataViewEditable;
import org.earthtime.reduxLabData.ReduxLabDataList;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public class PhysicalConstantsModel extends AbstractRatiosDataModel {

    // class variables
    private static final long serialVersionUID = 5345194857641736957L;
    private static final String classNameAliasForXML = "PhysicalConstantsModel";
    private static final Map<String, AbstractRatiosDataModel> modelInstances = //
            new HashMap<>();
    private static final ValueModel[] myRatios;
    private static final Map<String, BigDecimal> correlations;
    private static final Map<String, BigDecimal> EARTHTIMEatomicMolarMasses;
    private static final AbstractRatiosDataModel noneModel = //
            new PhysicalConstantsModel( //
                    ReduxConstants.NONE, //
                    1, 0, //
                    "No Lab",//
                    "2000-01-01",//
                    "Placeholder model",//
                    "Placeholder model");

    static {
        myRatios = new ValueModel[6];
        myRatios[0] = new ValueModelReferenced(//
                Lambdas.lambda230.getName(), //
                new BigDecimal("0.0000091577"), //9.1577 *10^-6
                "PCT", //
                new BigDecimal("0.15244"), BigDecimal.ZERO,
                "Cheng et al. 2000");
        myRatios[1] = new ValueModelReferenced(//
                Lambdas.lambda231.getName(), //
                new BigDecimal("0.0000211887"), //
                "PCT", //
                new BigDecimal("0.33578"), BigDecimal.ZERO,
                "Robert et al. 1969");
        myRatios[2] = new ValueModelReferenced(//
                Lambdas.lambda232.getName(), //
                new BigDecimal("0.0000000000493343"), //
                "PCT", //
                new BigDecimal("0.042769"), BigDecimal.ZERO,
                "Holden 1990");
        myRatios[3] = new ValueModelReferenced(//
                Lambdas.lambda234.getName(), //
                new BigDecimal("0.0000028262"), //
                "PCT", //
                new BigDecimal("0.00000000285"), BigDecimal.ZERO,
                "Cheng et al. 2000");
        myRatios[4] = new ValueModelReferenced(//
                Lambdas.lambda235.getName(), //
                new BigDecimal("0.00000000098485"), //
                "PCT", //
                new BigDecimal("0.068031"), BigDecimal.ZERO,
                "Jaffey et al. 1971");
        myRatios[5] = new ValueModelReferenced(//
                Lambdas.lambda238.getName(), //
                new BigDecimal("0.000000000155125"), //
                "PCT", //
                new BigDecimal("0.053505"), BigDecimal.ZERO,
                "Jaffey et al. 1971");

        correlations = new HashMap<>();

        EARTHTIMEatomicMolarMasses = new TreeMap<>();
        EARTHTIMEatomicMolarMasses.put("gmol204", new BigDecimal("203.973028"));
        EARTHTIMEatomicMolarMasses.put("gmol205", new BigDecimal("204.9737"));
        EARTHTIMEatomicMolarMasses.put("gmol206", new BigDecimal("205.974449"));
        EARTHTIMEatomicMolarMasses.put("gmol207", new BigDecimal("206.975880"));
        EARTHTIMEatomicMolarMasses.put("gmol208", new BigDecimal("207.976636"));
        EARTHTIMEatomicMolarMasses.put("gmol235", new BigDecimal("235.043922"));
        EARTHTIMEatomicMolarMasses.put("gmol238", new BigDecimal("238.050785"));
    }
    private static final AbstractRatiosDataModel EARTHTIMEPhysicalConstantsModel = //
            createInstance(//
                    "EARTHTIME Physical Constants Model",
                    1, 0, //
                    "EARTHTIME",//
                    "2008-01-01",//
                    "See individual constants for reference.",//
                    "This Physical Constants Model is the accepted default as of 2008.",//
                    myRatios, //
                    correlations,//
                    EARTHTIMEatomicMolarMasses);
    // instance variables
    private Map<String, BigDecimal> atomicMolarMasses;

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
    private PhysicalConstantsModel(//
            String modelName, int versionNumber, int minorVersionNumber, String labName, String dateCertified, String reference, String comment) {

        super(modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment);

        this.atomicMolarMasses = EARTHTIMEatomicMolarMasses;

        initializeNewRatiosAndRhos(false);
    }

    /**
     *
     * @param updateOnly
     */
    @Override
    public final void initializeNewRatiosAndRhos(boolean updateOnly) {

        this.ratios = new ValueModel[DataDictionary.MeasuredConstants.length];
        for (int i = 0; i < DataDictionary.MeasuredConstants.length; i++) {
            this.ratios[i]
                    = new ValueModelReferenced(
                            DataDictionary.MeasuredConstants[i][0],
                            BigDecimal.ZERO,
                            "PCT",
                            BigDecimal.ZERO, BigDecimal.ZERO,
                            "");
        }

        Arrays.sort(ratios, new DataValueModelNameComparator());

        buildRhosMap();

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
    public static AbstractRatiosDataModel getDefaultEARTHTIMEPhysicalConstantsModel() {
        // guarantee final model
        modelInstances.put(EARTHTIMEPhysicalConstantsModel.getNameAndVersion(), EARTHTIMEPhysicalConstantsModel);
        EARTHTIMEPhysicalConstantsModel.setImmutable(true);
        return EARTHTIMEPhysicalConstantsModel;
    }

    /**
     *
     * @return
     */
    public static Map<String, BigDecimal> getEARTHTIMEatomicMolarMasses() {
        return EARTHTIMEatomicMolarMasses;
    }

    // Produces hashmap for XML from treemap
    /**
     *
     * @return
     */
    public Map<String, BigDecimal> getAtomicMolarMassesForXMLSerialization() {
        Map<String, BigDecimal> atomicMolarMassesLocal = new HashMap<String, BigDecimal>();
        Iterator<String> rhosKeyIterator = atomicMolarMasses.keySet().iterator();
        while (rhosKeyIterator.hasNext()) {
            String key = rhosKeyIterator.next();
            atomicMolarMassesLocal.put(key, atomicMolarMasses.get(key));
        }

        return atomicMolarMassesLocal;
    }

    /**
     *
     *
     * @param modelName
     * @param versionNumber
     * @param minorVersionNumber
     * @param reference
     * @param labName
     * @param comment
     * @param dateCertified
     * @param ratios
     * @param rhos
     * @param atomicMolarMasses
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
            Map<String, BigDecimal> rhos,
            Map<String, BigDecimal> atomicMolarMasses) {

        AbstractRatiosDataModel myModel = modelInstances.get(makeNameAndVersion(modelName, versionNumber, minorVersionNumber));

        if (myModel == null) {
            myModel = new PhysicalConstantsModel(//
                    modelName, //
                    versionNumber, minorVersionNumber,//
                    labName, //
                    dateCertified, //
                    reference, //
                    comment);

            myModel.initializeModel(ratios, rhos, null);

            ((PhysicalConstantsModel) myModel).setAtomicMolarMasses(atomicMolarMasses);

            modelInstances.put(makeNameAndVersion(modelName, versionNumber, minorVersionNumber), myModel);
        }

        return myModel;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel createNewInstance() {

        AbstractRatiosDataModel myModel = new PhysicalConstantsModel(//
                "New Physical Constants Model", //
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

        AbstractRatiosDataModel myModel = new PhysicalConstantsModel(//
                this.modelName, //
                this.versionNumber, this.minorVersionNumber,//
                this.labName, //
                this.dateCertified, //
                this.reference, //
                this.comment);

        myModel.initializeModel(cloneData(), cloneRhosVarUnct(), null);

        return myModel;
    }

    /**
     *
     * @return
     */
    public Map<String, BigDecimal> cloneAtomicMolarMasses() {

        Map<String, BigDecimal> clonedAtomicMolarMasses = new HashMap<String, BigDecimal>();
        for (Map.Entry<String, BigDecimal> entry : atomicMolarMasses.entrySet()) {
            clonedAtomicMolarMasses.put(entry.getKey(), entry.getValue());
        }

        return clonedAtomicMolarMasses;
    }

    /**
     *
     * @param name
     * @return
     */
    public ValueModel getAtomicMolarMassByName(String name) {

        BigDecimal myAtomicMolarMassValue = atomicMolarMasses.get(name);
        if (myAtomicMolarMassValue == null) {
            myAtomicMolarMassValue = BigDecimal.ZERO;
        }

        ValueModel coeffModel
                = new ValueModel(//
                        name,
                        myAtomicMolarMassValue,
                        "NONE",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        return coeffModel;
    }

    /**
     *
     */
    @Override
    public void removeSelf() {
        modelInstances.remove(this.getReduxLabDataElementName());
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
        getNoneInstance();
        getDefaultEARTHTIMEPhysicalConstantsModel();

        loadModelsFromResources(modelInstances);

        ArrayList<AbstractRatiosDataModel> arrayListOfModels = new ReduxLabDataList<>("Physical Constants Models");
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
        xstream.registerConverter(new PhysicalConstantsModelXMLConverter());
        xstream.registerConverter(new ValueModelReferencedXMLConverter());

        xstream.alias("PhysicalConstantsModel", PhysicalConstantsModel.class);
        xstream.alias("ValueModelReferenced", ValueModelReferenced.class);
        xstream.alias("ValueModel", ValueModel.class);

        setClassXMLSchemaURL("URI_PhysicalConstantsModelXMLSchema");
    }

    @Override
    public String getClassNameAliasForXML() {
        return classNameAliasForXML;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        AbstractRatiosDataModel physicalConstantsModel = PhysicalConstantsModel.getDefaultEARTHTIMEPhysicalConstantsModel();

        try {
            ETSerializer.SerializeObjectToFile(physicalConstantsModel, "PhysicalConstantsModelTEST.ser");
        } catch (ETException eTException) {
        }
        AbstractRatiosDataModel physicalConstantsModel2 = (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile("PhysicalConstantsModelTEST.ser");

        String testFileName = "PhysicalConstantsModelTEST.xml";

        physicalConstantsModel2.serializeXMLObject(testFileName);
        try {
            physicalConstantsModel2.readXMLObject(testFileName, true);
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (ETException eTException) {
        } catch (BadOrMissingXMLSchemaException badOrMissingXMLSchemaException) {
        }

        AbstractRatiosDataView testView = new PhysicalConstantsDataViewEditable(PhysicalConstantsModel.getDefaultEARTHTIMEPhysicalConstantsModel(), null, false);

        testView.displayModelInFrame();
    }

//    private void readObject ( ObjectInputStream stream ) throws IOException,
//            ClassNotFoundException {
//        stream.defaultReadObject();
//
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName( PhysicalConstantsModel.class.getCanonicalName() ) );
//        long theSUID = myObject.getSerialVersionUID();
//
//        System.out.println( "Customized De-serialization of PhysicalConstantsModel "
//                + theSUID );
//    }
    /**
     * @return the atomicMolarMasses
     */
    public Map<String, BigDecimal> getAtomicMolarMasses() {
        return atomicMolarMasses;
    }

    /**
     * @param atomicMolarMasses the atomicMolarMasses to set
     */
    public void setAtomicMolarMasses(Map<String, BigDecimal> atomicMolarMasses) {
        this.atomicMolarMasses = atomicMolarMasses;
    }
}
