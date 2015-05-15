/* Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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

package org.earthtime.UPb_Redux.samples;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reports.ReportSettings;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;


/**
 * Any class that implements <code>Sample</code> should contain all of the
 * scientific data related to that geological sample as well as the
 * additional methods to manipulate this data.
 * 
 * @author Stan Gasque
 */
public interface UPbSampleInterface {

    /**
     * imports all <code>Aliquots</code> found in the XML file specified by
     * argument <code>location</code> to this <code>Sample</code>.
     *
     * @pre     argument <code>location</code> specifies an XML file containing
     * valid <code>Aliquots</code>
     * @post    all <code>Aliquots</code> found in the file are added to this
     * <code>Sample</code>
     * @param   location    the file to read data from
     * @return  <code>String</code> - parent of the file that was read
     * @throws  java.io.FileNotFoundException                               FileNotFoundException
     * @throws  org.earthtime.UPb_Redux.exceptions.BadLabDataException      BadLabDataException
     * @throws  java.io.IOException                                         IOException
     * @throws  org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException  BadOrMissingXMLSchemaException
     */
    String importAliquotLocalXMLDataFile(File location) throws FileNotFoundException, BadLabDataException, IOException, BadOrMissingXMLSchemaException;

    /**
     * imports all <code>UPbFractions</code> found in the XML file specified by
     * argument <code>location</code> to the <code>Aliquot</code> specified by
     * argument <code>aliquotNumber</code> in this <code>Sample</code>
     *
     * @pre     argument <code>location</code> specifies an XML file containing
     * valid <code>UPbFractions</code>
     * @post    all <code>UPbFractions</code> found in the file are added to the
     * <code>Aliquot</code> specified by <code>aliquotNumber</code> in
     * this <code>Sample</code>
     * @param   location        the file to read data from
     * @param   aliquotNumber   the number of the <code>Aliquot</code> which
     * the <code>Fractions</code> belong to
     * @param doValidate 
     * @return  <code>String</code> - parent of the file that was read
     * @throws  java.io.FileNotFoundException                           FileNotFoundException
     * @throws  org.earthtime.UPb_Redux.exceptions.BadLabDataException  BadLabDataException
     */
    public String importFractionXMLDataFiles(File location, int aliquotNumber,boolean doValidate) throws FileNotFoundException, BadLabDataException;

    /**
     * reads <code>Fractions</code> from the file specified by argument
     * <code>location</code> and adds them to the <code>Aliquot</code> specified
     * by argument <code>aliquotNumber</code> in this <code>Sample</code>.
     *
     * @pre     argument <code>location</code> specifies an XML file with valid
     * <code>UPbFractions</code> and argument <code>aliquotNumber</code>
     * specifies an <code>Aliquot</code> that exists in this <code>Sample</code>
     * @post    all <code>Fractions</code> found in the specified file are added
     * to the specified <code>Aliquot</code> in this <code>Sample</code>
     * @param   location        file to read data from
     * @param   aliquotNumber   number of <code>Aliquot</code> to add
     * <code>Fractions</code> from the file to
     * @return  <code>String</code> - path of the file that data was read from
     * @throws  org.earthtime.UPb_Redux.exceptions.BadLabDataException  BadLabDataException
     * @throws  java.io.FileNotFoundException                           FileNotFoundException
     */
    String importUPbFractionFolderForManualUpdate(File location, int aliquotNumber) throws ETException, BadLabDataException, FileNotFoundException;

    /**
     * 
     * @param aliquot
     * @param aliquotSource
     * @throws IOException
     * @throws ETException
     * @throws BadLabDataException 
     */
    public abstract void processXMLAliquot(Aliquot aliquot, String aliquotSource) throws IOException, ETException,  BadLabDataException;

    /**
     * reads in data from the XML file specified by argument
     * <code>fractionFile</code> and adds any <code>Fractions</code> found in
     * the file to this <code>Sample</code> under the <code>Aliquot</code>
     * specified by argument <code>aliquotNumber</code>.
     *
     * @pre     <code>fractionFile</code> is an XML file containing valid
     * <code>Fractions</code> and <code>aliquotNumber</code> specifies
     * an existing <code>Aliquot</code> in this <code>Sample</code>
     * @post    all <code>Fractions</code> found in the file are added to the
     * <code>Aliquot</code> specified by <code>aliquotNumber</code> in
     * this <code>Sample</code>
     * @param   fractionFile    the file to read data from
     * @param   aliquotNumber   the number of the <code>Aliquot</code> that
     * the <code>Fractions</code> being read from the
     * file belong to
     * @param validateSampleName 
     * @param doValidate 
     * @return 
     * @throws  org.earthtime.XMLExceptions.ETException           ETException
     */
    public String processXMLFractionFile(File fractionFile, int aliquotNumber,Boolean validateSampleName,boolean doValidate) throws ETException, BadLabDataException;

    /**
     * sets <code>ReduxLabData</code> of this <code>Sample</code> and all
     * <code>Aliquots</code> and <code>Fractions</code> contained within to the
     * argument <code>labData</code>.
     *
     * @pre     argument <code>labData</code> is valid <code>ReduxLabData</code>
     * @post    <code>ReduxLabData</code> of this <code>Sample</code> and all
     * of its <code>Aliquots</code> and <code>Fractions</code> is set
     * to argument <code>labData</code>
     * @param   labData     value to which this <code>Sample</code> and its
     * <code>Aliquots</code> and <code>Fractions</code>
     * should be set to
     */
    void registerSampleWithLabData(ReduxLabData labData);

    /**
     * removes the <code>UPbFraction</code> found at <code>index</code> from
     * this <code>Sample</code>'s set of <code>Fractions</code>
     *
     * @pre     a <code>Fraction</code> exists in this <code>Sample</code>'s set
     * of <code>Fractions</code> at <code>index</code>
     * @post    the <code>Fraction</code> found at <code>index</code> is removed
     * from the set of <code>Fractions</code>
     * @param   index   the index into the array of <code>Fractions</code> where
     * the <code>Fraction</code> to be removed can be found
     */
    void removeUPbReduxFraction(int index);

    /**
     * removes the <code>UPbFraction</code> from this <code>Sample</code>'s set
     * of <code>Fractions</code> that corresponds to the argument
     * <code>fraction</code>
     *
     * @pre     a <code>Fraction</code> exists in this <code>Sample</code>'s set
     * of <code>Fractions</code> that corresponds to <code>fraction</code>
     * @post    the <code>Fraction</code> that corresponds to the argument
     * <code>fraction</code> is removed
     * @param   fraction
     */
    void removeUPbReduxFraction(Fraction fraction);

    /**
     * removes <code>Fractions</code> from this <code>Sample</code>'s sample age
     * models that are no longer a part of this <code>Sample</code>'s
     * <code>Aliquots</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    any <code>Fractions</code> that are found in this <code>Sample</code>'s
     * sample age models that are no longer a part of this
     * <code>Sample</code>'s <code>Aliquots</code> are removed
     */
    void updateAndSaveSampleDateModelsByAliquot();

    /**
     * gets the <code>sampleAnnotations</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>sampleAnnotations</code> of this <code>Sample</code>
     * @return  <code>String</code> - <code>sampleAnnotations</code> of this
     * <code>Sample</code>
     */
    String getSampleAnnotations();

    /**
     * gets the <code>defaultFractionCounter</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>defaultFractionCounter</code> of this
     * <code>Sample</code>
     * @return  <code>int</code> - <code>defaultFractionCounter</code> of this
     * <code>Sample</code>
     */
    int getDefaultFractionCounter();

    /**
     * gets the <code>defaultFractionName</code> of this <code>Sample</code>
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>defaultFractionName</code> of this <code>Sample</code>
     * @return  <code>String</code> - <code>defaultFractionName</code> of this
     * <code>Sample</code>
     */
    String getDefaultFractionName();

    /**
     * gets the <code>reduxSampleFileName</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>reduxSampleFileName</code> of this
     * <code>Sample</code>
     * @return  <code>String</code> - <code>reduxSampleFileName</code> of this
     * <code>Sample</code>
     */
    String getReduxSampleFileName();

    /**
     * gets the <code>reduxSampleFilePath</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>reduxSampleFilePath</code> of this <code>Sample</code>
     * @return  <code>String</code> - <code>reduxSampleFilePath</code> of this
     * <code>Sample</code>
     */
    String getReduxSampleFilePath();

    /**
     * gets the <code>name</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>name</code> of this <code>Sample</code>
     * @return  <code>String</code> - <code>name</code> of this <code>Sample</code>
     */
    String getSampleName();

    /**
     * gets the <code>sampleType</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>sampleType</code> of this <code>Sample</code>
     * @return  <code>String</code> - <code>sampleType</code> of this
     * <code>Sample</code>
     */
    String getSampleType();

    /**
     * gets the <code>analyzed</code> field of this <code>Sample</code>
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>analyzed</code> field of this <code>Sample</code>
     * @return  <code>boolean</code> - <code>analyzed</code> field of this
     * <code>Sample</code>
     */
    boolean isAnalyzed();

    /**
     * gets the <code>changed</code> field of this <code>Sample</code>
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>changed</code> field of this <code>Sample</code>
     * @return  <code>boolean</code> - <code>changed</code> field of this
     * <code>Sample</code>
     */
    boolean isChanged();

    /**
     * gets the <code>fractionDataOverriddenOnImport</code> field of this
     * <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>fractionDataOverriddenOnImport</code> field
     * of this <code>Sample</code>
     * @return  <code>boolean</code> - <code>fractionDataOverriddenOnImport</code>
     * field of this <code>Sample</code>
     */
    boolean isFractionDataOverriddenOnImport();

    /**
     * sets the <code>aliquots</code> of this <code>Sample</code> to
     * the argument <code>aliquots</code>
     *
     * @pre     argument <code>aliquots</code> is a valid set of <code>Aliquots</code>
     * @post    this <code>Sample</code>'s <code>aliquots</code> is set
     * to argument <code>aliquots</code>
     * @param   aliquots    value to which <code>aliquots</code> of
     * this <code>Sample</code> will be set
     */
    void setAliquots(Vector<Aliquot> aliquots);

    /**
     * sets the <code>sampleAnnotations</code> of this <code>Sample</code> to
     * the argument <code>annotations</code>
     *
     * @pre     argument <code>annotations</code> is a valid <code>sampleAnnotations</code>
     * @post    this <code>Sample</code>'s <code>sampleAnnotations</code> is set
     * to argument <code>annotations</code>
     * @param   annotations     value to which <code>sampleAnnotations</code> of
     * this <code>Sample</code> will be set
     */
    void setSampleAnnotations(String annotations);

    /**
     * sets the <code>changed</code> field of this <code>Sample</code> to the
     * argument <code>changed</code>
     *
     * @pre     argument <code>changed</code> is a valid <code>boolean</code>
     * @post    this <code>Sample</code>'s <code>changed</code> field is set
     * to argument <code>changed</code>
     * @param   changed     vale to which <code>changed</code> field of this
     * <code>Sample</code> will be set
     */
    void setChanged(boolean changed);

    /**
     * sets the <code>defaultFractionCounter</code> of this <code>Sample</code>
     * to the argument <code>defaultFractionCounter</code>
     *
     * @pre     argument <code>defaultFractionCounters</code> is a valid
     * <code>defaultFractionCounter</code>
     * @post    this <code>Sample</code>'s <code>defaultFractionCounter</code>
     * is set to argument <code>defaultFractionCounter</code>
     * @param   defaultFractionCounter  value to which <code>defaultFractionCounter</code>
     * of this <code>Sample</code> will be set
     */
    void setDefaultFractionCounter(int defaultFractionCounter);

    /**
     * sets the <code>defaultFractionName</code> of this <code>Sample</code> to
     * the argument <code>defaultFractionName</code>
     *
     * @pre     argument <code>defaultFractionName</code> is a valid
     * <code>defaultFractionName</code>
     * @post    this <code>Sample</code>'s <code>defaultFractionName</code> is
     * set to argument <code>defaultFractionName</code>
     * @param   defaultFractionName value to which <code>defaultFractionName</code>
     * of this <code>Sample</code> will be set
     */
    void setDefaultFractionName(String defaultFractionName);

    /**
     * sets the <code>fractionDataOverriddenOnImport</code> field of this
     * <code>Sample</code> to the argument <code>fractionDataOverriddenOnImport</code>
     *
     * @pre     argument <code>fractionDataOverriddenOnImport</code> is a valid
     * <code>boolean</code>
     * @post    this <code>Sample</code>'s <code>fractionDataOverriddenOnImport</code>
     * is set to argument <code>fractionDataOverriddenOnImport</code>
     * @param   fractionDataOverriddenOnImport  value to which
     * <code>fractionDataOverriddenOnImport</code>
     * of this <code>Sample</code> will be set
     */
    void setFractionDataOverriddenOnImport(boolean fractionDataOverriddenOnImport);

    /**
     * sets the <code>physicalConstantsModel</code> of this <code>Sample</code>
     * to the argument <code>physicalConstantsModel</code>
     *
     * @pre     argument <code>physicalConstantsModel</code> is a valid
     * <code>PhysicalConstants</code>
     * @post    this <code>Sample</code>'s <code>physicalConstantsModel</code>
     * is set to argument <code>physicalConstantsModel</code>
     * @param   physicalConstantsModel  value to which <code>physicalConstantsModel</code>
     * of this <code>Sample</code> will be set
     */
    void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel);

    /**
     * sets the <code>reduxSampleFilePath</code> and <code>reduxSampleFileName</code>
     * of this <code>Sample</code> to the argument <code>reduxSampleFile</code>
     *
     * @pre     argument <code>reduxSampleFile</code> is a valid file
     * @post    this <code>Sample</code>'s <code>reduxSampleFilePath</code> and
     * <code>reduxSampleFileName</code> are set to argument
     * <code>reduxSamplefile</code>
     * @param   reduxSampleFile value to which <code>reduxSampleFilePath</code>
     * and <code>reduxSampleFileName</code> of this
     * <code>Sample</code> will be set
     */
    void setReduxSampleFilePath(File reduxSampleFile);

    /**
     * sets the <code>sampleAgeInterpretationGUISettings</code> of this
     * <code>Sample</code> to the argument <code>sampleAgeInterpretationGUISettings</code>
     *
     * @pre     argument <code>sampleAgeInterpretationGUISettings</code> is a
     * valid <code>SampleDateInterpretationGUIOptions</code>
     * @post    this <code>Sample</code>'s <code>sampleAgeInterpretationGUISettings</code>
     * is set to argument <code>sampleAgeInterpretationGUISettings</code>
     * @param   sampleAgeInterpretationGUISettings  value to which <code>
     * sampleAgeInterpretationGUISettings</code>
     * of this <code>Sample</code>
     * will be set
     */
    void setSampleAgeInterpretationGUISettings(SampleDateInterpretationGUIOptions sampleAgeInterpretationGUISettings);

    /**
     * sets the <code>sampleName</code> of this <code>Sample</code> to the
     * argument <code>sampleName</code>
     *
     * @pre     argument <code>sampleName</code> is a valid <code>sampleName</code>
     * @post    this <code>Sample</code>'s <code>sampleName</code> is set to
     * argument <code>sampleName</code>
     * @param   sampleName  value to which<code>sampleName</code> of this
     * <code>Sample</code> will be set
     */
    void setSampleName(String sampleName);

    /**
     * sets the <code>UPbFractions</code> of this <code>Sample</code> to
     * the argument <code>UPbFractions</code>
     *
     * @pre     argument <code>UPbFractions</code> is a valid set of
     * <code>UPbFractions</code>
     * @post    this <code>Sample</code>'s <code>UPbFractions</code> is set
     * to argument <code>UPbFractions</code>
     * @param   UPbFractions    value to which <code>UPbFractions</code> of
     * this <code>Sample</code> will be set
     */
    void setUPbFractions(Vector<Fraction> UPbFractions);

    public ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose ();
   
    /**
     *
     * @param reportSettingsModel
     */
    public void setReportSettingsModel(ReportSettings reportSettingsModel);
}
