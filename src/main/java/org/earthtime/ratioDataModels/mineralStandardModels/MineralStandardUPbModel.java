/*
 * MineralStandardUPbModel.java
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
package org.earthtime.ratioDataModels.mineralStandardModels;

import Jama.Matrix;
import com.thoughtworks.xstream.XStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataList;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age206_238r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_206r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_235r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age208_232r;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.MineralStandardUPbConcentrationsPPMEnum;
import org.earthtime.dataDictionaries.MineralStandardUPbRatiosEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelETXMLConverter;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public class MineralStandardUPbModel extends AbstractRatiosDataModel {

    // class variables
    private static final long serialVersionUID = -5845209084226246480L;
    private static Map<String, AbstractRatiosDataModel> modelInstances = //
            new HashMap<>();
    private static final AbstractRatiosDataModel noneModel = //
            new MineralStandardUPbModel( //
                    ReduxConstants.NONE, //
                    1, 0,//
                    "No Lab", //
                    "2000-01-01", //
                    "Placeholder model", "Placeholder model",//
                    "Placeholder model", "Placeholder model",
                    InitialPbModelET.getNoneInstance());

    private static final ValueModel[] myRatios = new ValueModel[5];
    private static final Map<String, BigDecimal> correlations = new HashMap<>();
    private static final ValueModel[] myConcentrationsPPM = new ValueModel[2];
    private ValueModel[] concentrationsPPM;

    // per Noah McLean October 2012
    // start EARTHTIMESriLankaStandardModel
    static {
        myRatios[0] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_207r.getName(), //
                new BigDecimal(16.9432435810912), //
                "ABS", //
                new BigDecimal(Double.toString(0.03149408314927265)),//
                true);
        myRatios[1] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_208r.getName(), //
                new BigDecimal(27.80), //
                "ABS", //
                new BigDecimal(Double.toString(0.4618800182829345)),//
                true);
        myRatios[2] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_238r.getName(), //
                new BigDecimal(0.09130), //
                "ABS", //
                new BigDecimal(Double.toString(0.000200520325346011)),//
                true);
        myRatios[3] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r208_232r.getName(), //
                new BigDecimal(Double.toString(0.028175744521)),//
                "ABS", //
                new BigDecimal(Double.toString(0.000061881812248)),//
                false);
        myRatios[4] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r238_235s.getName(), //
                new BigDecimal(137.818), //
                "ABS", //
                new BigDecimal(Double.toString(0.0225)),//
                true);

        correlations.put("rhoR206_207r__r206_238r", new BigDecimal("-0.0400671215735759"));

        // dec 2014 for LAICPMS
        myConcentrationsPPM[0] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concU238ppm.getName(), //
                new BigDecimal("564"), //
                "ABS", //
                new BigDecimal("10"), BigDecimal.ZERO);

        myConcentrationsPPM[1] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concTh232ppm.getName(), //
                new BigDecimal("150"), //
                "ABS", //
                new BigDecimal("10"), BigDecimal.ZERO);

    }
    private static final AbstractRatiosDataModel EARTHTIMESriLankaStandardModel = //
            createInstance("EARTHTIME SriLanka Standard",
                    1, 0,//
                    "EARTHTIME",//
                    "2012-04-01",
                    "Gehrels, G. E., V. A. Valencia, and J. Ruiz (2008), \nEnhanced precision, accuracy, efficiency, and spatial resolution of U-Pb ages by laser ablation.",//
                    "EARTHTIME-supplied model. 208Pb/232Th is calculated from measured 206Pb/238U using EARTHTIME physical constants v.1 and assuming concordance between the two systems.",//
                    myRatios, //
                    correlations, //
                    myConcentrationsPPM,//
                    "Sri Lanka",
                    "zircon",
                    InitialPbModelET.getEARTHTIMESriLankaInitialPbModel());

    // start EARTHTIMEPeixeStandardModel
    static {
        myRatios[0] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_207r.getName(), //
                new BigDecimal(Double.toString(16.9656343433015)), //
                "ABS", //
                new BigDecimal(Double.toString(0.0311547 / 2.0)),//
                true);
        myRatios[1] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_208r.getName(), //
                BigDecimal.ZERO, //
                "ABS", //
                BigDecimal.ZERO, //
                false);
        myRatios[2] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_238r.getName(), //
                new BigDecimal(Double.toString(0.091431895790691)), //
                "ABS", //
                new BigDecimal(Double.toString(0.000677233 / 2.0)),//
                true);
        myRatios[3] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r208_232r.getName(), //
                new BigDecimal(Double.toString(0.028215263290634)), //
                "ABS", //
                new BigDecimal(Double.toString(0.000202905 / 2.0)),//
                true);
        myRatios[4] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r238_235s.getName(), //
                new BigDecimal(Double.toString(137.818)), //
                "ABS", //
                new BigDecimal(Double.toString(0.0225)),//
                true);

        correlations.put("rhoR206_207r__r206_238r", new BigDecimal(Double.toString(-0.0400671215735759)));

        // dec 2014 for LAICPMS
        myConcentrationsPPM[0] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concU238ppm.getName(), //
                BigDecimal.ZERO, //
                "ABS", //
                BigDecimal.ZERO, BigDecimal.ZERO);

        myConcentrationsPPM[1] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concTh232ppm.getName(), //
                BigDecimal.ZERO, //
                "ABS", //
                BigDecimal.ZERO, BigDecimal.ZERO);

    }
    private static final AbstractRatiosDataModel EARTHTIMEPeixeStandardModel = //
            createInstance("EARTHTIME Peixe Standard",
                    1, 0,//
                    "EARTHTIME",//
                    "2012-04-01",
                    "No reference",//
                    "EARTHTIME-supplied model",//
                    myRatios, //
                    correlations,
                    myConcentrationsPPM,//
                    "Peixe",
                    "zircon",
                    InitialPbModelET.getEARTHTIMESriLankaInitialPbModel());

    // start EARTHTIMEPlesoviceStandardModel
    static {
        myRatios[0] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_207r.getName(), //
                new BigDecimal(Double.toString(018.6675)), //
                "ABS", //
                new BigDecimal(Double.toString(0.0933376)),//
                true);
        myRatios[1] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_208r.getName(), //
                BigDecimal.ZERO, //28.57
                "ABS", //
                BigDecimal.ZERO, //0.1429
                false);
        myRatios[2] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_238r.getName(), //
                new BigDecimal(Double.toString(0.05368)), //
                "ABS", //
                new BigDecimal(Double.toString(0.0002684)),//
                true);
        myRatios[3] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r208_232r.getName(), //
                BigDecimal.ZERO, //
                "ABS", //
                BigDecimal.ZERO,//
                false);
        myRatios[4] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r238_235s.getName(), //
                new BigDecimal(Double.toString(137.818)), //
                "ABS", //
                new BigDecimal(Double.toString(0.0450 / 2.0)),//
                true);

        correlations.put("rhoR206_207r__r206_238r", new BigDecimal(Double.toString(0.5)));

        // dec 2014 for LAICPMS
        myConcentrationsPPM[0] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concU238ppm.getName(), //
                BigDecimal.ZERO, //
                "ABS", //
                BigDecimal.ZERO, BigDecimal.ZERO);

        myConcentrationsPPM[1] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concTh232ppm.getName(), //
                BigDecimal.ZERO, //
                "ABS", //
                BigDecimal.ZERO, BigDecimal.ZERO);

    }
    private static final AbstractRatiosDataModel EARTHTIMEPlesoviceStandardModel = //
            createInstance("EARTHTIME Plesovice Standard",
                    1, 0,//
                    "EARTHTIME",//
                    "2012-04-01",
                    "Chris Fisher notes",//
                    "EARTHTIME-supplied model temporary Nov 2012",//
                    myRatios, //
                    correlations, myConcentrationsPPM,//
                    "Plesovice",
                    "zircon",
                    null);

    private static final String classNameAliasForXML = "MineralStandardUPbModel";

    // instance variables
    private ValueModel[] apparentDates;
    private ConcurrentMap<String, BigDecimal> parDerivTerms = new ConcurrentHashMap<>();
    private String mineralStandardName;
    private String mineralName;
    private AbstractRatiosDataModel initialPbModelET;

    private MineralStandardUPbModel(//
            String modelName, //
            int versionNumber,//
            int minorVersionNumber, //
            String labName, //
            String dateCertified,//
            String reference,//
            String comment,//
            String mineralStandardName,//
            String mineralName, //
            AbstractRatiosDataModel initialPbModelET) {

        super(modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment);

        this.mineralStandardName = mineralStandardName;
        this.mineralName = mineralName;
        this.initialPbModelET = initialPbModelET;
        if (initialPbModelET == null) {
            this.initialPbModelET = InitialPbModelET.getNoneInstance();
        }

        this.apparentDates = new ValueModel[0];
        this.parDerivTerms = new ConcurrentHashMap<>();

        initializeNewRatiosAndRhos(false);

        // dec 2014 not sure if this is final architecture
        initConcentrations();

    }

    /**
     *
     * @param updateOnly
     */
    @Override
    public final void initializeNewRatiosAndRhos(boolean updateOnly) {
        ArrayList<ValueModel> holdRatios = new ArrayList<ValueModel>();
        for (MineralStandardUPbRatiosEnum ratio : MineralStandardUPbRatiosEnum.values()) {
            holdRatios.add( //
                    new MineralStandardUPbRatioModel(ratio.getName(),
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO,
                            true));
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
     * @param dateCertified
     * @param reference
     * @param comment
     * @param ratios
     * @param rhos
     * @param concentrations the value of myConcentrationsPPM
     * @param mineralStandardName
     * @param mineralName
     * @param initialPbModelET the value of initialPbModelET
     * @return the org.earthtime.ratioDataModels.AbstractRatiosDataModel
     */
    public static AbstractRatiosDataModel createInstance(//
            String modelName, //
            int versionNumber, //
            int minorVersionNumber,//
            String labName,//
            String dateCertified,//
            String reference,//
            String comment,//
            ValueModel[] ratios,//
            Map<String, BigDecimal> rhos, //
            ValueModel[] concentrations,//
            String mineralStandardName, //
            String mineralName,//
            AbstractRatiosDataModel initialPbModelET) {

        AbstractRatiosDataModel myModel = modelInstances.get(makeNameAndVersion(modelName, versionNumber, minorVersionNumber));

        if (myModel == null) {
            myModel = new MineralStandardUPbModel(//
                    modelName,//
                    versionNumber, //
                    minorVersionNumber, //
                    labName, //
                    dateCertified, //
                    reference, //
                    comment, //
                    mineralStandardName, //
                    mineralName, //
                    initialPbModelET);

            myModel.initializeModel(ratios, rhos, null);

            if (concentrations != null) {
                ((MineralStandardUPbModel) myModel).initializeConcentrations(concentrations);
            }

            modelInstances.put(makeNameAndVersion(modelName, versionNumber, minorVersionNumber), myModel);
        }

        return myModel;
    }

    private void initializeConcentrations(ValueModel[] dataIncoming) {

        for (int i = 0; i < dataIncoming.length; i++) {
            ValueModel conc = getConcentrationByName(dataIncoming[i].getName());

            if (conc != null) {
                conc.copyValuesFrom(dataIncoming[i]);
            }
        }
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel createNewInstance() {

        AbstractRatiosDataModel myModel = new MineralStandardUPbModel(//
                "New Mineral Standard UPb Model", //
                1, 0, //
                "No Lab", //
                DateHelpers.defaultEarthTimeDateString(), //
                "No reference", //
                "No comment",//
                "Some Mineral Standard",//
                "Some Mineral",
                InitialPbModelET.getNoneInstance());

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
    public static AbstractRatiosDataModel getEARTHTIMESriLankaStandardModelInstance() {
        // guarantee final model
        modelInstances.put(EARTHTIMESriLankaStandardModel.getNameAndVersion(), EARTHTIMESriLankaStandardModel);
        EARTHTIMESriLankaStandardModel.setImmutable(true);
        return EARTHTIMESriLankaStandardModel;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel getEARTHTIMEPeixeStandardModelInstance() {
        // guarantee final model
        modelInstances.put(EARTHTIMEPeixeStandardModel.getNameAndVersion(), EARTHTIMEPeixeStandardModel);
        EARTHTIMEPeixeStandardModel.setImmutable(true);
        return EARTHTIMEPeixeStandardModel;
    }

    /**
     *
     * @return
     */
    public static AbstractRatiosDataModel getEARTHTIMEPlesoviceStandardModelInstance() {
        // guarantee final model
        modelInstances.put(EARTHTIMEPlesoviceStandardModel.getNameAndVersion(), EARTHTIMEPlesoviceStandardModel);
        EARTHTIMEPlesoviceStandardModel.setImmutable(true);
        return EARTHTIMEPlesoviceStandardModel;
    }

    /**
     *
     * @return
     */
    public static ArrayList<AbstractRatiosDataModel> getArrayListOfModels() {

        // guarantee final models
        getNoneInstance();
        getEARTHTIMEPeixeStandardModelInstance();
        getEARTHTIMESriLankaStandardModelInstance();
        getEARTHTIMEPlesoviceStandardModelInstance();

        loadModelsFromResources();

        ArrayList<AbstractRatiosDataModel> arrayListOfModels = new ReduxLabDataList<>("Mineral Standard Model");
        Iterator<String> modelsKeyInterator = modelInstances.keySet().iterator();
        while (modelsKeyInterator.hasNext()) {
            arrayListOfModels.add(modelInstances.get(modelsKeyInterator.next()));
        }

        Collections.sort(arrayListOfModels);

        return arrayListOfModels;
    }

    private static void loadModelsFromResources() {
        URL modelsLoc = MineralStandardUPbModel.class.getClassLoader().getResource("org/earthtime/parameterModels/mineralStandardModels");

        File modelsDir = null;
        try {
            modelsDir = new File(modelsLoc.toURI());
            FilenameFilter textFilter = (File dir, String name) -> {
                return name.toLowerCase().endsWith(".xml");
            };

            File[] modelFiles = modelsDir.listFiles(textFilter);
            for (File modelFile : modelFiles) {
                System.out.println("MODEL FOUND: " + modelFile.getAbsoluteFile());
                AbstractRatiosDataModel mineralStandardModel = MineralStandardUPbModel.getNoneInstance();

                try {
                    mineralStandardModel = mineralStandardModel.readXMLObject(modelFile.getCanonicalPath(), true);
                    modelInstances.put(mineralStandardModel.getNameAndVersion(), mineralStandardModel);
                    mineralStandardModel.setImmutable(true);
                } catch (IOException | ETException | BadOrMissingXMLSchemaException ex) {
                    if (ex instanceof ETException) {
                        new ETWarningDialog((ETException) ex).setVisible(true);
                    }
                    mineralStandardModel = null;
                }

            }
        } catch (URISyntaxException uRISyntaxException) {

        }

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

        AbstractRatiosDataModel myModel = new MineralStandardUPbModel(//
                this.modelName, //
                this.versionNumber,
                this.minorVersionNumber, //
                this.labName, //
                this.dateCertified, //
                this.reference, //
                this.comment, //
                this.mineralStandardName,//
                this.mineralName,
                this.initialPbModelET);

        myModel.initializeModel(cloneData(), cloneRhosVarUnct(), null);

        ((MineralStandardUPbModel) myModel).setConcentrationsPPM(this.getConcentrationsPPM().clone());

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
        this.apparentDates = new ValueModel[0];
        this.parDerivTerms = new ConcurrentHashMap<>();

        // dec 2014 not sure if this is final architecture
        if (concentrationsPPM == null) {
            initConcentrations();
        }

        super.initializeModel();
    }

    private void initConcentrations() {
        concentrationsPPM = new ValueModel[2];
        concentrationsPPM[0] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concU238ppm.getName(), //
                BigDecimal.ZERO, //
                "ABS", //
                BigDecimal.ZERO, BigDecimal.ZERO);

        concentrationsPPM[1] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concTh232ppm.getName(), //
                BigDecimal.ZERO, //
                "ABS", //
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     *
     */
    public void calculateApparentDates() {

        ValueModel lambda232 = new ValueModel();
        try {
            lambda232 = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel()//
                    .getDatumByName(Lambdas.lambda232.getName());
        } catch (BadLabDataException badLabDataException) {
        }

        ValueModel lambda235 = new ValueModel();
        try {
            lambda235 = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel()//
                    .getDatumByName(Lambdas.lambda235.getName());
        } catch (BadLabDataException badLabDataException) {
        }

        ValueModel lambda238 = new ValueModel();
        try {
            lambda238 = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel()//
                    .getDatumByName(Lambdas.lambda238.getName());
        } catch (BadLabDataException badLabDataException) {
        }

        ValueModel r206_238r = getDatumByName(MineralStandardUPbRatiosEnum.r206_238r.getName());

        // make inverted ratio
        ValueModel r206_207r = getDatumByName(MineralStandardUPbRatiosEnum.r206_207r.getName());
        BigDecimal invertedR206_207r = BigDecimal.ZERO;
        try {
            invertedR206_207r = BigDecimal.ONE.divide(r206_207r.getValue(), ReduxConstants.mathContext15);
        } catch (Exception e) {
        }

        ValueModel r207_206r = new ValueModel("r207_206r");
        r207_206r.setValue(invertedR206_207r);
        // percent uncertainty is constant between ratio and invert
        r207_206r.setUncertaintyType("PCT");
        r207_206r.setOneSigma(r206_207r.getOneSigmaPct());

        ValueModel r238_235s = getDatumByName(MineralStandardUPbRatiosEnum.r238_235s.getName());

        ValueModel r207_235r = new ValueModel("r207_235r");
        try {
            r207_235r.setValue(r238_235s.getValue()//
                    .multiply(r206_238r.getValue()//
                            .divide(r206_207r.getValue(), ReduxConstants.mathContext15)));

            // calculate uncertainty
            double[][] v = new double[1][dataCovariancesVarUnct.getRows().size()];
            Iterator<Integer> rowKeys = dataCovariancesVarUnct.getRows().keySet().iterator();
            while (rowKeys.hasNext()) {
                int rowKey = rowKeys.next();

                if (dataCovariancesVarUnct.getRows().get(rowKey).equalsIgnoreCase(//
                        MineralStandardUPbRatiosEnum.r206_207r.getName())) {
                    v[0][rowKey] = r206_238r.getValue()//
                            .multiply(r238_235s.getValue())//
                            .divide(r206_207r.getValue().pow(2), ReduxConstants.mathContext15)//
                            .doubleValue();
                }

                if (dataCovariancesVarUnct.getRows().get(rowKey).equalsIgnoreCase(//
                        MineralStandardUPbRatiosEnum.r206_208r.getName())) {
                    v[0][rowKey] = 0.0;
                }

                if (dataCovariancesVarUnct.getRows().get(rowKey).equalsIgnoreCase(//
                        MineralStandardUPbRatiosEnum.r206_238r.getName())) {
                    v[0][rowKey] = r238_235s.getValue()//
                            .divide(r206_207r.getValue(), ReduxConstants.mathContext15).doubleValue();
                }

                if (dataCovariancesVarUnct.getRows().get(rowKey).equalsIgnoreCase(//
                        MineralStandardUPbRatiosEnum.r208_232r.getName())) {
                    v[0][rowKey] = 0.0;
                }

                if (dataCovariancesVarUnct.getRows().get(rowKey).equalsIgnoreCase(//
                        MineralStandardUPbRatiosEnum.r238_235s.getName())) {
                    v[0][rowKey] = r206_238r.getValue()//
                            .divide(r206_207r.getValue(), ReduxConstants.mathContext15).doubleValue();
                }
            }

            Matrix V = new Matrix(v);

            double varianceR207_235r = V.times(dataCovariancesVarUnct.getMatrix()).times(V.transpose()).get(0, 0);

            r207_235r.setOneSigma(new BigDecimal(Math.sqrt(varianceR207_235r)));

        } catch (Exception e) {
        }

        ValueModel r208_232r = getDatumByName(MineralStandardUPbRatiosEnum.r208_232r.getName());

        apparentDates = new ValueModel[4];

        apparentDates[0] = new Age206_238r();
        getApparentDates()[0].calculateValue(
                new ValueModel[]{
                    r206_238r,
                    lambda238}, parDerivTerms);
        apparentDates[0].setOneSigma(//
                parDerivTerms.get("dAge206_238r__dR206_238r") //
                .abs()//
                .multiply(r206_238r.getOneSigmaAbs()));

        apparentDates[1] = new Age207_206r();
        getApparentDates()[1].calculateValue(
                new ValueModel[]{
                    r238_235s,
                    r207_206r,
                    apparentDates[0],
                    lambda235,
                    lambda238}, parDerivTerms);
        apparentDates[1].setOneSigma(//
                parDerivTerms.get("dAge207_206r__dR207_206r")//
                .abs()//
                .multiply(r207_206r.getOneSigmaAbs()));

        apparentDates[2] = new Age207_235r();
        getApparentDates()[2].calculateValue(
                new ValueModel[]{
                    r207_235r,
                    lambda235}, parDerivTerms);
        apparentDates[2].setOneSigma(//
                parDerivTerms.get("dAge207_235r__dR207_235r")//
                .abs()//
                .multiply(r207_235r.getOneSigmaAbs()));//******************************calc this with matrices

        apparentDates[3] = new Age208_232r();
        getApparentDates()[3].calculateValue(
                new ValueModel[]{
                    r208_232r,
                    lambda232}, parDerivTerms);
        apparentDates[3].setOneSigma(//
                parDerivTerms.get("dAge208_232r__dR208_232r")//
                .abs()//
                .multiply(r208_232r.getOneSigmaAbs()));
    }

    /**
     *
     * @return
     */
    public String listFormattedApparentDatesHTML() {
        calculateApparentDates();

        String retVal = "<html>";

        for (int i = 0; i < apparentDates.length; i++) {
            if (apparentDates[i].hasPositiveValue()) {
                retVal += "<br>" //
                        + apparentDates[i].getName() + " : " + apparentDates[i].formatValueAndTwoSigmaForPublicationSigDigMode( //
                                "ABS", -6, 2) //
                        + " (2\u03C3)  Ma";
            }
        }

        return retVal + "</html>";
    }

    @Override
    protected void customizeXstream(XStream xstream) {
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new MineralStandardUPbRatioModelXMLConverter());
        xstream.registerConverter(new InitialPbModelETXMLConverter());
        xstream.registerConverter(new MineralStandardUPbModelXMLConverter());

        xstream.alias("MineralStandardUPbModel", MineralStandardUPbModel.class);
        xstream.alias("ValueModel", ValueModel.class);
        xstream.alias("MineralStandardUPbRatioModel", MineralStandardUPbRatioModel.class);
        xstream.alias("InitialPbModelET", InitialPbModelET.class);

        setClassXMLSchemaURL("URI_MineralStandardUPbModelXMLSchemaURL");
    }

    /**
     * @return the mineralStandardName
     */
    public String getMineralStandardName() {
        return mineralStandardName;
    }

    /**
     * @return the mineralName
     */
    public String getMineralName() {
        return mineralName;
    }

    @Override
    public String getClassNameAliasForXML() {
        return classNameAliasForXML;
    }

    /**
     *
     * @return
     */
    public boolean hasInitialPb() {
        return (!initialPbModelET.equals(InitialPbModelET.getNoneInstance()));
    }

    /**
     * @return the initialPbModelET
     */
    public AbstractRatiosDataModel getInitialPbModelET() {
        return initialPbModelET;
    }

    /**
     * @return the apparentDates
     */
    public ValueModel[] getApparentDates() {
        return apparentDates;
    }

    /**
     * @return the parDerivTerms
     */
    public ConcurrentMap<String, BigDecimal> getParDerivTerms() {
        return parDerivTerms;
    }

    /**
     * @param mineralStandardName the mineralStandardName to set
     */
    public void setMineralStandardName(String mineralStandardName) {
        this.mineralStandardName = mineralStandardName;
    }

    /**
     * @param mineralName the mineralName to set
     */
    public void setMineralName(String mineralName) {
        this.mineralName = mineralName;
    }

    /**
     * @param initialPbModelET the initialPbModelET to set
     */
    public void setInitialPbModelET(AbstractRatiosDataModel initialPbModelET) {
        this.initialPbModelET = initialPbModelET;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ValueModel[] myTestRatios = new ValueModel[3];
        myTestRatios[0] = new ValueModel(//
                //
                "r206_204c", //
                BigDecimal.ZERO, //
                "ABS", //
                new BigDecimal(0.06298816629854530000 / 2.0), BigDecimal.ZERO);
        myTestRatios[1] = new ValueModel(//
                //
                "r207_204c", //
                BigDecimal.ZERO, //
                "ABS", //
                new BigDecimal(0.92376003656586900000 / 2.0), BigDecimal.ZERO);
        myTestRatios[2] = new ValueModel(//
                //
                "r208_204c", //
                BigDecimal.ZERO, //
                "ABS", //
                new BigDecimal(0.00040104065069202200 / 2.0), BigDecimal.ZERO);

        Map<String, BigDecimal> myTestCorrelations = new HashMap<>();
        myTestCorrelations.put("rhoR206_204c__r207_204c", new BigDecimal(-0.0400671215735759));
        myTestCorrelations.put("rhoR206_204c__r208_204c", new BigDecimal(-0.0400671215735759));
        myTestCorrelations.put("rhoR207_204c__r208_204c", new BigDecimal(-0.0400671215735759));
        AbstractRatiosDataModel initialPbModel1 = //
                InitialPbModelET.createInstance("initialPbModel1", 1, 0, "Test Lab", "2012-04-01", "NO REF", "NO COMMENT", myTestRatios, myTestCorrelations);

        myTestRatios = new ValueModel[4];
        myTestRatios[0] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_207r.getName(), //
                new BigDecimal(16.9432435810912), //
                "ABS", //
                new BigDecimal(0.06298816629854530000 / 2.0),//
                true);
        myTestRatios[1] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_208r.getName(), //
                new BigDecimal(27.80), //
                "ABS", //
                new BigDecimal(0.92376003656586900000 / 2.0),//
                true);
        myTestRatios[2] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r206_238r.getName(), //
                new BigDecimal(0.09130), //
                "ABS", //
                new BigDecimal(0.00040104065069202200 / 2.0),//
                true);
        myTestRatios[3] = new MineralStandardUPbRatioModel(//
                MineralStandardUPbRatiosEnum.r238_235s.getName(), //
                new BigDecimal(137.818), //
                "ABS", //
                new BigDecimal(0.04500000000000000000 / 2.0),//
                true);

        myTestCorrelations = new HashMap<String, BigDecimal>();
        myTestCorrelations.put("rhoR206_207r__r206_238r", new BigDecimal(-0.0400671215735759));

        AbstractRatiosDataModel sriLanka1 = createInstance(//
                "SriLanka", 1, 0, "Test Lab", "2000-01-01", "NO REF", "NO COMMENT", myTestRatios, myTestCorrelations, myConcentrationsPPM, "Sri Lanka", "zircon", initialPbModel1);

//        System.out.println( sriLanka1.getDataCorrelations().ToStringWithLabels() );
//        System.out.println( sriLanka1.getDataCovariances().ToStringWithLabels() );
        try {
            ETSerializer.SerializeObjectToFile(sriLanka1, "MineralStandardUPbModelTEST.ser");
        } catch (ETException eTException) {
        }
        AbstractRatiosDataModel sriLanka2 = //
                (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile("MineralStandardUPbModelTEST.ser");

        System.out.println(sriLanka1.getDataCorrelationsVarUnct().ToStringWithLabels());
        System.out.println(sriLanka1.getDataCovariancesVarUnct().ToStringWithLabels());

        String testFileName = "MineralStandardUPbModelTEST.xml";
        sriLanka2.serializeXMLObject(testFileName);
        try {
            sriLanka2.readXMLObject(testFileName, false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MineralStandardUPbModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ETException ex) {
            Logger.getLogger(MineralStandardUPbModel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadOrMissingXMLSchemaException ex) {
            Logger.getLogger(MineralStandardUPbModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(sriLanka2.getDataCorrelationsVarUnct().ToStringWithLabels());
        System.out.println(sriLanka2.getDataCovariancesVarUnct().ToStringWithLabels());
    }

    /**
     * @return the concentrationsPPM
     */
    public ValueModel[] getConcentrationsPPM() {
        // dec 2014 not decided on final architecture; then refactor
        if (concentrationsPPM == null) {
            initConcentrations();
        }
        return concentrationsPPM;
    }

    /**
     * @param concentrationsPPM the concentrationsPPM to set
     */
    public void setConcentrationsPPM(ValueModel[] concentrationsPPM) {
        this.concentrationsPPM = concentrationsPPM;
    }

    public ValueModel getConcentrationByName(String datumName) {

        ValueModel retVal = new ValueModel(datumName);
        for (int i = 0; i < concentrationsPPM.length; i++) {
            if (concentrationsPPM[i].getName().equals(datumName)) {
                retVal = concentrationsPPM[i];
            }
        }

        return retVal;
    }
}
