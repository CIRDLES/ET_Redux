/*
 * DetritalUraniumAndThoriumModel.java
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
package org.earthtime.ratioDataModels.detritalUraniumAndThoriumModels;

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
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.reduxLabData.ReduxLabDataList;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.DetritalUThRatiosEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public class DetritalUraniumAndThoriumModel extends AbstractRatiosDataModel {

    // class variables
    private static final long serialVersionUID = -1767799823303336291L;
    private static final String classNameAliasForXML = "DetritalUraniumAndThoriumModel";
    private static Map<String, AbstractRatiosDataModel> modelInstances
            = new HashMap<>();

    private static final ValueModel[] myRatios = new ValueModel[3];
    private static final Map<String, BigDecimal> correlations = new HashMap<>();

    static {
        myRatios[0] = new ValueModel(//
                "ar232Th_238U", //
                new BigDecimal(1.2), //
                "ABS", //
                new BigDecimal(0.3), BigDecimal.ZERO);
        myRatios[1] = new ValueModel(//
                "ar230Th_238U", //
                new BigDecimal(1.0), //
                "ABS", //
                new BigDecimal(0.25), BigDecimal.ZERO);
        myRatios[2] = new ValueModel(//
                "ar234U_238U", //
                new BigDecimal(1.0), //
                "ABS", //
                new BigDecimal(0.25), BigDecimal.ZERO);

        correlations.put("rhoAr232Th_238U__ar230Th_238U", new BigDecimal(0.0));
        correlations.put("rhoAr232Th_238U__ar234U_238U", new BigDecimal(0.0));
        correlations.put("rhoAr230Th_238U__ar234U_238U", new BigDecimal(0.5));
    }
    private static final AbstractRatiosDataModel noneModel
            = new DetritalUraniumAndThoriumModel( //
                    ReduxConstants.NONE, //
                    1, 0, //
                    "No Lab",//
                    "2000-01-01",//
                    "Placeholder model",//
                    "Placeholder model");
    private static final AbstractRatiosDataModel SecularEquilibriumDetritalUraniumThoriumInitialModel
            = createInstance(
                    "Secular Equilibrium UTh=3.8",
                    1, 0,//
                    "public domain",//
                    "2017-01-01",//
                    "Noah McLean",//
                    "",//
                    myRatios, //
                    correlations);

    private boolean initial = true;

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
    private DetritalUraniumAndThoriumModel(//
            String modelName,
            int versionNumber,
            int minorVersionNumber,
            String labName,
            String dateCertified,
            String reference,
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
        // DetritalUraniumAndThoriumModel has a defined set of ratios         
        ArrayList<ValueModel> holdRatios = new ArrayList<>();
        for (DetritalUThRatiosEnum ratio : DetritalUThRatiosEnum.values()) {
            holdRatios.add( //
                    new ValueModel(ratio.getName(),
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO,
                            BigDecimal.ZERO));
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
     *
     * @param modelName
     * @param versionNumber
     * @param minorVersionNumber
     * @param labName
     * @param reference
     * @param dateCertified
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
            myModel = new DetritalUraniumAndThoriumModel(//
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

        AbstractRatiosDataModel myModel = new DetritalUraniumAndThoriumModel(//
                "New Detrital Uranium And Thorium Model", //
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
    public static AbstractRatiosDataModel getSecularEquilibriumDetritalUraniumThoriumInitialModelInstance() {
        // guarantee final model
        modelInstances.put(SecularEquilibriumDetritalUraniumThoriumInitialModel.getNameAndVersion(), SecularEquilibriumDetritalUraniumThoriumInitialModel);
        SecularEquilibriumDetritalUraniumThoriumInitialModel.setImmutable(true);
        return SecularEquilibriumDetritalUraniumThoriumInitialModel;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel cloneModel() {

        AbstractRatiosDataModel myModel = new DetritalUraniumAndThoriumModel(//
                this.modelName, //
                this.versionNumber, this.minorVersionNumber,//
                this.labName, //
                this.dateCertified, //
                this.reference, //
                this.comment);

        myModel.initializeModel(cloneData(), cloneRhosVarUnct(), null);
        ((DetritalUraniumAndThoriumModel) myModel).setInitial(initial);

        return myModel;
    }

    /**
     *
     * @return
     */
    public static ArrayList<AbstractRatiosDataModel> getArrayListOfModels() {

        // guarantee final models
        getNoneInstance();
        getSecularEquilibriumDetritalUraniumThoriumInitialModelInstance();

        //loadModelsFromResources(modelInstances);
        ArrayList<AbstractRatiosDataModel> arrayListOfModels = new ReduxLabDataList<>("Detrital Uranium And Thorium Model");
        Iterator<String> modelsKeyInterator = modelInstances.keySet().iterator();
        while (modelsKeyInterator.hasNext()) {
            arrayListOfModels.add(modelInstances.get(modelsKeyInterator.next()));
        }

        Collections.sort(arrayListOfModels);

        return arrayListOfModels;
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

    @Override
    protected void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new DetritalUThModelXMLConverter());

        xstream.alias("DetritalUraniumAndThoriumModel", DetritalUraniumAndThoriumModel.class);
        xstream.alias("ValueModel", ValueModel.class);

        setClassXMLSchemaURL("URI_DetritalUraniumAndThoriumModelXMLSchema");
    }

    @Override
    public String getClassNameAliasForXML() {
        return classNameAliasForXML;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        AbstractRatiosDataModel pbBlankModelETs = DetritalUraniumAndThoriumModel.getSecularEquilibriumDetritalUraniumThoriumInitialModelInstance();
        try {
            ETSerializer.SerializeObjectToFile(pbBlankModelETs, "DetritalUraniumAndThoriumModelTEST.ser");
        } catch (ETException eTException) {
        }
        AbstractRatiosDataModel detritalUraniumAndThoriumModel = (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile("DetritalUraniumAndThoriumModelTEST.ser");

        String testFileName = "DetritalUraniumAndThoriumModelTEST.xml";

        detritalUraniumAndThoriumModel.serializeXMLObject(testFileName);
        try {
            detritalUraniumAndThoriumModel.readXMLObject(testFileName, true);
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (ETException eTException) {
        } catch (BadOrMissingXMLSchemaException badOrMissingXMLSchemaException) {
        }

    }
//    private void readObject(ObjectInputStream stream) throws IOException,
//            ClassNotFoundException {
//        stream.defaultReadObject();
//
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName(DetritalUraniumAndThoriumModel.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//
//        System.out.println("Customized De-serialization of DetritalUraniumAndThoriumModel "
//                + theSUID);
//    }

    /**
     * @return the initial
     */
    public boolean isInitial() {
        return initial;
    }

    /**
     * @param initial the initial to set
     */
    public void setInitial(boolean initial) {
        this.initial = initial;
    }
}
