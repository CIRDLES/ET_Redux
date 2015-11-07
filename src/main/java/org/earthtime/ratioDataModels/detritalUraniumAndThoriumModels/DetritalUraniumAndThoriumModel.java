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
import org.earthtime.dataDictionaries.DetritalUraniumAndThoriumRatiosEnum;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModelXMLConverter;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public class DetritalUraniumAndThoriumModel extends AbstractRatiosDataModel {

    // class variables
    //private static final long serialVersionUID = -5956272382837024475L;
    private static final String classNameAliasForXML = "DetritalUraniumAndThoriumModel";
    private static Map<String, AbstractRatiosDataModel> modelInstances = //
            new HashMap<>();
    private static final AbstractRatiosDataModel noneModel = //
            new DetritalUraniumAndThoriumModel( //
                    ReduxConstants.NONE, //
                    1, 0, //
                    "No Lab",//
                    "2000-01-01",//
                    "Placeholder model",//
                    "Placeholder model");
//    private static final ValueModel[] myRatiosnew =  new ValueModel[3];
//    private static final Map<String, BigDecimal> correlations = new HashMap<>();

//    static {
//        myRatios = new ValueModel[3];
//        myRatios[0] = new ValueModel(//
//                "r206_204b", //
//                new BigDecimal("18.4125383886609"), //
//                "ABS", //
//                new BigDecimal("0.239708852871076"), BigDecimal.ZERO);
//        myRatios[1] = new ValueModel(//
//                "r207_204b", //
//                new BigDecimal("15.4147799644064"), //
//                "ABS", //
//                new BigDecimal("0.145826833918035"), BigDecimal.ZERO);
//        myRatios[2] = new ValueModel(//
//                "r208_204b", //
//                new BigDecimal("37.6140251512604"), //
//                "ABS", //
//                new BigDecimal("0.564703810813301"), BigDecimal.ZERO);
//
//        correlations = new HashMap<>();
//        correlations.put("rhoR206_204b__r207_204b", new BigDecimal("0.754713271298224"));
//        correlations.put("rhoR206_204b__r208_204b", new BigDecimal("0.728547460722919"));
//        correlations.put("rhoR207_204b__r208_204b", new BigDecimal("0.864401000690869"));
//    }
//    private static final AbstractRatiosDataModel EARTHTIMEExampleDetritalUraniumAndThoriumModelInitial = //
//            createInstance(//
//                    "EARTHTIME Example Detrital Uranium And Thorium Model Initial",
//                    3, 0,//
//                    "EARTHTIME",//
//                    "2015-11-01",//
//                    "Noah McLean",//
//                    "EARTHTIME-supplied model",//
//                    myRatios, //
//                    correlations);
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
    protected DetritalUraniumAndThoriumModel(//
            String modelName, int versionNumber, int minorVersionNumber, String labName, String dateCertified, String reference, String comment) {

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
        for (DetritalUraniumAndThoriumRatiosEnum ratio : DetritalUraniumAndThoriumRatiosEnum.values()) {
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
     * @return
     */
    public static AbstractRatiosDataModel getNoneInstance() {
        // guarantee final model
        modelInstances.put(noneModel.getNameAndVersion(), noneModel);
        noneModel.setImmutable(true);
        return noneModel;
    }

//    /**
//     *
//     * @return
//     */
//    public static AbstractRatiosDataModel getEARTHTIMEExampleDetritalUraniumAndThoriumModelInitial() {
//        // guarantee final model
//        modelInstances.put(EARTHTIMEExampleDetritalUraniumAndThoriumModelInitial.getNameAndVersion(), EARTHTIMEExampleDetritalUraniumAndThoriumModelInitial);
//        EARTHTIMEExampleDetritalUraniumAndThoriumModelInitial.setImmutable(true);
//        return EARTHTIMEExampleDetritalUraniumAndThoriumModelInitial;
//    }
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

        return myModel;
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
//        getEARTHTIMEExampleDetritalUraniumAndThoriumModelInitial();

        loadModelsFromResources(modelInstances);

        ArrayList<AbstractRatiosDataModel> arrayListOfModels = new ReduxLabDataList<>("Detrital Uranium And Thorium Model");
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
        xstream.registerConverter(new PbBlankICModelXMLConverter());

        xstream.alias("DetritalUraniumAndThoriumModel", DetritalUraniumAndThoriumModel.class);
        xstream.alias("ValueModel", ValueModel.class);

        setClassXMLSchemaURL("URI_DetritalUraniumAndThoriumModelXMLSchema");
    }

    @Override
    public String getClassNameAliasForXML() {
        return classNameAliasForXML;
    }

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//
//        AbstractRatiosDataModel pbBlankModelETs = DetritalUraniumAndThoriumModel.getEARTHTIMEExampleDetritalUraniumAndThoriumModelInitial();
//
//        try {
//            ETSerializer.SerializeObjectToFile(pbBlankModelETs, "PbBlankICModelTEST.ser");
//        } catch (ETException eTException) {
//        }
//        AbstractRatiosDataModel pbBlankModelET = (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile("PbBlankICModelTEST.ser");
//
//        String testFileName = "PbBlankICModelTEST.xml";
//
//        pbBlankModelET.serializeXMLObject(testFileName);
//        try {
//            pbBlankModelET.readXMLObject(testFileName, true);
//        } catch (FileNotFoundException fileNotFoundException) {
//        } catch (ETException eTException) {
//        } catch (BadOrMissingXMLSchemaException badOrMissingXMLSchemaException) {
//        }
//
//    }
//    private void readObject ( ObjectInputStream stream ) throws IOException,
//            ClassNotFoundException {
//        stream.defaultReadObject();
//
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName( PbBlankICModel.class.getCanonicalName() ) );
//        long theSUID = myObject.getSerialVersionUID();
//
//        System.out.println( "Customized De-serialization of PbBlankICModel "
//                + theSUID );
//    }
}
