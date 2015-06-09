/*
 * ReportColumn.java
 *
 * Created on September 3, 2008, 9:38 AM
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

import java.io.Serializable;
import java.lang.reflect.Method;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.ReportSpecifications;
import org.earthtime.fractions.ETFractionInterface;

/**
 * A ReportColumn specifies the information shown in a data table column. Each
 * column is part of a single category of columns. Columns can be ordered within
 * the the category and categories can be ordered within the report.
 *
 * @author James F. Bowring
 */
public class ReportColumn implements
        Comparable<ReportColumn>,
        Serializable,
        ReportListItemI {

    private static final long serialVersionUID = 1474850196549001090L;
    // fields
    private String displayName1;
    private String displayName2;
    private String displayName3;
    private int positionIndex;
    private String units;
    private String retrieveMethodName;
    private String retrieveVariableName;
    private ReportColumn uncertaintyColumn;
    private String uncertaintyType;
    private boolean displayedWithArbitraryDigitCount;
    private int countOfSignificantDigits;
    private boolean visible;
    private String footnoteSpec;
    private String alternateDisplayName;
    private boolean needsPb;
    private boolean needsU;
    private boolean legacyData;
    private boolean amUncertaintyColumn;

    /**
     * Creates a new instance of ReportColumn
     */
    public ReportColumn() {
    }

    /**
     * Creates a new instance of ReportColumn
     *
     * @param displayName1
     * @param displayName2
     * @param footnoteSpec
     * @param positionIndex
     * @param displayName3
     * @param units
     * @param retrieveMethodName
     * @param retrieveVariableName
     * @param uncertaintyType
     * @param visible
     */
    public ReportColumn(
            String displayName1,
            String displayName2,
            String displayName3,
            int positionIndex,
            String units,
            String retrieveMethodName,
            String retrieveVariableName,
            String uncertaintyType,
            String footnoteSpec,
            boolean visible,
            boolean amUncertaintyColumn) {

        this.displayName1 = displayName1;
        this.displayName2 = displayName2;
        this.displayName3 = displayName3;
        this.positionIndex = positionIndex;
        this.units = units;
        this.retrieveMethodName = retrieveMethodName;
        this.retrieveVariableName = retrieveVariableName;
        this.uncertaintyType = uncertaintyType;
        this.footnoteSpec = footnoteSpec;
        this.visible = visible;
        this.amUncertaintyColumn = amUncertaintyColumn;

        this.uncertaintyColumn = null;;
        this.displayedWithArbitraryDigitCount = false;
        this.countOfSignificantDigits = 2;
    }

    /**
     *
     * @param reportColumn
     * @return
     * @throws ClassCastException
     */
    public int compareTo(ReportColumn reportColumn)
            throws ClassCastException {
        String reportColumnFullName
                = ((ReportColumn) reportColumn).getDisplayName();
        return this.getDisplayName().trim().//
                compareToIgnoreCase(reportColumnFullName.trim());
    }

    /**
     *
     * @param reportColumn
     * @return
     */
    @Override
    public boolean equals(Object reportColumn) {
        //check for self-comparison
        if (this == reportColumn) {
            return true;
        }

        if (!(reportColumn instanceof ReportColumn)) {
            return false;
        }

        ReportColumn myReportColumn = (ReportColumn) reportColumn;

        return (this.getDisplayName().trim().
                compareToIgnoreCase(myReportColumn.getDisplayName().trim()) == 0);

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
     * @param fraction
     * @param isNumeric
     * @return
     */
    public String[] getReportRecordByColumnSpec(ETFractionInterface fraction, boolean isNumeric) {
        // returns an entry for the value and one for the uncertainty if it exists
        // there are two possible modes : sigfig and arbitrary
        // if sigfig, the string contains only the sig digits forced to length
        // 20 with the decimal point at position 9 (0-based index) with space fillers
        // if arbitrary, the string is the bigdecimal number with 15 places after the
        // decimal
        // these "raw" strings will be post-processed by the report engine
        String[] retVal = new String[2];

        // default for value column
        retVal[0] = "NOT FOUND";
        // default for uncertainty column
        retVal[1] = "";

        // nov 2009 will use "-" character if lead or uranium required to display
        // feb 2010 oops need to modify if sample is already analyzed as in legacy data
        if (!legacyData && //
                ((needsPb && !((UPbFractionI) fraction).hasMeasuredLead())
                || (needsU && !((UPbFractionI) fraction).hasMeasuredUranium()))) {
            // get default width of column so we can correctly place the "-"
            String defaultContents = FormatNumericStringAlignDecimalPoint("0");
            retVal[0] = defaultContents.replace('0', '-');
            retVal[1] = retVal[0];
        } else {
            if (!getRetrieveMethodName().equals("")) {
                // get fraction field by using reflection
                //String retrieveVariableName = getRetrieveVariableName();
                try {
                    Class<?> fractionClass =//
                            Class.forName(ETFractionInterface.class.getCanonicalName()); // June 2015 Fraction APRIL 2014 ????Class.forName( UPbFraction.class.getCanonicalName() );

                    // this is the case of fractionID, the only string returned
                    if (retrieveVariableName.length() == 0) {
                        try {
                            Method meth
                                    = fractionClass.getMethod(//
                                            getRetrieveMethodName(),
                                            new Class[0]);

                            Object o = meth.invoke(fraction, new Object[0]);

                            retVal[0] = o.toString();
                        } catch (Throwable e) {
                            System.err.println(e);
                        }
                    } else {
                        try {
                            Method meth
                                    = fractionClass.getMethod(//
                                            getRetrieveMethodName(),
                                            new Class[]{String.class});

                            ValueModel vm = (ValueModel) meth.invoke(fraction, new Object[]{retrieveVariableName});

                            // nov 2014 to show when rhos not calculated
                            if (retrieveVariableName.startsWith("rhoR") && vm.getValue().doubleValue() == ReduxConstants.NO_RHO_FLAG) {
                                retVal[0] = "   not calc";
                            } else if (vm.amPositiveAndLessThanTolerance()) {
                                // may 2013 for tiny numbers due to below detection
                                retVal[0] = " bd "; // below detection

                            } else if (vm.hasZeroValue()) {//oct 2014
                                retVal[0] = " - ";

                            } else if (isNumeric) {
                                retVal[0]
                                        = vm.getValueInUnits(getUnits()).toPlainString().trim();
                            } else if (isDisplayedWithArbitraryDigitCount()) {
                                retVal[0]
                                        = ValueModel.formatBigDecimalForPublicationArbitraryMode(//
                                                vm.getValueInUnits(getUnits()),
                                                getCountOfSignificantDigits());
                            } else {
                                // value is in sigfig mode = two flavors
                                // if there is no uncertainty column, then show the value with
                                // normal sigfig formatting
                                // if there is an uncertainty column and it is in arbitrary mode, then
                                // also show value with normal sigfig formatting

                                retVal[0] = ValueModel.formatBigDecimalForPublicationSigDigMode(vm.getValue().movePointRight(ReduxConstants.getUnitConversionMoveCount(getUnits())),//
                                        getCountOfSignificantDigits());

                                // however, if uncertainty column is in sigfig mode, then
                                // use special algorithm to format value per the digits of
                                // the formatted uncertainty column
                                if (getUncertaintyColumn() != null) {
                                    if (!getUncertaintyColumn().isDisplayedWithArbitraryDigitCount()) {
                                        // uncertainty column is in sigfig mode
                                        retVal[0] = vm.formatValueFromTwoSigmaForPublicationSigDigMode(//
                                                getUncertaintyType(), ReduxConstants.getUnitConversionMoveCount(getUnits()),//
                                                getUncertaintyColumn().getCountOfSignificantDigits());
                                    }
                                }
//                            // in either case, we have a sigfig mode for the value
//                            retVal[0] = FormatNumericStringAlignDecimalPoint(retVal[0]);
                            }
                            // in nonnumeric case, we need to format string
                            if (!isNumeric) {
                                retVal[0] = FormatNumericStringAlignDecimalPoint(retVal[0]);
                            }

                            // report 2-sigma uncertainty
                            if (getUncertaintyColumn() != null) {
                                if (getUncertaintyColumn().isVisible()) {
                                    // check for reporting mode

                                    if (vm.amPositiveAndLessThanTolerance()) {
                                        // may 2013 for tiny numbers due to below detection
                                        retVal[1] = " bd "; // below detection

                                    } else if (vm.hasZeroValue()) {//oct 2014
                                        retVal[1] = " - ";

                                    } else if (isNumeric) {
                                        retVal[1]
                                                = vm.getTwoSigma(getUncertaintyType(), getUnits()).toPlainString().trim();
                                    } else if (getUncertaintyColumn().isDisplayedWithArbitraryDigitCount()) {
                                        retVal[1]
                                                = ValueModel.formatBigDecimalForPublicationArbitraryMode(//
                                                        vm.getTwoSigma(getUncertaintyType(), getUnits()),
                                                        getUncertaintyColumn().getCountOfSignificantDigits());
                                    } else {
                                        retVal[1] = vm.formatTwoSigmaForPublicationSigDigMode(//
                                                getUncertaintyType(),
                                                ReduxConstants.getUnitConversionMoveCount(getUnits()),
                                                getUncertaintyColumn().getCountOfSignificantDigits());
                                    }
                                    retVal[1] = FormatNumericStringAlignDecimalPoint(retVal[1]);
                                    // }
                                }
                            }

                        } catch (Throwable e) {
                            System.err.println("problem formatting " + retrieveVariableName + " for " + fraction.getFractionID() + " >> " + e);
                            e.printStackTrace();
                        }
                    }

                } catch (ClassNotFoundException classNotFoundException) {
                }
            }
        }

        return retVal;
    }

    /**
     *
     * @param numericString
     * @return
     */
    public String FormatNumericStringAlignDecimalPoint(String numericString) {
        // precondition: can fit within 123456789.0123456789
        int countOfLeadingDigits = 9;
        int totalStringLength = 25;
        String twentySpaces = "                         ";

        String retVal = "---";

        int indexOfPoint = numericString.indexOf(".");
        if (indexOfPoint == -1) {
            indexOfPoint = numericString.length();
        }

        // nov 2014
        if (numericString.length() > totalStringLength) {
            //retVal = "       Too Large";
            // Jan 2015 refinement
            retVal = numericString.substring(0, totalStringLength - 1);
        } else {
            // pad left
            retVal = twentySpaces.substring(0, Math.abs(countOfLeadingDigits - indexOfPoint)) + numericString;
        }

        // pad right
        try {
            retVal += twentySpaces.substring(0, totalStringLength - retVal.length());
        } catch (Exception e) {
            // Jan 2015 refinement
            retVal = retVal.substring(0, totalStringLength - 1);
            //System.out.println("RETVAL " + retVal);
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public String getDisplayName1() {
        return displayName1;
    }

    /**
     *
     * @param displayName1
     */
    public void setDisplayName1(String displayName1) {
        this.displayName1 = displayName1;
    }

    /**
     *
     * @return
     */
    public String getDisplayName2() {
        return displayName2;
    }

    /**
     *
     * @param displayName2
     */
    public void setDisplayName2(String displayName2) {
        this.displayName2 = displayName2;
    }

    /**
     *
     * @return
     */
    public int getPositionIndex() {
        return positionIndex;
    }

    /**
     *
     * @param positionIndex
     */
    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    /**
     *
     * @return
     */
    public String getUnits() {
        return units;
    }

    /**
     *
     * @return
     */
    public String getUnitsFoxXML() {
        String retVal = ReportSpecifications.unicodeConversionsToXML.get(units);
        if (retVal == null) {
            retVal = units;
        }
        return retVal;
    }

    /**
     *
     * @param units
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     *
     * @param xmlCode
     */
    public void setUnitsFromXML(String xmlCode) {
        units = ReportSpecifications.unicodeConversionsFromXML.get(xmlCode);
        if (units == null) {
            units = xmlCode;
        }
    }

    /**
     *
     * @return
     */
    public String getRetrieveMethodName() {
        return retrieveMethodName;
    }

    /**
     *
     * @param retrieveMethodName
     */
    public void setRetrieveMethodName(String retrieveMethodName) {
        this.retrieveMethodName = retrieveMethodName;
    }

    /**
     *
     * @return
     */
    public String getRetrieveVariableName() {
        return retrieveVariableName;
    }

    /**
     *
     * @param retrieveVariableName
     */
    public void setRetrieveVariableName(String retrieveVariableName) {
        this.retrieveVariableName = retrieveVariableName;
    }

    /**
     *
     * @return
     */
    public ReportColumn getUncertaintyColumn() {
        return uncertaintyColumn;
    }

    /**
     *
     * @param uncertaintyColumn
     */
    public void setUncertaintyColumn(ReportColumn uncertaintyColumn) {
        this.uncertaintyColumn = uncertaintyColumn;
    }

    /**
     *
     * @return
     */
    public String getUncertaintyType() {
        return uncertaintyType;
    }

    /**
     *
     * @param uncertaintyType
     */
    public void setUncertaintyType(String uncertaintyType) {
        this.uncertaintyType = uncertaintyType;
    }

    /**
     *
     * @return
     */
    public boolean isDisplayedWithArbitraryDigitCount() {
        return displayedWithArbitraryDigitCount;
    }

    /**
     *
     * @param displayedWithArbitraryDigitCount
     */
    public void setDisplayedWithArbitraryDigitCount(boolean displayedWithArbitraryDigitCount) {
        this.displayedWithArbitraryDigitCount = displayedWithArbitraryDigitCount;
    }

    /**
     *
     * @return
     */
    public int getCountOfSignificantDigits() {
        return countOfSignificantDigits;
    }

    /**
     *
     * @param countOfSignificantDigits
     */
    public void setCountOfSignificantDigits(int countOfSignificantDigits) {
        this.countOfSignificantDigits = countOfSignificantDigits;
    }

    /**
     *
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     *
     * @return
     */
    public String getFootnoteSpec() {
        return footnoteSpec;
    }

    /**
     *
     * @param footnoteSpec
     */
    public void setFootnoteSpec(String footnoteSpec) {
        this.footnoteSpec = footnoteSpec;
    }

    /**
     *
     * @return
     */
    public String getDisplayName() {
        if (getAlternateDisplayName().equals("")) {
            return getDisplayName1() + getDisplayName2() + getDisplayName3();
        } else {
            return getAlternateDisplayName();
        }
    }

    /**
     *
     */
    public void ToggleIsVisible() {
        setVisible(!isVisible());
    }

    /**
     * @return the displayName3
     */
    public String getDisplayName3() {
        return displayName3;
    }

    /**
     * @param displayName3 the displayName3 to set
     */
    public void setDisplayName3(String displayName3) {
        this.displayName3 = displayName3;
    }

    /**
     * @return the alternateDisplayName
     */
    public String getAlternateDisplayName() {
        return alternateDisplayName;
    }

    /**
     * @param alternateDisplayName the alternateDisplayName to set
     */
    public void setAlternateDisplayName(String alternateDisplayName) {
        this.alternateDisplayName = alternateDisplayName;
    }

    /**
     * @return the needsPb
     */
    public boolean isNeedsPb() {
        return needsPb;
    }

    /**
     * @param needsPb the needsPb to set
     */
    public void setNeedsPb(boolean needsPb) {
        this.needsPb = needsPb;
    }

    /**
     * @return the needsU
     */
    public boolean isNeedsU() {
        return needsU;
    }

    /**
     * @param needsU the needsU to set
     */
    public void setNeedsU(boolean needsU) {
        this.needsU = needsU;
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
    }

    public boolean hasUncertaintyColumn() {
        return uncertaintyColumn != null;
    }

    /**
     * @return the amUncertaintyColumn
     */
    public boolean isAmUncertaintyColumn() {
        return amUncertaintyColumn;
    }
}
