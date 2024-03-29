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
package org.earthtime.reports;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Vector;
import static org.earthtime.UPb_Redux.ReduxConstants.makeFormattedDate;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.UTh_Redux.fractions.UThFraction;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.RadDates;
import static org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsAbstract.unitsType;
import org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsUPb;
import org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsUTh_Carb;
import org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsUTh_Ign;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.dialogs.ReportSettingsManager;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.samples.SampleInterface;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface ReportSettingsInterface extends Comparable<ReportSettingsInterface>, ReduxLabDataListElementI, Serializable, XMLSerializationI {

    int FRACTION_DATA_START_ROW = 8;

    public default void normalizeReportCategories() {
        for (int i = 0; i < getReportCategories().size(); i++) {
            getReportCategories().get(i).setPositionIndex(i);
        }
    }

    /**
     *
     * @return
     */
    public default Map<Integer, ReportCategoryInterface> getReportCategoriesInOrder() {
        Map<Integer, ReportCategoryInterface> retVal = new HashMap<>();

        getReportCategories().stream().filter((rc) //
                -> (rc != null)).forEach((rc) -> {
            retVal.put(rc.getPositionIndex(), rc);
        });

        return retVal;
    }

    public default void assembleReportCategories() {
        setReportCategories(new ArrayList<>());
        getReportCategories().add(getFractionCategory());
        if (isdefaultReportSpecsType_UPb()) {
            getReportCategories().add(getDatesCategory());
            getReportCategories().add(getDatesPbcCorrCategory());
            getReportCategories().add(getCompositionCategory());
            getReportCategories().add(getIsotopicRatiosCategory());
            getReportCategories().add(getIsotopicRatiosPbcCorrCategory());
            getReportCategories().add(getRhosCategory());
            getReportCategories().add(getTraceElementsCategory());
        } else if (isdefaultReportSpecsType_UTh_Carb()) {
            getReportCategories().add(getConcentrationAndActivityCategory());
            getReportCategories().add(getMeasuredAtomAndActivityRatiosCategory());
            getReportCategories().add(getMeasuredCorrectedAtomAndActivityRatiosCategory());
            getReportCategories().add(getDatesCategory());
        } else if (isdefaultReportSpecsType_UTh_Ign()) {
            getReportCategories().add(getDatesCategory());
        }
        getReportCategories().add(getFractionCategory2());
    }

    public ReportSettingsInterface deepCopy();

    /**
     *
     * @return
     */
    public default XStream getXStreamWriter() {
        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @return
     */
    public default XStream getXStreamReader() {

        XStream xstream = new XStream(new DomDriver());

        customizeXstream(xstream);

        // clear out existing permissions and set own ones
        xstream.addPermission(NoTypePermission.NONE);
        // allow some basics
        xstream.addPermission(NullPermission.NULL);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(Collection.class);
        xstream.addPermission(AnyTypePermission.ANY);

        return xstream;
    }

    /**
     *
     *
     * @param filename
     * @param doValidate the value of doValidate
     * @return
     * @throws FileNotFoundException
     * @throws ETException
     * @throws BadOrMissingXMLSchemaException
     */
    @Override
    public default Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        ReportSettingsInterface myReportSettings = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode = true;

            XStream xstream = getXStreamReader();

            if (doValidate) {
                isValidOrAirplaneMode = URIHelper.validateXML(reader, filename, getReportSettingsXMLSchemaURL());
            }

            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myReportSettings = (ReportSettingsInterface) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

            } else {
                throw new ETException(null, "XML data file does not conform to schema.");
            }
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return myReportSettings;
    }

    /**
     * registers converter for argument <code>xstream</code> and sets aliases to
     * make the XML file more human-readable
     *
     * @pre argument <code>xstream</code> is a valid <code>XStream</code>
     * @post argument <code>xstream</code> is customized to produce a cleaner
     * output <code>file</code>
     *
     * @param xstream <code>XStream</code> to be customized
     */
    void customizeXstream(XStream xstream);

    /**
     *
     * @param reportSettingsModel
     * @return
     */
    @Override
    boolean equals(Object reportSettingsModel);

    public default void toggleMeasuredRatiosInCompositionCategory() {
        ReportCategoryInterface composition = getCompositionCategory();
        for (ReportColumnInterface column : composition.getCategoryColumns()) {
            if (column.getDisplayName1().contains("meas")) {
                column.ToggleIsVisible();
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public default String getReduxLabDataElementName() {
        return getNameAndVersion();
    }

    /**
     *
     * @return
     */
    public default String getNameAndVersion() {
        return getName().trim() + " v." + getVersion();
    }

    /**
     *
     * @return
     */
    ReportCategoryInterface getCompositionCategory();

    /**
     *
     * @return
     */
    ReportCategoryInterface getDatesCategory();

    /**
     * @return the datesPbcCorrCategory
     */
    ReportCategoryInterface getDatesPbcCorrCategory();

    /**
     *
     * @return
     */
    ReportCategoryInterface getFractionCategory();

    /**
     * @return the fractionCategory2
     */
    ReportCategoryInterface getFractionCategory2();

    /**
     *
     * @return
     */
    ReportCategoryInterface getIsotopicRatiosCategory();

    /**
     * @return the isotopicRatiosPbcCorrCategory
     */
    ReportCategoryInterface getIsotopicRatiosPbcCorrCategory();

    /**
     *
     * @return
     */
    String getName();

    //  accessors
    /**
     *
     * @return
     */
    String getReportSettingsComment();

    /**
     * @return the rhosCategory
     */
    ReportCategoryInterface getRhosCategory();

    /**
     * @return the traceElementsCategory
     */
    ReportCategoryInterface getTraceElementsCategory();

    /**
     *
     * @return
     */
    int getVersion();

    /**
     * @return the legacyData
     */
    boolean isLegacyData();

    /**
     *
     * @return
     */
    boolean isOutOfDate();

    /**
     *
     * @return
     */
    boolean isOutOfDateUPb();

    /**
     *
     * @return
     */
    boolean isOutOfDateUTh_Carb();

    public boolean isOutOfDateUTh_Ign();

    /**
     *
     */
    @Override
    void removeSelf();

    /**
     * sets the XML schema. Initializes <code>UPbReduxConfigurator</code> and
     * sets the location of the XML Schema
     *
     * @pre <code>UPbReduxConfigurator</code> class is available
     * @post <code>TracerXMLSchemaURL</code> will be set
     */
    void setClassXMLSchemaURL();

    /**
     *
     * @param compositionCategory
     */
    void setCompositionCategory(ReportCategoryInterface compositionCategory);

    /**
     *
     * @param datesCategory
     */
    void setDatesCategory(ReportCategoryInterface datesCategory);

    /**
     * @param datesPbcCorrCategory the datesPbcCorrCategory to set
     */
    void setDatesPbcCorrCategory(ReportCategoryInterface datesPbcCorrCategory);

    /**
     * @return the concentrationAndActivityCategory
     */
    public ReportCategoryInterface getConcentrationAndActivityCategory();

    /**
     * @param concentrationAndActivityCategory the
     * concentrationAndActivityCategory to set
     */
    public void setConcentrationAndActivityCategory(ReportCategoryInterface concentrationAndActivityCategory);

    /**
     * @return the measuredAtomAndActivityRatiosCategory
     */
    public ReportCategoryInterface getMeasuredAtomAndActivityRatiosCategory();

    /**
     * @param measuredAtomAndActivityRatiosCategory the
     * measuredAtomAndActivityRatiosCategory to set
     */
    public void setMeasuredAtomAndActivityRatiosCategory(ReportCategoryInterface measuredAtomAndActivityRatiosCategory);

    /**
     * @return the measuredCorrectedAtomAndActivityRatiosCategory
     */
    public ReportCategoryInterface getMeasuredCorrectedAtomAndActivityRatiosCategory();

    /**
     * @param measuredCorrectedAtomAndActivityRatiosCategory the
     * measuredCorrectedAtomAndActivityRatiosCategory to set
     */
    public void setMeasuredCorrectedAtomAndActivityRatiosCategory(ReportCategoryInterface measuredCorrectedAtomAndActivityRatiosCategory);

    /**
     *
     * @param fractionCategory
     */
    void setFractionCategory(ReportCategoryInterface fractionCategory);

    /**
     * @param fractionCategory2 the fractionCategory2 to set
     */
    void setFractionCategory2(ReportCategoryInterface fractionCategory2);

    /**
     *
     * @param isotopicRatiosCategory
     */
    void setIsotopicRatiosCategory(ReportCategoryInterface isotopicRatiosCategory);

    /**
     * @param isotopicRatiosPbcCorrCategory the isotopicRatiosPbcCorrCategory to
     * set
     */
    void setIsotopicRatiosPbcCorrCategory(ReportCategoryInterface isotopicRatiosPbcCorrCategory);

    /**
     * @param legacyData the legacyData to set
     */
    void setLegacyData(boolean legacyData);

    /**
     *
     * @param name
     */
    void setName(String name);

    /**
     *
     * @param reportSettingsComment
     */
    void setReportSettingsComment(String reportSettingsComment);

    /**
     * @param rhosCategory the rhosCategory to set
     */
    void setRhosCategory(ReportCategoryInterface rhosCategory);

    /**
     * @param traceElementsCategory the traceElementsCategory to set
     */
    void setTraceElementsCategory(ReportCategoryInterface traceElementsCategory);

    /**
     *
     * @param version
     */
    void setVersion(int version);

    public default String[][] reportActiveFractionsByNumberStyle(final SampleInterface sample, boolean numberStyleIsNumeric) {
        Vector<ETFractionInterface> fractions = sample.getActiveFractionsSortedByAliquot();

        return reportFractionsByNumberStyle(fractions, sample, numberStyleIsNumeric);
    }

    /**
     *
     * @param sample
     * @param numberStyleIsNumeric
     * @return
     */
    public default String[][] reportRejectedFractionsByNumberStyle(final SampleInterface sample, boolean numberStyleIsNumeric) {
        Vector<ETFractionInterface> fractions = sample.getFractionsRejected();

        return reportFractionsByNumberStyle(fractions, sample, numberStyleIsNumeric);
    }

    /**
     *
     * @param sample
     * @param fractions
     * @param numberStyleIsNumeric
     * @return
     */
    public default String[][] reportActiveAliquotFractionsByNumberStyle(final SampleInterface sample, Vector<ETFractionInterface> fractions, boolean numberStyleIsNumeric) {

        return reportFractionsByNumberStyle(fractions, sample, numberStyleIsNumeric);
    }

    public default String[][] reportFractionsByNumberStyle(//
            Vector<ETFractionInterface> fractions,
            final SampleInterface sample,
            boolean numberStyleIsNumeric) {

        String activityFootnoteEntry = "";
        String thU_MagmaFootnoteEntry = "";
        int zirconPopulationType = 0;

        // the first six (FRACTION_DATA_START_ROW) rows are provided for naming and formats
        String[][] retVal
                = new String[fractions.size() + FRACTION_DATA_START_ROW][];
        // column 0 will contain true for included fractions and false for rejected fractions
        // column 1 will contain aliquot name

        // special case oct 2009 to see if we force the display of <ar231_235Pa> and Th_Umagma columns
        if (fractions.size() > 0) {
            // check whether all values are equal in displayed fractions

            // force to not visible
            if (getCompositionCategory() != null) {
                getCompositionCategory().setVisibleCategoryColumn(AnalysisMeasures.ar231_235sample.getName(), false);
                getCompositionCategory().setVisibleCategoryColumn(AnalysisMeasures.rTh_Umagma.getName(), false);
            }

            if (isdefaultReportSpecsType_UPb()) {
                // get first activityValue and first Th_Umagma value and set standard footnote entries
                BigDecimal savedActivityValue
                        = fractions.get(0).getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()).getValue();
                BigDecimal savedMagmaValue
                        = fractions.get(0).getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()).getValue();

                activityFootnoteEntry = "= " + savedActivityValue.toString();
                thU_MagmaFootnoteEntry = "= " + savedMagmaValue.toString();

                // modified april 2010 to account for zircon population
                int zirconCount = 0;
                int fractionCount = 0;
                for (ETFractionInterface f : fractions) {
                    if (f instanceof FractionI) {
                        // we have a UPb fraction with zircon property
                        if (!f.isRejected()) {

                            BigDecimal activityValue = f.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()).getValue();
                            if (activityValue.compareTo(savedActivityValue) != 0) {
                                getCompositionCategory().setVisibleCategoryColumn(AnalysisMeasures.ar231_235sample.getName(), true);
                                // change footnote
                                activityFootnoteEntry = "specified";
                            }
                            BigDecimal magmaValue = f.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()).getValue();
                            if (magmaValue.compareTo(savedMagmaValue) != 0) {
                                getCompositionCategory().setVisibleCategoryColumn(AnalysisMeasures.rTh_Umagma.getName(), true);
                                // change footnote
                                thU_MagmaFootnoteEntry = "specified";
                            }

                            fractionCount++;
                            if (((FractionI) f).isZircon()) {
                                zirconCount++;
                            }
                        }
                    }
                }

                // zirconPopulationType = 0 is default condition = no zircons
                zirconPopulationType = 0;
                if ((zirconCount > 0) && (zirconCount < fractionCount)) {
                    zirconPopulationType = 2; // mixed
                } else if (zirconCount == fractionCount) {
                    zirconPopulationType = 1; // zircon
                }

            } // end special case **************************************************
        }

        // a special case oct 2009 to decide if units of dates is Ma or ka or auto
        // repeated oct 2014 to handle Pbc corrected dates category also
        if (fractions.size() > 0) {
            // June 2017 split by isotope type
            if (isdefaultReportSpecsType_UPb()) {
                if (getDatesCategory().getCategoryColumns().length > 0) {
                    // first get the unittype of the first date (all will be set the same so this is a flag)
                    String currentDateUnit = getDatesCategory().getCategoryColumns()[0].getUnits();
                    boolean isAuto = false;
                    // the default is ka, though it will usually be overwritten by Ma
                    if (currentDateUnit.equalsIgnoreCase("Auto")) {
                        currentDateUnit = "ka";
                        isAuto = true;
                    }
                    getDatesCategory().setDisplayName("Dates (" + currentDateUnit + ")");
                    for (ReportColumnInterface rc : getDatesCategory().getCategoryColumns()) {
                        if (!rc.getUnits().equalsIgnoreCase("")) {
                            rc.setUnits(currentDateUnit);
                        }
                    }

                    if (isAuto) {
                        // let's find out
                        BigDecimal threshold = new BigDecimal(1000000);
                        for (ETFractionInterface f : fractions) {
                            if (!f.isRejected()) {
                                BigDecimal date206_238Value = f.getRadiogenicIsotopeDateByName(RadDates.age206_238r).getValue();
                                if (date206_238Value.compareTo(threshold) > 0) {
                                    // we have Ma when any value is greater than threshold
                                    getDatesCategory().setDisplayName("Dates (Ma)");
                                    // now set units correctly
                                    for (ReportColumnInterface rc : getDatesCategory().getCategoryColumns()) {
                                        if (!rc.getUnits().equalsIgnoreCase("")) {
                                            rc.setUnits("Ma");
                                        }
                                    }
                                    break;
                                }

                            }

                        }
                    }
                }

                if (getDatesPbcCorrCategory().getCategoryColumns().length > 0) {
                    // first get the unittype of the first date (all will be set the same so this is a flag)
                    String currentDateUnit = getDatesPbcCorrCategory().getCategoryColumns()[1].getUnits();
                    boolean isAuto = false;
                    // the default is ka, though it will usually be overwritten by Ma
                    if (currentDateUnit.equalsIgnoreCase("Auto")) {
                        currentDateUnit = "ka";
                        isAuto = true;
                    }
                    getDatesPbcCorrCategory().setDisplayName("PbcCorr Dates (" + currentDateUnit + ")");
                    for (ReportColumnInterface rc : getDatesPbcCorrCategory().getCategoryColumns()) {
                        if (!rc.getUnits().equalsIgnoreCase("")) {
                            rc.setUnits(currentDateUnit);
                        }
                    }

                    if (isAuto) {
                        // let's find out
                        BigDecimal threshold = new BigDecimal(1000000);
                        for (ETFractionInterface f : fractions) {
                            if (!f.isRejected()) {
                                BigDecimal date206_238Value = f.getRadiogenicIsotopeDateByName(RadDates.age206_238_PbcCorr).getValue();
                                if (date206_238Value.compareTo(threshold) > 0) {
                                    // we have Ma when any value is greater than threshold
                                    getDatesPbcCorrCategory().setDisplayName("PbcCorr Dates (Ma)");
                                    // now set units correctly
                                    for (ReportColumnInterface rc : getDatesPbcCorrCategory().getCategoryColumns()) {
                                        if (!rc.getUnits().equalsIgnoreCase("")) {
                                            rc.setUnits("Ma");
                                        }
                                    }
                                    break;
                                }

                            }

                        }
                    }
                }
            } else {
                // UTh style
                // special handling?
            }
        }

        // end of special case for units of date
        // oct 2016 added another cell to flag whether fraction is filtered = true or false
        int countOfAllColumns = getCountOfAllColumns() + 2 + 1;

        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = new String[countOfAllColumns];
        }

        for (int i = 0; i < retVal.length; i++) {
            for (int j = 0; j < retVal[i].length; j++) {
                retVal[i][j] = "";
            }
        }

        // row 0 is reserved for category displayname
        // row 1 is reserved for column displayName1
        // row 2 is reserved for column displayName2
        // row 3 is reserved for column displayName3
        // row 4 is reserved for units
        // modified below oct 2009
        // row 5 is reserved for column footnotes as reference letters
        // row 6 is reserved for storage of actual footnotes in the correct order
        // fraction data starts at col 2, row FRACTION_DATA_START_ROW
        // FRACTION_DATA_START_ROW is stored in 0,0
        retVal[0][0] = Integer.toString(FRACTION_DATA_START_ROW);
        //July 2017
        retVal[1][0] = getDefaultReportSpecsType();

        SortedSet<String> filteredFractions = sample.getFilteredFractionIDs();

        int columnCount = 2;

        int footNoteCounter = 0;
        ArrayList<String> footNotesMap = new ArrayList<>();

        Map<Integer, ReportCategoryInterface> categories = getReportCategoriesInOrder();

        for (int c = 0; c < categories.size(); c++) {
            try {
                if (categories.get(c).isVisible()) {
                    Map<Integer, ReportColumnInterface> cat = categories.get(c).getCategoryColumnOrder();

                    for (int col = 0; col < cat.size(); col++) {
                        int colIncrement = 1;
                        ReportColumnInterface myCol = cat.get(col);

                        if (myCol.isVisible()) {
                            // record column headings
                            retVal[0][columnCount] = categories.get(c).getDisplayName();
                            retVal[1][columnCount] = myCol.getDisplayName1();//.replace( "\u03c1", "" ); // remove greek rho
                            retVal[2][columnCount] = myCol.getDisplayName2();
                            retVal[3][columnCount] = myCol.getDisplayName3();
                            // modified oct 2009 for date units
                            // and oct 2014 for Pbc corrected
                            if (!myCol.getUnits().equals("")
                                    && (!categories.get(c).getDisplayName().contains("Dates"))) {
                                if ((unitsType.get(myCol.getUnits()) == "date")) {
                                    // July 2017 provides for special case of BP in Useries, where BP is string in retVal[1]
                                    retVal[3][columnCount] += "(" + myCol.getUnits() + retVal[1][columnCount] + ")";
                                    retVal[1][columnCount] = "";
                                } else {
                                    retVal[3][columnCount] += "(" + myCol.getUnits() + ")";
                                }
                            }
                            retVal[4][columnCount] = myCol.getUnits();

                            // detect and handle footnotes, which are referred to in reportFractions[5]
                            // multiple footnotes are separated by "&" as in FN-1&FN-2
                            String superScript = "";
                            if (!myCol.getFootnoteSpec().equalsIgnoreCase("")) {
                                // split footnote to determine count
                                String[] footNotesList = myCol.getFootnoteSpec().split("&");

                                // determine if footnotes already used and get index(es) and build superscript
                                for (int i = 0; i < footNotesList.length; i++) {
                                    int footNoteIndex = footNotesMap.indexOf(footNotesList[i]);
                                    if (footNoteIndex < 0) {
                                        superScript += determineFootNoteLetter(footNoteCounter);
                                        footNotesMap.add(footNoteCounter++, footNotesList[i]);
                                    } else {
                                        superScript += determineFootNoteLetter(footNoteIndex);
                                    }
                                }
                            }
                            retVal[5][columnCount] = superScript;
                            // row 6 is written below as a rendition of footnotes by code (FN-n) in order from footNotesMap
                            // retVal[6][columnCount] = (String) (numberStyleIsNumeric ? "Numeric" : "");

                            if (myCol.getUncertaintyColumn() != null) {
                                if (myCol.getUncertaintyColumn().isVisible()) {
                                    colIncrement = 2;
                                    retVal[0][columnCount + 1] = categories.get(c).getDisplayName();
                                    retVal[1][columnCount + 1] = myCol.getUncertaintyColumn().getDisplayName1();
                                    retVal[2][columnCount + 1] = myCol.getUncertaintyColumn().getDisplayName2();
                                    retVal[3][columnCount + 1] = myCol.getUncertaintyColumn().getDisplayName3();
                                    retVal[4][columnCount + 1] = myCol.getUncertaintyColumn().getUnits();
                                    retVal[5][columnCount + 1] = "";
                                    //retVal[6][columnCount + 1] = (String) (numberStyleIsNumeric ? "Numeric" : "");
                                }
                            }

                            // walk all the fractions for each column
                            int fractionRowCount = FRACTION_DATA_START_ROW;
//                            SortedSet<String> filteredFractions = sample.getFilteredFractionIDs();
                            for (ETFractionInterface f : fractions) {

                                // test for included fraction on first data pass col=2==>fractionID
                                if (columnCount == 2) {
                                    if (f.isRejected()) {
                                        retVal[fractionRowCount][0] = "false";
                                    } else {
                                        retVal[fractionRowCount][0] = "true";
                                    }

                                    retVal[fractionRowCount][1]
                                            = sample.getAliquotByNumber(f.getAliquotNumber()).getAliquotName();

                                    // oct 2016 filtering true = not filtered out
                                    if (filteredFractions.contains(f.getFractionID())) {
                                        retVal[fractionRowCount][countOfAllColumns - 1] = "true";
                                    } else {
                                        retVal[fractionRowCount][countOfAllColumns - 1] = "false";
                                    }
                                }

                                // field contains the Value in field[0]
                                //and the uncertainty in field[1] if it exists/isvisible
                                String[] field = myCol.getReportRecordByColumnSpec(f, numberStyleIsNumeric);

                                // July 2017 for Useries
                                if (myCol.getDisplayName3().contains("Date")) {
//                                    System.out.println(field[0]);
                                    if (field[0].contains("-")) {
                                        field[0] = "         n/a";//\u221E"; // infinity
                                        // check for uncertainty column in next cell unless last cell
                                        if (!field[1].equals("") && (retVal[fractionRowCount].length > (columnCount + 1))) {
                                            field[1] = "           -";
                                        }
                                    }
                                }

                                retVal[fractionRowCount][columnCount] = field[0];
                                // check for uncertainty column in next cell unless last cell
                                if (!field[1].equals("") && (retVal[fractionRowCount].length > (columnCount + 1))) {
                                    retVal[fractionRowCount][columnCount + 1] = field[1];
                                }

                                fractionRowCount++;

                            }
                            // post process column
                            // if column is in sigfig mode, strip out spaces common to all rows (leading/trailing)
                            // if arbitrary, use digit counts to create an excel format for the 4th row
                            if (fractions.size() > 0) {

                                if (myCol.isDisplayedWithArbitraryDigitCount()) {
                                    // for now just trim. later need to handle case
                                    // where excel gets long number and format string
                                    trimColumn(retVal, columnCount);
                                    if (colIncrement == 2) {
                                        trimColumn(retVal, columnCount + 1);
                                    }
                                } else {
                                    trimColumn(retVal, columnCount);
                                    if (colIncrement == 2) {
                                        trimColumn(retVal, columnCount + 1);
                                    }
                                }
                            }

                            columnCount += colIncrement;
                        }
                    }
                }
            } catch (Exception e) {
            }

        }

        // July 2017 no footnotes with no fractions
        if (fractions.size() > 0) {
            // write footNotesMap and prepend superscript letter with "&" delimiter
            // write full text of footnote with variables replaced with values

            Map<String, String> reportTableFootnotes;
            switch (retVal[1][0]) {
                case "UPb":
                    reportTableFootnotes = ReportSpecificationsUPb.reportTableFootnotes;
                    break;
                case "UTh_Carb":
                    reportTableFootnotes = ReportSpecificationsUTh_Carb.reportTableFootnotes;
                    break;
                case "UTh_Ign":
                    reportTableFootnotes = ReportSpecificationsUTh_Ign.reportTableFootnotes;
                    break;
                default:
                    reportTableFootnotes = ReportSpecificationsUPb.reportTableFootnotes;
            }

            for (int i = 0;
                    i < footNotesMap.size();
                    i++) {
                String footNote = reportTableFootnotes.get(footNotesMap.get(i)).trim();

                // test for known variables in footnote
                // since lambda235 and 238 appear in same footnote, we first check whether the
                // references are the same so as to avoid repetition
                String lambda238Ref = "";
                String lambda235Ref = "";
                try {
                    lambda238Ref
                            = " (" + ((ValueModelReferenced) sample.getPhysicalConstantsModel()//
                                    .getDatumByName(Lambdas.lambda238.getName())).getReference() + ")";
                    lambda235Ref
                            = " (" + ((ValueModelReferenced) sample.getPhysicalConstantsModel()//
                                    .getDatumByName(Lambdas.lambda235.getName())).getReference() + ")";
                } catch (BadLabDataException badLabDataException) {
                }

                NumberFormat formatter = new DecimalFormat("0.000#####E0");

                String lambda238 = "\u03BB238 = ";
                try {
                    lambda238
                            += formatter.format(sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName())//
                                    .getValue().doubleValue());
                    lambda238 += lambda238Ref;
                } catch (BadLabDataException badLabDataException) {
                }

                String lambda235 = "\u03BB235 = ";
                try {
                    lambda235
                            += formatter.format(sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda235.getName())//
                                    .getValue().doubleValue());
                    lambda235 += lambda235Ref;
                } catch (BadLabDataException badLabDataException) {
                }

                String lambda230 = "\u03BB230 = ";
                try {
                    lambda230
                            += formatter.format(sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda230.getName())//
                                    .getValue().doubleValue());
                    lambda230
                            += " ("
                            + ((ValueModelReferenced) sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda230.getName()))
                                    .getReference()
                            + ")";
                } catch (BadLabDataException badLabDataException) {
                }

                String lambda232 = "\u03BB232 = ";
                try {
                    lambda232
                            += formatter.format(sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda232.getName())
                                    .getValue().doubleValue());
                    lambda232
                            += " ("
                            + ((ValueModelReferenced) sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda232.getName()))
                                    .getReference() //
                            + ")";
                } catch (BadLabDataException badLabDataException) {
                }
                String lambda234 = "\u03BB234 = ";
                try {
                    lambda234
                            += formatter.format(sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda234.getName())
                                    .getValue().doubleValue());
                    lambda234
                            += " ("
                            + ((ValueModelReferenced) sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda234.getName()))
                                    .getReference() //
                            + ")";
                } catch (BadLabDataException badLabDataException) {
                }
                String lambda226 = "\u03BB226 = ";
                try {
                    lambda226
                            += formatter.format(sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda226.getName())
                                    .getValue().doubleValue());
                    lambda226
                            += " ("
                            + ((ValueModelReferenced) sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda226.getName()))
                                    .getReference() //
                            + ")";
                } catch (BadLabDataException badLabDataException) {
                }
                // perform replacement of footnote parameters               
                footNote = footNote.replaceFirst("<lambda226>", lambda226);
                footNote = footNote.replaceFirst("<lambda230>", lambda230);
                footNote = footNote.replaceFirst("<lambda232>", lambda232);
                footNote = footNote.replaceFirst("<lambda234>", lambda234);
                footNote = footNote.replaceFirst("<lambda235>", lambda235);
                footNote = footNote.replaceFirst("<lambda238>", lambda238);
                footNote = footNote.replaceFirst("<ar231_235sample>", activityFootnoteEntry);
                footNote = footNote.replaceFirst("<rTh_Umagma>", thU_MagmaFootnoteEntry);

                // JULY 2017 for UTh footnotes            
                // ASSUMPTION - all fractions have same value for these fields
                if (fractions.get(0) instanceof UThFraction) {
                    if (footNote.contains("<secularOrGravimetric230238>")) {
                        boolean isSecular = ((UThFraction) fractions.get(0)).isSpikeCalibrationR230_238IsSecular();
                        footNote = footNote.replace("<secularOrGravimetric230238>", isSecular
                                ? "secular equilibrium reference material"
                                : "gravimetric reference solution");
                        String refMatName = ((UThFraction) fractions.get(0)).getR230Th_238Ufc_referenceMaterialName();
                        footNote = footNote.replace("<named230Th238Umodel>", refMatName.length() > 0
                                ? " (" + refMatName + ")."
                                : ".");
                    }
                    if (footNote.contains("<secularOrGravimetric234238>")) {
                        boolean isSecular = ((UThFraction) fractions.get(0)).isSpikeCalibrationR234_238IsSecular();
                        footNote = footNote.replace("<secularOrGravimetric234238>", isSecular
                                ? "secular equilibrium reference material"
                                : "gravimetric reference solution");
                        String refMatName = ((UThFraction) fractions.get(0)).getR234U_238Ufc_referenceMaterialName();
                        footNote = footNote.replace("<named234U238Umodel>", refMatName.length() > 0
                                ? " (" + refMatName + ")."
                                : ".");
                    }
                    if (footNote.contains("<dateOfAnalysis>")) {
                        long dateOfAnalysisMS = ((UThFraction) fractions.get(0)).getDateTimeMillisecondsOfAnalysis();
                        footNote = footNote.replace("<dateOfAnalysis>", makeFormattedDate(dateOfAnalysisMS));
                    }
                    // Aug 2019
                    BigDecimal pctLoss = ((UThFraction) fractions.get(0)).getPctLoss().getValue();
                    // calculate constants f234 and f230 
                    double f234 = 1.0 - pctLoss.doubleValue() / 100.0;
                    double f230 = ((f234 - 1.0) * (4.754 * 234 / 4.184 / 230.0) + 1.0 + f234) / 2.0;

                    if (footNote.contains("<pctLoss>")) {
                        footNote = footNote.replace("<pctLoss>", pctLoss.toPlainString());
                    }
                    if (footNote.contains("<234lossgain>")) {
                        footNote = footNote.replace("<234lossgain>", f234 < 1.0 ? "loss" : "gain");
                    }
                    if (footNote.contains("<230lossgain>")) {
                        footNote = footNote.replace("<230lossgain>", f230 < 1.0 ? "loss" : "gain");
                    }

                }

                if (fractions.get(0) instanceof UPbFraction) {
                    //<r207_206c>
                    //<bestDateDivider>
                    // april 2010 specialize footnote for zircons
                    switch (zirconPopulationType) {
                        case 1:
                            footNote = footNote.replaceFirst("<zirconPopulationChoice>", ReportSpecificationsUPb.reportTableFootnotes.get("FN-5zircon"));
                        case 2:
                            footNote = footNote.replaceFirst("<zirconPopulationChoice>", ReportSpecificationsUPb.reportTableFootnotes.get("FN-5mixed"));
                        default:
                            footNote = footNote.replaceFirst("<zirconPopulationChoice>", ReportSpecificationsUPb.reportTableFootnotes.get("FN-5noZircon"));
                    }
                }

                // july 2017
                if (fractions.get(0) instanceof UPbLAICPMSFraction) {
                    footNote = footNote.replaceFirst("<zirconPopulationChoice>", "Measured ratios corrected for fractionation.");
                }

                retVal[6][i] = determineFootNoteLetter(i) + "&" + footNote;

                // nov 2018
                if (fractions.get(0) instanceof UPbLegacyFraction) {
                    if (((UPbLegacyFraction) fractions.get(0)).isSquidLegacy()) {
                        if (footNote.contains("<zirconPopulationChoice>")) {
                            retVal[6][i] = "";
                        }
                    }
                }
            }
        }
        return retVal;

    }

    public default String determineFootNoteLetter(
            int location) {
        return "abcdefghijklmnopqrstuvwxyz".substring(location, location + 1);
    }

    public default void trimColumn(String[][] retVal, int columnCount) {

        // walk the column(s) and find the minimum count of leading spaces
        // and the minimum count of trailing spaces
        int minLeading = 10;
        int minTrailing = 15;
        for (int f = FRACTION_DATA_START_ROW; f
                < retVal.length; f++) {
            if (retVal[f][columnCount] != null) {
                String entry = retVal[f][columnCount];
                int lbCount = 0;
                for (int lb = 0; lb
                        < entry.length(); lb++) {
                    if (entry.substring(lb, lb + 1).equals(" ")) {
                        lbCount++;
                    } else {
                        break;
                    }
                }
                if (lbCount < minLeading) {
                    minLeading = lbCount;
                }

                int tbCount = 0;
                for (int tb = 0; tb
                        < entry.length(); tb++) {
                    if (entry.substring(entry.length() - 1 - tb, entry.length() - tb).equals(" ")) {
                        tbCount++;
                    } else {
                        break;
                    }

                }
                if (tbCount < minTrailing) {
                    minTrailing = tbCount;
                }

            }
        }

        // walk the column and trim the strings
//        for (int f = FRACTION_DATA_START_ROW; f
//                < retVal.length; f++) {
//            if (retVal[f][columnCount] != null) {
//                retVal[f][columnCount] = retVal[f][columnCount].substring(minLeading);
//                retVal[f][columnCount]
//                        = retVal[f][columnCount].substring(//
//                                0, retVal[f][columnCount].length() - minTrailing);
//
//            }
//
//        }
        // july 2019 from Squid
        for (int f = FRACTION_DATA_START_ROW; f
                < retVal.length; f++) {
            if ((retVal[f][columnCount] != null) && (retVal[f][columnCount].length() > 0)) {
                retVal[f][columnCount] = retVal[f][columnCount].substring(minLeading);
                if (retVal[f][columnCount].length() >= minTrailing) {
                    retVal[f][columnCount]
                            = retVal[f][columnCount].substring(//
                                    0, retVal[f][columnCount].length() - minTrailing);
                }
            }
        }

        // walk the column and padleft to meet width of displayname the strings
        // except for fraction name always in first col (0)
        int minWide = 3;
        if (!retVal[3][columnCount].trim().equalsIgnoreCase("Fraction")) {// (columnCount > 2) {

            // July 2017 removed trim() calls here
            int padLeft
                    = Math.max(minWide,//
                            Math.max(retVal[1][columnCount].length(), //
                                    Math.max(retVal[2][columnCount].length(), //
                                            retVal[3][columnCount].length() + retVal[5][columnCount].length() / 2)))//footnote length counts as half
                    - retVal[FRACTION_DATA_START_ROW][columnCount].length();

            if (padLeft > 0) {
                for (int f = FRACTION_DATA_START_ROW; f
                        < retVal.length; f++) {
                    if (retVal[f][columnCount] != null) {
                        try {
                            retVal[f][columnCount]
                                    = // "                 ".substring( 0, padLeft + 0 )//
                                    new String(new char[padLeft]).replace('\0', ' ')//
                                    + retVal[f][columnCount];
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                    }
                }
            }
        } else {
            // fraction column
            int maxWidth = "Fraction".length();
            int minWidth = 100;
            for (int f = FRACTION_DATA_START_ROW; f < retVal.length; f++) {
                if (maxWidth < retVal[f][columnCount].length()) {
                    maxWidth = retVal[f][columnCount].length();
                }
                if (retVal[f][columnCount].length() < minWidth) {
                    minWidth = retVal[f][columnCount].length();
                }
            }

            for (int f2 = FRACTION_DATA_START_ROW; f2 < retVal.length; f2++) {
                if (retVal[f2][columnCount] != null) {
                    retVal[f2][columnCount] += new String(new char[maxWidth - retVal[f2][columnCount].trim().length()]).replace('\0', ' ');
                }
            }
        }

    }

    public default int getCountOfAllColumns() {
        int retVal = 0;

        retVal = getReportCategories().stream().filter((rc) //
                -> (rc != null)).map((rc)//
                -> rc.getCountOfCategoryColumns()).reduce(retVal, Integer::sum);

        return retVal;
    }

    public ArrayList<ReportCategoryInterface> getReportCategories();

    /**
     * @param reportCategories the reportCategories to set
     */
    public void setReportCategories(ArrayList<ReportCategoryInterface> reportCategories);

    public String getReportSettingsXMLSchemaURL();

    /**
     * @return the isotopeStyle
     */
    public String getDefaultReportSpecsType();

    /**
     * @param isotopeStyle the isotopeStyle to set
     */
    public void setDefaultReportSpecsType(String isotopeStyle);

    /**
     * @return the isdefaultReportSpecsType_UPb
     */
    public boolean isdefaultReportSpecsType_UPb();

    public boolean isdefaultReportSpecsType_UTh_Carb();

    public boolean isdefaultReportSpecsType_UTh_Ign();

    /**
     *
     * @param reportSettingsModel the value of reportSettingsModel
     * @param parent the value of parent
     */
    public static void EditReportSettings(ReportSettingsInterface reportSettingsModel, Frame parent) {

        DialogEditor myReportSettingsManager
                = new ReportSettingsManager(parent, true, reportSettingsModel);
        myReportSettingsManager.setSize(455, 685);

        myReportSettingsManager.setVisible(true);
    }
}
