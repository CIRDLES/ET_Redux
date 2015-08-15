/*
 * Fraction.java
 *
 * Created on August 2, 2007, 10:04 AM
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
package org.earthtime.UPb_Redux.fractions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;

/**
 *
 * @author James F. Bowring
 */
public abstract class Fraction implements
        Comparable<Fraction>,
        FractionI,
        Serializable {

    private static final long serialVersionUID = -6610176652253689201L;
    // Instance variables
    private boolean isLegacy;
    private String sampleName;
    private String fractionID;
    private String grainID;
    private boolean zircon;
    private String imageURL;
    private Date timeStamp;
    private String mineralName;
    private String settingType;
    private int numberOfGrains;
    /**
     *
     */
    protected BigDecimal estimateDate;
    // moved here from UPbFraction oct 2010
    // march 2009 added in these two fields to handle customization of stacey kramers
    // basically they live in the UPbRedux fraction as inputs for StaceyKramer math set in  the
    // Aliquot fast fraction editor - but not saved in Aliquot for publication
    // as Stacey Kramers model will serialize with its current values when published
    /**
     *
     */
    protected BigDecimal staceyKramersOnePctUnct;
    /**
     *
     */
    protected BigDecimal staceyKramersCorrelationCoeffs;
    private boolean physicallyAbraded;
    private boolean leachedInHFAcid;
    private boolean annealedAndChemicallyAbraded;
    private boolean chemicallyPurifiedUPb;
    private String analysisFractionComment;
    private String pbBlankID;
    private String tracerID;
    private String physicalConstantsModelID;
    private String alphaPbModelID;
    private boolean fractionationCorrectedPb;
    private String alphaUModelID;
    private boolean fractionationCorrectedU;
    /**
     *
     */
    protected AbstractRatiosDataModel initialPbModel;
    private String pbCollectorType;
    private String uCollectorType;
    // results of analysis
    private ValueModel[] analysisMeasures;
    private ValueModel[] measuredRatios;
    private ValueModel[] radiogenicIsotopeRatios;
    private ValueModel[] radiogenicIsotopeDates;
    private ValueModel[] compositionalMeasures;
    private ValueModel[] sampleIsochronRatios;


    /**
     *
     */
    protected ConcurrentMap<String, BigDecimal> parDerivTerms;
    // april 2014
    private int rgbColor;
    // june 2014
    private ValueModel[] traceElements;

    /**
     * Creates a new instance of Fraction
     */
    public Fraction() {
        this.rgbColor = 0;

        this.isLegacy = false;
        this.sampleName = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.fractionID = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.grainID = fractionID;
        this.zircon = false;

        this.imageURL = "http://";
        this.timeStamp = new Date(System.currentTimeMillis());
        this.mineralName = ReduxLabData.getInstance().getDefaultMineralName();//   MineralTypes.OTHER.getName();//DataDictionary.StandardMineralNames[0];
        this.settingType = ReduxLabData.getInstance().getDefaultSettingType();    //DataDictionary.SettingType[0];

        this.numberOfGrains = ReduxLabData.getInstance().getDefaultNumberOfGrains();
        this.estimateDate = BigDecimal.ZERO;
        this.staceyKramersCorrelationCoeffs = BigDecimal.ZERO;
        this.staceyKramersOnePctUnct = BigDecimal.ZERO;

        this.physicallyAbraded = ReduxLabData.getInstance().isDefaultPhysicallyAbraded();
        this.leachedInHFAcid = ReduxLabData.getInstance().isDefaultLeachedInHFAcid();
        this.annealedAndChemicallyAbraded = ReduxLabData.getInstance().isDefaultAnnealedAndChemicallyAbraded();
        this.chemicallyPurifiedUPb = ReduxLabData.getInstance().isDefaultChemicallyPurifiedUPb();
        this.analysisFractionComment = "NONE";

        this.tracerID = "NONE";
        this.alphaPbModelID = "NONE";
        this.fractionationCorrectedPb = false;
        this.alphaUModelID = "NONE";
        this.fractionationCorrectedU = false;
        this.pbBlankID = "NONE";
        this.physicalConstantsModelID = "NONE";

        this.initialPbModel = InitialPbModelET.getNoneInstance();

        this.pbCollectorType = DataDictionary.PbCollectorType[0];
        this.uCollectorType = DataDictionary.UCollectorType[0];

        analysisMeasures = new ValueModel[AnalysisMeasures.getNames().length];
        for (int i = 0; i < AnalysisMeasures.getNames().length; i++) {
            analysisMeasures[i]
                    = new ValueModel(AnalysisMeasures.getNames()[i],
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        measuredRatios = new ValueModel[MeasuredRatios.getNames().length];
        for (int i = 0; i < MeasuredRatios.getNames().length; i++) {
            measuredRatios[i]
                    = new MeasuredRatioModel(MeasuredRatios.getNames()[i],
                            BigDecimal.ZERO,
                            "PCT",
                            BigDecimal.ZERO,
                            false,
                            false);
        }

        radiogenicIsotopeRatios = new ValueModel[DataDictionary.RadiogenicIsotopeRatioTypes.length];
        for (int i = 0; i < DataDictionary.RadiogenicIsotopeRatioTypes.length; i++) {
            radiogenicIsotopeRatios[i]
                    = new ValueModel(DataDictionary.getRadiogenicIsotopeRatioTypes(i),
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        // radiogenic isotope dates have a different name space from ratios
        radiogenicIsotopeDates = new ValueModel[RadDates.values().length];//DataDictionary.RadiogenicIsotopeDateTypes.length];
        for (int i = 0; i < RadDates.values().length; i++) {
            //for (int i = 0; i < DataDictionary.RadiogenicIsotopeDateTypes.length; i ++) {
            radiogenicIsotopeDates[i]
                    = new ValueModel(RadDates.values()[i].getName(),//     DataDictionary.getRadiogenicIsotopeDateTypes( i ),
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        compositionalMeasures = new ValueModel[DataDictionary.earthTimeUPbCompositionalMeasuresNames.length];
        for (int i = 0; i < DataDictionary.earthTimeUPbCompositionalMeasuresNames.length; i++) {
            compositionalMeasures[i]
                    = new ValueModel(DataDictionary.getEarthTimeUPbCompositionalMeasuresNames(i),
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        sampleIsochronRatios = new ValueModel[DataDictionary.SampleIsochronRatioNames.length];
        for (int i = 0; i < DataDictionary.SampleIsochronRatioNames.length; i++) {
            sampleIsochronRatios[i]
                    = new ValueModel(DataDictionary.getSampleIsochronRatioNames(i),
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        initializeTraceElements();

    }

    /**
     *
     * @param sampleName
     * @param fractionID
     */
    public Fraction(String sampleName, String fractionID) {
        this();
        this.sampleName = sampleName.trim();
        this.fractionID = fractionID.trim();
    }

    /**
     * Provides for ordering fractions
     *
     * @param fraction
     * @return
     * @throws ClassCastException
     */
    @Override
    public int compareTo(Fraction fraction) throws ClassCastException {
        String fractionTwoID = fraction.getFractionID().trim();
        String fractionOneID = this.getFractionID().trim();

        // oct 2010 put here
        Comparator<String> forNoah = new IntuitiveStringComparator<>();
        return forNoah.compare(fractionOneID, fractionTwoID);
    }

    /**
     *
     * @param fraction
     * @return
     */
    @Override
    public boolean equals(Object fraction) {
        //check for self-comparison
        if (this == fraction) {
            return true;
        }
        if (!(fraction instanceof FractionI)) {
            return false;
        }

        FractionI myFraction = (FractionI) fraction;

        // oct 2010 put here
        Comparator<String> forNoah = new IntuitiveStringComparator<>();
        return forNoah.compare(this.getFractionID().trim(), myFraction.getFractionID().trim()) == 0;
    }

    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     *
     * @return
     */
    @Override
    public int hashCode() {

        int hash = 1;
        hash = hash * 31 + fractionID.hashCode();
        return hash;
    }

    /**
     *
     * @param fraction
     * @param copyAnalysisMeasures
     */
    @Override
    public void getValuesFrom(ETFractionInterface fraction, boolean copyAnalysisMeasures) {

        setLegacy(fraction.isLegacy());
        setZircon(((FractionI)fraction).isZircon());

        setImageURL(fraction.getImageURL());
        setTimeStamp(fraction.getTimeStamp());

        setMineralName(((FractionI)fraction).getMineralName());
        setSettingType(((FractionI)fraction).getSettingType());

        setNumberOfGrains(fraction.getNumberOfGrains());
        setEstimatedDate(fraction.getEstimatedDate());
        setStaceyKramersOnePctUnct(((FractionI)fraction).getStaceyKramersOnePctUnct());
        setStaceyKramersCorrelationCoeffs(((FractionI)fraction).getStaceyKramersCorrelationCoeffs());

        setPhysicallyAbraded(((FractionI)fraction).isPhysicallyAbraded());
        setLeachedInHFAcid(((FractionI)fraction).isLeachedInHFAcid());
        setAnnealedAndChemicallyAbraded(((FractionI)fraction).isAnnealedAndChemicallyAbraded());
        setChemicallyPurifiedUPb(((FractionI)fraction).isChemicallyPurifiedUPb());

        setAnalysisFractionComment(fraction.getAnalysisFractionComment());

        try {
            setInitialPbModel(((FractionI)fraction).getInitialPbModel());//march 2012 upgrade.copy() );
        } catch (Exception e) {
            // case of UPbLegacyFraction
            setInitialPbModel(null);
        }

        setPbCollectorType(((FractionI)fraction).getPbCollectorType());
        setUCollectorType(((FractionI)fraction).getUCollectorType());

        if (copyAnalysisMeasures) {
            setAnalysisMeasures(fraction.copyAnalysisMeasures());
        }
        setRadiogenicIsotopeRatios(fraction.copyRadiogenicIsotopeRatios());
        setRadiogenicIsotopeDates(fraction.copyRadiogenicIsotopeDates());
        setCompositionalMeasures(fraction.copyCompositionalMeasures());
        setSampleIsochronRatios(fraction.copySampleIsochronRatios());
        traceElements = fraction.copyTraceElements();

        // aug 2010
        setFractionationCorrectedPb(((FractionI)fraction).isFractionationCorrectedPb());
        setFractionationCorrectedU(((FractionI)fraction).isFractionationCorrectedU());
    }

    // accessors
    /**
     *
     * @return
     */
    @Override
    public String getSampleName() {
        return sampleName;
    }

    /**
     *
     * @param sampleName
     */
    @Override
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getFractionID() {
        return fractionID;
    }

    /**
     *
     * @param FractionID
     */
    @Override
    public void setFractionID(String FractionID) {
        this.fractionID = FractionID.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getImageURL() {
        return imageURL;
    }

    /**
     *
     * @param imageURL
     */
    @Override
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public Date getTimeStamp() {
        return timeStamp;
    }

    /**
     *
     * @param timeStamp
     */
    @Override
    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     *
     * @return
     */
    public String getMineralName() {
        return mineralName;
    }

    /**
     *
     * @param mineralName
     */
    public void setMineralName(String mineralName) {
        this.mineralName = MineralTypes.validateStandardMineralTypeName(mineralName.trim());
    }

    /**
     *
     * @return
     */
    @Override
    public String getSettingType() {
        return settingType;
    }

    /**
     *
     * @param settingType
     */
    @Override
    public void setSettingType(String settingType) {
        // check if legal
        this.settingType = DataDictionary.validateSettingType(settingType.trim());
    }

    /**
     *
     * @return
     */
    @Override
    public int getNumberOfGrains() {
        return numberOfGrains;
    }

    /**
     *
     * @param numberOfGrains
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
        return estimateDate;
    }

    /**
     *
     * @param estimateDate
     */
    @Override
    public void setEstimatedDate(BigDecimal estimateDate) {
        this.estimateDate = estimateDate;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isPhysicallyAbraded() {
        return physicallyAbraded;
    }

    /**
     *
     * @param physicallyAbraded
     */
    @Override
    public void setPhysicallyAbraded(boolean physicallyAbraded) {
        this.physicallyAbraded = physicallyAbraded;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isLeachedInHFAcid() {
        return leachedInHFAcid;
    }

    /**
     *
     * @param leachedInHFAcid
     */
    @Override
    public void setLeachedInHFAcid(boolean leachedInHFAcid) {
        this.leachedInHFAcid = leachedInHFAcid;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isAnnealedAndChemicallyAbraded() {
        return annealedAndChemicallyAbraded;
    }

    /**
     *
     * @param annealedAndChemicallyAbraded
     */
    @Override
    public void setAnnealedAndChemicallyAbraded(boolean annealedAndChemicallyAbraded) {
        this.annealedAndChemicallyAbraded = annealedAndChemicallyAbraded;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isChemicallyPurifiedUPb() {
        return chemicallyPurifiedUPb;
    }

    /**
     *
     * @param chemicallyPurifiedUPb
     */
    @Override
    public void setChemicallyPurifiedUPb(boolean chemicallyPurifiedUPb) {
        this.chemicallyPurifiedUPb = chemicallyPurifiedUPb;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAnalysisFractionComment() {
        return analysisFractionComment;
    }

    /**
     *
     * @param analysisFractionComment
     */
    @Override
    public void setAnalysisFractionComment(String analysisFractionComment) {
        this.analysisFractionComment = analysisFractionComment.trim();
    }

    /**
     *
     * @return
     */
    public String getPbBlankID() {
        return pbBlankID;
    }

    /**
     *
     * @param pbBlankID
     */
    public void setPbBlankID(String pbBlankID) {
        this.pbBlankID = pbBlankID.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getTracerID() {
        return tracerID;
    }

    /**
     *
     * @param tracerID
     */
    @Override
    public void setTracerID(String tracerID) {
        this.tracerID = tracerID.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel getInitialPbModel() {
        return initialPbModel;
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getInitialPbModelForXMLSerialization() {

        AbstractRatiosDataModel retVal = null;

        if (initialPbModel != null) {
            if (initialPbModel.equals(InitialPbModelET.getNoneInstance())) {
                retVal = null;
            } else {
                retVal = initialPbModel.cloneModel();
            }
        }

        return retVal;
    }

    /**
     *
     * @param initialPbModel
     */
    @Override
    public void setInitialPbModel(AbstractRatiosDataModel initialPbModel) {
        this.initialPbModel = initialPbModel;
    }

    /**
     *
     * @return
     */
    @Override
    public String getPbCollectorType() {
        return pbCollectorType;
    }

    /**
     *
     * @param pbCollectorType
     */
    @Override
    public void setPbCollectorType(String pbCollectorType) {
        this.pbCollectorType = DataDictionary.validatePbCollectorType(pbCollectorType.trim());
    }

    /**
     *
     * @return
     */
    @Override
    public String getUCollectorType() {
        return uCollectorType;
    }

    /**
     *
     * @param uCollectorType
     */
    @Override
    public void setUCollectorType(String uCollectorType) {
        this.uCollectorType = DataDictionary.validateUCollectorType(uCollectorType.trim());
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getAnalysisMeasures() {
        return analysisMeasures;
    }

    /**
     *
     * @param analysisMeasures
     */
    @Override
    public void setAnalysisMeasures(ValueModel[] analysisMeasures) {
        this.analysisMeasures = ValueModel.cullNullsFromArray(analysisMeasures);
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getMeasuredRatios() {
        return measuredRatios;
    }

    /**
     *
     * @param measuredRatios
     */
    @Override
    public void setMeasuredRatios(ValueModel[] measuredRatios) {
        this.measuredRatios = ValueModel.cullNullsFromArray(measuredRatios);
    }

    /**
     *
     * @param myMeasuredRatio
     * @return
     */
    @Override
    public ValueModel getMeasuredRatioByName(MeasuredRatios myMeasuredRatio) {
        return getMeasuredRatioByName(myMeasuredRatio.getName());
    }

    /**
     *
     * @param myRatioName
     * @return
     */
    @Override
    public ValueModel getMeasuredRatioByName(String myRatioName) {
        // NOV 2009 NOTE: Tripoli still uses no r and no m ... TODO: fix this!!
        String ratioName = myRatioName.trim();
        ValueModel retval = null;

        // feb 2009 partial refactoring to fix problem of missing r....m
        String rName;
        if (ratioName.startsWith("r")) {
            rName = ratioName.substring(1, ratioName.length() - 1);
        } else {
            rName = "r" + ratioName + "m";
        }

        // look for ratio
        for (int i = 0; i < getMeasuredRatios().length; i++) {
            if (getMeasuredRatios()[i].getName().equalsIgnoreCase(ratioName)) {
                retval = getMeasuredRatios()[i];
            } else if (getMeasuredRatios()[i].getName().equalsIgnoreCase(rName)) {
                retval = getMeasuredRatios()[i];
            }
        }

        if (retval != null) {
            // fix name
            if (retval.getName().startsWith("2")) {
                retval.setName("r" + retval.getName() + "m");
            }
        } else {
            // send a dummy if not found
            retval = new MeasuredRatioModel(
                    ratioName,
                    BigDecimal.ZERO,
                    "PCT",
                    BigDecimal.ZERO,
                    false,
                    false);

            ValueModel[] temp = new ValueModel[getMeasuredRatios().length + 1];
            System.arraycopy(getMeasuredRatios(), 0, temp, 0, getMeasuredRatios().length);

            temp[temp.length - 1] = retval;
            setMeasuredRatios(temp);
        }
        return retval;

    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getRadiogenicIsotopeRatios() {
        return radiogenicIsotopeRatios;
    }

    /**
     *
     * @param radiogenicIsotopeRatios
     */
    @Override
    public void setRadiogenicIsotopeRatios(ValueModel[] radiogenicIsotopeRatios) {
        this.radiogenicIsotopeRatios = ValueModel.cullNullsFromArray(radiogenicIsotopeRatios);
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getRadiogenicIsotopeDates() {
        return radiogenicIsotopeDates;
    }

    /**
     *
     * @param radiogenicIsotopeDates
     */
    @Override
    public void setRadiogenicIsotopeDates(ValueModel[] radiogenicIsotopeDates) {
        this.radiogenicIsotopeDates = ValueModel.cullNullsFromArray(radiogenicIsotopeDates);
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getCompositionalMeasures() {
        return compositionalMeasures;
    }

    /**
     *
     * @param compositionalMeasures
     */
    @Override
    public void setCompositionalMeasures(ValueModel[] compositionalMeasures) {
        this.compositionalMeasures = ValueModel.cullNullsFromArray(compositionalMeasures);
    }

    /**
     *
     * @return
     */
    @Override
    public String getPhysicalConstantsModelID() {
        return physicalConstantsModelID;
    }

    /**
     *
     * @param physicalConstantsModelID
     */
    public void setPhysicalConstantsModelID(String physicalConstantsModelID) {
        this.physicalConstantsModelID = physicalConstantsModelID.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAlphaPbModelID() {
        return alphaPbModelID;
    }

    /**
     *
     * @param alphaPbModelID
     */
    @Override
    public void setAlphaPbModelID(String alphaPbModelID) {
        this.alphaPbModelID = alphaPbModelID.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAlphaUModelID() {
        return alphaUModelID;
    }

    /**
     *
     * @param alphaUModelID
     */
    @Override
    public void setAlphaUModelID(String alphaUModelID) {
        this.alphaUModelID = alphaUModelID.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isZircon() {
        return zircon;
    }

    /**
     *
     * @param zircon
     */
    @Override
    public void setZircon(boolean zircon) {
        this.zircon = zircon;
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getSampleIsochronRatios() {
        return sampleIsochronRatios;
    }

    /**
     *
     * @param sampleIsochronRatios
     */
    @Override
    public void setSampleIsochronRatios(ValueModel[] sampleIsochronRatios) {
        this.sampleIsochronRatios = ValueModel.cullNullsFromArray(sampleIsochronRatios);
    }

    /**
     * @return the grainID
     */
    @Override
    public String getGrainID() {
        if (grainID == null) {
            grainID = fractionID;
        }
        if (grainID.equalsIgnoreCase("")) {
            grainID = fractionID;
        }
        return grainID;
    }

    /**
     * @param grainID the grainID to set
     */
    public void setGrainID(String grainID) {
        this.grainID = grainID.trim();
    }

    /**
     * @return the fractionationCorrectedPb
     */
    public boolean isFractionationCorrectedPb() {
        return fractionationCorrectedPb;
    }

    /**
     * @param fractionationCorrectedPb the fractionationCorrectedPb to set
     */
    public void setFractionationCorrectedPb(boolean fractionationCorrectedPb) {
        this.fractionationCorrectedPb = fractionationCorrectedPb;
    }

    /**
     * @return the fractionationCorrectedU
     */
    public boolean isFractionationCorrectedU() {
        return fractionationCorrectedU;
    }

    /**
     * @param fractionationCorrectedU the fractionationCorrectedU to set
     */
    public void setFractionationCorrectedU(boolean fractionationCorrectedU) {
        this.fractionationCorrectedU = fractionationCorrectedU;
    }

    /**
     * @return the staceyKramersOnePctUnct
     */
    @Override
    public BigDecimal getStaceyKramersOnePctUnct() {
        if (staceyKramersOnePctUnct == null) {
            staceyKramersOnePctUnct = BigDecimal.ZERO;
        }
        return staceyKramersOnePctUnct;
    }

    /**
     * @param staceyKramersOnePctUnct the staceyKramersOnePctUnct to set
     */
    @Override
    public void setStaceyKramersOnePctUnct(BigDecimal staceyKramersOnePctUnct) {
        this.staceyKramersOnePctUnct = staceyKramersOnePctUnct;
    }

    /**
     * @return the staceyKramersCorrelationCoeffs
     */
    @Override
    public BigDecimal getStaceyKramersCorrelationCoeffs() {
        if (staceyKramersCorrelationCoeffs == null) {
            staceyKramersCorrelationCoeffs = BigDecimal.ZERO;
        }
        return staceyKramersCorrelationCoeffs;
    }

    /**
     * @param staceyKramersCorrelationCoeffs the staceyKramersCorrelationCoeffs
     * to set
     */
    public void setStaceyKramersCorrelationCoeffs(BigDecimal staceyKramersCorrelationCoeffs) {
        this.staceyKramersCorrelationCoeffs = staceyKramersCorrelationCoeffs;
    }

    /**
     * @return the isLegacy
     */
    @Override
    public boolean isLegacy() {
        return isLegacy;
    }

    /**
     * @param isLegacy the isLegacy to set
     */
    @Override
    public void setLegacy(boolean isLegacy) {
        this.isLegacy = isLegacy;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        AnalysisMeasures am = AnalysisMeasures.alphaPb;
        System.out.println(am.getName());
    }

    /**
     * @return the parDerivTerms
     */
    @Override
    public ConcurrentMap<String, BigDecimal> getParDerivTerms() {
        return parDerivTerms;
    }

    /**
     * @param parDerivTerms the parDerivTerms to set
     */
    @Override
    public void setParDerivTerms(ConcurrentMap<String, BigDecimal> parDerivTerms) {
        this.parDerivTerms = parDerivTerms;
    }

    /**
     * @return the rgbColor
     */
    @Override
    public int getRgbColor() {
        return rgbColor;
    }

    /**
     * @param rgbColor the rgbColor to set
     */
    @Override
    public void setRgbColor(int rgbColor) {
        this.rgbColor = rgbColor;
    }

    /**
     * @return the traceElements
     */
    @Override
    public ValueModel[] getTraceElements() {
        if (traceElements == null) {
            initializeTraceElements();
        }
        return traceElements;
    }

    /**
     * @param traceElements the traceElements to set
     */
    @Override
    public void setTraceElements(ValueModel[] traceElements) {
        this.traceElements = traceElements;
    }

    /**
     *
     * @param tmName
     * @param valueModel
     */
    public void setTraceElementByName(String tmName, ValueModel valueModel) {
        // make sure it exists
        getTraceElementByName(tmName.trim());
        //find it
        for (int i = 0; i < traceElements.length; i++) {
            if (traceElements[i].getName().equalsIgnoreCase(tmName.trim())) {
                traceElements[i] = valueModel;
            }
        }
    }


}
