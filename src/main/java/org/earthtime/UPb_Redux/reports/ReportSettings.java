/*
 * ReportSettings.java
 *
 * Created on September 3, 2008, 9:18 AM
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
package org.earthtime.UPb_Redux.reports;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.ReportSpecifications;
import org.earthtime.exceptions.ETException;
import org.earthtime.reports.ReportSettingsInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring
 */
public class ReportSettings implements
        Comparable<ReportSettings>,
        ReportSettingsInterface,
        XMLSerializationI,
        Serializable,
        ReduxLabDataListElementI {

    private static final long serialVersionUID = 3742875572117123821L;
    private transient String reportSettingsXMLSchemaURL;
    /**
     * Each time the report specifications evolve in DataDictionary, this
     * version number is advanced so that any existing analysis will update its
     * report models upon opening inU-Pb_Redux.
     */
    public final static transient int FRACTION_DATA_START_ROW = 8;
    private static transient int CURRENT_VERSION_REPORT_SETTINGS = 276;
    private static transient String activityFootnoteEntry = "";
    private static transient String thU_MagmaFootnoteEntry = "";
    // april 2010
    // no zircons in report = 0; all zircons = 1; mixture = 2
    private static transient int zirconPopulationType;
    // Fields
    private String name;
    private int version;
    private ReportCategory fractionCategory;
    private ReportCategory compositionCategory;
    private ReportCategory isotopicRatiosCategory;
    private ReportCategory isotopicRatiosPbcCorrCategory;
    private ReportCategory datesCategory;
    private ReportCategory datesPbcCorrCategory;
    private ReportCategory rhosCategory;
    private ReportCategory traceElementsCategory;
    private ReportCategory fractionCategory2;
    private ArrayList<ReportCategory> reportCategories;
    private String reportSettingsComment;
    private boolean legacyData;

    /**
     * Creates a new instance of ReportSettings
     */
    public ReportSettings() {
        this(ReduxConstants.NONE);
    }

    /**
     * Creates a new instance of ReportSettings
     *
     * @param name
     */
    public ReportSettings(String name) {

        this.name = name;
        this.version = CURRENT_VERSION_REPORT_SETTINGS;

        this.reportSettingsComment = "";

        this.fractionCategory
                = new ReportCategory(//
                        //
                        "Fraction",
                        0,
                        ReportSpecifications.ReportCategory_Fraction, true);

        this.datesCategory
                = new ReportCategory(//
                        //
                        "Dates",
                        1,
                        ReportSpecifications.ReportCategory_Dates, true);

        this.datesPbcCorrCategory
                = new ReportCategory(//
                        //
                        "PbcCorr Dates",
                        2,//7,
                        ReportSpecifications.ReportCategory_PbcCorrDates, false);

        this.compositionCategory
                = new ReportCategory(//
                        //
                        "Composition",
                        3,//2,
                        ReportSpecifications.ReportCategory_Composition, true);

        this.isotopicRatiosCategory
                = new ReportCategory(//
                        //
                        "Isotopic Ratios",
                        4,//3,
                        ReportSpecifications.ReportCategory_IsotopicRatios, true);

        this.isotopicRatiosPbcCorrCategory
                = new ReportCategory(//
                        //
                        "PbcCorr Isotopic Ratios",
                        5,//6,
                        ReportSpecifications.ReportCategory_PbcCorrIsotopicRatios, false);

        this.rhosCategory
                = new ReportCategory(//
                        //
                        "Correlation Coefficients",
                        6,//4,
                        ReportSpecifications.ReportCategory_CorrelationCoefficients, true);

        this.traceElementsCategory
                = new ReportCategory(//
                        //
                        "Trace Elements",
                        7,//5,
                        ReportSpecifications.ReportCategory_TraceElements, false);

        this.fractionCategory2
                = new ReportCategory(//
                        //
                        "Fraction",
                        8,
                        ReportSpecifications.ReportCategory_Fraction2, true);

        legacyData = false;

        assembleReportCategories();

    }

    public void assembleReportCategories() {
        this.reportCategories = new ArrayList<>();
        reportCategories.add(fractionCategory);
        reportCategories.add(datesCategory);
        reportCategories.add(datesPbcCorrCategory);
        reportCategories.add(compositionCategory);
        reportCategories.add(isotopicRatiosCategory);
        reportCategories.add(isotopicRatiosPbcCorrCategory);
        reportCategories.add(rhosCategory);
        reportCategories.add(traceElementsCategory);
        reportCategories.add(fractionCategory2);
    }

    /**
     *
     * @return
     */
    public static ReportSettings EARTHTIMEReportSettings() {

        ReportSettings EARTHTIME
                = new ReportSettings("EARTHTIME");

        return EARTHTIME;
    }

    public String[][] reportActiveFractionsByNumberStyle(final SampleInterface sample, boolean numberStyleIsNumeric) {
        Vector<Fraction> fractions = sample.getFractionsActive();

        return reportFractionsByNumberStyle(fractions, sample, numberStyleIsNumeric);
    }

    /**
     *
     * @param sample
     * @param numberStyleIsNumeric
     * @return
     */
    public String[][] reportRejectedFractionsByNumberStyle(final SampleInterface sample, boolean numberStyleIsNumeric) {
        Vector<Fraction> fractions = sample.getFractionsRejected();

        return reportFractionsByNumberStyle(fractions, sample, numberStyleIsNumeric);
    }

    /**
     *
     * @param sample
     * @param uPbFractions
     * @param numberStyleIsNumeric
     * @return
     */
    public String[][] reportActiveAliquotFractionsByNumberStyle(final SampleInterface sample, Vector<Fraction> fractions, boolean numberStyleIsNumeric) {

        return reportFractionsByNumberStyle(fractions, sample, numberStyleIsNumeric);
    }

    private String[][] reportFractionsByNumberStyle(//
            Vector<Fraction> fractions,
            final SampleInterface sample,
            boolean numberStyleIsNumeric) {

        // compatibility repair april 2012
        if (fractionCategory2 == null) {
            setFractionCategory2(new ReportCategory(//
                    //
                    "Fraction",
                    8,//oct 2014 was 5 before traceelements and pc corrections
                    ReportSpecifications.ReportCategory_Fraction2, true));

        }

        // the first six (FRACTION_DATA_START_ROW) rows are provided for naming and formats
        String[][] retVal
                = new String[fractions.size() + FRACTION_DATA_START_ROW][];
        // column 0 will contain true for included fractions and false for rejected fractions
        // column 1 will contain aliquot name

        // special case oct 2009 to see if we force the display of <ar231_235Pa> and Th_Umagma columns
        if (fractions.size() > 0) {
            // check whether all values are equal in displayed fractions

            // force to not visible
            getCompositionCategory().setVisibleCategoryColumn(AnalysisMeasures.ar231_235sample.getName(), false);
            getCompositionCategory().setVisibleCategoryColumn(AnalysisMeasures.rTh_Umagma.getName(), false);

            // get first activityValue and first Th_Umagma value and set standard footnote entries
            BigDecimal savedActivityValue = //
                    fractions.get(0).getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()).getValue();
            BigDecimal savedMagmaValue = //
                    fractions.get(0).getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()).getValue();

            activityFootnoteEntry = "= " + savedActivityValue.toString();
            thU_MagmaFootnoteEntry = "= " + savedMagmaValue.toString();

            // modified april 2010 to account for zircon population
            int zirconCount = 0;
            int fractionCount = 0;
            for (Fraction f : fractions) {
                if (!((UPbFractionI) f).isRejected()) {

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
                    if (f.isZircon()) {
                        zirconCount++;
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

        // a special case oct 2009 to decide if units of dates is Ma or ka or auto
        // repeated oct 2014 to handle Pbc corrected dates category also
        if (fractions.size() > 0) {
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
                for (ReportColumn rc : getDatesCategory().getCategoryColumns()) {
                    if (!rc.getUnits().equalsIgnoreCase("")) {
                        rc.setUnits(currentDateUnit);
                    }
                }

                if (isAuto) {
                    // let's find out
                    BigDecimal threshold = new BigDecimal(1000000);
                    for (Fraction f : fractions) {
                        if (!((UPbFractionI) f).isRejected()) {
                            BigDecimal date206_238Value = f.getRadiogenicIsotopeDateByName(RadDates.age206_238r).getValue();
                            if (date206_238Value.compareTo(threshold) > 0) {
                                // we have Ma when any value is greater than threshold
                                getDatesCategory().setDisplayName("Dates (Ma)");
                                // now set units correctly
                                for (ReportColumn rc : getDatesCategory().getCategoryColumns()) {
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
                String currentDateUnit = getDatesPbcCorrCategory().getCategoryColumns()[0].getUnits();
                boolean isAuto = false;
                // the default is ka, though it will usually be overwritten by Ma
                if (currentDateUnit.equalsIgnoreCase("Auto")) {
                    currentDateUnit = "ka";
                    isAuto = true;
                }
                getDatesPbcCorrCategory().setDisplayName("PbcCorr Dates (" + currentDateUnit + ")");
                for (ReportColumn rc : getDatesPbcCorrCategory().getCategoryColumns()) {
                    if (!rc.getUnits().equalsIgnoreCase("")) {
                        rc.setUnits(currentDateUnit);
                    }
                }

                if (isAuto) {
                    // let's find out
                    BigDecimal threshold = new BigDecimal(1000000);
                    for (Fraction f : fractions) {
                        if (!((UPbFractionI) f).isRejected()) {
                            BigDecimal date206_238Value = f.getRadiogenicIsotopeDateByName(RadDates.age206_238_PbcCorr).getValue();
                            if (date206_238Value.compareTo(threshold) > 0) {
                                // we have Ma when any value is greater than threshold
                                getDatesPbcCorrCategory().setDisplayName("PbcCorr Dates (Ma)");
                                // now set units correctly
                                for (ReportColumn rc : getDatesPbcCorrCategory().getCategoryColumns()) {
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
        }

        // end of special case for units of date
        int countOfAllColumns = getCountOfAllColumns() + 2;

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

        int columnCount = 2;

        int footNoteCounter = 0;
        ArrayList<String> footNotesMap = new ArrayList<String>();

        Map<Integer, ReportCategory> categories = getReportCategoriesInOrder();

        for (int c = 0; c < categories.size(); c++) {
            try {
                if (categories.get(c).isVisible()) {
                    Map<Integer, ReportColumn> cat = categories.get(c).getCategoryColumnOrder();

                    for (int col = 0; col < cat.size(); col++) {
                        int colIncrement = 1;
                        ReportColumn myCol = cat.get(col);

                        if (myCol.isVisible()) {
                            // record column headings
                            retVal[0][columnCount] = categories.get(c).getDisplayName();
                            retVal[1][columnCount] = myCol.getDisplayName1();//.replace( "\u03c1", "" ); // remove greek rho
                            retVal[2][columnCount] = myCol.getDisplayName2();
                            retVal[3][columnCount] = myCol.getDisplayName3();
                            // modified oct 2009 for date units
                            // and oct 2014 for Pbc corrected
                            if (!myCol.getUnits().equals("")
                                    && (!categories.get(c).getDisplayName().contains("Dates"))) {//.startsWith("Dates"))) {
                                retVal[3][columnCount] += "(" + myCol.getUnits() + ")";
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
                            for (Fraction f : fractions) {

                                // test for included fraction on first data pass col=2==>fractionID
                                if (columnCount == 2) {
                                    if (((UPbFractionI) f).isRejected()) {
                                        retVal[fractionRowCount][0] = "false";
                                    } else {
                                        retVal[fractionRowCount][0] = "true";
                                    }

                                    retVal[fractionRowCount][1] = //
                                            sample.getAliquotByNumber(((UPbFractionI) f).getAliquotNumber()).getAliquotName();
                                }

                                // field contains the Value in field[0]
                                //and the uncertainty in field[1] if it exists/isvisible
                                String[] field = myCol.getReportRecordByColumnSpec(f, numberStyleIsNumeric);

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
        // write footNotesMap and prepend superscript letter with "&" delimiter
        // write full text of footnote with variables replaced with values
        for (int i = 0;
                i < footNotesMap.size();
                i++) {
            String footNote = ReportSpecifications.reportTableFootnotes.get(footNotesMap.get(i)).trim();

            // test for known variables in footnote
            // since lambda235 and 238 appear in same footnote, we first check whether the
            // references are the same so as to aoid repetition
            String lambda238Ref = "";
            String lambda235Ref = "";
            try {
                lambda238Ref = //
                        " (" + ((ValueModelReferenced) sample.getPhysicalConstantsModel()//
                        .getDatumByName(Lambdas.lambda238.getName())).getReference() + ")";
                lambda235Ref = //
                        " (" + ((ValueModelReferenced) sample.getPhysicalConstantsModel()//
                        .getDatumByName(Lambdas.lambda235.getName())).getReference() + ")";
            } catch (BadLabDataException badLabDataException) {
            }

            if ((lambda235Ref.trim().equalsIgnoreCase(lambda238Ref.trim()))) {
                lambda238Ref = ""; // they appear in order 238, 235
            }

            String lambda238 = "\u03BB238 = ";
            try {
                lambda238 += //
                        sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName())//
                        .getValue().toString();
                lambda238 += lambda238Ref;
            } catch (BadLabDataException badLabDataException) {
            }

            String lambda235 = "\u03BB235 = ";
            try {
                lambda235 += //
                        sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda235.getName())//
                        .getValue().toString();
                lambda235 += lambda235Ref;
            } catch (BadLabDataException badLabDataException) {
            }

            String lambda232 = "\u03BB232 = ";
            try {
                lambda232 += //
                        sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda232.getName())//
                        .getValue().toString();
                lambda232 += //
                        " (" //
                        + ((ValueModelReferenced) sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda232.getName()))//
                        .getReference() //
                        + ")";
            } catch (BadLabDataException badLabDataException) {
            }

            // perform replacement of footnote parameters
            footNote = footNote.replaceFirst("<lambda238>", lambda238);
            footNote = footNote.replaceFirst("<lambda235>", lambda235);
            footNote = footNote.replaceFirst("<lambda232>", lambda232);
            footNote = footNote.replaceFirst("<ar231_235sample>", activityFootnoteEntry);
            footNote = footNote.replaceFirst("<rTh_Umagma>", thU_MagmaFootnoteEntry);
            //<r207_206c>
            //<bestDateDivider>

            // april 2010 specialize footnote for zircons
            switch (zirconPopulationType) {
                case 1:
                    footNote = footNote.replaceFirst("<zirconPopulationChoice>", ReportSpecifications.reportTableFootnotes.get("FN-5zircon"));
                case 2:
                    footNote = footNote.replaceFirst("<zirconPopulationChoice>", ReportSpecifications.reportTableFootnotes.get("FN-5mixed"));
                default:
                    footNote = footNote.replaceFirst("<zirconPopulationChoice>", ReportSpecifications.reportTableFootnotes.get("FN-5noZircon"));
            }

            retVal[6][i] = determineFootNoteLetter(i) + "&" + footNote;
        }

        return retVal;

    }

    private String determineFootNoteLetter(
            int location) {
        return "abcdefghijklmnopqrstuvwxyz".substring(location, location + 1);
    }

    private void trimColumn(String[][] retVal, int columnCount) {

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
        for (int f = FRACTION_DATA_START_ROW; f
                < retVal.length; f++) {
            if (retVal[f][columnCount] != null) {
                retVal[f][columnCount] = retVal[f][columnCount].substring(minLeading);
                retVal[f][columnCount] = //
                        retVal[f][columnCount].substring(//
                                0, retVal[f][columnCount].length() - minTrailing);

            }

        }

        // walk the column and padleft to meet width of displayname the strings
        // except for fraction name always in first col (0)
        int minWide = 3;
        if (!retVal[3][columnCount].trim().equalsIgnoreCase("Fraction")) {// (columnCount > 2) {

            int padLeft
                    = Math.max(minWide,//
                            Math.max(retVal[1][columnCount].trim().length(), //
                                    Math.max(retVal[2][columnCount].trim().length(), //
                                            retVal[3][columnCount].trim().length() + retVal[5][columnCount].trim().length() / 2)))//footnote length counts as half
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

    private int getCountOfAllColumns() {
        int retVal = 0;

        retVal = reportCategories.stream().filter((rc) //
                -> (rc != null)).map((rc)//
                        -> rc.getCountOfCategoryColumns()).reduce(retVal, Integer::sum);

        return retVal;
    }

    /**
     *
     * @return
     */
    public Map<Integer, ReportCategory> getReportCategoriesInOrder() {
        Map<Integer, ReportCategory> retVal = new HashMap<>();

        reportCategories.stream().filter((rc) //
                -> (rc != null)).forEach((rc) -> {
            retVal.put(rc.getPositionIndex(), rc);
        });

        return retVal;
    }

//  accessors
    /**
     *
     * @return
     */
    @Override
    public String getReduxLabDataElementName() {
        return getNameAndVersion();
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int getVersion() {
        return version;
    }

    /**
     *
     * @param version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     *
     * @return
     */
    public String getNameAndVersion() {
        return getName().trim() + " v." + getVersion();
    }

    /**
     *
     * @return
     */
    public ReportCategory getFractionCategory() {
        return fractionCategory;
    }

    /**
     *
     * @param fractionCategory
     */
    public void setFractionCategory(ReportCategory fractionCategory) {
        this.fractionCategory = fractionCategory;
    }

    /**
     *
     * @return
     */
    public ReportCategory getCompositionCategory() {
        return compositionCategory;
    }

    /**
     *
     * @param compositionCategory
     */
    public void setCompositionCategory(ReportCategory compositionCategory) {
        this.compositionCategory = compositionCategory;
    }

    /**
     *
     * @return
     */
    public ReportCategory getIsotopicRatiosCategory() {
        return isotopicRatiosCategory;
    }

    /**
     *
     * @param isotopicRatiosCategory
     */
    public void setIsotopicRatiosCategory(ReportCategory isotopicRatiosCategory) {
        this.isotopicRatiosCategory = isotopicRatiosCategory;
    }

    /**
     *
     * @return
     */
    public ReportCategory getDatesCategory() {
        return datesCategory;
    }

    /**
     *
     * @param datesCategory
     */
    public void setDatesCategory(ReportCategory datesCategory) {
        this.datesCategory = datesCategory;
    }

    /**
     *
     * @param reportSettingsModel
     * @return
     * @throws ClassCastException
     */
    public int compareTo(ReportSettings reportSettingsModel)
            throws ClassCastException {
        String reportSettingsModelNameAndVersion
                = ((ReportSettings) reportSettingsModel).getNameAndVersion();
        return this.getNameAndVersion().trim().//
                compareToIgnoreCase(reportSettingsModelNameAndVersion.trim());
    }

    /**
     *
     * @param reportSettingsModel
     * @return
     */
    @Override
    public boolean equals(Object reportSettingsModel) {
        //check for self-comparison
        if (this == reportSettingsModel) {
            return true;
        }

        if (!(reportSettingsModel instanceof ReportSettings)) {
            return false;
        }

        ReportSettings myReportSettings = (ReportSettings) reportSettingsModel;

        return (this.getNameAndVersion().trim().
                compareToIgnoreCase(myReportSettings.getNameAndVersion().trim()) == 0);

    }

// http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     *
     * @return
     */
    @Override
    public int hashCode() {

        return 0;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportSettings clone() {
        ReportSettings reportSettingsModel = null;

        // write out the settings
        serializeXMLObject("TEMPreportSettings.xml");

        // read them back in
        try {
            reportSettingsModel = (ReportSettings) readXMLObject("TEMPreportSettings.xml", true);
        } catch (FileNotFoundException | ETException | BadOrMissingXMLSchemaException fileNotFoundException) {
        }

        return reportSettingsModel;
    }

// XML Serialization
    /**
     *
     * @return
     */
    public XStream getXStreamWriter() {
        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @return
     */
    public XStream getXStreamReader() {

        XStream xstream = new XStream(new DomDriver());

        customizeXstream(xstream);

        return xstream;
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
    public void customizeXstream(XStream xstream) {

        xstream.registerConverter(new ReportSettingsXMLConverter());
        xstream.registerConverter(new ReportCategoryXMLConverter());
        xstream.registerConverter(new ReportColumnXMLConverter());

        xstream.alias("ReportSettings", ReportSettings.class);
        xstream.alias("ReportCategory", ReportCategory.class);
        xstream.alias("ReportColumn", ReportColumn.class);

        setClassXMLSchemaURL();
    }

    /**
     * sets the XML schema. Initializes <code>UPbReduxConfigurator</code> and
     * sets the location of the XML Schema
     *
     * @pre <code>UPbReduxConfigurator</code> class is available
     * @post <code>TracerXMLSchemaURL</code> will be set
     */
    public void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        reportSettingsXMLSchemaURL
                = myConfigurator.getResourceURI("URI_ReportSettingsXMLSchema");
    }

    /**
     *
     * @param filename
     */
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("ReportSettings",
                "ReportSettings  "//
                + ReduxConstants.XML_ResourceHeader//
                + reportSettingsXMLSchemaURL//
                + "\"");

        try {
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);

            // Write xml to file
            out.println(xml);
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        ReportSettings myReportSettings = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode = !doValidate;

            XStream xstream = getXStreamReader();

            isValidOrAirplaneMode = URIHelper.validateXML(reader, filename, reportSettingsXMLSchemaURL);

            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myReportSettings = (ReportSettings) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

////                System.out.println("This is your ReportSettings that was just read successfully:\n");
////
////                String xml2 = getXStreamWriter().toXML(myReportSettings);
////
////                System.out.println(xml2);
////                System.out.flush();
            } else {
                throw new ETException(null, "XML data file does not conform to schema.");
            }
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return myReportSettings;
    }

    /**
     *
     * @return
     */
    public String getReportSettingsComment() {
        return reportSettingsComment;
    }

    /**
     *
     * @param reportSettingsComment
     */
    public void setReportSettingsComment(String reportSettingsComment) {
        this.reportSettingsComment = reportSettingsComment;
    }

    /**
     * @return the rhosCategory
     */
    public ReportCategory getRhosCategory() {
        return rhosCategory;
    }

    /**
     * @param rhosCategory the rhosCategory to set
     */
    public void setRhosCategory(ReportCategory rhosCategory) {
        this.rhosCategory = rhosCategory;
    }

    /**
     * @return the fractionCategory2
     */
    public ReportCategory getFractionCategory2() {
        return fractionCategory2;
    }

    /**
     * @param fractionCategory2 the fractionCategory2 to set
     */
    public void setFractionCategory2(ReportCategory fractionCategory2) {
        this.fractionCategory2 = fractionCategory2;
    }

    /**
     *
     * @return
     */
    public boolean isOutOfDate() {
        return this.version < CURRENT_VERSION_REPORT_SETTINGS;
    }

    /**
     * @return the legacyData
     */
    public boolean isLegacyData() {
        return legacyData;
    }

    /**
     * @param legacyData the legacyData to set
     */
    public void setLegacyData(boolean legacyData) {
        this.legacyData = legacyData;

        reportCategories.stream().filter((rc) -> (rc != null)).forEach((rc) -> {
            rc.setLegacyData(legacyData);
        });
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ReportSettings reportSettings
                = new ReportSettings("Test ReportSettings");
        String testFileName = "ReportSettingsTEST.xml";

        reportSettings.serializeXMLObject(testFileName);
        reportSettings.readXMLObject(testFileName, true);

    }

    /**
     *
     */
    @Override
    public void removeSelf() {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * @return the traceElementsCategory
     */
    public ReportCategory getTraceElementsCategory() {
        return traceElementsCategory;
    }

    /**
     * @param traceElementsCategory the traceElementsCategory to set
     */
    public void setTraceElementsCategory(ReportCategory traceElementsCategory) {
        this.traceElementsCategory = traceElementsCategory;
    }

    /**
     * @return the isotopicRatiosPbcCorrCategory
     */
    public ReportCategory getIsotopicRatiosPbcCorrCategory() {
        return isotopicRatiosPbcCorrCategory;
    }

    /**
     * @param isotopicRatiosPbcCorrCategory the isotopicRatiosPbcCorrCategory to
     * set
     */
    public void setIsotopicRatiosPbcCorrCategory(ReportCategory isotopicRatiosPbcCorrCategory) {
        this.isotopicRatiosPbcCorrCategory = isotopicRatiosPbcCorrCategory;
    }

    /**
     * @return the datesPbcCorrCategory
     */
    public ReportCategory getDatesPbcCorrCategory() {
        return datesPbcCorrCategory;
    }

    /**
     * @param datesPbcCorrCategory the datesPbcCorrCategory to set
     */
    public void setDatesPbcCorrCategory(ReportCategory datesPbcCorrCategory) {
        this.datesPbcCorrCategory = datesPbcCorrCategory;
    }
}
