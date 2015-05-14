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

import java.io.File;
import java.util.Vector;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reports.ReportSettings;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface SampleInterface {

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
     * sets the <code>changed</code> field of each <code>UPbFraction</code> in
     * this <code>Sample</code> to <code>false</code> and saves this
     * <code>Sample</code> as a .redux file to <code>reduxSampleFilePath</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post this <code>Sample</code> is saved as a .redux file to the location
     * specified by <code>reduxSampleFilePath</code>
     */
    public abstract void saveTheSampleAsSerializedReduxFile();

    /**
     * saves this <code>Sample</code> to the file specified by argument
     * <code>file</code>.
     *
     * @pre argument <code>file</code> is a valid file
     * @post this <code>Sample</code> is saved to the location specified by
     * argument <code>file</code>
     * @param file the file where this <code>Sample</code> will be saved
     * @return  <code>String</code> - the path of the file where this
     * <code>Sample</code> was saved
     */
    public abstract String saveTheSampleAsSerializedReduxFile(File file);

    /**
     *
     * @return
     */
    public abstract boolean isAnalysisTypeTripolized();

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

    public abstract void setLegacyStatusForReportTable();

    /**
     * adds a <code>UPbFraction</code> to the <code>Sample</code>'s set of
     * <code>Fractions</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post a new default <code>Fraction</code> is added to this
     * <code>Sample</code>'s <code>Fractions</code>
     * @param newFraction the <code>Fraction</code> to add to this
     * <code>Sample</code>
     */
    public abstract void addUPbFraction(Fraction newFraction);

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
     * <code>aliquots</code> whose name correspongs to the argument
     * <code>name</code>
     */
    public abstract Aliquot getAliquotByName(String name);

    /**
     *
     * @return
     */
    public abstract ReportSettings getReportSettingsModelUpdatedToLatestVersion();

    public abstract Vector<Aliquot> getActiveAliquots();

    /**
     *
     * @return
     */
    public abstract ReportSettings getReportSettingsModel();

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
     * gets the <code>aliquots</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>aliquots</code> of this <code>Sample</code>
     * @return  <code>Vector</code> - set of <code>Aliquots</code> of this
     * <code>Sample</code>
     */
    public abstract Vector<Aliquot> getAliquots();

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
     * gets the <code>UPbFractions</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>UPbFractions</code> of this <code>Sample</code>
     * @return  <code>Vector</code> - set of <code>Fractions</code> that make up
     * the <code>UPbFractions</code> of this <code>Sample</code>
     */
    public abstract Vector<Fraction> getUPbFractions();

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
     *
     * @return
     */
    public abstract GraphAxesSetup getTerraWasserburgGraphAxesSetup();

    public abstract GraphAxesSetup getConcordiaGraphAxesSetup();

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

}
