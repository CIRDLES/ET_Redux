/*
 * Copyright 2006-2015 CIRDLES.org.
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
package org.earthtime.UTh_Redux.fractions;

import java.awt.geom.Path2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reports.ReportRowGUIInterface;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.MeasuredUThRatios;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.UncertaintyTypesEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.xmlUtilities.XMLSerializationI;

public class UThFraction implements
        UThFractionI,
        ReportRowGUIInterface,
        Serializable,
        XMLSerializationI {

    //private static final long serialVersionUID = -6610176652253689201L;
    private transient boolean selectedInDataTable;
    // Instance variables
    private String fractionID;
    private String grainID;
    private String sampleName;
    private int aliquotNumber;
    private boolean isLegacy;
    private String imageURL;
    private Date timeStamp;
    private int numberOfGrains;
    private BigDecimal estimatedDate;
    private ValueModel[] analysisMeasures;
    private ValueModel[] measuredRatios;
    private ValueModel[] radiogenicIsotopeRatios;
    private ValueModel[] radiogenicIsotopeDates;
    private ValueModel[] compositionalMeasures;
    private ValueModel[] sampleIsochronRatios;

    private boolean changed;
    private boolean deleted;
    private String fractionNotes;
    private boolean rejected;

    public UThFraction() {
        this.fractionID = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.grainID = fractionID;
        this.sampleName = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.aliquotNumber = 1;
        this.isLegacy = false;
        this.imageURL = "http://";
        this.timeStamp = new Date(System.currentTimeMillis());

        this.numberOfGrains = ReduxLabData.getInstance().getDefaultNumberOfGrains();
        this.estimatedDate = BigDecimal.ZERO;

        analysisMeasures = valueModelArrayFactory(AnalysisMeasures.getNames(), UncertaintyTypesEnum.ABS.getName());
        // note for UPb we use MeasuredRatioModel, but not here at least initially
        measuredRatios = valueModelArrayFactory(MeasuredUThRatios.getNames(), UncertaintyTypesEnum.PCT.getName());
        radiogenicIsotopeRatios = valueModelArrayFactory(DataDictionary.RadiogenicIsotopeRatioTypes, UncertaintyTypesEnum.ABS.getName());
        radiogenicIsotopeDates = valueModelArrayFactory(RadDates.getNamesSorted(), UncertaintyTypesEnum.ABS.getName());
        compositionalMeasures = valueModelArrayFactory(DataDictionary.earthTimeUPbCompositionalMeasuresNames, UncertaintyTypesEnum.ABS.getName());
        sampleIsochronRatios = valueModelArrayFactory(DataDictionary.SampleIsochronRatioNames, UncertaintyTypesEnum.ABS.getName());

        this.changed = false;
        this.deleted = false;
        this.fractionNotes = "";
        this.rejected = false;

//        initializeTraceElements();
    }

    /**
     * @return the fractionID
     */
    @Override
    public String getFractionID() {
        return fractionID;
    }

    /**
     * @param fractionID the fractionID to set
     */
    @Override
    public void setFractionID(String fractionID) {
        this.fractionID = fractionID;
    }

    /**
     * @return the grainID
     */
    public String getGrainID() {
        return grainID;
    }

    /**
     * @param grainID the grainID to set
     */
    public void setGrainID(String grainID) {
        this.grainID = grainID;
    }

    /**
     * @return the sampleName
     */
    @Override
    public String getSampleName() {
        return sampleName;
    }

    /**
     * @param sampleName the sampleName to set
     */
    @Override
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    /**
     * @return the aliquotNumber
     */
    @Override
    public int getAliquotNumber() {
        return aliquotNumber;
    }

    /**
     * @param aliquotNumber the aliquotNumber to set
     */
    @Override
    public void setAliquotNumber(int aliquotNumber) {
        this.aliquotNumber = aliquotNumber;
    }

    /**
     * @return the isLegacy
     */
    @Override
    public boolean isLegacy() {
        return isLegacy;
    }

    /**
     * @return the imageURL
     */
    @Override
    public String getImageURL() {
        return imageURL;
    }

    /**
     * @param imageURL the imageURL to set
     */
    @Override
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * @return the timeStamp
     */
    @Override
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     * @param timeStamp the timeStamp to set
     */
    @Override
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * @return the numberOfGrains
     */
    @Override
    public int getNumberOfGrains() {
        return numberOfGrains;
    }

    /**
     * @param numberOfGrains the numberOfGrains to set
     */
    @Override
    public void setNumberOfGrains(int numberOfGrains) {
        this.numberOfGrains = numberOfGrains;
    }

    /**
     *
     * @return
     */
    @Override
    public BigDecimal getEstimatedDate() {
        return estimatedDate;
    }

    /**
     *
     * @param estimatedDate
     */
    @Override
    public void setEstimatedDate(BigDecimal estimatedDate) {
        this.estimatedDate = estimatedDate;
    }

    /**
     * @return the analysisMeasures
     */
    @Override
    public ValueModel[] getAnalysisMeasures() {
        return analysisMeasures;
    }

    /**
     * @param analysisMeasures the analysisMeasures to set
     */
    @Override
    public void setAnalysisMeasures(ValueModel[] analysisMeasures) {
        this.analysisMeasures = analysisMeasures;
    }

    /**
     * @return the measuredRatios
     */
    @Override
    public ValueModel[] getMeasuredRatios() {
        return measuredRatios;
    }

    /**
     * @param measuredRatios the measuredRatios to set
     */
    @Override
    public void setMeasuredRatios(ValueModel[] measuredRatios) {
        this.measuredRatios = measuredRatios;
    }

    /**
     * @return the radiogenicIsotopeRatios
     */
    @Override
    public ValueModel[] getRadiogenicIsotopeRatios() {
        return radiogenicIsotopeRatios;
    }

    /**
     * @param radiogenicIsotopeRatios the radiogenicIsotopeRatios to set
     */
    @Override
    public void setRadiogenicIsotopeRatios(ValueModel[] radiogenicIsotopeRatios) {
        this.radiogenicIsotopeRatios = radiogenicIsotopeRatios;
    }

    /**
     * @return the radiogenicIsotopeDates
     */
    @Override
    public ValueModel[] getRadiogenicIsotopeDates() {
        return radiogenicIsotopeDates;
    }

    /**
     * @param radiogenicIsotopeDates the radiogenicIsotopeDates to set
     */
    @Override
    public void setRadiogenicIsotopeDates(ValueModel[] radiogenicIsotopeDates) {
        this.radiogenicIsotopeDates = radiogenicIsotopeDates;
    }

    /**
     * @return the compositionalMeasures
     */
    @Override
    public ValueModel[] getCompositionalMeasures() {
        return compositionalMeasures;
    }

    /**
     * @param compositionalMeasures the compositionalMeasures to set
     */
    @Override
    public void setCompositionalMeasures(ValueModel[] compositionalMeasures) {
        this.compositionalMeasures = compositionalMeasures;
    }

    /**
     * @return the sampleIsochronRatios
     */
    @Override
    public ValueModel[] getSampleIsochronRatios() {
        return sampleIsochronRatios;
    }

    /**
     * @param sampleIsochronRatios the sampleIsochronRatios to set
     */
    @Override
    public void setSampleIsochronRatios(ValueModel[] sampleIsochronRatios) {
        this.sampleIsochronRatios = sampleIsochronRatios;
    }

    /**
     * @return the changed
     */
    @Override
    public boolean isChanged() {
        return changed;
    }

    /**
     * @param changed the changed to set
     */
    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * @return the deleted
     */
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * @param deleted the deleted to set
     */
    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the fractionNotes
     */
    @Override
    public String getFractionNotes() {
        return fractionNotes;
    }

    /**
     * @param fractionNotes the fractionNotes to set
     */
    @Override
    public void setFractionNotes(String fractionNotes) {
        this.fractionNotes = fractionNotes;
    }

    /**
     * @return the rejected
     */
    @Override
    public boolean isRejected() {
        return rejected;
    }

    /**
     * @param rejected the rejected to set
     */
    @Override
    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    @Override
    public void toggleRejectedStatus() {
        this.rejected = !this.rejected;
    }

    @Override
    public boolean isStandard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStandard(boolean standard) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAnalysisFractionComment() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAnalysisFractionComment(String analysisFractionComment) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ValueModel getMeasuredRatioByName(String ratioName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ValueModel getMeasuredRatioByName(MeasuredRatios myMeasuredRatio) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ConcurrentMap<String, BigDecimal> getParDerivTerms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setParDerivTerms(ConcurrentMap<String, BigDecimal> parDerivTerms) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ValueModel[] getTraceElements() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTraceElements(ValueModel[] traceElements) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void initializeTraceElements() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setIsLegacy(boolean isLegacy) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPhysicalConstantsModelID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getRatioType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRatioType(String RatioType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Path2D getErrorEllipsePath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setErrorEllipsePath(Path2D errorEllipsePath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getEllipseRho() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEllipseRho(double ellipseRho) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTracerID() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTracerID(String tracerID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getRgbColor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRgbColor(int rgbColor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void getValuesFrom(ETFractionInterface fraction, boolean copyAnalysisMeasures) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSelectedInDataTable() {
        return selectedInDataTable;
    }

    @Override
    public void setSelectedInDataTable(boolean selectedInDataTable) {
        this.selectedInDataTable = selectedInDataTable;
    }

    @Override
    public void serializeXMLObject(String filename) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object readXMLObject(String filename, boolean doValidate) throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void readObject(
            ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ObjectStreamClass myObject = ObjectStreamClass.lookup(
                Class.forName(UThFraction.class.getCanonicalName()));
        long theSUID = myObject.getSerialVersionUID();
        System.out.println("Customized De-serialization of UThFraction " + theSUID);
    }
}
