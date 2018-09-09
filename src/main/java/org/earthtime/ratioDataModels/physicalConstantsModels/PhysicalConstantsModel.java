/*
 * PhysicalConstantsModel.java
 *
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.PhysicalConstantsDataViewEditable;
import org.earthtime.reduxLabData.ReduxLabData;
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
    private static final Map<String, AbstractRatiosDataModel> modelInstances
            = //
            new HashMap<>();
    private static final ValueModel[] myRatios;
    private static final Map<String, BigDecimal> correlations;
    private static final Map<String, BigDecimal> EARTHTIMEatomicMolarMasses;

    static {
        myRatios = new ValueModel[DataDictionary.MeasuredConstants.length];

        correlations = new HashMap<>();

        EARTHTIMEatomicMolarMasses = new TreeMap<>();
        EARTHTIMEatomicMolarMasses.put("gmol204", new BigDecimal("203.973028"));
        EARTHTIMEatomicMolarMasses.put("gmol205", new BigDecimal("204.9737"));
        EARTHTIMEatomicMolarMasses.put("gmol206", new BigDecimal("205.974449"));
        EARTHTIMEatomicMolarMasses.put("gmol207", new BigDecimal("206.975880"));
        EARTHTIMEatomicMolarMasses.put("gmol208", new BigDecimal("207.976636"));
        EARTHTIMEatomicMolarMasses.put("gmol230", new BigDecimal("230.033128"));
        EARTHTIMEatomicMolarMasses.put("gmol232", new BigDecimal("232.038051"));
        EARTHTIMEatomicMolarMasses.put("gmol235", new BigDecimal("235.043922"));
        EARTHTIMEatomicMolarMasses.put("gmol238", new BigDecimal("238.050785"));
    }

    // instance variables
    private Map<String, BigDecimal> atomicMolarMasses;

    private static final AbstractRatiosDataModel noneModel
            = //
            new PhysicalConstantsModel( //
                    ReduxConstants.NONE, //
                    1, 1, //
                    "No Lab",//
                    "2000-01-01",//
                    "Placeholder model",//
                    "Placeholder model");

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

        AbstractRatiosDataModel physicalConstantsModel = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel();

        try {
            ETSerializer.SerializeObjectToFile(physicalConstantsModel, "PhysicalConstantsModelTEST.ser");
        } catch (ETException eTException) {
        }
        AbstractRatiosDataModel physicalConstantsModel2 = (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile("PhysicalConstantsModelTEST.ser");

        String testFileName = "PhysicalConstantsModelTEST.xml";

        physicalConstantsModel2.serializeXMLObject(testFileName);
        try {
            physicalConstantsModel2.readXMLObject(testFileName, true);
        } catch (FileNotFoundException | ETException | BadOrMissingXMLSchemaException fileNotFoundException) {
        }

        AbstractRatiosDataView testView = new PhysicalConstantsDataViewEditable(ReduxLabData.getInstance().getDefaultPhysicalConstantsModel(), null, false);

        testView.displayModelInFrame();
    }

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
