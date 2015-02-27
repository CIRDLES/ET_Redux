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

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
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
public interface SampleI {

//    /**
//     * adds a <code>UPbFraction</code> with default parameters to this
//     * <code>Sample</code>'s set of <code>Fractions</code>.
//     *
//     * @pre     this <code>Sample</code> exists
//     * @post    a new default <code>Fraction</code> is created and added to this
//     * <code>Sample</code>'s <code>Fractions</code>
//     * @param   aliquotNumber   the number of the <code>Aliquot</code> to which
//     * the new <code>Fraction</code> will be added
//     * @throws  org.earthtime.UPb_Redux.exceptions.BadLabDataException  BadLabDataException
//     */
//    void addDefaultUPbFraction(int aliquotNumber) throws BadLabDataException;

//    /**
//     * adds an <code>Aliquot</code> to <code>aliquots</code>. It is created with
//     * a number relative to its position in the array such that the first
//     * <code>aliquot</code> in the array is given 1, the second is given 2, and
//     * so on. It is created under the name <code>Aliquot-#</code> with <code>#</code>
//     * being replaced by the same number that was given as the first paramater.
//     * The remaining fields are set to correspond to the data found in this
//     * <code>Sample</code>.
//     *
//     * @pre     this <code>Sample</code> exists with proper data
//     * @post    a new <code>Aliquot</code> is created and added to
//     * <code>aliquots</code> and the size of <code>Aliquots</code> is
//     * returned
//     * @return  <code>int</code> - if successful, returns the size of the array
//     * after adding the new <code>Aliquot</code>. Else, returns -1.
//     */
//    int addNewDefaultAliquot();

    /**
     * adds a <code>UPbFraction</code> to the <code>Sample</code>'s set of
     * <code>Fractions</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    a new default <code>Fraction</code> is added to this
     * <code>Sample</code>'s <code>Fractions</code>
     * @param   newFraction the <code>Fraction</code> to add to this
     * <code>Sample</code>
     */
    void addUPbFraction(Fraction newFraction);

    /**
     * opens a modal editor for the </code>Aliquot</code> specified by
     * <code>aliquotNum</code>. The <code>Aliquot</code>'s <code>Fractions</code>
     * are populated on the fly.
     *
     * @pre     an <code>Aliquot</code> exists with the number specified by
     * argument <code>aliquotNum</code>
     * @post    an editor for the specified <code>Aliquot</code> is opened
     * @param   aliquotNum  the number of the <code>Aliquot</code> to be edited
     */
    void editAliquotByNumber(int aliquotNum);

    /**
     * opens a modal editor for the <code>Fraction</code> indicated by argument
     * <code>fraction</code> and opened to the editing tab indicated by argument
     * <code>selectedTab</code>. <code>selectedTab</code> is valid only if it
     * contains a number between zero and seven inclusive.
     *
     * @pre     the <code>Fraction</code> corresponding to <code>fraction</code>
     * exists in this <code>Sample</code> and <code>selectedTab</code>
     * is a valid tab number
     * @post    an editor for the specified <code>Fraction</code> is opened to
     * the specified tab
     * @param   fraction    the <code>Fraction</code> to be edited
     * @param   selectedTab the tab to open the editor to
     */
    void editUPbFraction(Fraction fraction, int selectedTab);

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
    public String importUPbFractionXMLDataFiles(File location, int aliquotNumber,boolean doValidate) throws FileNotFoundException, BadLabDataException;

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
     * loads a Sample after a .redux file generation from an Excel file
     *
     * @pre     the chosen file located in the directory <code>dir</code> is a
     * proper .redux file generated from an Excel file.
     * @post    the file is returned for use by another method
     * @param   frame   the frame where the samples names to save were selected
     * @param   dir     the directory in which the redux files were saved
     * @return  <code>File</code> - the loaded file
     */
    File loadSampleAfterExcelImport(Component frame, File dir);

//    /**
//     * processes aliquot read from xml file
//     * <code>aliquot</code> and adds any <code>Aliquots</code> found in the
//     * file to this <code>Sample</code>.
//     *
//     * @pre     file specified by <code>aliquotFile</code> is an XML file containing
//     * valid <code>Aliquots</code>
//     * @post    all <code>Aliquots</code> found in the file are added to this
//     * <code>Sample</code>
//     * @param   aliquotFile    the XML file to read data from
//     * @throws  java.io.IOException                                         IOException
//     * @throws  java.io.FileNotFoundException                               FileNotFoundException
//     * @throws  org.earthtime.XMLExceptions.ETException               ETException
//     * @throws  org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException  BadOrMissingXMLSchemaException
//     * @throws  org.earthtime.UPb_Redux.exceptions.BadLabDataException      BadLabDataException
//     */
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


//    /**
//     * sets the <code>Fractions</code> in this <code>Sample</code> that have
//     * been selected
//     *
//     * @pre     argument <code>selectedRows</code> contains an array of valid
//     * indexes into this <code>Sample</code>'s set of <code>Fractions</code>
//     * @post    the <code>rejected</code> value of each <code>Fraction</code>
//     * specified by <code>selectedRows</code> is set to <code>true</code>
//     * and all others are set to <code>false</code>
//     * @param   selectedRows    indexes into the collection of <code>Fractions</code>
//     * to locate the selected <code>Fractions</code>
//     */
//    void SaveSampleSelectedFractions(int[] selectedRows);

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
     * sets the <code>changed</code> field of each <code>UPbFraction</code> in
     * this <code>Sample</code> to <code>false</code> and saves this
     * <code>Sample</code> as a .redux file to <code>reduxSampleFilePath</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    this <code>Sample</code> is saved as a .redux file to the
     * location specified by <code>reduxSampleFilePath</code>
     */
    void saveTheSampleAsSerializedReduxFile();

    /**
     * saves this <code>Sample</code> to the file specified by argument <code>file</code>.
     *
     * @pre     argument <code>file</code> is a valid file
     * @post    this <code>Sample</code> is saved to the location specified by
     * argument <code>file</code>
     * @param   file    the file where this <code>Sample</code> will be saved
     * @return  <code>String</code> - the path of the file where this
     * <code>Sample</code> was saved
     */
    String saveTheSampleAsSerializedReduxFile(File file);

    /**
     * finds the <code>Aliquot</code> named <code>name</code> in the array
     * <code>aliquots</code>.
     *
     * @pre     an <code>Aliquot</code> exists in <code>aliquots</code> named
     * <code>name</code>
     * @post    the <code>Aliquot</code> whose name corresponds to argument
     * <code>name</code> is found and returned
     * @param   name    name of the <code>Aliquot</code> to retrieve
     * @return  <code>Aliquot</code> - the <code>Aliquot</code> from
     * <code>aliquots</code> whose name correspongs to the argument
     * <code>name</code>
     */
    Aliquot getAliquotByName(String name);

    /**
     * finds the <code>Aliquot</code> numbered <code>aliquotNum</code> in the array
     * <code>aliquots</code>.
     *
     * @pre     an <code>Aliquot</code> exists in <code>aliquots</code> numbered
     * with <code>aliquotNum</code>
     * @post    the <code>Aliquot</code> whose number corresponds to argument
     * <code>aliquotNum</code> is found and returned
     * @param   aliquotNum  number of the <code>Aliquot</code> to retrieve
     * @return  <code>Aliquot</code> - the <code>Aliquot</code> from
     * <code>aliquots</code> whose number corresponds to the argument
     * <code>aliquotNum</code>
     */
    Aliquot getAliquotByNumber(int aliquotNum);

    /**
     * gets the <code>aliquots</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>aliquots</code> of this <code>Sample</code>
     * @return  <code>Vector</code> - set of <code>Aliquots</code> of this
     * <code>Sample</code>
     */
    Vector<Aliquot> getAliquots();

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
     * gets the <code>myReduxLabData</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>myReduxLabData</code> of this <code>Sampel</code>
     * @return  <code>ReduxLabData</code> - <code>myReduxLabData</code> of this
     * <code>Sample</code>
     */
    ReduxLabData getMyReduxLabData();

    /**
     * gets the <code>physicalConstantsModel</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>physicalConstantsModel</code> of this
     * <code>Sample</code>
     * @return  <code>PhysicalConstants</code> - <code>physicalConstantsModel</code>
     * of this <code>Sample</code>
     * @throws  org.earthtime.UPb_Redux.exceptions.BadLabDataException  BadLabDataException
     */
    AbstractRatiosDataModel getPhysicalConstantsModel() throws BadLabDataException;

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
     * gets the <code>sampleAgeInterpretationGUISettings</code> of this
     * <code>Sample</code>
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>sampleAgeInterpretationGUISettings</code> of
     * this <code>Sample</code>
     * @return  <code>SampleDateInterpretationGUIOptions</code> -
     * <code>sampleAgeInterpretationGUIOptions</code> of this
     * <code>Sample</code>
     */
    SampleDateInterpretationGUIOptions getSampleDateInterpretationGUISettings();

    /**
     * gets the <code>sampleIGSN</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>sampleIGSN</code> of this <code>Sample</code>
     * @return  <code>String</code> - <code>sampleIGSN</code> of this
     * <code>Sample</code>
     */
    String getSampleIGSN();

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
     * gets the <code>UPbFractions</code> of this <code>Sample</code>.
     *
     * @pre     this <code>Sample</code> exists
     * @post    returns the <code>UPbFractions</code> of this <code>Sample</code>
     * @return  <code>Vector</code> - set of <code>Fractions</code> that make up
     * the <code>UPbFractions</code> of this <code>Sample</code>
     */
    Vector<Fraction> getUPbFractions();

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
     * sets the <code>analyzed</code> field of this <code>Sample</code> to
     * the argument <code>analyzed</code>
     *
     * @pre     argument <code>analyzed</code> is a valid <code>boolean</code>
     * @post    this <code>Sample</code>'s <code>analyzed</code> field is set
     * to argument <code>analyzed</code>
     * @param   analyzed    value to which <code>analyzed</code> field of
     * this <code>Sample</code> will be set
     */
    void setAnalyzed(boolean analyzed);

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
     * sets the <code>myReduxLabData</code> field of this <code>Sample</code> to
     * the argument <code>myReduxLabData</code>
     *
     * @pre     argument <code>myReduxLabData</code> is a valid <code>ReduxLabData</code>
     * @post    this <code>Sample</code>'s <code>myReduxLabData</code> field is
     * set to argument <code>myReduxLabData</code>
     * @param   myReduxLabData  value to which <code>myReduxLabData</code> field
     * of this <code>Sample</code> will be set
     */
    void setMyReduxLabData(ReduxLabData myReduxLabData);

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
     * sets the <code>sampleIGSN</code> of this <code>Sample</code> to
     * the argument <code>sampleIGSN</code>
     *
     * @pre     argument <code>sampleIGSN</code> is a valid <code>sampleIGSN</code>
     * @post    this <code>Sample</code>'s <code>sampleIGSN</code> is set
     * to argument <code>sampleIGSN</code>
     * @param   sampleIGSN      value to which <code>sampleIGSN</code> of
     * this <code>Sample</code> will be set
     */
    void setSampleIGSN(String sampleIGSN);

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
     * sets the <code>sampleType</code> of this <code>Sample</code> to
     * the argument <code>sampleType</code>
     *
     * @pre     argument <code>sampleType</code> is a valid <code>sampleType</code>
     * @post    this <code>Sample</code>'s <code>sampleType</code> is set
     * to argument <code>sampleType</code>
     * @param   sampleType  value to which <code>sampleType</code> of
     * this <code>Sample</code> will be set
     */
    void setSampleType(String sampleType);

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
    
    public Vector<Aliquot> getActiveAliquots ();
    
    public ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose ();
    
    /**
     *
     * @return
     */
    public boolean isAnalysisTypeTripolized ();
    
    /**
     *
     * @return
     */
    public ReportSettings getReportSettingsModelUpdatedToLatestVersion ();
    
    public GraphAxesSetup getConcordiaGraphAxesSetup ();
    
    /**
     *
     * @return
     */
    public GraphAxesSetup getTerraWasserburgGraphAxesSetup ();
    
    /**
     *
     * @return
     */
    public ReportSettings getReportSettingsModel();
    
    /**
     *
     * @param reportSettingsModel
     */
    public void setReportSettingsModel(ReportSettings reportSettingsModel);

    public void setLegacyStatusForReportTable();
    
    public String getNameOfAliquotFromSample(int aliquotNum);
}
