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
import java.util.Iterator;
import java.util.Vector;
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
import org.earthtime.UPb_Redux.samples.UPbSampleInterface;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.exceptions.ETException;
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
     * @return
     */
    public abstract boolean isAnalysisTypeTripolized();

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
    public abstract boolean isTypeAnalysis();

    /**
     * @param sampleAnalysisType the sampleAnalysisType to set
     */
    public abstract void setSampleAnalysisType(String sampleAnalysisType);

    /**
     *
     * @return
     */
    public abstract boolean isAnalysisTypeIDTIMS();

    /**
     *
     * @return
     */
    public abstract boolean isAnalysisTypeLAICPMS();

    /**
     *
     * @return
     */
    public abstract boolean isTypeLiveUpdate();

    /**
     *
     * @return
     */
    public abstract boolean isTypeLegacy();

    /**
     *
     * @return
     */
    public abstract boolean isTypeProject();

    /**
     *
     * @return
     */
    public abstract boolean isAnalysisTypeCompiled();

    /**
     *
     * @return
     */
    public abstract boolean isSampleTypeLiveWorkflow();

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

    /**
     *
     */
    public abstract void reduceSampleData();

    // Aliquots **************************************************************** Aliquots ****************************************************************
    /**
     * finds the <code>Aliquot</code> named <code>name</code> in the array
     * <code>aliquots</code>.
     *
     * @pre an <code>Aliquot</code> exists in <code>aliquots</code> named
     * <code>name</code>
     * @post the <code>Aliquot</code> whose name corresponds to argument
     * <code>name</code> is found and returned
     * @param name name of the <code>Aliquot</code> to retrieve
     * @return  <code>Aliquot</code> - the <code>Aliquot</code> from
     * <code>aliquots</code> whose name corresponds to the argument
     * <code>name</code>
     */
    public abstract Aliquot getAliquotByName(String name);

    public abstract Vector<Aliquot> getActiveAliquots();

    /**
     * gets the <code>aliquots</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>aliquots</code> of this <code>Sample</code>
     * @return  <code>Vector</code> - set of <code>Aliquots</code> of this
     * <code>Sample</code>
     */
    public abstract Vector<Aliquot> getAliquots();

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
    public abstract void setAliquots(Vector<Aliquot> aliquots);

    /**
     * finds the <code>Aliquot</code> numbered <code>aliquotNum</code> in the
     * array <code>aliquots</code>.
     *
     * @pre an <code>Aliquot</code> exists in <code>aliquots</code> numbered
     * with <code>aliquotNum</code>
     * @post the <code>Aliquot</code> whose number corresponds to argument
     * <code>aliquotNum</code> is found and returned
     * @param aliquotNum number of the <code>Aliquot</code> to retrieve
     * @return  <code>Aliquot</code> - the <code>Aliquot</code> from
     * <code>aliquots</code> whose number corresponds to the argument
     * <code>aliquotNum</code>
     */
    public abstract Aliquot getAliquotByNumber(int aliquotNum);

    public abstract String getNameOfAliquotFromSample(int aliquotNum);

    /**
     *
     * @param aliquotName
     * @return
     * @throws ETException
     */
    public abstract Aliquot addNewAliquot(String aliquotName) throws ETException;

    /**
     *
     * @param aliquot the value of aliquot
     * @param sample the value of sample
     */
    public static void copyAliquotIntoSample(Aliquot aliquot, SampleInterface sample) {
        Aliquot importedAliquot = new UPbReduxAliquot();

        Vector<Aliquot> aliquots = sample.getAliquots();
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

            ((UPbFractionI) fraction).setAliquotNumber(aliquotNumber);
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
    public static void importAliquotIntoSample(SampleInterface sample, Aliquot aliquot, String aliquotSource)
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
                    tracer = sample.getMyReduxLabData().getNoneTracer();
                }
                AbstractRatiosDataModel pbBlank = aliquot.getAPbBlank(fraction.getPbBlankID());
                if (pbBlank == null) {
                    pbBlank = sample.getMyReduxLabData().getNonePbBlankModel();
                }

                // aug 2010 set initialPbModel
                if (fraction.getInitialPbModel() == null) {
                    // model ReduxConstants.NONE
                    fraction.setInitialPbModel(sample.getMyReduxLabData().getNoneInitialPbModel());
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
                            sample.getMyReduxLabData(),
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
                    alphaPbModel = sample.getMyReduxLabData().getNoneAlphaPbModel();
                    nextFraction.setAlphaPbModelID(alphaPbModel.getName());
                }

                ValueModel alphaUModel = aliquot.getAnAlphaUModel(fraction.getAlphaUModelID());
                if (alphaUModel == null) {
                    alphaUModel = sample.getMyReduxLabData().getFirstAlphaUModel();
                    nextFraction.setAlphaUModelID(alphaUModel.getName());
                }

                ((UPbFractionI) nextFraction).setAlphaPbModel(alphaPbModel);
                ((UPbFractionI) nextFraction).setAlphaUModel(alphaUModel);

                ((UPbFractionI) nextFraction).setPhysicalConstantsModel(aliquot.getPhysicalConstants());// was sample.getphys...

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
                            sample.getMyReduxLabData());
                }

                ((UPbFractionI) nextFraction).setPhysicalConstantsModel(aliquot.getPhysicalConstants());

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
    public abstract void removeAliquot(Aliquot aliquot);

    /**
     *
     * @param nameAliquotA
     * @param nameAliquotB
     * @return
     */
    public abstract boolean swapOrderOfTwoAliquots(String nameAliquotA, String nameAliquotB);

    /**
     *
     * @param fID
     * @return
     */
    public abstract String getAliquotNameByFractionID(
            String fID);

    // Fractions *************************************************************** Fractions ***************************************************************
    /**
     * adds a <code>Fraction</code> to the <code>Sample</code>'s set of
     * <code>Fractions</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post a new default <code>Fraction</code> is added to this
     * <code>Sample</code>'s <code>Fractions</code>
     * @param newFraction the <code>Fraction</code> to add to this
     * <code>Sample</code>
     */
    public abstract void addFraction(Fraction newFraction);

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
    public abstract Fraction getFractionByID(String ID);

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

    public abstract Vector<Fraction> getUpbFractionsRejected();

    /**
     * removes the <code>UPbFraction</code> found at <code>index</code> from
     * this <code>Sample</code>'s set of <code>Fractions</code>
     *
     * @pre a <code>Fraction</code> exists in this <code>Sample</code>'s set of
     * <code>Fractions</code> at <code>index</code>
     * @post the <code>Fraction</code> found at <code>index</code> is removed
     * from the set of <code>Fractions</code>
     * @param index the index into the array of <code>Fractions</code> where the
     * <code>Fraction</code> to be removed can be found
     */
    void removeUPbReduxFraction(int index);

    /**
     * removes the <code>UPbFraction</code> from this <code>Sample</code>'s set
     * of <code>Fractions</code> that corresponds to the argument
     * <code>fraction</code>
     *
     * @pre a <code>Fraction</code> exists in this <code>Sample</code>'s set of
     * <code>Fractions</code> that corresponds to <code>fraction</code>
     * @post the <code>Fraction</code> that corresponds to the argument
     * <code>fraction</code> is removed
     * @param fraction
     */
    void removeUPbReduxFraction(Fraction fraction);

    /**
     *
     * @param name
     * @return
     */
    public abstract Fraction getSampleFractionByName(
            String name);

    /**
     *
     * @return
     */
    public abstract Vector<Fraction> getUpbFractionsActive();

    /**
     *
     * @return
     */
    public Vector<Fraction> getUpbFractionsUnknown();

    /**
     *
     * @param filteredFractions
     */
    public abstract void updateSetOfActiveFractions(Vector<Fraction> filteredFractions);

    /**
     *
     * @return
     */
    public abstract Vector<String> getSampleFractionIDs();

    /**
     *
     */
    public abstract void deSelectAllFractionsInDataTable();

    /**
     *
     */
    public abstract void deSelectAllFractions();

    /**
     *
     */
    public abstract void selectAllFractions();

    /**
     *
     * @param fractions
     * @param aliquotNumber
     */
    public abstract void addUPbFractionVector(Vector<Fraction> fractions, int aliquotNumber);

    // Sample Date Models ***************************************************************** Sample Date Models *****************************************************************
    /**
     *
     */
    public abstract void updateSampleDateModels();

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
     * @param modelName
     * @return
     */
    public abstract ValueModel getSampleDateModelByName(String modelName);

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
    // Report Settings ***************************************************************Report Settings ************************************************************

    /**
     *
     * @return
     */
    public abstract ReportSettings getReportSettingsModelUpdatedToLatestVersion();

    /**
     *
     * @param reportSettingsModel
     */
    public abstract void setReportSettingsModel(ReportSettings reportSettingsModel);

    public abstract void setLegacyStatusForReportTable();

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
    public static String[][] reportActiveAliquotFractionsByNumberStyle(SampleInterface sample, Aliquot aliquot, boolean isNumeric) {

        return sample.getReportSettingsModel().reportActiveAliquotFractionsByNumberStyle(sample, ((UPbReduxAliquot) aliquot).getActiveAliquotFractions(), isNumeric);
    }

    /**
     *
     * @param reportsFolderPath
     * @return
     * @throws BadLabDataException
     */
    public abstract String saveReportSettingsToFile(String reportsFolderPath)
            throws BadLabDataException;

    /**
     *
     */
    public abstract void restoreDefaultReportSettingsModel();

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

        for (int UPbFractionsIndex = 0; UPbFractionsIndex
                < sample.getFractions().size(); UPbFractionsIndex++) {
            ((UPbFractionI) sample.getFractions().get(UPbFractionsIndex)).setChanged(false);
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
     * sets the <code>myReduxLabData</code> field of this <code>Sample</code> to
     * the argument <code>myReduxLabData</code>
     *
     * @pre argument <code>myReduxLabData</code> is a valid
     * <code>ReduxLabData</code>
     * @post this <code>Sample</code>'s <code>myReduxLabData</code> field is set
     * to argument <code>myReduxLabData</code>
     * @param myReduxLabData value to which <code>myReduxLabData</code> field of
     * this <code>Sample</code> will be set
     */
    public abstract void setMyReduxLabData(ReduxLabData myReduxLabData);

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
     * gets the <code>myReduxLabData</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>myReduxLabData</code> of this <code>Sampel</code>
     * @return  <code>ReduxLabData</code> - <code>myReduxLabData</code> of this
     * <code>Sample</code>
     */
    public abstract ReduxLabData getMyReduxLabData();

    /**
     * sets <code>ReduxLabData</code> of this <code>Sample</code> and all
     * <code>Aliquots</code> and <code>Fractions</code> contained within to the
     * argument <code>labData</code>.
     *
     * @param sample the value of sample
     * @param labData value to which this <code>Sample</code> and its
     * <code>Aliquots</code> and <code>Fractions</code> should be set to
     * @pre argument      <code>labData</code> is valid <code>ReduxLabData</code>
     * @post <code>ReduxLabData</code> of this <code>Sample</code> and all of
     * its <code>Aliquots</code> and <code>Fractions</code> is set to argument
     * <code>labData</code>
     */
    public static void registerSampleWithLabData(SampleInterface sample, ReduxLabData labData) {
        sample.setMyReduxLabData(labData);

        // register incoming models - by file - with lab data
        // TODO verify names and contents align
        for (int UPbFractionsIndex = 0; UPbFractionsIndex
                < sample.getFractions().size(); UPbFractionsIndex++) {
            Fraction nextFraction = sample.getFractions().get(UPbFractionsIndex);
            if (!nextFraction.isLegacy()) {
                labData.registerFractionWithLabData(nextFraction);
            }
        }

        for (Aliquot a : sample.getActiveAliquots()) {
            a.getMineralStandardModels().stream().forEach((msm) -> {
                labData.registerMineralStandardModel(msm, false);
            });
        }
    }

    // Graphics
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

    //TODO: Refactor to static
    /**
     *
     * @param sample the value of sample
     * @param myFractionEditor the value of myFractionEditor
     * @throws ETException
     */
    public abstract void automaticUpdateOfUPbSampleFolder(SampleInterface sample, DialogEditor myFractionEditor) throws ETException;
}
