/*
 * TracerUPbModel.java
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
package org.earthtime.ratioDataModels.tracers;

import com.thoughtworks.xstream.XStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.TracerUPbRatiosAndConcentrations;
import org.earthtime.dataDictionaries.TracerUPbTypesEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabDataList;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author James F. Bowring
 */
public class TracerUPbModel extends AbstractRatiosDataModel {

    // class variables
    private static final long serialVersionUID = -3351436105905394538L;
    private static Map<String, AbstractRatiosDataModel> modelInstances = //
            new HashMap<>();
    private static final AbstractRatiosDataModel noneModel = //
            new TracerUPbModel( //
                    ReduxConstants.NONE, //
                    1, 0, //
                    ReduxConstants.NONE, //
                    "No Lab", //
                    "2000-01-01", //
                    "Placeholder model", //
                    "Placeholder model");

    // START ********** ET535Model  **************  ET535Model  ***********  ET535Model *******************
    private static final ValueModel[] myRatiosET535Model = new ValueModel[8];
    private static final Map<String, BigDecimal> correlationsET535Model = new HashMap<>();

    static {
        myRatiosET535Model[0] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r204_205t.getName(), //
                new BigDecimal("0.00009"), //
                "ABS", //
                new BigDecimal("0.000009"), BigDecimal.ZERO);
        myRatiosET535Model[1] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r206_205t.getName(), //
                new BigDecimal("0.000388739872278295"), //
                "ABS", //
                new BigDecimal("0.00016886001521521"), BigDecimal.ZERO);
        myRatiosET535Model[2] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r207_205t.getName(), //
                new BigDecimal("0.000296068587594844"), //
                "ABS", //
                new BigDecimal("0.000140143894670955"), BigDecimal.ZERO);
        myRatiosET535Model[3] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r208_205t.getName(), //
                new BigDecimal("0.000744376162255465"), //
                "ABS", //
                new BigDecimal("0.000347037829188955"), BigDecimal.ZERO);

        // the next four are identical to those for ET2535
        myRatiosET535Model[4] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r233_235t.getName(), //
                new BigDecimal("0.9950621757"), //
                "ABS", //
                new BigDecimal("0.0000538364810724104"), BigDecimal.ZERO);
        myRatiosET535Model[5] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r238_235t.getName(), //
                new BigDecimal("0.0030799297"), //
                "ABS", //
                new BigDecimal("3.95548132250173E-07"), BigDecimal.ZERO);
        myRatiosET535Model[6] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.concPb205t.getName(), //
                new BigDecimal("0.0000000000103116"), //
                "ABS", //
                new BigDecimal("0.000000000000025779"), BigDecimal.ZERO);
        myRatiosET535Model[7] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.concU235t.getName(), //
                new BigDecimal("1.03356161714094E-09"), //
                "ABS", //
                new BigDecimal("2.59506527095644E-12"), BigDecimal.ZERO);

        correlationsET535Model.put("rhoR204_205t__r206_205t", new BigDecimal("0.981367466390299"));
        correlationsET535Model.put("rhoR204_205t__r207_205t", new BigDecimal("0.989936347334727"));
        correlationsET535Model.put("rhoR204_205t__r208_205t", new BigDecimal("0.975476664128529"));
        correlationsET535Model.put("rhoR204_205t__concU235t", new BigDecimal("-0.000601507547689546"));

        correlationsET535Model.put("rhoR206_205t__r207_205t", new BigDecimal("0.992109313058621"));
        correlationsET535Model.put("rhoR206_205t__r208_205t", new BigDecimal("0.988226606955946"));
        correlationsET535Model.put("rhoR206_205t__concU235t", new BigDecimal("-0.000625524583951292"));

        correlationsET535Model.put("rhoR207_205t__r208_205t", new BigDecimal("0.992617413594994"));
        correlationsET535Model.put("rhoR207_205t__concU235t", new BigDecimal("-0.000637072197103421"));

        correlationsET535Model.put("rhoR208_205t__concU235t", new BigDecimal("-0.000564938004436441"));

        // the next four are identical to those for ET2535
        correlationsET535Model.put("rhoR233_235t__r238_235t", new BigDecimal("-0.593855080520382"));
        correlationsET535Model.put("rhoR233_235t__concU235t", new BigDecimal("0.0329317147303595"));

        correlationsET535Model.put("rhoR238_235t__concU235t", new BigDecimal("-0.0213714959352477"));

        correlationsET535Model.put("rhoConcPb205t__concU235t", new BigDecimal("0.995699056887309"));

    }
    private static final AbstractRatiosDataModel ET535Model = //
            createInstance(//
                    "ET535",
                    3, 0,//
                    "mixed 205-233-235",//
                    "EARTHTIME",//
                    "2012-07-09",
                    "No reference",//
                    "EARTHTIME-supplied model",//
                    myRatiosET535Model, //
                    correlationsET535Model);

    // END ********** ET535Model  **************  ET535Model  ***********  ET535Model *******************
    //
    // START ********** ET2535Model  **************  ET2535Model  ***********  ET2535Model *******************
    private static final ValueModel[] myRatiosET2535Model = new ValueModel[9];
    private static final Map<String, BigDecimal> correlationsET2535Model = new HashMap<String, BigDecimal>();

    static {
        myRatiosET2535Model[0] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r202_205t.getName(), //
                new BigDecimal("0.999239128056085"), //
                "ABS", //
                new BigDecimal("0.000265554717724025"), BigDecimal.ZERO);
        myRatiosET2535Model[1] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r204_205t.getName(), //
                new BigDecimal("0.000105"), //
                "ABS", //
                new BigDecimal("9.15843141929646E-06"), BigDecimal.ZERO);
        myRatiosET2535Model[2] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r206_205t.getName(), //
                new BigDecimal("0.000482509449698359"), //
                "ABS", //
                new BigDecimal("0.000166042236484442"), BigDecimal.ZERO);
        myRatiosET2535Model[3] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r207_205t.getName(), //
                new BigDecimal("0.000432369168809505"), //
                "ABS", //
                new BigDecimal("0.000137658376026938"), BigDecimal.ZERO);
        myRatiosET2535Model[4] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r208_205t.getName(), //
                new BigDecimal("0.00104222718281"), //
                "ABS", //
                new BigDecimal("0.000334948793014228"), BigDecimal.ZERO);

        // the next four are identical to those for ET535
        myRatiosET2535Model[5] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r233_235t.getName(), //
                new BigDecimal("0.9950621757"), //
                "ABS", //
                new BigDecimal("0.0000538364810724104"), BigDecimal.ZERO);
        myRatiosET2535Model[6] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.r238_235t.getName(), //
                new BigDecimal("0.0030799297"), //
                "ABS", //
                new BigDecimal("3.95548132250173E-07"), BigDecimal.ZERO);
        myRatiosET2535Model[7] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.concPb205t.getName(), //
                new BigDecimal("0.0000000000103116"), //
                "ABS", //
                new BigDecimal("0.000000000000025779"), BigDecimal.ZERO);
        myRatiosET2535Model[8] = new ValueModel(//
                TracerUPbRatiosAndConcentrations.concU235t.getName(), //
                new BigDecimal("1.03356161714094E-09"), //
                "ABS", //
                new BigDecimal("2.59506527095644E-12"), BigDecimal.ZERO);

        correlationsET2535Model.put("rhoR202_205t__r204_205t", new BigDecimal("0.00739680731141555"));
        correlationsET2535Model.put("rhoR202_205t__r206_205t", new BigDecimal("0.00807546785387666"));
        correlationsET2535Model.put("rhoR202_205t__r207_205t", new BigDecimal("0.00794136244518988"));
        correlationsET2535Model.put("rhoR202_205t__r208_205t", new BigDecimal("0.00773502668653982"));
        correlationsET2535Model.put("rhoR202_205t__r233_235t", new BigDecimal("-0.0140916024945161"));
        correlationsET2535Model.put("rhoR202_205t__r288_235t", new BigDecimal("0.0265871068016038"));
        correlationsET2535Model.put("rhoR202_205t__concU235t", new BigDecimal("-0.0847323000860479"));

        correlationsET2535Model.put("rhoR204_205t__r206_205t", new BigDecimal("0.998212278075014"));
        correlationsET2535Model.put("rhoR204_205t__r207_205t", new BigDecimal("0.99902225892771"));
        correlationsET2535Model.put("rhoR204_205t__r208_205t", new BigDecimal("0.997578360594083"));
        correlationsET2535Model.put("rhoR204_205t__concU235t", new BigDecimal("-0.000591102087394544"));

        correlationsET2535Model.put("rhoR206_205t__r207_205t", new BigDecimal("0.999242385302839"));
        correlationsET2535Model.put("rhoR206_205t__r208_205t", new BigDecimal("0.998839747943053"));
        correlationsET2535Model.put("rhoR206_205t__concU235t", new BigDecimal("-0.000636139894281656"));

        correlationsET2535Model.put("rhoR207_205t__r208_205t", new BigDecimal("0.99925971261631"));
        correlationsET2535Model.put("rhoR207_205t__concU235t", new BigDecimal("-0.00064857498297949"));

        correlationsET2535Model.put("rhoR208_205t__concU235t", new BigDecimal("-0.000585327855406347"));

        // the next four are identical to those for ET535 
        correlationsET2535Model.put("rhoR233_235t__r238_235t", new BigDecimal("-0.593855080520382"));
        correlationsET2535Model.put("rhoR233_235t__concU235t", new BigDecimal("0.0329317147303595"));

        correlationsET2535Model.put("rhoR238_235t__concU235t", new BigDecimal("-0.0213714959352477"));

        correlationsET2535Model.put("rhoConcPb205t__concU235t", new BigDecimal("0.995699056887309"));
    }
    private static final AbstractRatiosDataModel ET2535Model = //
            createInstance(//
                    "ET2535",
                    3, 0, //
                    "mixed 202-205-233-235",//
                    "EARTHTIME",//
                    "2012-07-09",
                    "No reference",//
                    "EARTHTIME-supplied model",//
                    myRatiosET2535Model, //
                    correlationsET2535Model);
    // END ********** ET2535Model  **************  ET2535Model  ***********  ET2535Model *******************
    private static final String classNameAliasForXML = "TracerUPbModel";
    // instance variables
    private String tracerType;

    private TracerUPbModel(//
            String modelName, int versionNumber, int minorVersionNumber, String tracerType, String labName, String dateCertified, String reference, String comment) {

        super(modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment);

        this.tracerType = tracerType;

        initializeNewRatiosAndRhos(false);

    }

    /**
     *
     * @param updateOnly
     */
    @Override
    public final void initializeNewRatiosAndRhos(boolean updateOnly) {
        ArrayList<ValueModel> holdRatios = new ArrayList<ValueModel>();
        for (TracerUPbRatiosAndConcentrations ratioSpecification : TracerUPbRatiosAndConcentrations.values()) {
            // handle  #NONE#
            if (!tracerType.equalsIgnoreCase(ReduxConstants.NONE)) {
                if (!TracerUPbTypesEnum.valueFromName(tracerType).excludesRatioOrConcentration(ratioSpecification)) {
                    ValueModel existingRatio = getDatumByName(ratioSpecification.getName());
                    if (updateOnly && (existingRatio != null)) {
                        holdRatios.add(existingRatio);
                    } else {

                        holdRatios.add(new ValueModel( //
                                //
                                ratioSpecification.getName(),
                                BigDecimal.ZERO,
                                "ABS",
                                BigDecimal.ZERO, BigDecimal.ZERO));
                    }
                }
            }
        }

        ratios = holdRatios.toArray(new ValueModel[holdRatios.size()]);

        Arrays.sort(ratios, new DataValueModelNameComparator());

        buildRhosMap();
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
     * @param modelName
     * @param versionNumber
     * @param minorVersionNumber
     * @param tracerType
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
            int versionNumber,//
            int minorVersionNumber, //
            String tracerType,//
            String labName,//
            String dateCertified,//
            String reference, //
            String comment, //
            ValueModel[] ratios,//
            Map<String, BigDecimal> rhos) {

        AbstractRatiosDataModel myModel = modelInstances.get(makeNameAndVersion(modelName, versionNumber, minorVersionNumber));

        if (myModel == null) {
            myModel = new TracerUPbModel(//
                    modelName,//
                    versionNumber, //
                    minorVersionNumber,//
                    tracerType,//
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

        AbstractRatiosDataModel myModel = new TracerUPbModel(//
                "New Tracer UPb Model",//
                1, 0, DataDictionary.TracerType[0], //
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
    public static AbstractRatiosDataModel getET535ModelInstance() {
        // guarantee final model
        modelInstances.put(ET535Model.getNameAndVersion(), ET535Model);
        ET535Model.setImmutable(true);
        return ET535Model;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel getET2535ModelInstance() {
        // guarantee final model
        modelInstances.put(ET2535Model.getNameAndVersion(), ET2535Model);
        ET2535Model.setImmutable(true);
        return ET2535Model;
    }

    /**
     *
     * @return
     */
    public static ArrayList<AbstractRatiosDataModel> getArrayListOfModels() {

        // guarantee final models
        getNoneInstance();
        getET535ModelInstance();
        getET2535ModelInstance();
//        modelInstances.put( noneModel.getNameAndVersion(), noneModel );
//        modelInstances.put( ET535Model.getNameAndVersion(), ET535Model );
//        modelInstances.put( ET2535Model.getNameAndVersion(), ET2535Model );

        ArrayList<AbstractRatiosDataModel> arrayListOfModels = //
                new ReduxLabDataList<>("Tracer");
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

    @Override
    public AbstractRatiosDataModel cloneModel() {

        AbstractRatiosDataModel myModel = new TracerUPbModel(//
                this.modelName,//
                this.versionNumber,
                this.minorVersionNumber,
                this.tracerType,
                this.labName, //
                this.dateCertified, //
                this.reference, //
                this.comment);

        myModel.initializeModel(cloneData(), cloneRhosVarUnct(), null);

        return myModel;
    }

    @Override
    protected void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new TracerUPbModelXMLConverter());

        xstream.alias("TracerUPbModel", TracerUPbModel.class);
        xstream.alias("ValueModel", ValueModel.class);

        setClassXMLSchemaURL("URI_TracerUPbModelXMLSchema");
    }

    @Override
    public String getClassNameAliasForXML() {
        return classNameAliasForXML;
    }

    @Override
    public void removeSelf() {
        modelInstances.remove(this.getReduxLabDataElementName());
    }

    /**
     * @return the tracerType
     */
    public String getTracerType() {
        return tracerType;
    }

    /**
     * @param tracerType the tracerType to set
     */
    public void setTracerType(String tracerType) {
        this.tracerType = tracerType;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        AbstractRatiosDataModel tracerUPbModel = TracerUPbModel.getET535ModelInstance();

        String testFileName = "TracerUPbModelTEST.xml";

        tracerUPbModel.serializeXMLObject(testFileName);
        try {
            tracerUPbModel.readXMLObject(testFileName, true);
        } catch (FileNotFoundException | ETException | BadOrMissingXMLSchemaException fileNotFoundException) {
        }
    }

}
