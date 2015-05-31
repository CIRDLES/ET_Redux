/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.samples;

import java.awt.Frame;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.filters.ReduxFileFilter;
import org.earthtime.UPb_Redux.filters.XMLFileFilter;
import org.earthtime.UPb_Redux.fractions.AnalysisFraction;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reports.ReportSettings;
import org.earthtime.UPb_Redux.samples.SESARSampleMetadata;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.samples.UPbSampleInterface;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.SampleDateTypes;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.FractionInterface;
import org.earthtime.projects.EarthTimeSerializedFileInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.utilities.FileHelper;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface SampleInterface {

    /**
     *
     * @param myLabData
     */
    public abstract void setUpSample(ReduxLabData myLabData);

    /**
     * gets the <code>file</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>file</code> of this <code>Sample</code>
     *
     * @return <code>String</code> - <code>file</code> of this
     * <code>Sample</code>
     */
    public abstract String getSampleName();

    /**
     * @param sampleName the sampleName to set
     */
    public abstract void setSampleName(String sampleName);

    /**
     * sets the <code>sampleType</code> of this <code>Sample</code> to the
     * argument <code>sampleType</code>
     *
     * @pre argument <code>sampleType</code> is a valid <code>sampleType</code>
     * @post this <code>Sample</code>'s <code>sampleType</code> is set to
     * argument <code>sampleType</code>
     * @param sampleType value to which <code>sampleType</code> of this
     * <code>Sample</code> will be set
     */
    public abstract void setSampleType(String sampleType);

    /**
     * gets the <code>sampleType</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>sampleType</code> of this <code>Sample</code>
     * @return  <code>String</code> - <code>sampleType</code> of this
     * <code>Sample</code>
     */
    public abstract String getSampleType();

    /**
     * sets the <code>analyzed</code> field of this <code>Sample</code> to the
     * argument <code>analyzed</code>
     *
     * @pre argument <code>analyzed</code> is a valid <code>boolean</code>
     * @post this <code>Sample</code>'s <code>analyzed</code> field is set to
     * argument <code>analyzed</code>
     * @param analyzed value to which <code>analyzed</code> field of this
     * <code>Sample</code> will be set
     */
    public abstract void setAnalyzed(boolean analyzed);

    /**
     * gets the <code>analyzed</code> field of this <code>Sample</code>
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>analyzed</code> field of this <code>Sample</code>
     * @return  <code>boolean</code> - <code>analyzed</code> field of this
     * <code>Sample</code>
     */
    public abstract boolean isAnalyzed();

    /**
     *
     * @param analysisType the value of analysisType
     * @return the boolean
     */
    public static boolean isAnalysisTypeTripolized(String analysisType) {
        return (analysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.TRIPOLIZED.getName()));
    }

    /**
     * gets the <code>changed</code> field of this <code>Sample</code>
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>changed</code> field of this <code>Sample</code>
     * @return  <code>boolean</code> - <code>changed</code> field of this
     * <code>Sample</code>
     */
    boolean isChanged();

    /**
     * sets the <code>changed</code> field of this <code>Sample</code> to the
     * argument <code>changed</code>
     *
     * @pre argument <code>changed</code> is a valid <code>boolean</code>
     * @post this <code>Sample</code>'s <code>changed</code> field is set to
     * argument <code>changed</code>
     * @param changed vale to which <code>changed</code> field of this
     * <code>Sample</code> will be set
     */
    public abstract void setChanged(boolean changed);

    /**
     * sets the <code>sampleAnnotations</code> of this <code>Sample</code> to
     * the argument <code>annotations</code>
     *
     * @pre argument <code>annotations</code> is a valid
     * <code>sampleAnnotations</code>
     * @post this <code>Sample</code>'s <code>sampleAnnotations</code> is set to
     * argument <code>annotations</code>
     * @param annotations value to which <code>sampleAnnotations</code> of this
     * <code>Sample</code> will be set
     */
    public abstract void setSampleAnnotations(String annotations);

    /**
     * gets the <code>sampleAnnotations</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>sampleAnnotations</code> of this
     * <code>Sample</code>
     * @return  <code>String</code> - <code>sampleAnnotations</code> of this
     * <code>Sample</code>
     */
    public abstract String getSampleAnnotations();

    public abstract ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose();

    /**
     * @param analysisPurpose the analysisPurpose to set
     */
    public abstract void setAnalysisPurpose(ReduxConstants.ANALYSIS_PURPOSE analysisPurpose);

    /**
     * gets the <code>reduxSampleFileName</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>reduxSampleFileName</code> of this
     * <code>Sample</code>
     * @return  <code>String</code> - <code>reduxSampleFileName</code> of this
     * <code>Sample</code>
     */
    public abstract String getReduxSampleFileName();

    /**
     * gets the <code>reduxSampleFilePath</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>reduxSampleFilePath</code> of this
     * <code>Sample</code>
     * @return  <code>String</code> - <code>reduxSampleFilePath</code> of this
     * <code>Sample</code>
     */
    public abstract String getReduxSampleFilePath();

    /**
     * sets the <code>reduxSampleFilePath</code> and
     * <code>reduxSampleFileName</code> of this <code>Sample</code> to the
     * argument <code>reduxSampleFile</code>
     *
     * @pre argument <code>reduxSampleFile</code> is a valid file
     * @post this <code>Sample</code>'s <code>reduxSampleFilePath</code> and
     * <code>reduxSampleFileName</code> are set to argument
     * <code>reduxSamplefile</code>
     * @param reduxSampleFile value to which <code>reduxSampleFilePath</code>
     * and <code>reduxSampleFileName</code> of this <code>Sample</code> will be
     * set
     */
    public abstract void setReduxSampleFilePath(File reduxSampleFile);

    /**
     *
     * @return
     */
    public default boolean isSampleTypeAnalysis() {
        return (getSampleType().equalsIgnoreCase(SampleTypesEnum.ANALYSIS.getName()));
    }

    public default boolean isSampleTypeCompilation() {
        return (getSampleType().equalsIgnoreCase(SampleTypesEnum.COMPILATION.getName()));
    }



    /**
     * @param sampleAnalysisType the sampleAnalysisType to set
     */
    public abstract void setSampleAnalysisType(String sampleAnalysisType);

    /**
     *
     * @return
     */
    public default boolean isAnalysisTypeIDTIMS() {
        boolean retVal = false;
        try {
            retVal = SampleAnalysisTypesEnum.IDTIMS.equals(SampleAnalysisTypesEnum.valueOf(getSampleAnalysisType()));
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public default boolean isAnalysisTypeLAICPMS() {
        boolean retVal = false;
        try {
            retVal = SampleAnalysisTypesEnum.LAICPMS.equals(SampleAnalysisTypesEnum.valueOf(getSampleAnalysisType()));
        } catch (Exception e) {
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public default boolean isSampleTypeLegacy() {
        return (getSampleType().equalsIgnoreCase(SampleTypesEnum.LEGACY.getName()));
    }

    /**
     *
     * @return
     */
    public abstract boolean isSampleTypeProject();

    /**
     *
     * @return
     */
    public default boolean isAnalysisTypeCompiled() {
        return (getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.COMPILED.getName()));
    }

    /**
     *
     * @return
     */
    public default boolean isSampleTypeLiveWorkflow() {
        return getSampleType().equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName());
    }


    /**
     * @return the sampleAnalysisType
     */
    public abstract String getSampleAnalysisType();

    /**
     * @return the mineralName
     */
    public abstract String getMineralName();

    /**
     * @param mineralName the mineralName to set
     */
    public abstract void setMineralName(String mineralName);

    /**
     * @return the sampleFolderSaved
     */
    public abstract File getSampleFolderSaved();

    /**
     * @param sampleFolderSaved the sampleFolderSaved to set
     */
    public abstract void setSampleFolderSaved(File sampleFolderSaved);

    /**
     * @param automaticDataUpdateMode the automaticDataUpdateMode to set
     */
    public abstract void setAutomaticDataUpdateMode(boolean automaticDataUpdateMode);

    /**
     * @return the automaticDataUpdateMode
     */
    public abstract boolean isAutomaticDataUpdateMode();

    /**
     * @param calculateTWrhoForLegacyData the calculateTWrhoForLegacyData to set
     */
    public abstract void setCalculateTWrhoForLegacyData(boolean calculateTWrhoForLegacyData);

    /**
     * @return the calculateTWrhoForLegacyData
     */
    public abstract boolean isCalculateTWrhoForLegacyData();

    // Aliquots **************************************************************** Aliquots ****************************************************************
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
    public default AliquotInterface getAliquotByName(String name) {
        AliquotInterface retAliquot = null;
        Vector<AliquotInterface> aliquots = getAliquots();
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
     * @return
     */
    public default Vector<AliquotInterface> getActiveAliquots() {
        // May 2010  refresh aliquots to   remove empty ones
        Vector<AliquotInterface> activeAliquots = new Vector<>();
        getAliquots().stream().filter((aliquot)
                -> (((UPbReduxAliquot) aliquot).getAliquotFractions().size() > 0)).filter((aliquot)
                        -> (((UPbReduxAliquot) aliquot).containsActiveFractions())).forEach(activeAliquots::add);
        return activeAliquots;
    }

    /**
     * gets the <code>aliquots</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>aliquots</code> of this <code>Sample</code>
     * @return  <code>Vector</code> - set of <code>Aliquots</code> of this
     * <code>Sample</code>
     */
    public abstract Vector<AliquotInterface> getAliquots();

    /**
     * sets the <code>aliquots</code> of this <code>Sample</code> to the
     * argument <code>aliquots</code>
     *
     * @pre argument <code>aliquots</code> is a valid set of
     * <code>Aliquots</code>
     * @post this <code>Sample</code>'s <code>aliquots</code> is set to argument
     * <code>aliquots</code>
     * @param aliquots value to which <code>aliquots</code> of this
     * <code>Sample</code> will be set
     */
    public abstract void setAliquots(Vector<AliquotInterface> aliquots);

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
    public default AliquotInterface getAliquotByNumber(int aliquotNum) {
        // here we populate the aliquotFractionFiles of aliquot in case they have changed
        // aliquots are really aliquot view of the aliquotFractionFiles (MVC architecture)
        AliquotInterface retAliquot = getAliquots().get(aliquotNum - 1);

        Vector<Fraction> retFractions = new Vector<>();

        for (Iterator it = getFractions().iterator(); it.hasNext();) {
            Fraction temp = ((Fraction) it.next());
            if (((UPbFractionI) temp).getAliquotNumber() == aliquotNum) {
                retFractions.add(temp);
            }
        }

        ((UPbReduxAliquot) retAliquot).setAliquotFractions(retFractions);
        ((UPbReduxAliquot) retAliquot).setMyReduxLabData(ReduxLabData.getInstance());

        return retAliquot;
    }

    /**
     * Feb 2015 This method handles the messy situation where a project refers
     * to each of its aliquots by index 1...n but to upload project aliquots
     * individually to Geochron with concordia etc means there is only one
     * aliquot per sample and we ignore its number.
     *
     * @param aliquotnum
     * @return
     */
    public default String getNameOfAliquotFromSample(int aliquotNum) {
        String retval;
        if (getAliquots().size() == 1) {
            retval = getAliquots().get(0).getAliquotName();
        } else {
            retval = getAliquots().get(aliquotNum - 1).getAliquotName();
        }

        return retval;
    }

    /**
     *
     * @param aliquotName
     * @return
     * @throws ETException
     */
    public default AliquotInterface addNewAliquot(String aliquotName) throws ETException {
        if (getAliquotByName(aliquotName) == null) {
            AliquotInterface tempAliquot;
            tempAliquot = getAliquotByNumber(addNewDefaultAliquot());
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
     * <code>aliquots</code> and the size of      <code>Aliquots</code> is returned
     *
     * @return <code>int</code> - if successful, returns the size of the array
     * after adding the new <code>Aliquot</code>. Else, returns -1.
     */
    public default int addNewDefaultAliquot() {
        int retval = -1;
        try {
            AliquotInterface tempAliquot
                    = new UPbReduxAliquot(
                            getAliquots().size() + 1,
                            "Aliquot-" + Integer.toString(getAliquots().size() + 1),
                            ReduxLabData.getInstance(),
                            getPhysicalConstantsModel(),
                            isAnalyzed(),
                            getMySESARSampleMetadata());

            tempAliquot.setSampleIGSN(getSampleIGSN());
            getAliquots().add(tempAliquot);
            setChanged(true);
            retval = getAliquots().size();
        } catch (BadLabDataException ex) {
            Logger.getLogger(Sample.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retval;
    }

    /**
     *
     * @param aliquot the value of aliquot
     * @param sample the value of sample
     */
    public static void copyAliquotIntoSample(AliquotInterface aliquot, SampleInterface sample) {
        AliquotInterface importedAliquot = new UPbReduxAliquot();

        Vector<AliquotInterface> aliquots = sample.getAliquots();
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

        ((UPbReduxAliquot) importedAliquot).setAliquotFractions(new Vector<>());

        Iterator<Fraction> aliquotFractionIterator = ((UPbReduxAliquot) aliquot).getAliquotFractions().iterator();
        while (aliquotFractionIterator.hasNext()) {
            Fraction fraction = aliquotFractionIterator.next();

            ((FractionInterface) fraction).setAliquotNumber(aliquotNumber);
            ((UPbReduxAliquot) importedAliquot).getAliquotFractions().add(fraction);

            sample.getFractions().add(fraction);
        }

        aliquots.add(importedAliquot);
    }

    /**
     * reads in data from the XML file specified by argument
     * <code>aliquotFile</code> and adds any <code>Aliquots</code> found in the
     * file to this <code>Sample</code>.
     *
     * @param sample the value of sample
     * @param aliquot
     * @param aliquotSource
     * @pre file specified by <code>aliquotFile</code> is an XML file containing
     * valid <code>Aliquots</code>
     * @post all <code>Aliquots</code> found in the file are added to this
     * <code>Sample</code>
     * @throws java.io.IOException IOException
     * @throws org.earthtime.XMLExceptions.ETException ETException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    public static void importAliquotIntoSample(SampleInterface sample, AliquotInterface aliquot, String aliquotSource)
            throws IOException,
            ETException,
            BadLabDataException {

        // aliquot numbering is 1-based
        int aliquotNumber = sample.getAliquots().size() + 1;

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
                    tracer = ReduxLabData.getInstance().getNoneTracer();
                }
                AbstractRatiosDataModel pbBlank = aliquot.getAPbBlank(fraction.getPbBlankID());
                if (pbBlank == null) {
                    pbBlank = ReduxLabData.getInstance().getNonePbBlankModel();
                }

                // aug 2010 set initialPbModel
                if (fraction.getInitialPbModel() == null) {
                    // model ReduxConstants.NONE
                    fraction.setInitialPbModel(ReduxLabData.getInstance().getNoneInitialPbModel());
                }

                Fraction nextFraction;
                if (fraction.isLegacy()) {
                    nextFraction = new UPbLegacyFraction(
                            aliquotNumber,
                            fraction);
                } else {
                    nextFraction = new UPbFraction(
                            aliquotNumber,
                            fraction,
                            ReduxLabData.getInstance(),
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
                    alphaPbModel = ReduxLabData.getInstance().getNoneAlphaPbModel();
                    nextFraction.setAlphaPbModelID(alphaPbModel.getName());
                }

                ValueModel alphaUModel = aliquot.getAnAlphaUModel(fraction.getAlphaUModelID());
                if (alphaUModel == null) {
                    alphaUModel = ReduxLabData.getInstance().getFirstAlphaUModel();
                    nextFraction.setAlphaUModelID(alphaUModel.getName());
                }

                ((UPbFractionI) nextFraction).setAlphaPbModel(alphaPbModel);
                ((UPbFractionI) nextFraction).setAlphaUModel(alphaUModel);

                ((FractionInterface) nextFraction).setPhysicalConstantsModel(aliquot.getPhysicalConstants());// was sample.getphys...

                ((UPbReduxAliquot) aliquot).getAliquotFractions().add(nextFraction);

                sample.getFractions().add(nextFraction);
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
                            ReduxLabData.getInstance());
                }

                ((FractionInterface) nextFraction).setPhysicalConstantsModel(aliquot.getPhysicalConstants());

                ((UPbReduxAliquot) aliquot).getAliquotFractions().add(nextFraction);

                sample.getFractions().add(nextFraction);
            }
        }
        sample.getAliquots().add(aliquot);
        sample.setChanged(false);
    }

    /**
     * imports all <code>Aliquots</code> found in the XML file specified by
     * argument <code>folderName</code> to this <code>Sample</code>.
     *
     * @param sample the value of sample
     * @param folderName the file to read data from
     * @pre argument <code>location</code> specifies an XML file containing
     * valid <code>Aliquots</code>
     * @post all <code>Aliquots</code> found in the file are added to this
     * <code>Sample</code>
     * @throws java.io.FileNotFoundException FileNotFoundException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     * @throws java.io.IOException IOException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException
     * BadOrMissingXMLSchemaException
     * @return the java.lang.String
     */
    public static String importAliquotFromLocalXMLFileIntoSample(SampleInterface sample, File folderName)
            throws FileNotFoundException, BadLabDataException, IOException, BadOrMissingXMLSchemaException {
        String retval = "";

        String dialogTitle = "Select a U-Pb Aliquot XML File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();
        File aliquotFile
                = FileHelper.AllPlatformGetFile(dialogTitle, folderName, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (aliquotFile != null) {
            try {
                Aliquot aliquotFromFile = new UPbReduxAliquot();

                aliquotFromFile
                        = (Aliquot) ((XMLSerializationI) aliquotFromFile).readXMLObject(
                                aliquotFile.getCanonicalPath(), true);

                importAliquotIntoSample(sample, aliquotFromFile, aliquotFile.getName());
            } catch (ETException ex) {
            } finally {
                // return folder for persistent state even if fails
                retval = aliquotFile.getParent();
            }
        } else {
            throw new FileNotFoundException();
        }

        return retval;
    }

    /**
     * imports all <code>UPbFractions</code> found in the XML file specified by
     * argument <code>location</code> to the <code>Aliquot</code> specified by
     * argument <code>aliquotNumber</code> in this <code>Sample</code>
     *
     * @param sample the value of sample
     * @param folder the file to read data from
     * @param aliquotNumber the number of the <code>Aliquot</code> which the
     * <code>Fractions</code> belong to
     * @param doValidate
     * @pre argument <code>location</code> specifies an XML file containing
     * valid <code>UPbFractions</code>
     * @post all <code>UPbFractions</code> found in the file are added to the
     * <code>Aliquot</code> specified by <code>aliquotNumber</code> in this
     * <code>Sample</code>
     * @throws java.io.FileNotFoundException FileNotFoundException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     * @return the java.lang.String
     */
    public static String importFractionsFromXMLFilesIntoSample(
            SampleInterface sample, File folder, int aliquotNumber, boolean doValidate)
            throws FileNotFoundException, BadLabDataException {

        String retval = null;

        String dialogTitle = "Select one or more U-Pb Redux Fraction File(s) to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        File[] returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, folder, fileExtension, nonMacFileFilter, true, new JFrame());

        int successCount = 0;
        if (returnFile[0] != null) {
            // nov 2008
            // first determine if the sample is empty and if it is,
            // use the first xml file as the automatic source of the
            // sample file
            if (sample.getFractions().size() <= 1) {
                try {
                    sample.setSampleName(((UPbSampleInterface) sample).processXMLFractionFile(returnFile[0], aliquotNumber, false, doValidate));
                    successCount = 1;
                } catch (ETException uPbReduxException) {
                }
            }

            for (int i = successCount; i < returnFile.length; i++) {
                try {
                    ((UPbSampleInterface) sample).processXMLFractionFile(returnFile[i], aliquotNumber, true, doValidate);
                    successCount++;
                } catch (ETException ex) {
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
     * removes <code>Fractions</code> from this <code>Sample</code>'s sample age
     * models that are no longer aliquot part of this <code>Sample</code>'s
     * <code>Aliquots</code>.
     *
     * @param sample
     * @pre this <code>Sample</code> exists
     * @post any <code>Fractions</code> that are found in this
     * <code>Sample</code>'s sample age models that are no longer a part of this
     * <code>Sample</code>'s <code>Aliquots</code> are removed
     */
    public static void updateAndSaveSampleDateModelsByAliquot(SampleInterface sample) {
        // May 2008
        // process all sampleAgeModels' included fraction vectors to remove missing aliquotFractionFiles
        sample.getAliquots().stream().map((nextAliquot) -> {
            // may 2012 next line to force reduction
            ((UPbReduxAliquot) nextAliquot).reduceData();
            return nextAliquot;
        }).forEach((nextAliquot) -> {
            nextAliquot.updateSampleDateModels();
        });
    }

    /**
     *
     * @param aliquot
     */
    public default void removeAliquot(AliquotInterface aliquot) {
        Vector<Fraction> aliquotFractions = ((UPbReduxAliquot) aliquot).getAliquotFractions();
        for (Fraction aliquotFraction : aliquotFractions) {
            removeUPbReduxFraction(aliquotFraction);
        }

    }

    /**
     *
     * @param nameAliquotA
     * @param nameAliquotB
     * @return
     */
    public default boolean swapOrderOfTwoAliquots(String nameAliquotA, String nameAliquotB) {

        boolean retVal = false;

        AliquotInterface aliquotA = getAliquotByName(nameAliquotA);
        AliquotInterface aliquotB = getAliquotByName(nameAliquotB);

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
            getAliquots().setElementAt(aliquotA, numberAliquotB - 1);
            getAliquots().setElementAt(aliquotB, numberAliquotA - 1);

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
     *
     * @param fID
     * @return
     */
    public default String getAliquotNameByFractionID(String fID) {
        return getAliquotByNumber(//
                ((FractionInterface) getFractionByID(fID)).getAliquotNumber()).getAliquotName();
    }

    // Fractions *************************************************************** Fractions ***************************************************************
    /**
     * gets the <code>Fractions</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>Fraction</code> of this <code>Sample</code>
     * @return  <code>Vector</code> - set of <code>Fractions</code> that make up
     * the <code>Fraction</code> of this <code>Sample</code>
     */
    public abstract Vector<Fraction> getFractions();

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
    public default Fraction getFractionByID(String ID) {
        Fraction retFraction = null;
        Vector<Fraction> fractions = getFractions();

        for (int fractionIndex = 0; fractionIndex < fractions.size(); fractionIndex++) {
            if (fractions.get(fractionIndex).getFractionID().equalsIgnoreCase(ID)) {
                retFraction = fractions.get(fractionIndex);
                break;
            }
        }

        return retFraction;
    }

    /**
     * sets the <code>UPbFractions</code> of this <code>Sample</code> to the
     * argument <code>UPbFractions</code>
     *
     * @pre argument <code>UPbFractions</code> is a valid set of
     * <code>UPbFractions</code>
     * @post this <code>Sample</code>'s <code>UPbFractions</code> is set to
     * argument <code>UPbFractions</code>
     * @param UPbFractions value to which <code>UPbFractions</code> of this
     * <code>Sample</code> will be set
     */
    public abstract void setUPbFractions(Vector<Fraction> UPbFractions);

    /**
     *
     * @return
     */
    public default Vector<Fraction> getFractionsRejected() {
        Vector<Fraction> retval = new Vector<>();

        getFractions().stream().filter((f) -> (((FractionInterface) f).isRejected())).forEach((f) -> {
            retval.add(f);
        });

        return retval;
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
    public default void removeUPbReduxFraction(int index) {
        boolean fracStatus = ((FractionInterface) getFractions().get(index)).isChanged();
        try {
            getFractions().remove(index);
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
    public default void removeUPbReduxFraction(Fraction fraction) {
        boolean fracStatus = ((FractionInterface) fraction).isChanged();
        try {
            getFractions().remove(fraction);
            ((FractionInterface) fraction).getAliquotNumber();
        } finally {
        }
        setChanged(isChanged() || fracStatus);
    }

    /**
     *
     * @param name
     * @return
     */
    public default Fraction getSampleFractionByName(String name) {
        Fraction retVal = null;

        for (Fraction f : getFractions()) {
            if (f.getFractionID().equalsIgnoreCase(name)) {
                retVal = f;
            }
        }
        return retVal;

    }

    /**
     *
     * @return
     */
    public default Vector<Fraction> getFractionsActive() {
        Vector<Fraction> retval = new Vector<>();

        getFractions().stream().filter((f) -> (!((FractionInterface) f).isRejected())).forEach((f) -> {
            retval.add(f);
        });

        return retval;
    }

    /**
     *
     * @return
     */
    public default Vector<Fraction> getUpbFractionsUnknown() {
        Vector<Fraction> retval = new Vector<>();

        getFractions().stream().filter((f) -> (!((FractionInterface) f).isStandard())).forEach((f) -> {
            retval.add(f);
        });

        return retval;
    }

    /**
     *
     * @param filteredFractions
     */
    public default void updateSetOfActiveFractions(Vector<Fraction> filteredFractions) {
        getFractions().stream().forEach((UPbFraction) -> {
            ((FractionInterface) UPbFraction).setRejected(!filteredFractions.contains(UPbFraction));
        });
    }

    /**
     *
     * @return
     */
    public default Vector<String> getSampleFractionIDs() {
        Vector<String> retVal = new Vector<>();

        getFractions().stream().filter((f) -> (!((UPbFractionI) f).isRejected())).forEach((f) -> {
            retVal.add(f.getFractionID());
        });
        return retVal;

    }

    /**
     *
     */
    public default void deSelectAllFractionsInDataTable() {
        getFractions().stream().forEach((fraction) -> {
            ((FractionInterface) fraction).setSelectedInDataTable(false);
        });
    }

    /**
     *
     */
    public default void deSelectAllFractions() {
        getFractions().stream().forEach((f) -> {
            ((UPbFractionI) f).setRejected(true);
        });
    }

    /**
     *
     */
    public default void selectAllFractions() {
        getFractions().stream().forEach((f) -> {
            ((FractionInterface) f).setRejected(false);
        });
    }

    /**
     *
     * @param sampleName
     */
    public default void updateSampleFractionsWithSampleName(String sampleName) {
        for (int i = 0; i
                < getFractions().size(); i++) {
            getFractions().get(i).setSampleName(sampleName);
        }

    }

    /**
     *
     * @param fractions
     * @param aliquotNumber
     */
    public default void addFractionsVector(Vector<Fraction> fractions, int aliquotNumber) {
        for (Fraction f : fractions) {
            f.setSampleName(getSampleName());
            ((FractionInterface) f).setAliquotNumber(aliquotNumber);
            addFraction(f);
        }
    }

    /**
     *
     * @param fractions
     * @param aliquotNumber
     */
    public default void addUPbFractionArrayList(ArrayList<Fraction> fractions, int aliquotNumber) {

        for (Fraction f : fractions) {
            f.setSampleName(getSampleName());
            ((FractionInterface) f).setAliquotNumber(aliquotNumber);
            addFraction(f);
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
    public default void addFraction(Fraction newFraction) {
        getFractions().add(newFraction);
        setChanged(true);
    }

    // Sample Date Models ***************************************************************** Sample Date Models *****************************************************************
    /**
     *
     */
    public default void updateSampleDateModels() {
        // process all sampleDateModels' included fraction vectors to remove missing aliquotFractionFiles
        Vector<String> includedFractionIDs = getSampleFractionIDs();
        Vector<String> excludedFractionIDs = new Vector<>();
        boolean existsPreferredDate = false;

        for (ValueModel SAM : getSampleDateModels()) {
            Vector<String> SAMFractionIDs = ((SampleDateModel) SAM).getIncludedFractionIDsVector();

            for (String fractionID : SAMFractionIDs) {
                if (!includedFractionIDs.contains(fractionID)) {
                    excludedFractionIDs.add(fractionID);
                }
            }
            // remove found exclusions (these are ones that were rejected after processing
            excludedFractionIDs.stream().forEach((fractionID) -> {
                ((SampleDateModel) SAM).getIncludedFractionIDsVector().remove(fractionID);
            });

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
     * @param sampleDateModels the sampleDateModels to set
     */
    public abstract void setSampleDateModels(Vector<ValueModel> sampleDateModels);

    /**
     * @return the sampleDateModels
     */
    public abstract Vector<ValueModel> getSampleDateModels();

    /**
     *
     * @param selectedFractionIDs
     * @return
     */
    public default Vector<Fraction> getSampleDateModelSelectedFractions(Vector<String> selectedFractionIDs) {
        Vector<Fraction> retVal = new Vector<>();

        selectedFractionIDs.stream().forEach((fID) -> {
            retVal.add(getSampleFractionByName(fID));
        });

        return retVal;
    }

    /**
     *
     * @param selectedFractionIDs
     * @return
     */
    public default Vector<Fraction> getSampleDateModelDeSelectedFractions(Vector<String> selectedFractionIDs) {
        Vector<Fraction> retVal = new Vector<>();

        getSampleFractionIDs().stream().filter((fID) -> (!selectedFractionIDs.contains(fID))).forEach((fID) -> {
            retVal.add(getSampleFractionByName(fID));
        });
        return retVal;
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
    public abstract void setFractionDataOverriddenOnImport(boolean fractionDataOverriddenOnImport);

    /**
     *
     * @return
     */
    public default ValueModel getPreferredSampleDateModel() {
        ValueModel retVal = null;
        Iterator it = getSampleDateModels().iterator();

        while (it.hasNext()) {
            retVal = (ValueModel) it.next();
            if (((SampleDateModel) retVal).isPreferred()) {
                return retVal;
            }
        }
        return retVal;
    }

    /**
     *
     * @param sampleDateModel
     */
    public default void setPreferredSampleDateModel(ValueModel sampleDateModel) {
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
    public default boolean containsSampleDateModelByName(String sampleDateModelName) {
        boolean retVal = false;

        for (ValueModel sam : getSampleDateModels()) {
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
    public default ValueModel getSampleDateModelByName(
            String sampleDateModelName) {
        ValueModel retVal = null;

        for (ValueModel sdm : getSampleDateModels()) {
            if (sdm.getName().equalsIgnoreCase(sampleDateModelName)) {
                retVal = sdm;
            }
        }
        return retVal;
    }

    /**
     *
     * @param includeSingleDates
     * @return
     */
    public default Vector<ValueModel> determineUnusedSampleDateModels(boolean includeSingleDates) {
        Vector<ValueModel> retVal = new Vector<ValueModel>();
        // choose models not already in use by Aliquot
        for (int i = 0; i < SampleDateTypes.getSampleDateModelTypes().length; i++) {
            if (!includeSingleDates
                    && SampleDateTypes.getSampleDateType(i).startsWith("single")) {
                // do nothing
            } else {
                if (getSampleDateModelByName(SampleDateTypes.getSampleDateType(i)) == null) {
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

    // Report Settings ***************************************************************Report Settings ************************************************************
    /**
     *
     * @param reportSettingsModel
     */
    public abstract void setReportSettingsModel(ReportSettings reportSettingsModel);

    /**
     *
     */
    public default void setLegacyStatusForReportTable() {
        // feb 2010 added legacyData field to force display when no reduction happening
        try {
            getReportSettingsModel().setLegacyData(isAnalysisTypeCompiled() || isAnalyzed());
        } catch (Exception e) {
        }
    }

    /**
     *
     * @return
     */
    public abstract ReportSettings getReportSettingsModel();

    /**
     *
     * @param sample the value of sample
     * @param isNumeric
     * @return the java.lang.String[][]
     */
    public static String[][] reportRejectedFractionsByNumberStyle(SampleInterface sample, boolean isNumeric) {
        return sample.getReportSettingsModel().reportRejectedFractionsByNumberStyle(sample, isNumeric);
    }

    /**
     *
     * @param sample the value of sample
     * @param isNumeric
     * @return the java.lang.String[][]
     */
    public static String[][] reportActiveFractionsByNumberStyle(SampleInterface sample, boolean isNumeric) {
        return sample.getReportSettingsModel().reportActiveFractionsByNumberStyle(sample, isNumeric);
    }

    /**
     *
     * @param sample the value of sample
     * @param aliquot
     * @param isNumeric
     * @return the java.lang.String[][]
     */
    public static String[][] reportActiveAliquotFractionsByNumberStyle(SampleInterface sample, AliquotInterface aliquot, boolean isNumeric) {

        return sample.getReportSettingsModel().reportActiveAliquotFractionsByNumberStyle(sample, ((UPbReduxAliquot) aliquot).getActiveAliquotFractions(), isNumeric);
    }

    /**
     *
     */
    public default void restoreDefaultReportSettingsModel() {
        try {
            setReportSettingsModel(ReduxLabData.getInstance().getDefaultReportSettingsModel());
        } catch (BadLabDataException badLabDataException) {
        }
    }

    /**
     *
     * @param reportsFolderPath
     * @return
     * @throws BadLabDataException
     */
    public default String saveReportSettingsToFile(String reportsFolderPath)
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
            getReportSettingsModel().serializeXMLObject(selectedFile.getAbsolutePath());
            retVal = selectedFile.getParent();
        }

        return retVal;
    }

    // Archiving *************************************************************** Archiving ***************************************************************
    /**
     * sets the <code>sampleIGSN</code> of this <code>Sample</code> to the
     * argument <code>sampleIGSN</code>
     *
     * @pre argument <code>sampleIGSN</code> is a valid <code>sampleIGSN</code>
     * @post this <code>Sample</code>'s <code>sampleIGSN</code> is set to
     * argument <code>sampleIGSN</code>
     * @param sampleIGSN value to which <code>sampleIGSN</code> of this
     * <code>Sample</code> will be set
     */
    public abstract void setSampleIGSN(String sampleIGSN);

    /**
     * gets the <code>sampleIGSN</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>sampleIGSN</code> of this <code>Sample</code>
     * @return  <code>String</code> - <code>sampleIGSN</code> of this
     * <code>Sample</code>
     */
    public abstract String getSampleIGSN();

    /**
     * @param sampleRegistry the sampleRegistry to set
     */
    public abstract void setSampleRegistry(SampleRegistries sampleRegistry);

    /**
     *
     * @return
     */
    public abstract String getSampleIGSNnoRegistry();

    /**
     * @return the archivedInRegistry
     */
    public abstract boolean isArchivedInRegistry();

    /**
     * @param archivedInRegistry the archivedInRegistry to set
     */
    public abstract void setArchivedInRegistry(boolean archivedInRegistry);

    /**
     * @return the sampleRegistry
     */
    public abstract SampleRegistries getSampleRegistry();

    /**
     * @param validatedSampleIGSN the validatedSampleIGSN to set
     */
    public abstract void setValidatedSampleIGSN(boolean validatedSampleIGSN);

    /**
     * @return the validatedSampleIGSN
     */
    public abstract boolean isValidatedSampleIGSN();

    /**
     * sets the <code>changed</code> field of each <code>UPbFraction</code> in
     * this <code>Sample</code> to <code>false</code> and saves this
     * <code>Sample</code> as aliquot .redux file to
     * <code>reduxSampleFilePath</code>.
     *
     * @param sample the value of sample
     * @pre this <code>Sample</code> exists
     * @post this <code>Sample</code> is saved as a .redux file to the location
     * specified by <code>reduxSampleFilePath</code>
     */
    public static void saveSampleAsSerializedReduxFile(SampleInterface sample) {
        sample.setChanged(false);

        for (int fractionsIndex = 0; fractionsIndex
                < sample.getFractions().size(); fractionsIndex++) {
            ((FractionInterface) sample.getFractions().get(fractionsIndex)).setChanged(false);
        }

        if (sample.getReduxSampleFilePath().length() > 0) {

            try {
                ETSerializer.SerializeObjectToFile(sample, sample.getReduxSampleFilePath());
            } catch (ETException eTException) {
            }
        }

    }

    /**
     * saves this <code>Sample</code> to the file specified by argument
     * <code>file</code>.
     *
     * @param sample the value of sample
     * @param file the file where this <code>Sample</code> will be saved
     * @pre argument <code>file</code> is a valid file
     * @post this <code>Sample</code> is saved to the location specified by
     * argument <code>file</code>
     * @return the java.lang.String
     */
    public static String saveSampleAsSerializedReduxFile(
            SampleInterface sample, File file) {
        sample.setReduxSampleFilePath(file);
        saveSampleAsSerializedReduxFile(sample);

        return sample.getReduxSampleFilePath();
    }

    /**
     *
     * @param sample the value of sample
     * @param MRUSampleFolderPath the value of MRUSampleFolderPath @throws
     * BadLabDataException
     * @return the java.io.File
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     */
    public static File saveSampleFileAs(SampleInterface sample, String MRUSampleFolderPath) throws BadLabDataException {

        String dialogTitle = "Save Redux file for this Sample: *.redux";
        final String fileExtension = ".redux";
        String sampleFileName = sample.getSampleName() + fileExtension;
        FileFilter nonMacFileFilter = new ReduxFileFilter();

        File selectedFile;
        String sampleFolderPath = null;
        if (sample.getSampleFolderSaved() != null) {
            sampleFolderPath = sample.getSampleFolderSaved().getAbsolutePath();
        } else {
            sampleFolderPath = MRUSampleFolderPath;
        }

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(),
                dialogTitle,
                sampleFolderPath,
                fileExtension,
                sampleFileName,
                nonMacFileFilter);

        if (selectedFile != null) {
            saveSampleAsSerializedReduxFile(sample, selectedFile);

            // handle LIVEWORKFLOW because it contains no data yet
            if (sample.getSampleType().equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName())) {
                sample.setSampleFolderSaved(selectedFile.getParentFile());
            }
        }
        return selectedFile;
    }

    /**
     * @return the mySESARSampleMetadata
     */
    public abstract SESARSampleMetadata getMySESARSampleMetadata();

    // Parameter Models ********************************************************
    /**
     * sets the <code>physicalConstantsModel</code> of this <code>Sample</code>
     * to the argument <code>physicalConstantsModel</code>
     *
     * @pre argument <code>physicalConstantsModel</code> is a valid
     * <code>PhysicalConstants</code>
     * @post this <code>Sample</code>'s <code>physicalConstantsModel</code> is
     * set to argument <code>physicalConstantsModel</code>
     * @param physicalConstantsModel value to which
     * <code>physicalConstantsModel</code> of this <code>Sample</code> will be
     * set
     */
    public abstract void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel);

    /**
     * gets the <code>physicalConstantsModel</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>physicalConstantsModel</code> of this
     * <code>Sample</code>
     * @return  <code>PhysicalConstants</code> -
     * <code>physicalConstantsModel</code> of this <code>Sample</code>
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    public abstract AbstractRatiosDataModel getPhysicalConstantsModel() throws BadLabDataException;

    /**
     * sets <code>ReduxLabData</code> of this <code>Sample</code> and all
     * <code>Aliquots</code> and <code>Fractions</code> contained within to the
     * argument <code>labData</code>.
     *
     * @param sample the value of sample
     * @pre argument      <code>labData</code> is valid <code>ReduxLabData</code>
     * @post <code>ReduxLabData</code> of this <code>Sample</code> and all of
     * its <code>Aliquots</code> and <code>Fractions</code> is set to argument
     * <code>labData</code>
     */
    public static void registerSampleWithLabData(SampleInterface sample) {
        // register incoming models - by file - with lab data
        // TODO verify names and contents align
        for (int UPbFractionsIndex = 0; UPbFractionsIndex
                < sample.getFractions().size(); UPbFractionsIndex++) {
            Fraction nextFraction = sample.getFractions().get(UPbFractionsIndex);
            if (!nextFraction.isLegacy()) {
                ReduxLabData.getInstance().registerFractionWithLabData(nextFraction);
            }
        }

        for (AliquotInterface a : sample.getActiveAliquots()) {
            a.getMineralStandardModels().stream().forEach((msm) -> {
                ReduxLabData.getInstance().registerMineralStandardModel(msm, false);
            });
        }
    }

    // Graphics ********************************************************************
    /**
     *
     * @return
     */
    public abstract GraphAxesSetup getTerraWasserburgGraphAxesSetup();

    public abstract GraphAxesSetup getConcordiaGraphAxesSetup();

    /**
     * gets the <code>sampleAgeInterpretationGUISettings</code> of this
     * <code>Sample</code>
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>sampleAgeInterpretationGUISettings</code> of this
     * <code>Sample</code>
     * @return  <code>SampleDateInterpretationGUIOptions</code> -
     * <code>sampleAgeInterpretationGUIOptions</code> of this
     * <code>Sample</code>
     */
    public abstract SampleDateInterpretationGUIOptions getSampleDateInterpretationGUISettings();

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
     * @param sampleAgeInterpretationGUISettings value to which <code>
     * sampleAgeInterpretationGUISettings</code> of this <code>Sample</code>
     * will be set
     */
    void setSampleAgeInterpretationGUISettings(SampleDateInterpretationGUIOptions sampleAgeInterpretationGUISettings);

    //TODO: Refactor to static oe other tasks
    /**
     *
     * @param sample the value of sample
     * @param myFractionEditor the value of myFractionEditor
     * @throws ETException
     */
    public abstract void automaticUpdateOfUPbSampleFolder(SampleInterface sample, DialogEditor myFractionEditor) throws ETException;

    /**
     *
     */
    public default void reduceSampleData() {
        for (AliquotInterface aliquot : getAliquots()) {
            ((UPbReduxAliquot) aliquot).reduceData();

            // oct 2014 
            ((UPbReduxAliquot) aliquot).updateBestAge();
        }
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
     */
    public default void updateSampleLabName() {
        for (AliquotInterface a : getAliquots()) {
            a.setLaboratoryName(ReduxLabData.getInstance().getLabName());
        }
    }
}
