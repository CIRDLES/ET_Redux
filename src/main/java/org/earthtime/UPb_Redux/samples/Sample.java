/*
 * Sample.java
 *
 *
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.earthtime.ETReduxFrame;
import org.earthtime.Tripoli.sessions.TripoliSession;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotEditorDialog;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotEditorForLAICPMS;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotLegacyEditorForIDTIMS;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotLegacyEditorForLAICPMS;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.UPbFractionEditorDialog;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.UPbLegacyFractionEditorDialog;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.filters.FractionXMLFileFilter;
import org.earthtime.UPb_Redux.filters.ReduxFileFilter;
import org.earthtime.UPb_Redux.filters.XMLFileFilter;
import org.earthtime.UPb_Redux.fractions.AnalysisFraction;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reports.ReportSettings;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.SampleDateTypes;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.projects.EarthTimeSerializedFileInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.samples.AbstractSample;
import org.earthtime.utilities.FileHelper;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * A
 * <code>Sample</code> object contains all of the scientific data related to
 * aliquot single geological sample as well as additional methods to manipulate
 * this data.
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class Sample extends AbstractSample implements
        Serializable,
        SampleI,
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
     *
     */
    public static transient ETReduxFrame parentFrame;
    /**
     *
     */
    public transient DialogEditor myFractionEditor;
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
    /**
     * the path to which this <code>Sample</code> will be saved.
     */
    private String reduxSampleFilePath;
    /**
     * the collection of aliquots created for this <code>Sample</code>;
     * contained in aliquot vector for thread safety
     */
    private Vector<Aliquot> aliquots;
    /**
     * collection of individual aliquotFractionFiles within this
     * <code>Sample</code>.
     */
    private Vector<Fraction> UPbFractions;
    /**
     * the file of this <code>Sample</code>.
     */
//    private String sampleName;
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
    private ReportSettings reportSettingsModel;
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
     * @param labData the data of this <code>Sample</code> to which
     * <code>myReduxLabData</code> will be
     * @param sampleAnalysisType
     * @param defaultAnalysisPurpose
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    public Sample(
            String sampleName,
            String sampleType,
            String sampleAnalysisType,
            ReduxLabData labData,
            ANALYSIS_PURPOSE defaultAnalysisPurpose)
            throws BadLabDataException {
        this.sampleName = sampleName;
        this.sampleType = sampleType;
        this.sampleAnalysisType = sampleAnalysisType;
        this.analyzed = false;
        this.sampleIGSN = ReduxConstants.DEFAULT_IGSN;//"NONE";
        this.validatedSampleIGSN = false;
        this.sampleAnnotations = "";
        this.reduxSampleFileName = "";
        this.reduxSampleFilePath = "";

        Sample.saving = false;

        this.myReduxLabData = labData;
        this.reportSettingsModel = ReduxLabData.getInstance().getDefaultReportSettingsModel();

        this.sampleAgeInterpretationGUISettings = new SampleDateInterpretationGUIOptions();

        this.aliquots = new Vector<Aliquot>();
        this.UPbFractions = new Vector<Fraction>();

        this.physicalConstantsModel = myReduxLabData.getDefaultPhysicalConstantsModel();

        this.automaticDataUpdateMode = false;
        this.sampleFolderSaved = null;
        this.sampleDateModels = new Vector<ValueModel>();

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
    }

    /**
     *
     * @param sampleType
     * @param sampleAnalysisType
     * @param labData
     * @param analysisPurpose
     * @return
     * @throws BadLabDataException
     */
    public static Sample initializeNewSample( //
            String sampleType, //
            String sampleAnalysisType,
            ReduxLabData labData,
            ANALYSIS_PURPOSE analysisPurpose)
            throws BadLabDataException {

        String sampleName = "NEW SAMPLE";
        boolean analyzed = false;

        if (sampleType.equalsIgnoreCase(SampleTypesEnum.PROJECT.getName())) {
            sampleName = SampleTypesEnum.PROJECT.getName();
            analyzed = true;
        } else if (sampleType.equalsIgnoreCase(SampleTypesEnum.LEGACY.getName())) {
            sampleName = "LEGACY SAMPLE";
            analyzed = true;
        } else if (sampleType.equalsIgnoreCase(SampleTypesEnum.COMPILATION.getName())) {
            sampleName = "COMPILED SAMPLE";
            //analyzed = false;//true;
        } else if (sampleType.equalsIgnoreCase("NONE")) {
            sampleName = "NONE";
        } else if (sampleType.equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName())) {
            sampleName = "LIVE WORKFLOW SAMPLE";
            // feb 2010: the intent is to refactor to just SAMPLEFOLDER and remove auto-detected and liveworkflow
        } else if (sampleType.equalsIgnoreCase(SampleTypesEnum.SAMPLEFOLDER.getName())) {
            sampleName = "NEW SAMPLE";
        }

        Sample retVal = //
                new Sample(sampleName, sampleType, sampleAnalysisType, labData, analysisPurpose);

        //set flag for whether analysis was performed elsewhere and we just have legacy results
        retVal.setAnalyzed(analyzed);

        retVal.setAutomaticDataUpdateMode(false);

        return retVal;
    }

    /**
     *
     * @param myLabData
     */
    public void setUpSample(ReduxLabData myLabData) {
        // refactored to here from UPbReduxFrame Jan 2012
        // oct 2011
        // force aliquot registry onto sample SESAR starting oct 2014
        if (getSampleRegistry() == null) {
            setSampleRegistry(SampleRegistries.SESAR);
        }

        // May 2010 update sampleAnalysisType in preparation for LAICPMS analysis
        if (isTypeAnalysis() && getSampleAnalysisType().equals("")) {
            setSampleAnalysisType(SampleAnalysisTypesEnum.IDTIMS.getName());
        }

        // April 2011 we are altering SampleIGSN to be of form rrr.IGSN
        //  where rrr is registry as per enum SampleRegistries
        // this means that if sample is already flagged as validated - i.e. at SESAR
        // we check that it is valid at GeochronID and change SampleIGSN and percolate it
        // down to all Aliquots
        updateWithRegistrySampleIGSN();

        if (!isTypeLegacy()) {
            registerSampleWithLabData(myLabData);
        } else {
            setMyReduxLabData(myLabData);

            // dec 2012
            if (getUPbFractions().size() > 0) {
                // June 2010 fix for old legacy fractions
                Vector<Fraction> convertedF = new Vector<Fraction>();
                for (Fraction f : getUPbFractions()) {
                    if (f instanceof UPbFraction) {
                        // convert to UPbLegacyFraction
                        System.out.println("Converting legacy legacy");
                        Fraction legacyF = new UPbLegacyFraction(f.getFractionID());

                        legacyF.setAnalysisMeasures(f.getAnalysisMeasures());
                        // these two are legacy leftovers and need to be zeroed so report settings does not show columns
                        legacyF.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()).setValue(BigDecimal.ZERO);
                        legacyF.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()).setValue(BigDecimal.ZERO);

                        legacyF.setRadiogenicIsotopeRatios(f.getRadiogenicIsotopeRatios());
                        legacyF.setRadiogenicIsotopeDates(f.getRadiogenicIsotopeDates());
                        legacyF.setCompositionalMeasures(f.getCompositionalMeasures());
                        legacyF.setSampleIsochronRatios(f.getSampleIsochronRatios());

                        legacyF.setSampleName(f.getSampleName());
                        legacyF.setZircon(f.isZircon());

                        ((UPbFractionI) legacyF).setAliquotNumber(((UPbFraction) f).getAliquotNumber());
                        ((UPbFractionI) legacyF).setRejected(((UPbFraction) f).isRejected());
                        ((UPbFractionI) legacyF).setFractionNotes(((UPbFraction) f).getFractionNotes());
                        ((UPbFractionI) legacyF).setPhysicalConstantsModel(((UPbFraction) f).getPhysicalConstantsModel());
                        ((UPbFractionI) legacyF).setChanged(false);

                        legacyF.setIsLegacy(true);

                        convertedF.add(legacyF);
                    } else {
                        f.setIsLegacy(true);
                        convertedF.add(f);
                    }
                }

                setUPbFractions(convertedF);

                // modified logic oct 2010 ... sample manager allows reset
                // additional test for missing T-W rho calculation
                // use first fraction to test for rho < -1 or 0  (both used as default for non-existent rho)
                double twRho = //
                        getUPbFractions().get(0).//
                        getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").getValue().doubleValue();
                if ( /*
                         * (twRho == 0) ||
                         */(twRho < -1.0)) {
                    for (Fraction f : getUPbFractions()) {
//                        try {
                        //((UPbLegacyFraction) f).calculateTeraWasserburgRho();
                        f.getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r")//
                                .setValue(BigDecimal.ZERO);
//                        } catch (Exception e) {
//                        }
                    }
                }
            }
        }

        // June 2010 be sure lab name is updated to labdata labname when used in reduction
        if (isTypeAnalysis() || isTypeLiveUpdate() || isTypeLegacy()) {
            updateSampleLabName();
        }

    }

    /**
     * adds an <code>Aliquot</code> to <code>aliquots</code>. It is created with
     * aliquot number relative to its position in the array such that the first
     * <code>aliquot</code> in the array is given 1, the second is given 2, and
     * so on. It is created under the file <code>Aliquot-#</code> with
     * <code>#</code> being replaced by the same number that was given as the
     * first paramater. The remaining fields are set to correspond to the data
     * found in this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists with proper data
     * @post a new <code>Aliquot</code> is created and added to
     * <code>aliquots</code> and the size of      <code>Aliquots</code> is
     *          returned
     *
     * @return
     * <code>int</code> - if successful, returns the size of the array after
     * adding the new <code>Aliquot</code>. Else, returns -1.
     */
    private int addNewDefaultAliquot() {
        int retval = -1;
        try {
            Aliquot tempAliquot
                    = new UPbReduxAliquot(
                            aliquots.size() + 1,
                            "Aliquot-" + Integer.toString(aliquots.size() + 1),
                            getMyReduxLabData(),
                            getPhysicalConstantsModel(),
                            isAnalyzed(),
                            getMySESARSampleMetadata());

            tempAliquot.setSampleIGSN(getSampleIGSN());
            aliquots.add(tempAliquot);
            setChanged(true);
            retval = aliquots.size();
        } catch (BadLabDataException ex) {
            Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    /**
     *
     * @param aliquotName
     * @return
     * @throws ETException
     */
    public Aliquot addNewAliquot(String aliquotName) throws ETException {
        if (getAliquotByName(aliquotName) == null) {
            Aliquot tempAliquot;// = null;
            tempAliquot = (UPbReduxAliquot) getAliquotByNumber(addNewDefaultAliquot());
            if (aliquotName.length() > 0) {
                tempAliquot.setAliquotName(aliquotName);
            }

            // dec 2011 initialize weighted mean options
            Map<String, String> weightedMeanOptions = getSampleDateInterpretationGUISettings().getWeightedMeanOptions();

            for (int i = 0; i < SampleDateTypes.getSampleDateModelTypes().length; i++) {
                String sampleDateType = SampleDateTypes.getSampleDateType(i);
                if (sampleDateType.startsWith("weighted")) {
                    String aliquotFlagsString = weightedMeanOptions.get(sampleDateType);
                    if (aliquotFlagsString == null) {
                        aliquotFlagsString = "";
                    }

                    StringBuilder aliquotFlags = new StringBuilder(aliquotFlagsString);

                    aliquotFlags.append("0");

                    weightedMeanOptions.put(sampleDateType, aliquotFlags.toString());
                }

            }

            return tempAliquot;
        } else {
            throw new ETException(null, "Sample already contains this aliquot name");
        }
    }

    /**
     *
     * @param nameAliquotA
     * @param nameAliquotB
     * @return
     */
    public boolean swapOrderOfTwoAliquots(String nameAliquotA, String nameAliquotB) {

        boolean retVal = false;

        Aliquot aliquotA = getAliquotByName(nameAliquotA);
        Aliquot aliquotB = getAliquotByName(nameAliquotB);

        if ((aliquotA != null) && (aliquotB != null)) {

            int numberAliquotA = ((UPbReduxAliquot) aliquotA).getAliquotNumber();
            Vector<Fraction> fractionsAliquotA = ((UPbReduxAliquot) aliquotA).getAliquotFractions();

            int numberAliquotB = ((UPbReduxAliquot) aliquotB).getAliquotNumber();
            Vector<Fraction> fractionsAliquotB = ((UPbReduxAliquot) aliquotB).getAliquotFractions();

            // switch assigned aliquot numbers
            ((UPbReduxAliquot) aliquotA).setAliquotNumber(numberAliquotB);
            for (Fraction f : fractionsAliquotA) {
                ((UPbFractionI) f).setAliquotNumber(numberAliquotB);
            }
            ((UPbReduxAliquot) aliquotB).setAliquotNumber(numberAliquotA);
            for (Fraction f : fractionsAliquotB) {
                ((UPbFractionI) f).setAliquotNumber(numberAliquotA);
            }
            // switch aliquots in sample's aliquot vector which stores them in order
            // note that this vector is effectively one-based so aliquot shift of one is required
            aliquots.setElementAt(aliquotA, numberAliquotB - 1);
            aliquots.setElementAt(aliquotB, numberAliquotA - 1);

            // modified dec 2011 to also switch weighted mean display choices
            Map<String, String> weightedMeanOptions = getSampleDateInterpretationGUISettings().getWeightedMeanOptions();

            for (int i = 0; i < SampleDateTypes.getSampleDateModelTypes().length; i++) {
                String sampleDateType = SampleDateTypes.getSampleDateType(i);
                if (sampleDateType.startsWith("weighted")) {
                    StringBuilder aliquotFlags = new StringBuilder(weightedMeanOptions.get(sampleDateType));
                    String aliquotAWMflag = aliquotFlags.toString().substring(numberAliquotA - 1, numberAliquotA);
                    String aliquotBWMflag = aliquotFlags.toString().substring(numberAliquotB - 1, numberAliquotB);
                    aliquotFlags.replace(numberAliquotA - 1, numberAliquotA, aliquotBWMflag);
                    aliquotFlags.replace(numberAliquotB - 1, numberAliquotB, aliquotAWMflag);

                    weightedMeanOptions.put(sampleDateType, aliquotFlags.toString());
                }

            }

            retVal = true;
        }

        return retVal;
    }

    /**
     * loads aliquot Sample after aliquot .redux file generation from an Excel
     * file
     *
     * @pre the chosen file located in the directory <code>dir</code> is a
     * proper .redux file generated from an Excel file.
     * @post the file is returned for use by another method
     *
     * @param frame the frame where the samples names to save were selected
     * @param dir the directory in which the redux files were saved
     * @return <code>File</code> - the loaded file
     */
    public File loadSampleAfterExcelImport(Component frame, File dir) {

        JOptionPane jopt = new JOptionPane();
        File sampleFile = null;

        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Redux file(s) successfully generated."//
                + "Do you want to open one of the imported files ?",//
                "import a redux file?",//
                JOptionPane.YES_NO_OPTION);
        if (choice == 0) {
            String dialogTitle = "Select a U-Pb Redux File to Open: *.redux";
            final String fileExtension = ".redux";
            FileFilter nonMacFileFilter = new ReduxFileFilter();

            sampleFile
                    = FileHelper.AllPlatformGetFile(dialogTitle, dir, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        }
        return sampleFile;
    }

    /**
     *
     * @param aliquot
     */
    public void removeAliquot(Aliquot aliquot) {
        Vector<Fraction> aliquotFractions = ((UPbReduxAliquot) aliquot).getAliquotFractions();
        for (int i = 0; i < aliquotFractions.size(); i++) {
            removeUPbReduxFraction((UPbFraction) aliquotFractions.get(i));
        }

    }

    /**
     * finds the <code>Aliquot</code> numbered <code>aliquotNum</code> in the
     * array <code>aliquots</code>.
     *
     * @pre an <code>Aliquot</code> exists in <code>aliquots</code> numbered
     * with <code>aliquotNum</code>
     * @post the <code>Aliquot</code> whose number corresponds to argument
     * <code>aliquotNum</code> is found and returned
     *
     * @param aliquotNum number of the <code>Aliquot</code> to retrieve
     * @return <code>Aliquot</code> - the <code>Aliquot</code> from
     * <code>aliquots</code> whose number corresponds to the argument
     * <code>aliquotNum</code>
     */
    public Aliquot getAliquotByNumber(int aliquotNum) {
        // here we populate the aliquotFractionFiles of aliquot in case they have changed
        // aliquots are really aliquot view of the aliquotFractionFiles (MVC architecture)
        Aliquot retAliquot = aliquots.get(aliquotNum - 1);

        Vector<Fraction> retFractions = new Vector<Fraction>();

        for (Iterator it = getUPbFractions().iterator(); it.hasNext();) {
            Fraction temp = ((Fraction) it.next());
            if (((UPbFractionI) temp).getAliquotNumber() == aliquotNum) {
                retFractions.add(temp);
            }
        }

        ((UPbReduxAliquot) retAliquot).setAliquotFractions(retFractions);
        ((UPbReduxAliquot) retAliquot).setMyReduxLabData(getMyReduxLabData());

        return retAliquot;
    }
    
    /**
     * Feb 2015 This method handles the messy situation where a project refers to each of its aliquots
     * by index 1...n but to upload project aliquots individually to Geochron with concordia etc
     * means there is only one aliquot per sample and we ignore its number.
     * @param aliquotnum
     * @return 
     */
    public String getNameOfAliquotFromSample(int aliquotNum){
        String retval;
        if (aliquots.size() == 1){
            retval = aliquots.get(0).getAliquotName();
        } else {
            retval = aliquots.get(aliquotNum - 1).getAliquotName();
        }
        
        return retval;
    }

    /**
     * finds the <code>Aliquot</code> named <code>file</code> in the array
     * <code>aliquots</code>.
     *
     * @pre an <code>Aliquot</code> exists in <code>aliquots</code> named
     * <code>file</code>
     * @post the <code>Aliquot</code> whose file corresponds to argument
     * <code>file</code> is found and returned
     *
     * @return <code>Aliquot</code> - the <code>Aliquot</code> from
     * <code>aliquots</code> whose file correspongs to the argument
     * <code>file</code>
     */
    public Aliquot getAliquotByName(String name) {
        Aliquot retAliquot = null;

        for (int aliquotIndex = 0; aliquotIndex < aliquots.size(); aliquotIndex++) {
            if (aliquots.get(aliquotIndex).getAliquotName().equalsIgnoreCase(name)) {
                retAliquot = aliquots.get(aliquotIndex);
                // update via this miserable hack the list of upbfractions Nov 2009 to support live update manager
                getAliquotByNumber(aliquotIndex + 1);
                break;
            }
        }

        return retAliquot;
    }

    /**
     *
     * @param fractions
     * @param aliquotNumber
     */
    public void addUPbFractionVector(Vector<Fraction> fractions, int aliquotNumber) {
        for (Fraction f : fractions) {
            f.setSampleName(sampleName);
            ((UPbFractionI) f).setAliquotNumber(aliquotNumber);
            addUPbFraction(f);
        }
    }

    /**
     *
     * @param fractions
     * @param aliquotNumber
     */
    public void addUPbFractionArrayList(ArrayList<Fraction> fractions, int aliquotNumber) {

        for (Fraction f : fractions) {
            f.setSampleName(sampleName);
            ((UPbFractionI) f).setAliquotNumber(aliquotNumber);
            addUPbFraction(f);
        }
    }

    /**
     * adds aliquot <code>UPbFraction</code> to the <code>Sample</code>'s set of
     * <code>Fractions</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post a new default <code>Fraction</code> is added to this
     * <code>Sample</code>'s <code>Fractions</code>
     *
     * @param newFraction the <code>Fraction</code> to add to this
     * <code>Sample</code>
     */
    public void addUPbFraction(Fraction newFraction) {
        getUPbFractions().add(newFraction);
        setChanged(true);
    }

    /**
     * removes the <code>UPbFraction</code> found at <code>index</code> from
     * this <code>Sample</code>'s set of <code>Fractions</code>
     *
     * @pre a <code>Fraction</code> exists in this <code>Sample</code>'s set of
     * <code>Fractions</code> at <code>index</code>
     * @post the <code>Fraction</code> found at <code>index</code> is removed
     * from the set of <code>Fractions</code>
     *
     * @param index the index into the array of <code>Fractions</code> where the
     * <code>Fraction</code> to be removed can be found
     */
    public void removeUPbReduxFraction(int index) {
        boolean fracStatus = ((UPbFractionI) getUPbFractions().get(index)).isChanged();
        try {
            getUPbFractions().remove(index);
        } finally {
        }
        setChanged(isChanged() || fracStatus);

    }

    /**
     * removes the <code>UPbFraction</code> from this <code>Sample</code>'s set
     * of <code>Fractions</code> that corresponds to the argument
     * <code>fraction</code>
     *
     * @pre a <code>Fraction</code> exists in this <code>Sample</code>'s set of
     * <code>Fractions</code> that corresponds to <code>fraction</code>
     * @post the <code>Fraction</code> that corresponds to the argument
     * <code>fraction</code> is removed
     *
     * @param fraction
     */
    public void removeUPbReduxFraction(Fraction fraction) {
        boolean fracStatus = ((UPbFractionI) fraction).isChanged();
        try {
            getUPbFractions().remove(fraction);
            ((UPbFractionI) fraction).getAliquotNumber();
        } finally {
        }
        setChanged(isChanged() || fracStatus);
    }

    /**
     *
     * @param aliquotNumber
     * @throws BadLabDataException
     */
    public void addDefaultUPbFractionToAliquot(int aliquotNumber)
            throws BadLabDataException {
        Fraction defFraction = new UPbFraction("NONE");
        ((UPbFractionI) defFraction).setAliquotNumber(aliquotNumber);

        initializeDefaultUPbFraction(defFraction);

        // sept 2010 add Aliquot defaults
        Aliquot aliquot = getAliquotByNumber(aliquotNumber);
        ReduxLabData labData = ((UPbReduxAliquot) aliquot).getMyReduxLabData();

        String tracerID = ((UPbReduxAliquot) aliquot).getDefaultTracerID();
        AbstractRatiosDataModel tracer = labData.getATracerModel(tracerID);

        ((UPbFraction) defFraction).setMyLabData(labData);
        ((UPbFraction) defFraction).setTracer(tracer);

    }

    /**
     *
     * @param aliquotNumber
     * @throws BadLabDataException
     */
    public void addDefaultUPbLegacyFractionToAliquot(int aliquotNumber)
            throws BadLabDataException {
        Fraction defFraction = new UPbLegacyFraction("NONE");
        ((UPbFractionI) defFraction).setAliquotNumber(aliquotNumber);

        initializeDefaultUPbFraction(defFraction);
    }

    private void initializeDefaultUPbFraction(Fraction defFraction)
            throws BadLabDataException {
        //reset counter if no aliquotFractionFiles
        if (getUPbFractions().isEmpty()) {
            setDefaultFractionCounter(0);
        }

        setDefaultFractionCounter(getDefaultFractionCounter() + 1);

        defFraction.setSampleName(getSampleName());
        defFraction//
                .setFractionID(getDefaultFractionName() + Integer.toString(getDefaultFractionCounter()));
        defFraction//
                .setGrainID(defFraction.getFractionID());

        Fraction existingFraction = getFractionByID(defFraction.getFractionID());
        // handle repeated default fractionIDs
        if (existingFraction != null) {
            defFraction//
                    .setFractionID(((UPbFraction) defFraction).getFractionID() + "r"); // not robust but does it for now feb 2010
            defFraction//
                    .setGrainID(defFraction.getGrainID());
        }
        // must be saved or is assumed deleted during edit
        ((UPbFractionI) defFraction).setDeleted(false);
        ((UPbFractionI) defFraction).setChanged(false);

        addUPbFraction(defFraction);
    }

    //TODO: refactor these edit methods out of sample - MVC !!
    /**
     * opens aliquot modal editor for the <code>Fraction</code> indicated by
     * argument <code>fraction</code> and opened to the editing tab indicated by
     * argument <code>selectedTab</code>. <code>selectedTab</code> is valid only
     * if it contains aliquot number between zero and seven inclusive.
     *
     * @pre the <code>Fraction</code> corresponding to <code>fraction</code>
     * exists in this <code>Sample</code> and <code>selectedTab</code> is a
     * valid tab number
     * @post an editor for the specified <code>Fraction</code> is opened to the
     * specified tab
     *
     * @param fraction the <code>Fraction</code> to be edited
     * @param selectedTab the tab to open the editor to
     */
    @Override
    public void editUPbFraction(Fraction fraction, int selectedTab) {

        // oct 2014
        parentFrame.forceCloseOfSampleDateInterpretations();

        // Create aliquot new FractionEditorDialog
        int aliquotNumber = ((UPbFractionI) fraction).getAliquotNumber();

        myFractionEditor = null;

        if (sampleType.equalsIgnoreCase(SampleTypesEnum.LEGACY.getName())
                || (sampleType.equalsIgnoreCase(SampleTypesEnum.COMPILATION.getName())
                && !fraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).hasPositiveValue())//
                || fraction.isLegacy()) {
            myFractionEditor
                    = new UPbLegacyFractionEditorDialog(
                            parentFrame,
                            true,
                            getAliquotByNumber(aliquotNumber),
                            fraction,
                            selectedTab,
                            false);
        } else if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.IDTIMS.getName())
                || (sampleType.equalsIgnoreCase(SampleTypesEnum.COMPILATION.getName()))){
// TODO: Need kwiki page for LAICPMS               || (sampleType.equalsIgnoreCase(SampleTypesEnum.PROJECT.getName()))) {

            myFractionEditor
                    = new UPbFractionEditorDialog(
                            parentFrame,
                            true,
                            getAliquotByNumber(aliquotNumber),
                            fraction,
                            selectedTab,
                            sampleType.equalsIgnoreCase(SampleTypesEnum.COMPILATION.getName()));
        }

        if (myFractionEditor != null) {
            try {
                myFractionEditor.setTitle(
                        "Sample: "// 
                        + getSampleName()// 
                        + "   [Physical Constants: "//
                        + getPhysicalConstantsModel().getNameAndVersion() + "]");

            } catch (BadLabDataException badLabDataException) {
            }

            myFractionEditor.setVisible(true);

            // post-process the editor's results
            setChanged(isChanged() || ((UPbFractionI) fraction).isChanged());
            // feb 2010
            if (isChanged()) {
                saveTheSampleAsSerializedReduxFile();
            }

            if (((UPbFractionI) fraction).isDeleted()) {
                removeUPbReduxFraction(fraction);
            }

            // these statements release editor and prevent livwWorkflow from backtracking as it must do
            // if navigating fractions while staying open
            myFractionEditor.dispose();
            myFractionEditor = null;
        }

    }

    /**
     * opens aliquot modal editor for the </code>Aliquot</code> specified by
     * <code>aliquotNum</code>. The <code>Aliquot</code>'s
     * <code>Fractions</code> are populated on the fly.
     *
     * @pre an <code>Aliquot</code> exists with the number specified by argument
     * <code>aliquotNum</code>
     * @post an editor for the specified <code>Aliquot</code> is opened
     *
     * @param aliquotNum the number of the <code>Aliquot</code> to be edited
     */
    public void editAliquotByNumber(int aliquotNum) {

        // added march 2009 so that changes to fraction tab are saved upon use of aliquot button
        this.saveTheSampleAsSerializedReduxFile();

        editAliquot(getAliquotByNumber(aliquotNum));
    }

    /**
     *
     * @param aliquotName
     */
    public void editAliquotByName(String aliquotName) {

        // added march 2009 so that changes to fraction tab are saved upon use of aliquot button
        this.saveTheSampleAsSerializedReduxFile();

        editAliquot(getAliquotByName(aliquotName));
    }

    /**
     *
     * @param aliquot
     */
    public void editAliquot(Aliquot aliquot) {
        DialogEditor myEditor = null;

        if (sampleType.equalsIgnoreCase(SampleTypesEnum.PROJECT.getName())) {
            // do nothing for now
            myEditor = new AliquotLegacyEditorForLAICPMS(parentFrame, true, this, aliquot);
        } else if (sampleType.equalsIgnoreCase(SampleTypesEnum.LEGACY.getName())//
                && getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.IDTIMS.getName())) {
            // May 2010 backward compatibility
            ((UPbReduxAliquot) aliquot).setCompiled(false);
            myEditor = new AliquotLegacyEditorForIDTIMS(parentFrame, true, this, aliquot);

        } else if (sampleType.equalsIgnoreCase(SampleTypesEnum.LEGACY.getName()) //
                && getSampleAnalysisType().toUpperCase().startsWith(SampleAnalysisTypesEnum.LAICPMS.getName())) {
            // May 2010 backward compatibility
            ((UPbReduxAliquot) aliquot).setCompiled(false);
            myEditor = new AliquotLegacyEditorForLAICPMS(parentFrame, true, this, aliquot);

            // oct 2014 
        } else if (sampleType.equalsIgnoreCase(SampleTypesEnum.LEGACY.getName()) //
                && getSampleAnalysisType().toUpperCase().startsWith(SampleAnalysisTypesEnum.LASS.getName())) {
            // May 2010 backward compatibility
            ((UPbReduxAliquot) aliquot).setCompiled(false);
            myEditor = new AliquotLegacyEditorForLAICPMS(parentFrame, true, this, aliquot);

        } else if (sampleType.equalsIgnoreCase(SampleTypesEnum.ANALYSIS.getName()) //
                && getSampleAnalysisType().toUpperCase().startsWith(SampleAnalysisTypesEnum.LAICPMS.getName())) {
            // June 2013 temp for project samples from Tripolized LAICPMS
            ((UPbReduxAliquot) aliquot).setCompiled(false);
            myEditor = new AliquotEditorForLAICPMS(parentFrame, true, this, aliquot);

        } else {
            myEditor = new AliquotEditorDialog(parentFrame, true, this, aliquot);
        }

        JDialog.setDefaultLookAndFeelDecorated(true);

        if (myEditor != null) {
            myEditor.setVisible(true);
        }
    }

    /**
     *
     * @param aliquot
     */
    public void toggleAliquotFractionsRejectedStatus(UPbReduxAliquot aliquot) {
        for (int i = 0; i < aliquot.getAliquotFractions().size(); i++) {
            ((UPbFractionI) aliquot.getAliquotFractions().get(i)).toggleRejectedStatus();
        }
    }

    /**
     *
     * @param aliquot
     */
    public void importAliquotFromAnotherSample(Aliquot aliquot) {
        Aliquot importedAliquot = new UPbReduxAliquot();

        // aliquot numbering is 1-based
        int aliquotNumber = aliquots.size() + 1;

        ((UPbReduxAliquot) importedAliquot).setAliquotNumber(aliquotNumber);
        ((UPbReduxAliquot) importedAliquot).setMyReduxLabData(ReduxLabData.getInstance());
        ((UPbReduxAliquot) importedAliquot).setCompiled(false);

        // prepend filename of sample to aliquot
        importedAliquot.setAliquotName(//
                ((UPbReduxAliquot) aliquot).getAliquotFractions().get(0).getSampleName().trim()//
                + "::"//
                + aliquot.getAliquotName());

        ((UPbReduxAliquot) importedAliquot).setAliquotFractions(new Vector<Fraction>());

        Iterator<Fraction> aliquotFractionIterator = ((UPbReduxAliquot) aliquot).getAliquotFractions().iterator();
        while (aliquotFractionIterator.hasNext()) {
            Fraction fraction = aliquotFractionIterator.next();

            Fraction nextFraction = new UPbLAICPMSFraction(
                    aliquotNumber,
                    fraction);

            ((UPbFractionI) fraction).setAliquotNumber(aliquotNumber);
            ((UPbReduxAliquot) importedAliquot).getAliquotFractions().add(fraction);

            UPbFractions.add(fraction);
        }

        aliquots.add(importedAliquot);
    }

    /**
     * reads in data from the XML file specified by argument
     * <code>aliquotFile</code> and adds any <code>Aliquots</code> found in the
     * file to this <code>Sample</code>.
     *
     * @param aliquot
     * @param aliquotSource
     * @pre file specified by <code>aliquotFile</code> is an XML file containing
     * valid
     * <code>Aliquots</code>
     * @post all <code>Aliquots</code> found in the file are added to this
     * <code>Sample</code>
     * @throws java.io.IOException IOException
     * @throws org.earthtime.XMLExceptions.ETException ETException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    @Override
    public void processXMLAliquot(Aliquot aliquot, String aliquotSource)
            throws IOException,
            ETException,
            BadLabDataException {

        // aliquot numbering is 1-based
        int aliquotNumber = aliquots.size() + 1;

        ((UPbReduxAliquot) aliquot).setAliquotNumber(aliquotNumber);
        ((UPbReduxAliquot) aliquot).setMyReduxLabData(ReduxLabData.getInstance());
        ((UPbReduxAliquot) aliquot).setCompiled(true);

        // prepend filename of sample to aliquot
        aliquot.setAliquotName(//
                aliquot.getAnalysisFractions().get(0).getSampleName().trim()//
                + "::"//
                + aliquot.getAliquotName());

        ((UPbReduxAliquot) aliquot).setAliquotFractions(new Vector<Fraction>());

        // jan 2015 this needs reworking to detect the type of fraction coming in
        // for now we will assume an aliquot has all the same type of fraction
        // in terms of instrumentalmethod
        Iterator aliquotFractionIterator = aliquot.getAnalysisFractions().iterator();

        if (aliquot.usesIDTIMS()) {
            while (aliquotFractionIterator.hasNext()) {
                // convert analysisFraction to UPbReduxFraction
                AnalysisFraction fraction = (AnalysisFraction) aliquotFractionIterator.next();

                // identify tracer and pbBlank
                AbstractRatiosDataModel tracer = aliquot.getATracer(fraction.getTracerID());
                if (tracer == null) {
                    tracer = getMyReduxLabData().getNoneTracer();
                }
                AbstractRatiosDataModel pbBlank = aliquot.getAPbBlank(fraction.getPbBlankID());
                if (pbBlank == null) {
                    pbBlank = getMyReduxLabData().getNonePbBlankModel();
                }

                // aug 2010 set initialPbModel
                if (fraction.getInitialPbModel() == null) {
                    // model ReduxConstants.NONE
                    fraction.setInitialPbModel(getMyReduxLabData().getNoneInitialPbModel());
                }

                Fraction nextFraction = null;
                if (fraction.isLegacy()) {
                    nextFraction = new UPbLegacyFraction(
                            aliquotNumber,
                            fraction);
                } else {
                    nextFraction = new UPbFraction(
                            aliquotNumber,
                            fraction,
                            getMyReduxLabData(),
                            tracer,
                            pbBlank);

                    // aug 2010 we need to reset mean alphas if fractionation corrected
                    if (fraction.isFractionationCorrectedPb()) {
                        ((UPbFraction) nextFraction).setMeanAlphaPb(fraction.getAnalysisMeasure(AnalysisMeasures.alphaPb.getName()).getValue());
                    }

                    if (fraction.isFractionationCorrectedU()) {
                        ((UPbFraction) nextFraction).setMeanAlphaU(fraction.getAnalysisMeasure(AnalysisMeasures.alphaU.getName()).getValue());
                    }

                    // aug 2010 refine the source info for compiled fractions
                    ((UPbFraction) nextFraction).setSourceFilePb(aliquotSource);
                    ((UPbFraction) nextFraction).setSourceFileU(aliquotSource);

                }

                ValueModel alphaPbModel = aliquot.getAnAlphaPbModel(fraction.getAlphaPbModelID());
                if (alphaPbModel == null) {
                    alphaPbModel = myReduxLabData.getNoneAlphaPbModel();
                    nextFraction.setAlphaPbModelID(alphaPbModel.getName());
                }

                ValueModel alphaUModel = aliquot.getAnAlphaUModel(fraction.getAlphaUModelID());
                if (alphaUModel == null) {
                    alphaUModel = myReduxLabData.getFirstAlphaUModel();
                    nextFraction.setAlphaUModelID(alphaUModel.getName());
                }

                ((UPbFractionI) nextFraction).setAlphaPbModel(alphaPbModel);
                ((UPbFractionI) nextFraction).setAlphaUModel(alphaUModel);

                ((UPbFractionI) nextFraction).setPhysicalConstantsModel(aliquot.getPhysicalConstants());// was sample.getphys...

                ((UPbReduxAliquot) aliquot).getAliquotFractions().add(nextFraction);

                getUPbFractions().add(nextFraction);
            }
            
        } else if (aliquot.usesMCIPMS()) {
            while (aliquotFractionIterator.hasNext()) {
                // convert analysisFraction to UPbReduxFraction
                AnalysisFraction fraction = (AnalysisFraction) aliquotFractionIterator.next();

                Fraction nextFraction = null;
                if (fraction.isLegacy()) {
                    nextFraction = new UPbLegacyFraction(
                            aliquotNumber,
                            fraction);
                } else {
                    nextFraction = new UPbLAICPMSFraction(
                            aliquotNumber,
                            fraction,
                            getMyReduxLabData());
                }

                ((UPbFractionI) nextFraction).setPhysicalConstantsModel(aliquot.getPhysicalConstants());// was sample.getphys...

                ((UPbReduxAliquot) aliquot).getAliquotFractions().add(nextFraction);

                getUPbFractions().add(nextFraction);
            }
        }
        aliquots.add(aliquot);
        setChanged(false);
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

        Fraction fractionFromFile = new UPbFraction("NONE");
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
                    && !((UPbFraction) fractionFromFile).getSampleName().equalsIgnoreCase(getSampleName())) {
                throw new ETException(
                        null,
                        new String[]{"The sample name: " + ((UPbFraction) fractionFromFile).getSampleName() + "\n",
                            "specified in the Fraction File:\n",
                            fractionFile.getName() + "\n",
                            "differs from the open Sample's name: " + getSampleName() + ".\n",
                            "\nPlease correct the discrepancy and try again."
                        });
            }// else {
            Fraction existingFraction = getFractionByID(fractionFromFile.getFractionID());
            if (existingFraction == null) {
                System.out.println("New UPbReduxFraction");
                // AUG 2011 moved this improved logic here from readXMLFraction
                if (((UPbFraction) fractionFromFile).getTracer() == null) {
                    ((UPbFraction) fractionFromFile)//
                            .setTracer(((UPbFraction) fractionFromFile).getMyLabData().getNoneTracer());
                }
                addUPbFraction(fractionFromFile);
            } else {
                System.out.println("Existing Fraction = " + existingFraction.getFractionID() + " updating type = " + ((UPbFraction) fractionFromFile).getRatioType());
                boolean didUpdate
                        = ((UPbFraction) existingFraction).updateUPbFraction(fractionFromFile, isFractionDataOverriddenOnImport());

                setChanged(didUpdate);
            }

            //  }
        } else {
            // do nothing
        }

        // returns "NONE" if file is not processed
        return ((UPbFraction) fractionFromFile).getSampleName();
    }

    /**
     * retrieves the <code>Fraction</code> specified by argument <code>ID</code>
     * from this <code>Sample</code>'s set of <code>Fractions</code>.
     *
     * @pre a <code>Fraction</code> exists whose ID corresponds to argument
     * <code>ID</code>
     * @post that <code>Fraction</code> is found and returned
     *
     * @param ID the ID of the <code>Fraction</code> that should be retrieved
     * @return <code>Fraction</code> - the <code>Fraction</code> in this
     * <code>Sample</code> whose ID corresponds to argument <code>ID</code>
     */
    public Fraction getFractionByID(String ID) {
        Fraction retFraction = null;

        for (int UPbFractionIndex = 0; UPbFractionIndex < UPbFractions.size(); UPbFractionIndex++) {
            if (UPbFractions.get(UPbFractionIndex).getFractionID().equalsIgnoreCase(ID)) {
                retFraction = UPbFractions.get(UPbFractionIndex);
                break;
            }
        }

        return retFraction;
    }

    /**
     *
     * @return @throws BadLabDataException
     */
    public File saveSampleFileAs() throws BadLabDataException {

        String dialogTitle = "Save Redux file for this Sample: *.redux";
        final String fileExtension = ".redux";
        String sampleFileName = getSampleName() + fileExtension;
        FileFilter nonMacFileFilter = new ReduxFileFilter();

        File selectedFile;
        String sampleFolderPath = null;
        if (getSampleFolderSaved() != null) {
            sampleFolderPath = getSampleFolderSaved().getAbsolutePath();
        } else {
            sampleFolderPath = ((ETReduxFrame) parentFrame).getMyState().getMRUSampleFolderPath();
        }

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(),
                dialogTitle,
                sampleFolderPath,
                fileExtension,
                sampleFileName,
                nonMacFileFilter);

        if (selectedFile != null) {
            saveTheSampleAsSerializedReduxFile(selectedFile);

            // handle LIVEWORKFLOW because it contains no data yet
            if (getSampleType().equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName())) {
                setSampleFolderSaved(selectedFile.getParentFile());
            }
        }
        return selectedFile;
    }

    /**
     * imports all <code>Aliquots</code> found in the XML file specified by
     * argument <code>location</code> to this <code>Sample</code>.
     *
     * @pre argument <code>location</code> specifies an XML file containing
     * valid <code>Aliquots</code>
     * @post all <code>Aliquots</code> found in the file are added to this
     * <code>Sample</code>
     *
     * @param location the file to read data from
     * @return <code>String</code> - parent of the file that was read
     * @throws java.io.FileNotFoundException FileNotFoundException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     * @throws java.io.IOException IOException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException
     * BadOrMissingXMLSchemaException
     */
    @Override
    public String importAliquotLocalXMLDataFile(File location)
            throws FileNotFoundException, BadLabDataException, IOException, BadOrMissingXMLSchemaException {
        String retval = "";

        String dialogTitle = "Select a U-Pb Aliquot XML File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();
        File aliquotFile
                = FileHelper.AllPlatformGetFile(dialogTitle, location, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (aliquotFile != null) {
            try {
                Aliquot aliquotFromFile = new UPbReduxAliquot();

                aliquotFromFile
                        = (Aliquot) ((XMLSerializationI) aliquotFromFile).readXMLObject(
                                aliquotFile.getCanonicalPath(), true);

                processXMLAliquot(aliquotFromFile, aliquotFile.getName());
            } catch (ETException ex) {
            } finally {
                // return folder for persistent state even if fails
                retval = aliquotFile.getParent();
            }
        } else {
            throw new FileNotFoundException();
            //retval = location.getPath();
        }

        return retval;
    }

    /**
     *
     * http://www.geochronportal.org/post_to_search_service.html
     *
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException
     * @throws org.earthtime.XMLExceptions.ETException
     */
    public String importOneOrMoreGeochronAliquotXMLDataFiles()
            throws FileNotFoundException,
            BadLabDataException,
            IOException,
            BadOrMissingXMLSchemaException,
            ETException {

        String retval = "";

        // ask the user for Aliquot IGSN         
        String aliquotIGSNs = JOptionPane.showInputDialog(//
                null,
                "NOTE: If you need private records, set your GEOCHRON credentials\n"
                + " in the Compilation Sample Manager. \n\n"//
                + "Enter one or more Aliquot IGSN, separated by commas: \n",
                "U-Pb_Redux for Geochron", 1);

        if (aliquotIGSNs != null) {
            String aliquotList[] = aliquotIGSNs.split(",");
            for (int i = 0; i < aliquotList.length; i++) {
                String aliquotIGSN = aliquotList[i].trim();
                if (aliquotIGSN.length() > 0) {
                    retval += retrieveGeochronAliquotFile(aliquotIGSN) + "\n";
                }
            }
        }

        return retval;
    }

    private String retrieveGeochronAliquotFile(String aliquotIGSN) {
        Aliquot myDownAliquot = new UPbReduxAliquot();

        String userName = ((ETReduxFrame) parentFrame).getMyState().getReduxPreferences().getGeochronUserName();
        String password = ((ETReduxFrame) parentFrame).getMyState().getReduxPreferences().getGeochronPassWord();

        String downloadURL = //
                "http://www.geochron.org/getxml.php?igsn="//
                + aliquotIGSN.toUpperCase().trim()//
                + "&username="//
                + userName//
                + "&password="//
                + password;

        try {
            myDownAliquot
                    = (Aliquot) ((UPbReduxAliquot) myDownAliquot).readXMLObject(
                            downloadURL, true);
            if (myDownAliquot != null) {
                // xml is added here for consistency and because we test whether aliquot source file is xml ... probably
                // should get rid of xml test and just make it aliquot non-zero length string
                processXMLAliquot(myDownAliquot, "GeochronDownloadOfAliquot_" + aliquotIGSN.toUpperCase().trim() + ".xml");
                System.out.println("got one " + myDownAliquot.getAnalystName());
            } else {
                return "Missing (or private) aliquot: " + aliquotIGSN;
            }
        } catch (IOException ex) {
//            ex.printStackTrace();
            myDownAliquot = null;
        } catch (ETException ex) {
//            ex.printStackTrace();
            myDownAliquot = null;
        } catch (BadOrMissingXMLSchemaException ex) {
//            ex.printStackTrace();
            myDownAliquot = null;
        }

        return "Found: " + myDownAliquot.getAliquotIGSN();
    }

    /**
     * imports all <code>UPbFractions</code> found in the XML file specified by
     * argument <code>location</code> to the <code>Aliquot</code> specified by
     * argument <code>aliquotNumber</code> in this <code>Sample</code>
     *
     * @pre argument <code>location</code> specifies an XML file containing
     * valid <code>UPbFractions</code>
     * @post all <code>UPbFractions</code> found in the file are added to the
     * <code>Aliquot</code> specified by <code>aliquotNumber</code> in this
     * <code>Sample</code>
     *
     * @param location the file to read data from
     * @param aliquotNumber the number of the <code>Aliquot</code> which the
     * <code>Fractions</code> belong to
     * @param doValidate
     * @return <code>String</code> - parent of the file that was read
     * @throws java.io.FileNotFoundException FileNotFoundException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    @Override
    public String importUPbFractionXMLDataFiles(
            File location,
            int aliquotNumber,
            boolean doValidate)
            throws FileNotFoundException, BadLabDataException {

        String retval = null;

        String dialogTitle = "Select one or more U-Pb Redux Fraction File(s) to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        File[] returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, location, fileExtension, nonMacFileFilter, true, new JFrame());

        int successCount = 0;
        if (returnFile[0] != null) {
            // nov 2008
            // first determine if the sample is empty and if it is,
            // use the first xml file as the automatic source of the
            // sample file
            if (getUPbFractions().size() <= 1) {
                try {
                    setSampleName(processXMLFractionFile(returnFile[0], aliquotNumber, false, doValidate));
                    successCount = 1;
                } catch (ETException uPbReduxException) {
                }
            }

            for (int i = successCount; i < returnFile.length; i++) {
                try {
                    processXMLFractionFile(returnFile[i], aliquotNumber, true, doValidate);
                    successCount++;
                } catch (ETException ex) {
                    //ex.printStackTrace();
                }
            }
            // return folder for persistent state 
            if (successCount > 0) {
                retval = returnFile[0].getParent();
            }
        } else {
            throw new FileNotFoundException();
        }

        return retval;
    }

    /**
     *
     * @param fractions
     * @param aliquotNumber
     * @param doValidate
     * @return
     * @throws ETException
     */
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
        if (getUPbFractions().isEmpty()) {
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
     * @throws ETException
     */
    public synchronized void automaticUpdateOfUPbSampleFolder() throws ETException {

        File sampleFolder = new File(getReduxSampleFilePath()).getParentFile();

        File[] aliquotFolders = sampleFolder.listFiles(new java.io.FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return (pathname.isDirectory()
                        && !pathname.isHidden()//
                        && !pathname.getName().equalsIgnoreCase(ReduxConstants.NAME_OF_SAMPLEMETADATA_FOLDER));
            }
        });

        if (aliquotFolders.length == 0) {
            throw new ETException(null,
                    "The selected Sample Folder does not contain any Aliquot folders.");
        } else {

            // proceed to read in the aliquotFractionFiles from each aliquot folder
            File[] aliquotFractionFiles;

            for (final File aliquotFolder : aliquotFolders) {

                // take the first prisoner
                // if (aliquotFractionFiles.length == 0) {
                aliquotFractionFiles = aliquotFolder.listFiles(new java.io.FileFilter() {
                    // 20 second cushion
                    @Override
                    public boolean accept(File file) {
                        // want .xml files and only freshones in live-update, but all of them in auto-update
                        boolean isXML = file.getName().toLowerCase().endsWith(".xml");

                        if (getSampleType().equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName())) {
                            return ((file.lastModified() >= (aliquotFolder.lastModified() - 20000l))
                                    && isXML);
                        } else {
                            return isXML;
                        }
                    }
                });///(new FractionXMLFileFilter());

                // assume xml files are in good shape with doValidate = false
                updateSampleAliquot(aliquotFolder, aliquotFractionFiles, false);
            }
        }

    }

    /**
     *
     * @param aliquotFolder
     * @param aliquotFractionFiles
     * @param doValidate
     */
    public synchronized void updateSampleAliquot(File aliquotFolder, File[] aliquotFractionFiles, boolean doValidate) {

        System.out.println("CHANGED count of fresh data = " + aliquotFractionFiles.length);
        int aliquotNumber;

        // determine aliquot number
        if (aliquotFractionFiles.length > 0) {
            Aliquot aliquot = getAliquotByName(aliquotFolder.getName());
            if (aliquot == null) {
                // check if last aliquot was empty (i.e. the initial first dummy aliquot)
                if (((UPbReduxAliquot) aliquots.get(aliquots.size() - 1)).getAliquotFractions().isEmpty()) {
                    aliquotNumber = ((UPbReduxAliquot) aliquots.get(aliquots.size() - 1)).getAliquotNumber();
                } else {
                    aliquotNumber = addNewDefaultAliquot();
                }
            } else {
                aliquotNumber = ((UPbReduxAliquot) aliquot).getAliquotNumber();
            }

            Fraction savedCurrentFraction = null;
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

                    ((UPbReduxAliquot) getAliquotByNumber(aliquotNumber)).//
                            reduceData();

                    if (myFractionEditor != null) {
                        if (doRestoreAutoUranium) {
                            ((UPbFraction) savedCurrentFraction).autoGenerateMeasuredUranium();
                        }
                        ((UPbFractionEditorDialog) myFractionEditor).InitializeFractionData(savedCurrentFraction);

                        UPbFractionReducer.getInstance().fullFractionReduce(savedCurrentFraction, true);

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
     * by argument
     * <code>aliquotNumber</code> in this <code>Sample</code>.
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
                importAliquotFolder(fractions, aliquotNumber, true);

                ((UPbReduxAliquot) getAliquotByNumber(aliquotNumber)).//
                        reduceData();
            }

        }
        if (getUPbFractions().size() > 0) {
            // return folder for persistent state
            retval = fractionFolder.getPath();
        }

        return retval;
    }

    /**
     * sets <code>ReduxLabData</code> of this <code>Sample</code> and all
     * <code>Aliquots</code> and <code>Fractions</code> contained within to the
     * argument <code>labData</code>.
     *
     * @pre argument      <code>labData</code> is valid
     * <code>ReduxLabData</code>
     * @post <code>ReduxLabData</code> of this <code>Sample</code> and all of
     * its <code>Aliquots</code> and <code>Fractions</code> is set to argument
     * <code>labData</code>
     *
     * @param labData value to which this <code>Sample</code> and its
     * <code>Aliquots</code> and <code>Fractions</code> should be set to
     */
    public void registerSampleWithLabData(ReduxLabData labData) {

        setMyReduxLabData(labData);

        // register incoming models - by file - with lab data
        // TODO verify names and contents align
        for (int UPbFractionsIndex = 0; UPbFractionsIndex
                < getUPbFractions().size(); UPbFractionsIndex++) {
            Fraction nextFraction = getUPbFractions().get(UPbFractionsIndex);
            if (!nextFraction.isLegacy()) {
                labData.registerFractionWithLabData(nextFraction);
            }
        }

        for (Aliquot a : getActiveAliquots()) {
            for (AbstractRatiosDataModel msm : a.getMineralStandardModels()) {
                labData.registerMineralStandardModel(msm, false);
            }

        }
    }

    /**
     *
     */
    public void updateSampleLabName() {
        for (Aliquot a : aliquots) {
            a.setLaboratoryName(getMyReduxLabData().getLabName());
        }
    }

    /**
     *
     * @param sampleName
     */
    public void updateSampleFractionsWithSampleName(String sampleName) {
        for (int i = 0; i
                < getUPbFractions().size(); i++) {
            getUPbFractions().get(i).setSampleName(sampleName);
        }

    }

    /**
     * removes <code>Fractions</code> from this <code>Sample</code>'s sample age
     * models that are no longer aliquot part of this <code>Sample</code>'s
     * <code>Aliquots</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post any <code>Fractions</code> that are found in this
     * <code>Sample</code>'s sample age models that are no longer a part of this
     * <code>Sample</code>'s <code>Aliquots</code> are removed
     */
    @Override
    public final void updateAndSaveSampleDateModelsByAliquot() {
        // May 2008
        // process all sampleAgeModels' included fraction vectors to remove missing aliquotFractionFiles
        for (Aliquot nextAliquot : aliquots) {
            // may 2012 next line to force reduction
            ((UPbReduxAliquot) nextAliquot).reduceData();

            nextAliquot.updateSampleDateModels();
        }

    }

    /**
     * sets the <code>changed</code> field of each <code>UPbFraction</code> in
     * this <code>Sample</code> to <code>false</code> and saves this
     * <code>Sample</code> as aliquot .redux file to
     * <code>reduxSampleFilePath</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post this <code>Sample</code> is saved as a .redux file to the location
     * specified by <code>reduxSampleFilePath</code>
     */
    @Override
    public final void saveTheSampleAsSerializedReduxFile() {
        setChanged(false);

        for (int UPbFractionsIndex = 0; UPbFractionsIndex
                < getUPbFractions().size(); UPbFractionsIndex++) {
            ((UPbFractionI) getUPbFractions().get(UPbFractionsIndex)).setChanged(false);
        }

        if (getReduxSampleFilePath().length() > 0) {

            try {
                ETSerializer.SerializeObjectToFile(this, getReduxSampleFilePath());
            } catch (ETException eTException) {
            }
        }

    }

    /**
     * saves this <code>Sample</code> to the file specified by argument
     * <code>file</code>.
     *
     * @pre argument <code>file</code> is a valid file
     * @post this <code>Sample</code> is saved to the location specified by
     * argument <code>file</code>
     *
     * @param file the file where this <code>Sample</code> will be saved
     * @return <code>String</code> - the path of the file where this
     * <code>Sample</code> was saved
     */
    @Override
    public final String saveTheSampleAsSerializedReduxFile(
            File file) {
        setReduxSampleFilePath(file);
        saveTheSampleAsSerializedReduxFile();

        return getReduxSampleFilePath();
    }

    /**
     * reads aliquot <code>Sample</code> from the file specified by argument
     * <code>file</code> and returns it.
     *
     * @pre argument <code>file</code> specified a file containing a valid
     * <code>Sample</code>
     * @post returns the <code>Sample</code> read in from the file
     *
     * @param file the file to read aliquot <code>Sample</code> from
     * @return <code>Sample</code> - the <code>Sample</code> that has been read
     */
    public static EarthTimeSerializedFileInterface getTheSampleFromSerializedReduxFile(
            File file) {
        return (EarthTimeSerializedFileInterface) ETSerializer.GetSerializedObjectFromFile(file.getPath());
    }

    /**
     *
     * @param MRUreportSettingsModelFolder
     * @return
     */
    public String setReportSettingsModelFromXMLFile(String MRUreportSettingsModelFolder) {

        String retVal = MRUreportSettingsModelFolder;
        String dialogTitle = "Select a Report Settings Model xml file to LOAD: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        File returnFile
                = FileHelper.AllPlatformGetFile(//
                        dialogTitle, //
                        new File(MRUreportSettingsModelFolder), //
                        fileExtension, nonMacFileFilter, false, parentFrame)[0];

        if (returnFile != null) {
            ReportSettings reportSettings = new ReportSettings();
            try {
                reportSettingsModel = (ReportSettings) reportSettings.readXMLObject(returnFile.getAbsolutePath(), true);
                retVal = returnFile.getParent();
            } catch (FileNotFoundException fileNotFoundException) {
            } catch (ETException eTException) {
            } catch (BadOrMissingXMLSchemaException badOrMissingXMLSchemaException) {
            }
        }

        return retVal;
    }

    /**
     *
     */
    public void restoreDefaultReportSettingsModel() {
        try {
            reportSettingsModel = ReduxLabData.getInstance().getDefaultReportSettingsModel();
        } catch (BadLabDataException badLabDataException) {
        }
    }

    /**
     *
     * @param reportsFolderPath
     * @return
     * @throws BadLabDataException
     */
    public String saveReportSettingsToFile(String reportsFolderPath)
            throws BadLabDataException {

        String retVal = "";

        String dialogTitle = "Save Report Settings Model as XML file: *.xml";
        final String fileExtension = ".xml";
        String sampleFileName = "ReportSettings" + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        File selectedFile = null;

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(),
                dialogTitle,
                reportsFolderPath,
                fileExtension,
                sampleFileName,
                nonMacFileFilter);

        if (selectedFile != null) {
            reportSettingsModel.serializeXMLObject(selectedFile.getAbsolutePath());
            retVal = selectedFile.getParent();
        }

        return retVal;
    }

//    /**
//     * gets the <code>file</code> of this <code>Sample</code>.
//     *
//     * @pre this <code>Sample</code> exists
//     * @post returns the <code>file</code> of this <code>Sample</code>
//     *
//     * @return <code>String</code> - <code>file</code> of this
//     * <code>Sample</code>
//     */
//    public String getSampleName() {
//        return sampleName;
//
//    }

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
    public boolean isChanged() {
        for (int UPbFractionsIndex = 0; UPbFractionsIndex
                < UPbFractions.size(); UPbFractionsIndex++) {
            changed = changed || ((UPbFractionI) UPbFractions.get(UPbFractionsIndex)).isChanged();

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
    public String getReduxSampleFilePath() {
        return reduxSampleFilePath;

    }

    /**
     * sets the <code>reduxSampleFilePath</code> and
     * <code>reduxSampleFileName</code> of this <code>Sample</code> to the
     * argument <code>reduxSampleFile</code>
     *
     * @pre argument <code>reduxSampleFile</code> is a valid file
     * @post this <code>Sample</code>'s <code>reduxSampleFilePath</code> and
     * <code>reduxSampleFileName</code> are set to argument
     * <code>reduxSamplefile</code>
     *
     * @param reduxSampleFile value to which <code>reduxSampleFilePath</code>
     * and <code>reduxSampleFileName</code> of this <code>Sample</code> will be
     * set
     */
    public void setReduxSampleFilePath(File reduxSampleFile) {
        boolean isChanged = false;
        // set redux extension

        if (!reduxSampleFile.getPath().endsWith(".redux")) {
            isChanged = isChanged || (this.reduxSampleFilePath.compareToIgnoreCase(reduxSampleFile.getPath() + ".redux") != 0);

            this.reduxSampleFilePath = reduxSampleFile.getPath() + ".redux";
            isChanged
                    = isChanged || (this.reduxSampleFileName.compareToIgnoreCase(reduxSampleFile.getName() + ".redux") != 0);

            this.reduxSampleFileName = reduxSampleFile.getName() + ".redux";

        } else {
            isChanged = isChanged || (this.reduxSampleFilePath.compareToIgnoreCase(reduxSampleFile.getPath()) != 0);

            this.reduxSampleFilePath = reduxSampleFile.getPath();
            isChanged
                    = isChanged || (this.reduxSampleFileName.compareToIgnoreCase(reduxSampleFile.getName()) != 0);

            this.reduxSampleFileName = reduxSampleFile.getName();

        }

        setChanged(isChanged);
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
    public String getReduxSampleFileName() {
        return reduxSampleFileName;
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
     * gets the <code>myReduxLabData</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>myReduxLabData</code> of this <code>Sampel</code>
     *
     * @return <code>ReduxLabData</code> - <code>myReduxLabData</code> of this
     * <code>Sample</code>
     */
    @Override
    public ReduxLabData getMyReduxLabData() {
        return myReduxLabData;
    }

    /**
     * sets the <code>myReduxLabData</code> field of this <code>Sample</code> to
     * the argument <code>myReduxLabData</code>
     *
     * @pre argument <code>myReduxLabData</code> is a valid
     * <code>ReduxLabData</code>
     * @post this <code>Sample</code>'s <code>myReduxLabData</code> field is set
     * to argument <code>myReduxLabData</code>
     *
     * @param myReduxLabData value to which <code>myReduxLabData</code> field of
     * this <code>Sample</code> will be set
     */
    @Override
    public void setMyReduxLabData(ReduxLabData myReduxLabData) {
        this.myReduxLabData = myReduxLabData;
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
    public Vector<Fraction> getUPbFractions() {
        return UPbFractions;
    }

    /**
     *
     * @return
     */
    public Vector<Fraction> getUpbFractionsActive() {
        Vector<Fraction> retval = new Vector<Fraction>();

        for (Fraction f : UPbFractions) {
            if (!((UPbFractionI) f).isRejected()) {
                retval.add(f);
            }
        }

        return retval;
    }

    /**
     *
     * @return
     */
    public Vector<Fraction> getUpbFractionsRejected() {
        Vector<Fraction> retval = new Vector<Fraction>();

        for (Fraction f : UPbFractions) {
            if (((UPbFractionI) f).isRejected()) {
                retval.add(f);
            }
        }

        return retval;
    }

    /**
     *
     * @return
     */
    public Vector<Fraction> getUpbFractionsUnknown() {
        Vector<Fraction> retval = new Vector<Fraction>();

        for (Fraction f : UPbFractions) {
            if (!((UPbFractionI) f).isStandard()) {
                retval.add(f);
            }
        }

        return retval;
    }

    /**
     *
     * @param filteredFractions
     */
    public void updateSetOfActiveFractions(Vector<Fraction> filteredFractions) {
        for (int i = 0; i < UPbFractions.size(); i++) {
            ((UPbFractionI) UPbFractions.get(i)).setRejected(!filteredFractions.contains(UPbFractions.get(i)));
        }
    }

    // april 2010
    /**
     *
     * @return
     */
    public Fraction getFirstActiveUPbFraction() {
        Fraction retVal = null;

        for (int UPbFractionsIndex = 0; UPbFractionsIndex
                < UPbFractions.size(); UPbFractionsIndex++) {
            if ((UPbFractions.get(UPbFractionsIndex) instanceof UPbFraction)
                    && !((UPbFractionI) (UPbFractions.get(UPbFractionsIndex))).isRejected()) {
                retVal = UPbFractions.get(UPbFractionsIndex);

                break;

            }
        }

        return retVal;
    }

    /**
     *
     */
    public void selectAllFractions() {
        for (Fraction f : UPbFractions) {
            ((UPbFractionI) f).setRejected(false);
        }
    }

    /**
     *
     */
    public void deSelectAllFractions() {
        for (Fraction f : UPbFractions) {
            ((UPbFractionI) f).setRejected(true);
        }
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
    public void setUPbFractions(Vector<Fraction> UPbFractions) {
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
     *
     * @return
     */
    public String getSampleIGSNnoRegistry() {
        String retVal = "";
        String parse[] = sampleIGSN.split("\\.");
        if (parse.length > 0) {
            // returns index 0 if no registry, 1 otherwise
            retVal = parse[parse.length - 1];
        }

        return retVal;
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
        }
    }

    // april 2011 update to rrr.igsn
    /**
     *
     */
    public void updateWithRegistrySampleIGSN() {
        //if ( isValidatedSampleIGSN() ) {
        String newID = SampleRegistries.updateSampleID(sampleIGSN);
        // check for addition of default registry
        if (!newID.equalsIgnoreCase(sampleIGSN)) {
            // sets and percolates through aliquots
            setSampleIGSN(newID);
            // if sampleIGSN is not valid at default registry, invalidate flags
            //if (SampleRegistries.isSampleIdentifierValidAtRegistry(sampleIGSN)) {
            setValidatedSampleIGSN(SampleRegistries.isSampleIdentifierValidAtRegistry(sampleIGSN));
            //}
        }

    }
    //}

    /**
     * gets the <code>aliquots</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>aliquots</code> of this <code>Sample</code>
     *
     * @return <code>Vector</code> - set of <code>Aliquots</code> of this
     * <code>Sample</code>
     */
    public Vector<Aliquot> getAliquots() {
        return aliquots;
    }

    /**
     *
     * @return
     */
    public Vector<Aliquot> getActiveAliquots() {
        // May 2010  refresh aliquots to   remove empty ones
        Vector<Aliquot> activeAliquots = new Vector<Aliquot>();
        for (Aliquot aliquot : aliquots) {
            if (((UPbReduxAliquot) aliquot).getAliquotFractions().size() > 0) {
                // oct 2014 check for no active fractions
                if (((UPbReduxAliquot) aliquot).containsActiveFractions()) {
                    activeAliquots.add(aliquot);
                }
            }
        }
        return activeAliquots;
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
    public void setAliquots(Vector<Aliquot> aliquots) {
        this.aliquots = aliquots;
    }

    /**
     *
     */
    public void repairAliquotNumberingDec2011() {
        // walk aliquots and remove empty ones 
        ArrayList<Aliquot> aliquotsToDelete = new ArrayList<>();
        for (int i = 0; i < aliquots.size(); i++) {
            Aliquot aliquot = aliquots.get(i);//    Feb 2015 getAliquotByNumber(i + 1);
            if (((UPbReduxAliquot) aliquot).getAliquotFractions().isEmpty()) {
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
            Aliquot aliquot = aliquots.get(i);
            ((UPbReduxAliquot) aliquot).setAliquotNumber(i + 1);

            Vector<Fraction> aliquotFractions = ((UPbReduxAliquot) aliquot).getAliquotFractions();
            for (int j = 0; j < aliquotFractions.size(); j++) {
                ((UPbFractionI) aliquotFractions.get(j)).setAliquotNumber(i + 1);
            }
        }
    }

    /**
     *
     */
    public void reduceSampleData() {
        for (Aliquot aliquot : aliquots) {
            ((UPbReduxAliquot) aliquot).reduceData();

            // oct 2014 
            ((UPbReduxAliquot) aliquot).updateBestAge();
        }
    }

    /**
     * gets the <code>physicalConstantsModel</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>physicalConstantsModel</code> of this
     * <code>Sample</code>
     *
     * @return <code>PhysicalConstants</code> -      <code>physicalConstantsModel</code>
     *          of this
     * <code>Sample</code>
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel()
            throws BadLabDataException {
        if (physicalConstantsModel == null) {
            physicalConstantsModel = getMyReduxLabData().getDefaultPhysicalConstantsModel();
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
            for (Aliquot aliquot : aliquots) {
                Aliquot nextAliquot = getAliquotByNumber(((UPbReduxAliquot) aliquot).getAliquotNumber());
                try {
                    nextAliquot.setPhysicalConstants(getPhysicalConstantsModel());

                } catch (BadLabDataException badLabDataException) {
                }
            }
        }
    }

    /**
     * gets the <code>reportSettingsModel</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>reportSettingsModel</code> of this
     * <code>Sample</code>
     *
     * @return <code>ReportSettings</code> -      <code>reportSettingsModel</code>
     *          of this
     * <code>Sample</code>
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    @Override
    public synchronized ReportSettings getReportSettingsModelUpdatedToLatestVersion() {
        if (reportSettingsModel == null) {
            try {
                reportSettingsModel = ReduxLabData.getInstance().getDefaultReportSettingsModel();
            } catch (BadLabDataException badLabDataException) {
            }
        } else {

            // this provides for seamless updates to reportsettings implementation
            // new approach oct 2014
            if (reportSettingsModel.isOutOfDate()) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"As part of our ongoing development efforts,",
                            "the report settings file you are using is being updated.",
                            "You may lose some report customizations. Thank you for your patience."//,
                        //"If you need to save aliquot copy, please re-export."
                        });
                String myReportSettingsName = reportSettingsModel.getName();
                reportSettingsModel = new ReportSettings(myReportSettingsName);
            }
        }

        //TODO http://www.javaworld.com/article/2077736/open-source-tools/xml-merging-made-easy.html
        setLegacyStatusForReportTable();

        return reportSettingsModel;
    }

    /**
     *
     */
    public void setLegacyStatusForReportTable() {
        // feb 2010 added legacyData field to force display when no reduction happening
        reportSettingsModel.setLegacyData(isAnalysisTypeCompiled() || isAnalyzed());
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
     *
     * @return
     */
    public boolean isSampleTypeLiveWorkflow() {
        return sampleType.equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName());
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
    public boolean isTypeAnalysis() {
        return (sampleType.equalsIgnoreCase(SampleTypesEnum.ANALYSIS.getName()));
    }

    /**
     *
     * @return
     */
    public boolean isTypeLiveUpdate() {
        return (sampleType.equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName()));
    }

    /**
     *
     * @return
     */
    public boolean isTypeLegacy() {
        return (sampleType.equalsIgnoreCase(SampleTypesEnum.LEGACY.getName()));
    }

    /**
     *
     * @return
     */
    public boolean isTypeProject() {
        return (sampleType.equalsIgnoreCase(SampleTypesEnum.PROJECT.getName()));
    }

    /**
     *
     * @return
     */
    public boolean isAnalysisTypeCompiled() {
        return (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.COMPILED.getName()));
    }

    /**
     *
     * @return
     */
    public boolean isAnalysisTypeTripolized() {
        return (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.TRIPOLIZED.getName()));
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
    public void setSampleAgeInterpretationGUISettings(SampleDateInterpretationGUIOptions sampleAgeInterpretationGUISettings) {
        this.sampleAgeInterpretationGUISettings = sampleAgeInterpretationGUISettings;
    }

    /**
     * @return the automaticDataUpdateMode
     */
    public boolean isAutomaticDataUpdateMode() {
        return automaticDataUpdateMode;
    }

    /**
     * @param automaticDataUpdateMode the automaticDataUpdateMode to set
     */
    public void setAutomaticDataUpdateMode(boolean automaticDataUpdateMode) {
        this.automaticDataUpdateMode = automaticDataUpdateMode;
    }

    /**
     * @return the sampleFolderSaved
     */
    public File getSampleFolderSaved() {
        return sampleFolderSaved;
    }

    /**
     * @param sampleFolderSaved the sampleFolderSaved to set
     */
    public void setSampleFolderSaved(File sampleFolderSaved) {
        this.sampleFolderSaved = sampleFolderSaved;
    }

// section added April 2009 copied from aliquot to support compilation mode
//compilation mode is when sampledate interpretations are super-sample specific
    /**
     * @return the sampleDateModels
     */
    public Vector<ValueModel> getSampleDateModels() {
        if (sampleDateModels == null) {
            sampleDateModels = new Vector<ValueModel>();
        }
        return sampleDateModels;
    }

    /**
     * @param sampleDateModels the sampleDateModels to set
     */
    public void setSampleDateModels(Vector<ValueModel> sampleDateModels) {
        this.sampleDateModels = sampleDateModels;
    }

    /**
     *
     * @param modelName
     * @return
     */
    public ValueModel getASampleDateModelByName(
            String modelName) {
        Iterator it = getSampleDateModels().iterator();
        ValueModel retval = null;

        while (it.hasNext()) {
            retval = (SampleDateModel) it.next();
            if (retval.getName().
                    equalsIgnoreCase(modelName)) {
                return retval;

            } else {
                retval = null;
            }
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public ValueModel getPreferredSampleDateModel() {
        ValueModel retVal = null;
        Iterator it = getSampleDateModels().iterator();

        while (it.hasNext()) {
            retVal = (SampleDateModel) it.next();
            if (((SampleDateModel) retVal).isPreferred()) {
                return retVal;
            }
        }
        return retVal;
    }

    /**
     *
     * @param includeSingleDates
     * @return
     */
    public Vector<ValueModel> determineUnusedSampleDateModels(boolean includeSingleDates) {
        Vector<ValueModel> retVal = new Vector<ValueModel>();
        // choose models not already in use by Aliquot
        for (int i = 0; i < SampleDateTypes.getSampleDateModelTypes().length; i++) {
            if (!includeSingleDates
                    && SampleDateTypes.getSampleDateType(i).startsWith("single")) {
                // do nothing
            } else {
                if (getASampleDateModelByName(SampleDateTypes.getSampleDateType(i)) == null) {
                    ValueModel tempModel = null;

                    if (SampleDateTypes.getSampleDateType(i).endsWith("intercept")) {
                        tempModel = //
                                new SampleDateInterceptModel(//
                                        SampleDateTypes.getSampleDateType(i),
                                        SampleDateTypes.getSampleDateTypeMethod(i),
                                        SampleDateTypes.getSampleDateTypeName(i),
                                        BigDecimal.ZERO,
                                        "ABS",
                                        BigDecimal.ZERO);

                        ((SampleDateModel) tempModel).setSample(this);
                    } else {
                        tempModel = //
                                new SampleDateModel(//
                                        SampleDateTypes.getSampleDateType(i),
                                        SampleDateTypes.getSampleDateTypeMethod(i),
                                        SampleDateTypes.getSampleDateTypeName(i),
                                        BigDecimal.ZERO,
                                        "ABS",
                                        BigDecimal.ZERO);

                        ((SampleDateModel) tempModel).setSample(this);
                    }

                    retVal.add(tempModel);

                }
            }
        }
        return retVal;

    }

    /**
     *
     * @param sampleDateModel
     */
    public void setPreferredSampleDateModel(ValueModel sampleDateModel) {
        // set all to false
        for (ValueModel sam : getSampleDateModels()) {
            ((SampleDateModel) sam).setPreferred(false);

        }

        ((SampleDateModel) sampleDateModel).setPreferred(true);
        Collections.sort(getSampleDateModels());

    }

    /**
     *
     * @param sampleDateModelName
     * @return
     */
    public boolean containsSampleDateModelByName(String sampleDateModelName) {
        boolean retVal = false;

        for (ValueModel sam : sampleDateModels) {
            if (sam.getName().equalsIgnoreCase(sampleDateModelName)) {
                retVal = true;

            }

        }
        return retVal;

    }

    /**
     *
     * @param sampleDateModelName
     * @return
     */
    public ValueModel getSampleDateModelByName(
            String sampleDateModelName) {
        ValueModel retVal = null;

        for (ValueModel sdm : sampleDateModels) {
            if (sdm.getName().equalsIgnoreCase(sampleDateModelName)) {
                retVal = sdm;

            }

        }
        return retVal;

    }

    /**
     *
     * @return
     */
    public Vector<String> getSampleFractionIDs() {
        Vector<String> retVal = new Vector<String>();

        for (Fraction f : UPbFractions) {
            if (!((UPbFractionI) f).isRejected()) {
                retVal.add(f.getFractionID());

            }

        }
        return retVal;

    }

    /**
     *
     */
    public final void updateSampleDateModels() {
        // process all sampleDateModels' included fraction vectors to remove missing aliquotFractionFiles
        Vector<String> includedFractionIDs = getSampleFractionIDs();
        Vector<String> excludedFractionIDs = new Vector<String>();
        boolean existsPreferredDate = false;

        for (ValueModel SAM : getSampleDateModels()) {
            Vector<String> SAMFractionIDs = ((SampleDateModel) SAM).getIncludedFractionIDsVector();

            for (int f = 0; f
                    < SAMFractionIDs.size(); f++) {
                String fractionID = SAMFractionIDs.get(f);
                // for (String fractionID : SAMFractionIDs) {

                if (!includedFractionIDs.contains(fractionID)) {
                    excludedFractionIDs.add(fractionID);
                }

            }
            // remove found exclusions (these are ones that were rejected after processing
            for (String fractionID : excludedFractionIDs) {
                ((SampleDateModel) SAM).getIncludedFractionIDsVector().remove(fractionID);
            }

            if (((SampleDateModel) SAM).isPreferred()) {
                existsPreferredDate = true;
            }
        }

        // guarantee preferred date model
        if (!existsPreferredDate && (getSampleDateModels().size() > 0)) {
            ((SampleDateModel) getSampleDateModels().get(0)).setPreferred(true);
        }

    }

    /**
     *
     * @param name
     * @return
     */
    public Fraction getSampleFractionByName(
            String name) {
        Fraction retVal = null;

        for (Fraction f : getUPbFractions()) {
            if (f.getFractionID().equalsIgnoreCase(name)) {
                retVal = f;
            }

        }
        return retVal;

    }

    /**
     *
     * @param fID
     * @return
     */
    public String getAliquotNameByFractionID(
            String fID) {
        return getAliquotByNumber(//
                ((UPbFractionI) getFractionByID(fID)).getAliquotNumber()).getAliquotName();
    }

    /**
     *
     * @param selectedFractionIDs
     * @return
     */
    public Vector<Fraction> getSampleDateModelSelectedFractions(Vector<String> selectedFractionIDs) {
        Vector<Fraction> retVal = new Vector<Fraction>();

        for (String fID : selectedFractionIDs) {
            retVal.add(getSampleFractionByName(fID));
        }

        return retVal;
    }

    /**
     *
     * @param selectedFractionIDs
     * @return
     */
    public Vector<Fraction> getSampleDateModelDeSelectedFractions(Vector<String> selectedFractionIDs) {
        Vector<Fraction> retVal = new Vector<Fraction>();

        for (String fID : getSampleFractionIDs()) {
            if (!selectedFractionIDs.contains(fID)) {
                retVal.add(getSampleFractionByName(fID));

            }
        }

        return retVal;
    }

//    public String[][] reportAllFractionsByNumberStyle(boolean isNumeric) {
//
//        return getReportSettingsModel().reportAllFractionsByNumberStyle(this, isNumeric);
//    }
    /**
     *
     * @param isNumeric
     * @return
     */
    public String[][] reportActiveFractionsByNumberStyle(boolean isNumeric) {

        return getReportSettingsModel().reportActiveFractionsByNumberStyle(this, isNumeric);
    }

    /**
     *
     * @param aliquot
     * @param isNumeric
     * @return
     */
    public String[][] reportActiveAliquotFractionsByNumberStyle(Aliquot aliquot, boolean isNumeric) {

        return getReportSettingsModel().reportActiveAliquotFractionsByNumberStyle(this, ((UPbReduxAliquot) aliquot).getActiveAliquotFractions(), isNumeric);
    }

    /**
     *
     * @param isNumeric
     * @return
     */
    public String[][] reportRejectedFractionsByNumberStyle(boolean isNumeric) {

        return getReportSettingsModel().reportRejectedFractionsByNumberStyle(this, isNumeric);
    }

    /**
     * @return the sampleAnalysisType
     */
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
    public void setSampleAnalysisType(String sampleAnalysisType) {
        this.sampleAnalysisType = sampleAnalysisType;

    }

    /**
     *
     * @return
     */
    public boolean isAnalysisTypeIDTIMS() {
        boolean retVal = false;
        try {
            retVal = SampleAnalysisTypesEnum.IDTIMS.equals(SampleAnalysisTypesEnum.valueOf(sampleAnalysisType));
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public boolean isAnalysisTypeLAICPMS() {
        boolean retVal = false;
        try {
            retVal = SampleAnalysisTypesEnum.LAICPMS.equals(SampleAnalysisTypesEnum.valueOf(sampleAnalysisType));
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     * @return the concordiaGraphAxesSetup
     */
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
    public String getMineralName() {
        if (mineralName == null) {
            mineralName = MineralTypes.OTHER.getName();
        }
        return mineralName;
    }

    /**
     * @param mineralName the mineralName to set
     */
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
    public void setAnalysisPurpose(ANALYSIS_PURPOSE analysisPurpose) {
        this.analysisPurpose = analysisPurpose;
    }

    /**
     * @return the calculateTWrhoForLegacyData
     */
    public boolean isCalculateTWrhoForLegacyData() {
        return calculateTWrhoForLegacyData;
    }

    /**
     * @param calculateTWrhoForLegacyData the calculateTWrhoForLegacyData to set
     */
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
    public void setReportSettingsModel(ReportSettings reportSettingsModel) {
        this.reportSettingsModel = reportSettingsModel;
    }

    /**
     *
     */
    public void deselectAllFractions() {
        for (Fraction UPbFraction : this.UPbFractions) {
            ((UPbFractionI) UPbFraction).setSelectedInDataTable(false);
        }
    }

    /**
     * @return the reportSettingsModel
     */
    @Override
    public ReportSettings getReportSettingsModel() {
        return reportSettingsModel;
    }
}
