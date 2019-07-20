/*
 * Sample.java
 *
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain aliquot copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.UPb_Redux.samples;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.earthtime.Tripoli.sessions.TripoliSession;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.UPbFractionEditorDialog;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.filters.FractionXMLFileFilter;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.evolution.seaWater.SeaWaterInitialDelta234UTableModel;
import org.earthtime.projects.EarthTimeSerializedFileInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reports.ReportSettingsInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.FileHelper;

/**
 * A
 * <code>Sample</code> object contains all of the scientific data related to
 * aliquot single geological sample as well as additional methods to manipulate
 * this data.
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class Sample implements
        Serializable,
        SampleInterface,
        UPbSampleInterface,
        EarthTimeSerializedFileInterface {

    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = 1691080926513942156L;
    /**
     * used to flag current execution of saving dialog to prevent multiple
     * windows from being opened at the same time. Implemented for
     * {@link #SaveExcelSamplesFile SaveExcelSamplesFile}.
     */
    private static boolean saving;
    /**
     * ReduxLabData, which is made available to any active sample; contains
     * information regarding tracers and the various models for each sample
     */
    private transient ReduxLabData myReduxLabData;
    /**
     * the file that this <code>Sample</code> will be saved under
     */
    private String reduxSampleFileName;
    /**
     * the type that this <code>Sample</code> is classified as. Valid fields
     * are: "ANALYSIS" or "COMPILATION"
     */
    private String sampleType;
    // added april 2010 to differentiate IDTIMS from LAICPMS ETC
    private String sampleAnalysisType;
    /**
     * used to flag analyzed files such as imported aliquots and MC-ICPMS Excel
     * files. Set to <code>true</code> when the <code>Sample</code> has been
     * analyzed and <code>false</code> when it has not.
     */
    private boolean analyzed;
    private String isotopeSystem;
    private String defaultReportSpecsType;
    /**
     * the path to which this <code>Sample</code> will be saved.
     */
    private String reduxSampleFilePath;
    /**
     * the collection of aliquots created for this <code>Sample</code>;
     * contained in aliquot vector for thread safety
     */
    private Vector<AliquotInterface> aliquots;
    /**
     * collection of individual aliquotFractionFiles within this
     * <code>Sample</code>.
     */
    private Vector<ETFractionInterface> UPbFractions;
    /**
     * the file of this <code>Sample</code>.
     */
    private String sampleName;
    /**
     * the International Geo Sample Number of this <code>Sample</code>.
     */
    private String sampleIGSN;
    private boolean validatedSampleIGSN;
    /**
     * any comments or clarifications regarding this <code>Sample</code>.
     */
    private String sampleAnnotations;
    /**
     * used to show whether this <code>Sample</code> has been altered. Set to
     * <code>true</code> when it has been changed, <code>false</code> if it has
     * not.
     */
    private boolean changed;
    /**
     * used to flag when the existing data in this <code>Sample</code> was
     * overridden during an import; <code>true</code> if it was,
     * <code>false</code> if it was not. It is set each time the sample is
     * opened using the value from redux preferences.
     */
    private boolean fractionDataOverriddenOnImport = true;// per kwiki update modality march 2009
    /**
     * the default file of any empty <code>Fraction</code> created within this
     * <code>Sample</code>.
     */
    private String defaultFractionName = "F-";
    /**
     * the default number of any empty <code>Fraction</code> created within this
     * <code>Sample</code>.
     */
    private int defaultFractionCounter = 1;
    /**
     * the <code>PhysicalConstants</code> for use with this <code>Sample</code>,
     * containing information regarding atomic molar masses and measured
     * constants.
     */
    private AbstractRatiosDataModel physicalConstantsModel;
    /**
     * the <code>ReportSettings</code> for use with this <code>Sample</code>,
     * containing information regarding columnar report layout.
     */
    private ReportSettingsInterface reportSettingsModel;
    /**
     * the settings for the user interface of the sample age interpretation.
     */
    private SampleDateInterpretationGUIOptions sampleAgeInterpretationGUISettings;
    private GraphAxesSetup concordiaGraphAxesSetup;
    private GraphAxesSetup terraWasserburgGraphAxesSetup;
    private boolean automaticDataUpdateMode;
    /**
     * the sample folder to be used for autoupdate mode
     */
    private File sampleFolderSaved;
    /**
     * used to store dateModels in compilation mode
     */
    private Vector<ValueModel> sampleDateModels;
    // added oct 2010 to handle legacy imports and improved metadata
    private String mineralName;
    private ANALYSIS_PURPOSE analysisPurpose;
    private boolean calculateTWrhoForLegacyData;
    private SESARSampleMetadata mySESARSampleMetadata;
    private boolean archivedInRegistry;
    private SampleRegistries sampleRegistry;
    // July 2011
    private TripoliSession tripoliSession;
    private SortedSet<String> filteredFractionIDs;

    /**
     *
     */
    public Sample() {
    }

    /**
     * creates aliquot new instance of <code>Sample</code> with aliquot
     * specified <code>sampleName</code>, <code>sampleType</code>, and
     * <code>myReduxLabData</code>. All other fields are initialized to default
     * values and aliquot default <code>Aliquot</code> is added to the
     * <code>Sample</code>.
     *
     * @param sampleName
     * @param sampleType the type of this <code>Sample</code> to which
     * <code>sampleType</code> will be set
     * @param sampleAnalysisType
     * @param defaultAnalysisPurpose
     * @param isotopeSystem the value of isotopeSystem
     * @param defaultReportSpecsType
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    public Sample(
            String sampleName, String sampleType, String sampleAnalysisType, ANALYSIS_PURPOSE defaultAnalysisPurpose, String isotopeSystem, String defaultReportSpecsType) {
        this.sampleName = sampleName;
        this.sampleType = sampleType;
        this.sampleAnalysisType = sampleAnalysisType;
        this.analyzed = false;
        this.sampleIGSN = ReduxConstants.DEFAULT_IGSN;
        this.validatedSampleIGSN = false;
        this.sampleAnnotations = "";
        this.reduxSampleFileName = "";
        this.reduxSampleFilePath = "";
        this.isotopeSystem = isotopeSystem;
        this.defaultReportSpecsType = defaultReportSpecsType;

        Sample.saving = false;

        this.myReduxLabData = ReduxLabData.getInstance();
        this.reportSettingsModel = myReduxLabData.getDefaultReportSettingsModelBySpecsType(defaultReportSpecsType);

        this.sampleAgeInterpretationGUISettings = new SampleDateInterpretationGUIOptions();

        this.aliquots = new Vector<>();
        this.UPbFractions = new Vector<>();

        this.physicalConstantsModel = myReduxLabData.getDefaultPhysicalConstantsModel();

        this.automaticDataUpdateMode = false;
        this.sampleFolderSaved = null;
        this.sampleDateModels = new Vector<>();

        this.concordiaGraphAxesSetup = new GraphAxesSetup("C", 2);
        this.terraWasserburgGraphAxesSetup = new GraphAxesSetup("T-W", 2);

        this.mineralName = "zircon";

        this.changed = false;

        this.analysisPurpose = defaultAnalysisPurpose;

        this.calculateTWrhoForLegacyData = true;

        this.mySESARSampleMetadata = new SESARSampleMetadata();

        this.tripoliSession = null;

        this.archivedInRegistry = false;

        this.sampleRegistry = SampleRegistries.SESAR;

        this.filteredFractionIDs = null;
    }

    /**
     *
     * @param aliquotNumber the value of aliquotNumber
     * @param aliquotName the value of aliquotName
     * @param physicalConstants the value of physicalConstants
     * @param compiled the value of compiled
     * @param mySESARSampleMetadata the value of mySESARSampleMetadata
     * @param sampleAnalysisType the value of sampleAnalysisType
     */
    @Override
    public AliquotInterface generateDefaultAliquot(//
            int aliquotNumber, String aliquotName, AbstractRatiosDataModel physicalConstants, boolean compiled, SESARSampleMetadata mySESARSampleMetadata, String sampleAnalysisType) {

        return new UPbReduxAliquot(aliquotNumber, aliquotName, physicalConstants, compiled, mySESARSampleMetadata);
    }
    //
//    
//    @Override
//    public AliquotInterface generateDefaultAliquot() {
//        return new UPbReduxAliquot();
//    }

    /**
     *
     */
    @Override
    public void setUpSample() {

        // force aliquot registry onto sample SESAR starting oct 2014
        if (getSampleRegistry() == null) {
            setSampleRegistry(SampleRegistries.SESAR);
        }

        // May 2010 update sampleAnalysisType in preparation for LAICPMS analysis
        if (isSampleTypeAnalysis() && getSampleAnalysisType().equals("")) {
            setSampleAnalysisType(SampleAnalysisTypesEnum.IDTIMS.getName());
        }

        // April 2011 we are altering SampleIGSN to be of form rrr.IGSN
        //  where rrr is registry as per enum SampleRegistries
        // this means that if sample is already flagged as validated - i.e. at SESAR
        // we check that it is valid at registry and change SampleIGSN and percolate it
        // down to all Aliquots
        updateWithRegistrySampleIGSN();

        if (!isSampleTypeLegacy()) {
            SampleInterface.registerSampleWithLabData(this);
        } else // dec 2012
        if (getFractions().size() > 0) {
            // June 2010 fix for old legacy fractions
            Vector<ETFractionInterface> convertedF = new Vector<>();
            for (ETFractionInterface f : getFractions()) {
                if (f instanceof UPbFraction) {
                    // convert to UPbLegacyFraction
                    System.out.println("Converting legacy legacy");
                    FractionI legacyF = new UPbLegacyFraction(f.getFractionID());

                    legacyF.setAnalysisMeasures(f.getAnalysisMeasures());
                    // these two are legacy leftovers and need to be zeroed so report settings does not show columns
                    legacyF.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()).setValue(BigDecimal.ZERO);
                    legacyF.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()).setValue(BigDecimal.ZERO);

                    legacyF.setRadiogenicIsotopeRatios(f.getRadiogenicIsotopeRatios());
                    legacyF.setIsotopeDates(f.getIsotopeDates());
                    legacyF.setCompositionalMeasures(f.getCompositionalMeasures());
                    legacyF.setSampleIsochronRatios(f.getSampleIsochronRatios());

                    legacyF.setSampleName(f.getSampleName());
                    legacyF.setZircon(((FractionI) f).isZircon());

                    legacyF.setAliquotNumber(f.getAliquotNumber());
                    legacyF.setRejected(f.isRejected());
                    legacyF.setFractionNotes(f.getFractionNotes());
                    legacyF.setPhysicalConstantsModel(f.getPhysicalConstantsModel());
                    legacyF.setChanged(false);

                    legacyF.setLegacy(true);

                    convertedF.add(legacyF);
                } else {
                    f.setLegacy(true);
                    convertedF.add(f);
                }
            }

            setFractions(convertedF);

            // modified logic oct 2010 ... sample manager allows reset
            // additional test for missing T-W rho calculation
            // use first fraction to test for rho < -1 or 0  (both used as default for non-existent rho)
            double twRho
                    = getFractions().get(0).//
                            getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").getValue().doubleValue();
            if ((twRho < -1.0)) {
                for (ETFractionInterface f : getFractions()) {
                    f.getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r")//
                            .setValue(BigDecimal.ZERO);
                }
            }
        }

        // June 2010 be sure lab name is updated to labdata labname when used in reduction
        if (isSampleTypeAnalysis() || isSampleTypeLiveWorkflow() || isSampleTypeLegacy()) {
            updateSampleLabName();
        }

    }

    /**
     *
     * @param aliquotNumber
     * @throws BadLabDataException
     */
    @Override
    public void addDefaultUPbFractionToAliquot(int aliquotNumber)
            throws BadLabDataException {
        FractionI defFraction = new UPbFraction("NONE");
        defFraction.setAliquotNumber(aliquotNumber);

        initializeDefaultUPbFraction(defFraction);

        // sept 2010 add Aliquot defaults
        AliquotInterface aliquot = getAliquotByNumber(aliquotNumber);
        ReduxLabData labData = ((UPbReduxAliquot) aliquot).getMyReduxLabData();

        String tracerID = ((UPbReduxAliquot) aliquot).getDefaultTracerID();
        AbstractRatiosDataModel tracer = labData.getATracerModel(tracerID);

        ((UPbFraction) defFraction).setMyLabData(labData);
        ((UPbFractionI) defFraction).setTracer(tracer);

    }

    /**
     *
     * @param aliquotNumber
     * @throws BadLabDataException
     */
    @Override
    public void addDefaultUPbLegacyFractionToAliquot(int aliquotNumber)
            throws BadLabDataException {
        FractionI defFraction = new UPbLegacyFraction("NONE");
        defFraction.setAliquotNumber(aliquotNumber);

        initializeDefaultUPbFraction(defFraction);
    }

    private void initializeDefaultUPbFraction(FractionI defFraction)
            throws BadLabDataException {
        //reset counter if no aliquotFractionFiles
        if (getFractions().isEmpty()) {
            setDefaultFractionCounter(0);
        }

        setDefaultFractionCounter(getDefaultFractionCounter() + 1);

        defFraction.setSampleName(getSampleName());
        defFraction//
                .setFractionID(getDefaultFractionName() + Integer.toString(getDefaultFractionCounter()));
        defFraction//
                .setGrainID(defFraction.getFractionID());

        ETFractionInterface existingFraction = getFractionByID(defFraction.getFractionID());
        // handle repeated default fractionIDs
        if (existingFraction != null) {
            defFraction//
                    .setFractionID(defFraction.getFractionID() + "r"); // not robust but does it for now feb 2010
            defFraction//
                    .setGrainID(defFraction.getGrainID());
        }
        // must be saved or is assumed deleted during edit
        defFraction.setDeleted(false);
        defFraction.setChanged(false);

        addFraction(defFraction);
    }

    /**
     * reads in data from the XML file specified by argument
     * <code>fractionFile</code> and adds any <code>Fractions</code> found in
     * the file to this <code>Sample</code> under the <code>Aliquot</code>
     * specified by argument <code>aliquotNumber</code>.
     *
     * @pre <code>fractionFile</code> is an XML file containing valid
     * <code>Fractions</code> and <code>aliquotNumber</code> specifies an
     * existing <code>Aliquot</code> in this <code>Sample</code>
     * @post all <code>Fractions</code> found in the file are added to the
     * <code>Aliquot</code> specified by <code>aliquotNumber</code> in this
     * <code>Sample</code>
     *
     * @param fractionFile the file to read data from
     * @param aliquotNumber the number of the <code>Aliquot</code> that the
     * <code>Fractions</code> being read from the file belong to
     * @param validateSampleName
     * @param doValidate
     * @return
     * @throws org.earthtime.XMLExceptions.ETException ETException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    @Override
    public String processXMLFractionFile(
            File fractionFile,
            int aliquotNumber,
            Boolean validateSampleName,
            boolean doValidate)
            throws ETException, BadLabDataException {

        FractionI fractionFromFile = new UPbFraction("NONE");
        boolean badFile = true;

        try {
            fractionFromFile
                    = ((UPbFraction) fractionFromFile).readXMLFraction(
                            fractionFile.getCanonicalPath(), aliquotNumber, doValidate);
            badFile = (fractionFromFile == null);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        } catch (BadOrMissingXMLSchemaException ex) {
            throw new ETException(
                    null, "Cannot import " + fractionFile.getName());
        }
        if (!badFile) {
            if (validateSampleName
                    && !fractionFromFile.getSampleName().equalsIgnoreCase(getSampleName())) {
                throw new ETException(
                        null,
                        new String[]{"The sample name: " + fractionFromFile.getSampleName() + "\n",
                            "specified in the Fraction File:\n",
                            fractionFile.getName() + "\n",
                            "differs from the open Sample's name: " + getSampleName() + ".\n",
                            "\nPlease correct the discrepancy and try again."
                        });
            }// else {
            ETFractionInterface existingFraction = getFractionByID(fractionFromFile.getFractionID());
            if (existingFraction == null) {
                System.out.println("New UPbReduxFraction");
                // AUG 2011 moved this improved logic here from readXMLFraction
                if (((UPbFraction) fractionFromFile).getTracer() == null) {
                    ((UPbFractionI) fractionFromFile)//
                            .setTracer(((UPbFraction) fractionFromFile).getMyLabData().getNoneTracer());
                }
                addFraction(fractionFromFile);
            } else {
                System.out.println("Existing Fraction = " + existingFraction.getFractionID() + " updating type = " + fractionFromFile.getRatioType());
                boolean didUpdate
                        = ((UPbFraction) existingFraction).updateUPbFraction(fractionFromFile, isFractionDataOverriddenOnImport());

                setChanged(didUpdate);
            }

            //  }
        } else {
            // do nothing
        }

        // returns "NONE" if file is not processed
        return fractionFromFile.getSampleName();
    }

    public boolean importAliquotFolder(File[] fractions, int aliquotNumber, boolean doValidate)
            throws ETException {
        if (fractions == null) {
            throw new ETException(null,
                    "The selected aliquot folder does not contain any XML fraction files.");
        }
        // nov 2008
        // first determine if the sample is empty and if it is,
        // use the first xml file as the automatic source of the
        // sample file
        boolean retval = false;
        if (getFractions().isEmpty()) {
            try {
                setSampleName(processXMLFractionFile(fractions[0], aliquotNumber, false, doValidate));
            } catch (ETException uPbReduxException) {
            }
        }

        long latestFractionFileModified = 0L;
        for (int f = 0; f < fractions.length; f++) {
            // test if fractionFile is newer than last update to Aliquot
            // or whether we are in auto-update mode versu live-update
            // auto-update reads every fraction
            if (fractions[f].lastModified() > ((UPbReduxAliquot) getAliquotByNumber(aliquotNumber)).getAliquotFolderTimeStamp().getTime()
                    || getSampleType().equalsIgnoreCase(SampleTypesEnum.ANALYSIS.getName())) {
                if (fractions[f].lastModified() > latestFractionFileModified) {
                    latestFractionFileModified = fractions[f].lastModified();
                }
                try {
                    processXMLFractionFile(fractions[f], aliquotNumber, true, doValidate);
                    retval = true;
                } catch (ETException ex) {
                    // Modal dialog with OK/cancel and aliquot text field
                    if (f < (fractions.length - 1)) {
                        int response = JOptionPane.showConfirmDialog(null,
                                new String[]{"Continue to process folder?"},
                                "ET Redux Warning",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE);
                        if (response == JOptionPane.NO_OPTION) {
                            break;
                        }
                    }
                }
            }
        }

        //  stamp the aliquot
        if (latestFractionFileModified > 0) {
            ((UPbReduxAliquot) getAliquotByNumber(aliquotNumber)).setAliquotFolderTimeStamp(new Date(latestFractionFileModified));
        }

        return retval;
    }

    /**
     *
     * @param sample the value of sample
     * @param myFractionEditor the value of myFractionEditor
     * @throws ETException
     */
    @Override // should this be synchronized?
    public void automaticUpdateOfUPbSampleFolder(SampleInterface sample, DialogEditor myFractionEditor) throws ETException {

        File sampleFolder = new File(sample.getReduxSampleFilePath()).getParentFile();

        File[] aliquotFolders = sampleFolder.listFiles((File pathname) -> (pathname.isDirectory()
                && !pathname.isHidden()//
                && !pathname.getName().equalsIgnoreCase(ReduxConstants.NAME_OF_SAMPLEMETADATA_FOLDER)));

        if (aliquotFolders.length == 0) {
            throw new ETException(null,
                    "The selected Sample Folder does not contain any Aliquot folders.");
        } else {

            // proceed to read in the aliquotFractionFiles from each aliquot folder
            File[] aliquotFractionFiles;

            for (final File aliquotFolder : aliquotFolders) {

                // take the first prisoner
                aliquotFractionFiles = aliquotFolder.listFiles((File file) -> {
                    // want .xml files and only freshones in live-update, but all of them in auto-update
                    boolean isXML = file.getName().toLowerCase().endsWith(".xml");
                    boolean isValidUPbFraction = false;
                    if (isXML) {
                        // nov 2016 need more robust test of file to be fraction file
                        // contains "<UPbReduxFraction"
                        List<String> fractionData;
                        try {
                            fractionData = Files.readLines(file, Charsets.ISO_8859_1);
//                            System.out.println(fractionData.get(0));
                            // check first 10 lines
                            for (int line = 0; line < 10; line++) {
                                if (fractionData.get(line).contains("<UPbReduxFraction")) {
                                    isValidUPbFraction = true;
                                    break;
                                }
                            }
                        } catch (IOException iOException) {
                        }
                    }

                    if (isValidUPbFraction && getSampleType().equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName())) {
                        return ((file.lastModified() >= (aliquotFolder.lastModified() - 20000l)));
                    } else {
                        return isValidUPbFraction && getSampleType().equalsIgnoreCase(SampleTypesEnum.ANALYSIS.getName());
                    }

                } // 20 second cushion
                );

                // assume xml files are in good shape with doValidate = false
                if (aliquotFractionFiles.length > 0) {
                    updateSampleAliquot(aliquotFolder, aliquotFractionFiles, false, myFractionEditor);
                }
            }
        }

        // Nov 2016
        initFilteredFractionsToAll();

    }

    private synchronized void updateSampleAliquot(File aliquotFolder, File[] aliquotFractionFiles, boolean doValidate, DialogEditor myFractionEditor) {

        System.out.println("CHANGED count of fresh data = " + aliquotFractionFiles.length);
        int aliquotNumber;

        // determine aliquot number
        if (aliquotFractionFiles.length > 0) {
            AliquotInterface aliquot = getAliquotByName(aliquotFolder.getName());
            if (aliquot == null) {
                // check if last aliquot was empty (i.e. the initial first dummy aliquot)
                if (((ReduxAliquotInterface) aliquots.get(aliquots.size() - 1)).getAliquotFractions().isEmpty()) {
                    aliquotNumber = ((ReduxAliquotInterface) aliquots.get(aliquots.size() - 1)).getAliquotNumber();
                } else {
                    aliquotNumber = addNewDefaultAliquot();
                }
            } else {
                aliquotNumber = ((ReduxAliquotInterface) aliquot).getAliquotNumber();
            }

            ETFractionInterface savedCurrentFraction = null;
            boolean doRestoreAutoUranium = false;
            try {
                if (myFractionEditor != null) {
                    savedCurrentFraction = ((UPbFractionEditorDialog) myFractionEditor).getMyFraction();
                    doRestoreAutoUranium = ((UPbFractionEditorDialog) myFractionEditor).restoreAllFractions(savedCurrentFraction);
                }

                if (importAliquotFolder(aliquotFractionFiles, aliquotNumber, doValidate)) {
                    getAliquotByNumber(aliquotNumber).setAliquotName(aliquotFolder.getName());

                    ((UPbReduxAliquot) getAliquotByNumber(aliquotNumber)).//
                            setContainingSampleDataFolder(getSampleFolderSaved());

                    ((UPbReduxAliquot) getAliquotByNumber(aliquotNumber)).//
                            setAutomaticDataUpdateMode(true);

                    ((ReduxAliquotInterface) getAliquotByNumber(aliquotNumber)).//
                            reduceData(false);

                    if (myFractionEditor != null) {
                        if (doRestoreAutoUranium) {
//                            ((UPbFraction) savedCurrentFraction).autoGenerateMeasuredUranium();
                        }
                        ((UPbFractionEditorDialog) myFractionEditor).InitializeFractionData(savedCurrentFraction);

                        // intentional static call for now
                        UPbFractionReducer.getInstance().fullFractionReduce((FractionI) savedCurrentFraction, true);

                        ((UPbFractionEditorDialog) myFractionEditor).reInitializeKwikiTab(savedCurrentFraction);
                    }
                }
            } catch (ETException uPbReduxException) {
            }
        }
    }

    /**
     * reads <code>Fractions</code> from the file specified by argument
     * <code>location</code> and adds them to the      <code>Aliquot</code> specified
     * by argument <code>aliquotNumber</code> in this <code>Sample</code>.
     *
     * @pre argument <code>location</code> specifies an XML file with valid
     * <code>UPbFractions</code> and argument <code>aliquotNumber</code>
     * specifies an <code>Aliquot</code> that exists in this <code>Sample</code>
     * @post all <code>Fractions</code> found in the specified file are added to
     * the specified <code>Aliquot</code> in this <code>Sample</code>
     *
     * @param location file to read data from
     * @param aliquotNumber number of <code>Aliquot</code> to add
     * <code>Fractions</code> from the file to
     * @return <code>String</code> - path of the file that data was read from
     * @throws org.earthtime.XMLExceptions.ETException ETException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     * @throws java.io.FileNotFoundException FileNotFoundException
     */
    @Override
    public String importUPbFractionFolderForManualUpdate(
            File location,
            int aliquotNumber)
            throws ETException, BadLabDataException, FileNotFoundException {

        String retval = null;

        String dialogTitle = "Select an ET_Redux Fractions Folder of XML files to import";

        File fractionFolder
                = FileHelper.AllPlatformGetFolder(dialogTitle, location);

        if (fractionFolder == null) {
            throw new FileNotFoundException();
        } else {
            // get all the files and try to import them one by one

            File[] fractions = fractionFolder.listFiles(new FractionXMLFileFilter());

            if (fractions.length == 0) {
                throw new ETException(null,
                        "The selected folder does not contain any XML fraction files.");
            } else {
                importAliquotFolder(fractions, aliquotNumber, false); // jan 2016true);

                ((ReduxAliquotInterface) getAliquotByNumber(aliquotNumber)).//
                        reduceData(false);
            }

        }
        if (getFractions().size() > 0) {
            // return folder for persistent state
            retval = fractionFolder.getPath();
        }

        return retval;
    }

    /**
     * gets the <code>file</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>file</code> of this <code>Sample</code>
     *
     * @return <code>String</code> - <code>file</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getSampleName() {
        if (sampleName == null) {
            sampleName = "NONE";
        }
        return sampleName;
    }

    /**
     * sets the <code>sampleName</code> of this <code>Sample</code> to the
     * argument <code>sampleName</code>
     *
     * @pre argument <code>sampleName</code> is a valid <code>sampleName</code>
     * @post this <code>Sample</code>'s <code>sampleName</code> is set to
     * argument <code>sampleName</code>
     *
     * @param sampleName value to which<code>sampleName</code> of this
     * <code>Sample</code> will be set
     */
    @Override
    public void setSampleName(String sampleName) {
        setChanged(this.sampleName.compareToIgnoreCase(sampleName) != 0);

        this.sampleName = sampleName;

        if (isChanged()) {
            updateSampleFractionsWithSampleName(sampleName);
        }

    }

    /**
     * gets the <code>sampleAnnotations</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>sampleAnnotations</code> of this
     * <code>Sample</code>
     *
     * @return <code>String</code> - <code>sampleAnnotations</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getSampleAnnotations() {
        return sampleAnnotations;

    }

    /**
     * sets the <code>sampleAnnotations</code> of this <code>Sample</code> to
     * the argument <code>annotations</code>
     *
     * @param sampleAnnotations
     * @pre argument <code>annotations</code> is a valid
     * <code>sampleAnnotations</code>
     * @post this <code>Sample</code>'s <code>sampleAnnotations</code> is set to
     * argument <code>annotations</code>
     */
    @Override
    public void setSampleAnnotations(String sampleAnnotations) {
        setChanged(this.sampleAnnotations.compareToIgnoreCase(sampleAnnotations) != 0);

        this.sampleAnnotations = sampleAnnotations;
    }

    /**
     * gets the <code>changed</code> field of this <code>Sample</code>
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>changed</code> field of this <code>Sample</code>
     *
     * @return <code>boolean</code> - <code>changed</code> field of this
     * <code>Sample</code>
     */
    @Override
    public boolean isChanged() {
        for (ETFractionInterface UPbFraction : UPbFractions) {
            changed = changed || UPbFraction.isChanged();
        }

        return changed;

    }

    /**
     * sets the <code>changed</code> field of this <code>Sample</code> to the
     * argument <code>changed</code>
     *
     * @pre argument <code>changed</code> is a valid <code>boolean</code>
     * @post this <code>Sample</code>'s <code>changed</code> field is set to
     * argument <code>changed</code>
     *
     * @param changed vale to which <code>changed</code> field of this
     * <code>Sample</code> will be set
     */
    public void setChanged(boolean changed) {
        this.changed = changed;

    }

    /**
     * gets the <code>saving</code> field of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>saving</code> field of this <code>Sample</code>
     *
     * @return <code>boolean</code> - <code>true</code> if the saving dialog for
     * <code>SaveExcelSamplesFile</code> is open, else <code>false</code>
     */
    public boolean isSaving() {
        return saving;

    }

    /**
     * sets the <code>saving</code> field of this <code>Sample</code> to the
     * argument <code>saving</code>.
     *
     * @pre argument <code>saving</code> is a valid <code>boolean</code>
     * @post this <code>Sample</code>'s <code>saving</code> field is set to
     * argument <code>saving</code>
     *
     * @param saving value to which <code>saving</code> field of this
     * <code>Sample</code> will be set
     */
    public void setSaving(boolean saving) {
        Sample.saving = saving;

    }

    /**
     * gets the <code>reduxSampleFilePath</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>reduxSampleFilePath</code> of this
     * <code>Sample</code>
     *
     * @return <code>String</code> - <code>reduxSampleFilePath</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getReduxSampleFilePath() {
        return reduxSampleFilePath;
    }

    @Override
    public void setReduxSampleFilePath(String reduxSampleFilePath) {
        this.reduxSampleFilePath = reduxSampleFilePath;
    }

    /**
     * gets the <code>reduxSampleFileName</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>reduxSampleFileName</code> of this
     * <code>Sample</code>
     *
     * @return <code>String</code> - <code>reduxSampleFileName</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getReduxSampleFileName() {
        return reduxSampleFileName;
    }

    @Override
    public void setReduxSampleFileName(String reduxSampleFileName) {
        this.reduxSampleFileName = reduxSampleFileName;
    }

    /**
     * gets the <code>fractionDataOverriddenOnImport</code> field of this
     * <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>fractionDataOverriddenOnImport</code> field of
     * this <code>Sample</code>
     *
     * @return <code>boolean</code> -
     * <code>fractionDataOverriddenOnImport</code> field of this
     * <code>Sample</code>
     */
    @Override
    public boolean isFractionDataOverriddenOnImport() {
        return fractionDataOverriddenOnImport;
    }

    /**
     * sets the <code>fractionDataOverriddenOnImport</code> field of this
     * <code>Sample</code> to the argument
     * <code>fractionDataOverriddenOnImport</code>
     *
     * @pre argument <code>fractionDataOverriddenOnImport</code> is a valid
     * <code>boolean</code>
     * @post this <code>Sample</code>'s
     * <code>fractionDataOverriddenOnImport</code> is set to argument
     * <code>fractionDataOverriddenOnImport</code>
     *
     * @param fractionDataOverriddenOnImport value to which
     * <code>fractionDataOverriddenOnImport</code> of this <code>Sample</code>
     * will be set
     */
    @Override
    public void setFractionDataOverriddenOnImport(boolean fractionDataOverriddenOnImport) {
        this.fractionDataOverriddenOnImport = fractionDataOverriddenOnImport;
    }

    /**
     * gets the <code>defaultFractionName</code> of this <code>Sample</code>
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>defaultFractionName</code> of this
     * <code>Sample</code>
     *
     * @return <code>String</code> - <code>defaultFractionName</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getDefaultFractionName() {
        return defaultFractionName;
    }

    /**
     * sets the <code>defaultFractionName</code> of this <code>Sample</code> to
     * the argument <code>defaultFractionName</code>
     *
     * @pre argument <code>defaultFractionName</code> is a valid
     * <code>defaultFractionName</code>
     * @post this <code>Sample</code>'s <code>defaultFractionName</code> is set
     * to argument <code>defaultFractionName</code>
     *
     * @param defaultFractionName value to which
     * <code>defaultFractionName</code> of this <code>Sample</code> will be set
     */
    @Override
    public void setDefaultFractionName(String defaultFractionName) {
        this.defaultFractionName = defaultFractionName;

    }

    /**
     * gets the <code>defaultFractionCounter</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>defaultFractionCounter</code> of this
     * <code>Sample</code>
     *
     * @return <code>int</code> - <code>defaultFractionCounter</code> of this
     * <code>Sample</code>
     */
    @Override
    public int getDefaultFractionCounter() {
        return defaultFractionCounter;
    }

    /**
     * sets the <code>defaultFractionCounter</code> of this <code>Sample</code>
     * to the argument <code>defaultFractionCounter</code>
     *
     * @pre argument <code>defaultFractionCounters</code> is a valid
     * <code>defaultFractionCounter</code>
     * @post this <code>Sample</code>'s <code>defaultFractionCounter</code> is
     * set to argument <code>defaultFractionCounter</code>
     *
     * @param defaultFractionCounter value to which
     * <code>defaultFractionCounter</code> of this <code>Sample</code> will be
     * set
     */
    @Override
    public void setDefaultFractionCounter(int defaultFractionCounter) {
        this.defaultFractionCounter = defaultFractionCounter;
    }

    /**
     * gets the <code>UPbFractions</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>UPbFractions</code> of this <code>Sample</code>
     *
     * @return <code>Vector</code> - set of <code>Fractions</code> that make up
     * the <code>UPbFractions</code> of this <code>Sample</code>
     */
    @Override
    public Vector<ETFractionInterface> getFractions() {
        return UPbFractions;
    }

    /**
     * sets the <code>UPbFractions</code> of this <code>Sample</code> to the
     * argument <code>UPbFractions</code>
     *
     * @pre argument <code>UPbFractions</code> is a valid set of
     * <code>UPbFractions</code>
     * @post this <code>Sample</code>'s <code>UPbFractions</code> is set to
     * argument <code>UPbFractions</code>
     *
     * @param UPbFractions value to which <code>UPbFractions</code> of this
     * <code>Sample</code> will be set
     */
    @Override
    public void setFractions(Vector<ETFractionInterface> UPbFractions) {
        this.UPbFractions = UPbFractions;
    }

    /**
     * gets the <code>sampleIGSN</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>sampleIGSN</code> of this <code>Sample</code>
     *
     * @return <code>String</code> - <code>sampleIGSN</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getSampleIGSN() {
        return sampleIGSN;
    }

    /**
     * sets the <code>sampleIGSN</code> of this <code>Sample</code> to the
     * argument <code>sampleIGSN</code>
     *
     * @pre argument <code>sampleIGSN</code> is a valid <code>sampleIGSN</code>
     * @post this <code>Sample</code>'s <code>sampleIGSN</code> is set to
     * argument <code>sampleIGSN</code>
     *
     * @param sampleIGSN value to which <code>sampleIGSN</code> of this
     * <code>Sample</code> will be set
     */
    @Override
    public void setSampleIGSN(String sampleIGSN) {
        this.sampleIGSN = sampleIGSN;
        // we also have to percolate this change to all the Aliquots
        for (int aliquotIndex = 0; aliquotIndex
                < aliquots.size(); aliquotIndex++) {
            aliquots.get(aliquotIndex).setSampleIGSN(sampleIGSN);
            aliquots.get(aliquotIndex).setAnalysisPurpose(analysisPurpose);
        }
    }

    /**
     * gets the <code>aliquots</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>aliquots</code> of this <code>Sample</code>
     *
     * @return <code>Vector</code> - set of <code>Aliquots</code> of this
     * <code>Sample</code>
     */
    @Override
    public Vector<AliquotInterface> getAliquots() {
        return aliquots;
    }

    /**
     * sets the <code>aliquots</code> of this <code>Sample</code> to the
     * argument <code>aliquots</code>
     *
     * @pre argument <code>aliquots</code> is a valid set of
     * <code>Aliquots</code>
     * @post this <code>Sample</code>'s <code>aliquots</code> is set to argument
     * <code>aliquots</code>
     *
     * @param aliquots value to which <code>aliquots</code> of this
     * <code>Sample</code> will be set
     */
    @Override
    public void setAliquots(Vector<AliquotInterface> aliquots) {
        this.aliquots = aliquots;
    }

    /**
     *
     */
    public void repairAliquotNumberingDec2011() {
        // walk aliquots and remove empty ones 
        ArrayList<AliquotInterface> aliquotsToDelete = new ArrayList<>();
        for (int i = 0; i < aliquots.size(); i++) {
            AliquotInterface aliquot = aliquots.get(i);//    Feb 2015 getAliquotByNumber(i + 1);
            if (((ReduxAliquotInterface) aliquot).getAliquotFractions().isEmpty()) {
                // save aliquot for later deletion
                aliquotsToDelete.add(aliquot);
            }
        }
        // get rid of them
        for (int i = 0; i < aliquotsToDelete.size(); i++) {
            aliquots.remove(aliquotsToDelete.get(i));
        }

        aliquots.trimToSize();

        // renumber remaining aliquots
        for (int i = 0; i < aliquots.size(); i++) {
            AliquotInterface aliquot = aliquots.get(i);
            ((ReduxAliquotInterface) aliquot).setAliquotNumber(i + 1);

            Vector<ETFractionInterface> aliquotFractions = ((ReduxAliquotInterface) aliquot).getAliquotFractions();
            for (int j = 0; j < aliquotFractions.size(); j++) {
                aliquotFractions.get(j).setAliquotNumber(i + 1);
            }
        }
    }

    /**
     * gets the <code>physicalConstantsModel</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>physicalConstantsModel</code> of this
     * <code>Sample</code>
     *
     * @return <code>PhysicalConstants</code> -
     * <code>physicalConstantsModel</code> of this <code>Sample</code>
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel()
            throws BadLabDataException {
        if (physicalConstantsModel == null) {
            physicalConstantsModel = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel();
        }
        return physicalConstantsModel;
    }

    /**
     * sets the <code>physicalConstantsModel</code> of this <code>Sample</code>
     * to the argument <code>physicalConstantsModel</code>
     *
     * @pre argument <code>physicalConstantsModel</code> is a valid
     * <code>PhysicalConstants</code>
     * @post this <code>Sample</code>'s <code>physicalConstantsModel</code> is
     * set to argument <code>physicalConstantsModel</code>
     *
     * @param physicalConstantsModel value to which
     * <code>physicalConstantsModel</code> of this <code>Sample</code> will be
     * set
     */
    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        if ((this.physicalConstantsModel == null)
                || (!this.physicalConstantsModel.equals(physicalConstantsModel))) {
            this.physicalConstantsModel = physicalConstantsModel;
            this.setChanged(true);
            // all existing UPbAliquots must be updated (they in turn update aliquotFractionFiles)
            for (AliquotInterface aliquot : aliquots) {
                AliquotInterface nextAliquot = getAliquotByNumber(((ReduxAliquotInterface) aliquot).getAliquotNumber());
                try {
                    nextAliquot.setPhysicalConstantsModel(getPhysicalConstantsModel());

                } catch (BadLabDataException badLabDataException) {
                }
            }
        }
    }

    /**
     * gets the <code>sampleType</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>sampleType</code> of this <code>Sample</code>
     *
     * @return <code>String</code> - <code>sampleType</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getSampleType() {
        return sampleType;
    }

    /**
     * sets the <code>sampleType</code> of this <code>Sample</code> to the
     * argument <code>sampleType</code>
     *
     * @pre argument <code>sampleType</code> is a valid <code>sampleType</code>
     * @post this <code>Sample</code>'s <code>sampleType</code> is set to
     * argument <code>sampleType</code>
     *
     * @param sampleType value to which <code>sampleType</code> of this
     * <code>Sample</code> will be set
     */
    @Override
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    /**
     *
     * @return
     */
    public boolean isSampleNONE() {
        return (this.sampleName.compareToIgnoreCase("NONE") == 0);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isSampleTypeProject() {
        return (sampleType.equalsIgnoreCase(SampleTypesEnum.PROJECT.getName()));
    }

    /**
     * gets the <code>analyzed</code> field of this <code>Sample</code>
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>analyzed</code> field of this <code>Sample</code>
     *
     * @return <code>boolean</code> - <code>analyzed</code> field of this
     * <code>Sample</code>
     */
    @Override
    public boolean isAnalyzed() {
        return analyzed;
    }

    /**
     * sets the <code>analyzed</code> field of this <code>Sample</code> to the
     * argument <code>analyzed</code>
     *
     * @pre argument <code>analyzed</code> is a valid <code>boolean</code>
     * @post this <code>Sample</code>'s <code>analyzed</code> field is set to
     * argument <code>analyzed</code>
     *
     * @param analyzed value to which <code>analyzed</code> field of this
     * <code>Sample</code> will be set
     */
    @Override
    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }

    /**
     * gets the <code>sampleAgeInterpretationGUISettings</code> of this
     * <code>Sample</code>
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>sampleAgeInterpretationGUISettings</code> of this
     * <code>Sample</code>
     *
     * @return <code>SampleDateInterpretationGUIOptions</code> -
     * <code>sampleAgeInterpretationGUIOptions</code> of this
     * <code>Sample</code>
     */
    @Override
    public SampleDateInterpretationGUIOptions getSampleDateInterpretationGUISettings() {
        return sampleAgeInterpretationGUISettings;
    }

    /**
     * sets the <code>sampleAgeInterpretationGUISettings</code> of this
     * <code>Sample</code> to the argument
     * <code>sampleAgeInterpretationGUISettings</code>
     *
     * @pre argument <code>sampleAgeInterpretationGUISettings</code> is a valid
     * <code>SampleDateInterpretationGUIOptions</code>
     * @post this <code>Sample</code>'s
     * <code>sampleAgeInterpretationGUISettings</code> is set to argument
     * <code>sampleAgeInterpretationGUISettings</code>
     *
     * @param sampleAgeInterpretationGUISettings value to which      <code>
     *                                              sampleAgeInterpretationGUISettings</code> of this <code>Sample</code>
     * will be set
     */
    @Override
    public void setSampleAgeInterpretationGUISettings(SampleDateInterpretationGUIOptions sampleAgeInterpretationGUISettings) {
        this.sampleAgeInterpretationGUISettings = sampleAgeInterpretationGUISettings;
    }

    /**
     * @return the automaticDataUpdateMode
     */
    @Override
    public boolean isAutomaticDataUpdateMode() {
        return automaticDataUpdateMode;
    }

    /**
     * @param automaticDataUpdateMode the automaticDataUpdateMode to set
     */
    @Override
    public void setAutomaticDataUpdateMode(boolean automaticDataUpdateMode) {
        this.automaticDataUpdateMode = automaticDataUpdateMode;
    }

    /**
     * @return the sampleFolderSaved
     */
    @Override
    public File getSampleFolderSaved() {
        return sampleFolderSaved;
    }

    /**
     * @param sampleFolderSaved the sampleFolderSaved to set
     */
    @Override
    public void setSampleFolderSaved(File sampleFolderSaved) {
        this.sampleFolderSaved = sampleFolderSaved;
    }

// section added April 2009 copied from aliquot to support compilation mode
//compilation mode is when sampledate interpretations are super-sample specific
    /**
     * @return the sampleDateModels
     */
    @Override
    public Vector<ValueModel> getSampleDateModels() {
        if (sampleDateModels == null) {
            sampleDateModels = new Vector<>();
        }
        return sampleDateModels;
    }

    /**
     * @param sampleDateModels the sampleDateModels to set
     */
    @Override
    public void setSampleDateModels(Vector<ValueModel> sampleDateModels) {
        this.sampleDateModels = sampleDateModels;
    }

    /**
     * @return the sampleAnalysisType
     */
    @Override
    public String getSampleAnalysisType() {
        // May 2010 backwards compatible
        if (sampleAnalysisType == null) {
            sampleAnalysisType = SampleAnalysisTypesEnum.IDTIMS.getName();
        }
        return sampleAnalysisType;
    }

    /**
     * @param sampleAnalysisType the sampleAnalysisType to set
     */
    @Override
    public void setSampleAnalysisType(String sampleAnalysisType) {
        this.sampleAnalysisType = sampleAnalysisType;

    }

    /**
     * @return the concordiaGraphAxesSetup
     */
    @Override
    public GraphAxesSetup getConcordiaGraphAxesSetup() {
        if (concordiaGraphAxesSetup == null) {
            concordiaGraphAxesSetup = new GraphAxesSetup("C", 2);
        }
        return concordiaGraphAxesSetup;
    }

    /**
     * @param concordiaGraphAxesSetup the concordiaGraphAxesSetup to set
     */
    public void setConcordiaGraphAxesSetup(GraphAxesSetup concordiaGraphAxesSetup) {
        this.concordiaGraphAxesSetup = concordiaGraphAxesSetup;
    }

    /**
     * @return the terraWasserburgGraphAxesSetup
     */
    @Override
    public GraphAxesSetup getTerraWasserburgGraphAxesSetup() {
        if (terraWasserburgGraphAxesSetup == null) {
            terraWasserburgGraphAxesSetup = new GraphAxesSetup("T-W", 2);
        }
        return terraWasserburgGraphAxesSetup;
    }

    /**
     * @param terraWasserburgGraphAxesSetup the terraWasserburgGraphAxesSetup to
     * set
     */
    public void setTerraWasserburgGraphAxesSetup(GraphAxesSetup terraWasserburgGraphAxesSetup) {
        this.terraWasserburgGraphAxesSetup = terraWasserburgGraphAxesSetup;
    }

    /**
     * @return the mineralName
     */
    @Override
    public String getMineralName() {
        if (mineralName == null) {
            mineralName = MineralTypes.OTHER.getName();
        }
        return mineralName;
    }

    /**
     * @param mineralName the mineralName to set
     */
    @Override
    public void setMineralName(String mineralName) {
        this.mineralName = MineralTypes.validateStandardMineralTypeName(mineralName.trim());
    }

    /**
     * @return the analysisPurpose
     */
    @Override
    public ANALYSIS_PURPOSE getAnalysisPurpose() {
        if (analysisPurpose == null) {
            analysisPurpose = ANALYSIS_PURPOSE.NONE;
        }
        return analysisPurpose;
    }

    /**
     * @param analysisPurpose the analysisPurpose to set
     */
    @Override
    public void setAnalysisPurpose(ANALYSIS_PURPOSE analysisPurpose) {
        this.analysisPurpose = analysisPurpose;
    }

    /**
     * @return the calculateTWrhoForLegacyData
     */
    @Override
    public boolean isCalculateTWrhoForLegacyData() {
        return calculateTWrhoForLegacyData;
    }

    /**
     * @param calculateTWrhoForLegacyData the calculateTWrhoForLegacyData to set
     */
    @Override
    public void setCalculateTWrhoForLegacyData(boolean calculateTWrhoForLegacyData) {
        this.calculateTWrhoForLegacyData = calculateTWrhoForLegacyData;
    }

    /**
     * @return the mySESARSampleMetadata
     */
    public SESARSampleMetadata getMySESARSampleMetadata() {
        if (mySESARSampleMetadata == null) {
            mySESARSampleMetadata = new SESARSampleMetadata();
        }
        return mySESARSampleMetadata;
    }

    /**
     * @param mySESARSampleMetadata the mySESARSampleMetadata to set
     */
    public void setMySESARSampleMetadata(SESARSampleMetadata mySESARSampleMetadata) {
        this.mySESARSampleMetadata = mySESARSampleMetadata;
    }

    /**
     * @return the validatedSampleIGSN
     */
    public boolean isValidatedSampleIGSN() {
        return validatedSampleIGSN;
    }

    /**
     * @param validatedSampleIGSN the validatedSampleIGSN to set
     */
    @Override
    public void setValidatedSampleIGSN(boolean validatedSampleIGSN) {
        this.validatedSampleIGSN = validatedSampleIGSN;
    }

    /**
     * @return the tripoliSession
     */
    public TripoliSession getTripoliSession() {
        return tripoliSession;
    }

    /**
     * @param tripoliSession the tripoliSession to set
     */
    public void setTripoliSession(TripoliSession tripoliSession) {
        this.tripoliSession = tripoliSession;
    }

    /**
     * @return the archivedInRegistry
     */
    public boolean isArchivedInRegistry() {
        return archivedInRegistry;
    }

    /**
     * @param archivedInRegistry the archivedInRegistry to set
     */
    public void setArchivedInRegistry(boolean archivedInRegistry) {
        this.archivedInRegistry = archivedInRegistry;
    }

    /**
     * @return the sampleRegistry
     */
    public SampleRegistries getSampleRegistry() {
        return sampleRegistry;
    }

    /**
     * @param sampleRegistry the sampleRegistry to set
     */
    public void setSampleRegistry(SampleRegistries sampleRegistry) {
        this.sampleRegistry = sampleRegistry;
    }

    /**
     * @param reportSettingsModel the reportSettingsModel to set
     */
    @Override
    public void setReportSettingsModel(ReportSettingsInterface reportSettingsModel) {
        this.reportSettingsModel = reportSettingsModel;
    }

    /**
     * @return the reportSettingsModel
     */
    @Override
    public ReportSettingsInterface getReportSettingsModel() {
        return reportSettingsModel;
    }

    /**
     *
     */
    @Override
    public void restoreDefaultReportSettingsModel() {
        setReportSettingsModel(ReduxLabData.getInstance().getDefaultReportSettingsModelBySpecsType((getDefaultReportSpecsType() == null) ? "UPb" : getDefaultReportSpecsType()));
    }

    // used for deserialization to enforce backwards compatibility
    /**
     *
     * @return
     */
    protected Object readResolve() {
        if ((sampleAnalysisType == null) && !analyzed && isSampleTypeAnalysis()) {
            // backward compatible
            sampleAnalysisType = SampleAnalysisTypesEnum.IDTIMS.getName();
        }

        if ((sampleAnalysisType == null) && isSampleTypeCompilation()) {
            sampleAnalysisType = SampleAnalysisTypesEnum.COMPILED.getName();
            analyzed = true;
        }
        return this;
    }

    /**
     * @return the isotopeSystem
     */
    @Override
    public String getIsotopeSystem() {
        if (isotopeSystem == null) {
            isotopeSystem = "UPb";
        }
        return isotopeSystem;
    }

    /**
     * @return the defaultReportSpecsType
     */
    public String getDefaultReportSpecsType() {
        if (defaultReportSpecsType == null) {
            defaultReportSpecsType = "UPb";
        }
        return defaultReportSpecsType;
    }

    /**
     * @return the filteredFractionIDs
     */
    @Override
    public SortedSet<String> getFilteredFractionIDs() {
        if (filteredFractionIDs == null) {
            initFilteredFractionsToAll();
        }
        return filteredFractionIDs;
    }

    /**
     * @param filteredFractionIDs the filteredFractionIDs to set
     */
    @Override
    public void setFilteredFractionIDs(SortedSet<String> filteredFractionIDs) {
        this.filteredFractionIDs = filteredFractionIDs;
    }

    @Override
    public void initFilteredFractionsToAll() {
        this.filteredFractionIDs = Collections.synchronizedSortedSet(new TreeSet<>());
        for (int i = 0; i < UPbFractions.size(); i++) {
            filteredFractionIDs.add(UPbFractions.get(i).getFractionID());
        }
    }

    @Override
    public List<SeaWaterInitialDelta234UTableModel> getSeaWaterInitialDelta234UTableModels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSeaWaterInitialDelta234UTableModels(List<SeaWaterInitialDelta234UTableModel> seaWaterInitialDelta234UTableModels) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
