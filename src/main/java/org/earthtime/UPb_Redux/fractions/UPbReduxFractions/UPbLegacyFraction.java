/*
 * UPbLegacyFraction.java
 *
 * Created on 21 February 2010
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions;

import Jama.Matrix;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.reportViews.ReportRowGUIInterface;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;

/**
 *
 * @author James F. Bowring
 */
public class UPbLegacyFraction extends Fraction implements
        FractionI,
        UPbFractionI,
        ETFractionInterface,
        ReportRowGUIInterface,
        Serializable {
//TODO: refactor this class = quick copy and simplification of UPbFraction, but has many common features. a

    // also has extraneous stuff like top panel = useless for legacy
    // Class variables
    private static final long serialVersionUID = 890915287558027634L;
    // Instance variables
    private transient Path2D errorEllipsePath;
    private transient double ellipseRho;
    // march 2014 for isoplot 
//    private transient org.cirdles.isoplot.chart.concordia.ErrorEllipse errorEllipseNode;
    private transient boolean hasMeasuredLead;
    private transient boolean hasMeasuredUranium;
    private transient boolean selectedInDataTable;
    private String ratioType;
    private AbstractRatiosDataModel physicalConstantsModel; // fraction class has physicalConstantsModelID
    private int aliquotNumber;
    private boolean changed;
    private boolean deleted;
    private boolean rejected;
    private String fractionNotes;
    // added july 2010 for detrital filtering
    private boolean filtered;
    private boolean standard;

    /**
     *
     */
    public UPbLegacyFraction() {
        super(ReduxConstants.DEFAULT_OBJECT_NAME, ReduxConstants.DEFAULT_OBJECT_NAME);
        setIsLegacy(true);
        this.ratioType = "UPb";

        this.physicalConstantsModel = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel();

        this.aliquotNumber = 1;

        this.changed = true;
        this.deleted = false;

        this.fractionNotes = "";

        this.rejected = false;

        // trimming our size
        setAnalysisMeasures(new ValueModel[0]);
        setRadiogenicIsotopeRatios(new ValueModel[0]);
        setRadiogenicIsotopeDates(new ValueModel[0]);
        setCompositionalMeasures(new ValueModel[0]);
        setSampleIsochronRatios(new ValueModel[0]);

        setInitialPbModel(null);

        hasMeasuredLead = false;

        hasMeasuredUranium = false;

        this.standard = false;

    }

    /**
     * Creates a new instance of UPbFraction with default attributes.
     * UPbLegacyFraction is used to receive incoming fraction data and to save
     * fraction data via xml serialization. UPbFraction is also used as the
     * analysis framework, but is not propagated directly to the Aliquot.
     *
     * @param fractionID
     */
    public UPbLegacyFraction(String fractionID) {
        this();
        this.setFractionID(fractionID);
    }

    /**
     * Creates a new UPbLegacyFraction from an imported AnalysisFraction. The
     * imported fraction cannot be changed, only manipulated.
     *
     * @param aliquotNum
     * @param fraction
     */
    public UPbLegacyFraction(
            int aliquotNum,
            FractionI fraction) {

        this();

        // custom settings
        this.aliquotNumber = aliquotNum;
        this.changed = false;

        // Fraction fields
        this.setSampleName(fraction.getSampleName());
        this.setFractionID(fraction.getFractionID());
        this.setGrainID(fraction.getFractionID());

        this.getValuesFrom(fraction, true);

        this.setMeasuredRatios(fraction.copyMeasuredRatios());

    }

    /**
     * Provides for ordering fractions in aliquots case insensitive
     *
     * @param fraction
     * @return
     * @throws java.lang.ClassCastException
     */
    @Override
    public int compareTo(Fraction fraction) throws ClassCastException {
        // TODO May 2010 Eventaully consider grainID
        String uPbFractionID = fraction.getFractionID();
        String uPbFractionAliquotNum = String.valueOf(((FractionI) fraction).getAliquotNumber());
        String myID = (uPbFractionAliquotNum + "." + uPbFractionID).toUpperCase();

        Comparator<String> forNoah = new IntuitiveStringComparator<>();

        return forNoah.compare((String.valueOf(this.getAliquotNumber()) + "." + this.getFractionID()).toUpperCase(), myID);
    }

    /**
     *
     * @return
     */
    @Override
    public Object[] getFractionTableRowData() {
        String tracerName = "N/A";

        Object[] retval = {
            String.valueOf(getAliquotNumber()), !isRejected(), // oct 2009 for fraction selector where SELECTED = NOT rejected
            getFractionNotes().length() > 0,// notes column added nov 2009 >0 ==> bold
            getFractionID(), // for fraction edit button
            tableEntryForMeasuredRatio(MeasuredRatios.r206_204m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r206_207m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r206_208m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r206_205m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r207_205m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r208_205m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r202_205m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r238_235m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r233_235m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r233_236m.getName()),
            tracerName
        };

        return retval;
    }

    private String tableEntryForMeasuredRatio(String measuredRatio) {
        String retVal = " ";

        if (getMeasuredRatioByName(measuredRatio).getValue().compareTo(BigDecimal.ZERO) != 0) {
            retVal = ((MeasuredRatioModel) getMeasuredRatioByName(measuredRatio)).toTableFormat();
        }

        return retVal;
    }

    /**
     *
     * @param ipmName
     * @return
     */
    public ValueModel getInitialPbModelRatioByName(String ipmName) {
        return getInitialPbModel().getDatumByName(ipmName);
    }

    /**
     *
     * @return
     */
    public static String[] getColumnNames() {
        return columnNames;
    }

    /**
     *
     * @return
     */
    @Override
    public int getAliquotNumber() {
        return aliquotNumber;
    }

    /**
     *
     * @param aliquotNumber
     */
    @Override
    public void setAliquotNumber(int aliquotNumber) {
        this.aliquotNumber = aliquotNumber;
        setChanged(true);
    }

    /**
     *
     * @return
     */
    @Override
    public String getRatioType() {
        return ratioType;
    }

    /**
     *
     * @param RatioType
     */
    @Override
    public void setRatioType(String RatioType) {
        this.ratioType = RatioType;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isChanged() {
        return changed;
    }

    /**
     *
     * @param changed
     */
    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    /**
     *
     * @param deleted
     */
    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     *
     * @return
     */
    @Override
    public String getPhysicalConstantsModelID() {
        return getPhysicalConstantsModel().getNameAndVersion();
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        if (physicalConstantsModel == null) {
            physicalConstantsModel = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel();
        }
        return physicalConstantsModel;
    }

    /**
     *
     * @param physicalConstantsModel
     */
    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        if ((this.physicalConstantsModel == null)
                || (!this.physicalConstantsModel.equals(physicalConstantsModel))) {
            this.physicalConstantsModel = physicalConstantsModel;
            this.setChanged(true);
            System.out.println(this.getFractionID() //
                    + "  is getting new physical constants model = "//
                    + physicalConstantsModel.getNameAndVersion());
        }
    }

    /**
     *
     * @return
     */
    public boolean isRejected() {
        return rejected;
    }

    /**
     *
     * @param rejected
     */
    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    /**
     *
     */
    public void toFileAllDataValues() {
        File dataValuesFile = new File("ALL_VALUES_" + getFractionID() + ".txt");
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(new FileWriter(dataValuesFile));
            outputWriter.println("\n\n******   FRACTION " + getFractionID() + "   ********************\n\n");

            // measured ratios
            outputWriter.println("measured ratios");
            for (int i = 0; i < getMeasuredRatios().length; i++) {
                outputWriter.println(getMeasuredRatios()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // AnalysisMeasures
            outputWriter.println("AnalysisMeasures");
            Arrays.sort(getAnalysisMeasures());
            for (int i = 0; i < getAnalysisMeasures().length; i++) {
                outputWriter.println(getAnalysisMeasures()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // CompositionalMeasures
            outputWriter.println("CompositionalMeasures");
            Arrays.sort(getCompositionalMeasures());
            for (int i = 0; i < getCompositionalMeasures().length; i++) {
                outputWriter.println(getCompositionalMeasures()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // RadiogenicIsotopeRatios
            outputWriter.println("RadiogenicIsotopeRatios");
            Arrays.sort(getRadiogenicIsotopeRatios());
            for (int i = 0; i < getRadiogenicIsotopeRatios().length; i++) {
                outputWriter.println(getRadiogenicIsotopeRatios()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // RadiogenicIsotopeDates
            outputWriter.println("RadiogenicIsotopeDates");
            Arrays.sort(getRadiogenicIsotopeDates());
            for (int i = 0; i < getRadiogenicIsotopeDates().length; i++) {
                outputWriter.println(getRadiogenicIsotopeDates()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(dataValuesFile.getCanonicalPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     *
     */
    public void calculateTeraWasserburgRho() {
        // created to handle legacy data

        double rho = getRadiogenicIsotopeRatioByName("rhoR206_238r__r207_235r").getValue().doubleValue();
        if ((rho >= -1.0) && (rho <= 1.0)) {
            try {
                double[][] rawCovW = new double[2][2];
                rawCovW[0][0] = getRadiogenicIsotopeRatioByName("r206_238r").getOneSigmaAbs().pow(2).doubleValue();
                rawCovW[0][1] = getRadiogenicIsotopeRatioByName("rhoR206_238r__r207_235r").getValue().//
                        multiply(getRadiogenicIsotopeRatioByName("r206_238r").getOneSigmaAbs()).//
                        multiply(getRadiogenicIsotopeRatioByName("r207_235r").getOneSigmaAbs()).doubleValue();
                rawCovW[1][0] = rawCovW[0][1];
                rawCovW[1][1] = getRadiogenicIsotopeRatioByName("r207_235r").getOneSigmaAbs().pow(2).doubleValue();
                Matrix CovW = new Matrix(rawCovW);
//        System.out.println(">>>>  CovW");
//        CovW.print(10, 10);

                double[][] rawJ = new double[2][2];
                rawJ[0][0] = -1.0 / getRadiogenicIsotopeRatioByName("r206_238r").getValue().pow(2).doubleValue();
                rawJ[0][1] = 0.0;
                rawJ[1][0] = -1.0 //
                        * (getRadiogenicIsotopeRatioByName("r207_235r").getValue().//
                        divide(getRadiogenicIsotopeRatioByName("r206_238r").getValue().pow(2), ReduxConstants.mathContext15).//
                        // missing from legacy data as of oct 2010 divide( getAnalysisMeasure( AnalysisMeasures.r238_235s.getName() ).getValue(), ReduxConstants.mathContext15 ).doubleValue());
                        divide(new BigDecimal(137.88), ReduxConstants.mathContext15).doubleValue());
                rawJ[1][1] = BigDecimal.ONE.//
                        divide(getRadiogenicIsotopeRatioByName("r206_238r").getValue(), ReduxConstants.mathContext15).//
                        //divide( getAnalysisMeasure( AnalysisMeasures.r238_235s.getName() ).getValue(), ReduxConstants.mathContext15 ).doubleValue();
                        divide(new BigDecimal(137.88), ReduxConstants.mathContext15).doubleValue();
                Matrix J = new Matrix(rawJ);
//        System.out.println(">>>>  J");
//        J.print(10,10);

                Matrix CovTW = J.times(CovW).times(J.transpose());
//        System.out.println(">>>>  CovTW");
//        CovTW.print(10,10);

                getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").setValue( //
                        new BigDecimal(CovTW.get(0, 1) / Math.sqrt(CovTW.get(0, 0) * CovTW.get(1, 1))));
            } catch (Exception e) {
                getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").setValue(new BigDecimal(ReduxConstants.NO_RHO_FLAG));
            }
        } else {
            getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").setValue(new BigDecimal(ReduxConstants.NO_RHO_FLAG));
        }

    }

    /**
     *
     * @return
     */
    @Override
    /**
     * Added to handle text view in combo box
     */
    public String toString() {
        return getFractionID();
    }

    /**
     * @return the errorEllipsePath
     */
    public Path2D getErrorEllipsePath() {
        return errorEllipsePath;
    }

    /**
     * @param errorEllipsePath the errorEllipsePath to set
     */
    public void setErrorEllipsePath(Path2D errorEllipsePath) {
        this.errorEllipsePath = errorEllipsePath;
    }

    /**
     * @return the hasMeasuredLead
     */
    public boolean hasMeasuredLead() {
        return hasMeasuredLead;
    }

    /**
     * @param existsMeasuredLead
     */
    public void setHasMeasuredLead(boolean existsMeasuredLead) {
        this.hasMeasuredLead = existsMeasuredLead;
    }

    /**
     * @return the hasMeasuredUranium
     */
    public boolean hasMeasuredUranium() {
        return hasMeasuredUranium;
    }

    /**
     * @param existsMeasuredUranium
     */
    public void setHasMeasuredUranium(boolean existsMeasuredUranium) {
        this.hasMeasuredUranium = existsMeasuredUranium;
    }

    // feb 2010 these methods are placeholders due to use of
    // UpbFractionsI ... will disappear once legacy development is matured
    // TODO: needed for backward compatibility feb 2009 REMOVE by 2010
    /**
     *
     */
    public void setSavedFractionationModels() {
        // do nothing for legacy
    }

    /**
     * @return the fractionNotes
     */
    public String getFractionNotes() {
        if (fractionNotes == null) {
            fractionNotes = "";
        }
        return fractionNotes;
    }

    /**
     * @param fractionNotes the fractionNotes to set
     */
    public void setFractionNotes(String fractionNotes) {
        this.fractionNotes = fractionNotes;
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getPbBlank() {
        return null;
    }

    /**
     *
     * @param pbBlank
     */
    public void setPbBlank(AbstractRatiosDataModel pbBlank) {
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getTracer() {
        return null;
    }

    /**
     *
     * @param Tracer
     */
    @Override
    public void setTracer(AbstractRatiosDataModel Tracer) {
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel getAlphaPbModel() {
        return null;
    }

    /**
     *
     * @param alphaPbModel
     */
    @Override
    public void setAlphaPbModel(ValueModel alphaPbModel) {
    }

    /**
     *
     * @return
     */
    public ValueModel getAlphaUModel() {
        return null;
    }

    /**
     *
     * @param alphaUModel
     */
    public void setAlphaUModel(ValueModel alphaUModel) {
    }

    /**
     *
     * @param ratioName
     * @return
     */
    public ValueModel getRadiogenicIsotopeDateWithTracerUnctByName(String ratioName) {
        return new ValueModel(ratioName,
                BigDecimal.ZERO,
                "ABS",
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     *
     * @param ratioName
     * @return
     */
    public ValueModel getRadiogenicIsotopeDateWithAllUnctByName(String ratioName) {
        return new ValueModel(ratioName,
                BigDecimal.ZERO,
                "ABS",
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * @return the ellipseTilt
     */
    public double getEllipseRho() {
        return ellipseRho;
    }

    /**
     * @param ellipseRho
     */
    public void setEllipseRho(double ellipseRho) {
        this.ellipseRho = ellipseRho;
    }

    /**
     * @return the filtered
     */
    public boolean isFiltered() {
        return filtered;
    }

    /**
     * @param filtered the filtered to set
     */
    @Override
    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    /**
     * @return the selectedInDataTable
     */
    @Override
    public boolean isSelectedInDataTable() {
        return selectedInDataTable;
    }

    /**
     * @param selectedInDataTable the selectedInDataTable to set
     */
    public void setSelectedInDataTable(boolean selectedInDataTable) {
        this.selectedInDataTable = selectedInDataTable;
    }

    /**
     *
     */
    public void toggleRejectedStatus() {
        this.rejected = !this.rejected;
    }

    /**
     * @return the standard
     */
    public boolean isStandard() {
        return standard;
    }

    /**
     * @param standard the standard to set
     */
    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    @Override
    public boolean isCommonLeadLossCorrected() {
        return false; // dec 2014 until we learn that this is the case
    }

    @Override
    public boolean hasXMLUSourceFile() {
        return false;
    }

    @Override
    public boolean hasXMLPbSourceFile() {
        return false;
    }

    @Override
    public boolean isAnOxide() {
        return false;
    }
}
