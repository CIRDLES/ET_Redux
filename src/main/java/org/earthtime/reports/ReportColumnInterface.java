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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.ReportSpecifications;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.reportViews.ReportListItemI;

/**
 *
 * @author bowring
 */
public interface ReportColumnInterface extends Comparable<ReportColumnInterface>, ReportListItemI, Serializable {

    /**
     *
     * @param reportColumn
     * @return
     * @throws ClassCastException
     */
    @Override
    public default int compareTo(ReportColumnInterface reportColumn)
            throws ClassCastException {
        String reportColumnFullName
                = ((ReportColumnInterface) reportColumn).getDisplayName();
        return this.getDisplayName().trim().//
                compareToIgnoreCase(reportColumnFullName.trim());
    }

    /**
     *
     * @return
     */
    @Override
    public default String getDisplayName() {
        if (getAlternateDisplayName().equals("")) {
            return getDisplayName1() + getDisplayName2() + getDisplayName3();
        } else {
            return getAlternateDisplayName();
        }
    }

    /**
     *
     */
    @Override
    public default void ToggleIsVisible() {
        setVisible(!isVisible());
    }

    /**
     *
     * @param reportColumn
     * @return
     */
    @Override
    boolean equals(Object reportColumn);

    /**
     * @return the alternateDisplayName
     */
    String getAlternateDisplayName();

    /**
     *
     * @return
     */
    int getCountOfSignificantDigits();

    /**
     *
     * @return
     */
    String getDisplayName1();

    /**
     *
     * @return
     */
    String getDisplayName2();

    /**
     * @return the displayName3
     */
    String getDisplayName3();

    /**
     *
     * @return
     */
    String getFootnoteSpec();

    /**
     *
     * @return
     */
    int getPositionIndex();

    /**
     *
     * @return
     */
    String getRetrieveMethodName();

    /**
     *
     * @return
     */
    String getRetrieveVariableName();

    /**
     *
     * @return
     */
    ReportColumnInterface getUncertaintyColumn();

    /**
     *
     * @return
     */
    String getUncertaintyType();

    /**
     *
     * @return
     */
    String getUnits();

    /**
     *
     * @return
     */
    public default String getUnitsFoxXML() {
        String retVal = ReportSpecifications.unicodeConversionsToXML.get(getUnits());
        if (retVal == null) {
            retVal = getUnits();
        }
        return retVal;
    }

    boolean hasUncertaintyColumn();

    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     *
     * @return
     */
    int hashCode();

    /**
     * @return the amUncertaintyColumn
     */
    boolean isAmUncertaintyColumn();

    /**
     *
     * @return
     */
    boolean isDisplayedWithArbitraryDigitCount();

    /**
     * @return the legacyData
     */
    boolean isLegacyData();

    /**
     * @return the needsPb
     */
    boolean isNeedsPb();

    /**
     * @return the needsU
     */
    boolean isNeedsU();

    /**
     *
     * @return
     */
    @Override
    boolean isVisible();

    /**
     * @param alternateDisplayName the alternateDisplayName to set
     */
    void setAlternateDisplayName(String alternateDisplayName);

    /**
     *
     * @param countOfSignificantDigits
     */
    void setCountOfSignificantDigits(int countOfSignificantDigits);

    /**
     *
     * @param displayName1
     */
    void setDisplayName1(String displayName1);

    /**
     *
     * @param displayName2
     */
    void setDisplayName2(String displayName2);

    /**
     * @param displayName3 the displayName3 to set
     */
    void setDisplayName3(String displayName3);

    /**
     *
     * @param displayedWithArbitraryDigitCount
     */
    void setDisplayedWithArbitraryDigitCount(boolean displayedWithArbitraryDigitCount);

    /**
     *
     * @param footnoteSpec
     */
    void setFootnoteSpec(String footnoteSpec);

    /**
     * @param legacyData the legacyData to set
     */
    void setLegacyData(boolean legacyData);

    /**
     * @param needsPb the needsPb to set
     */
    void setNeedsPb(boolean needsPb);

    /**
     * @param needsU the needsU to set
     */
    void setNeedsU(boolean needsU);

    /**
     *
     * @param positionIndex
     */
    @Override
    void setPositionIndex(int positionIndex);

    /**
     *
     * @param retrieveMethodName
     */
    void setRetrieveMethodName(String retrieveMethodName);

    /**
     *
     * @param retrieveVariableName
     */
    void setRetrieveVariableName(String retrieveVariableName);

    /**
     *
     * @param uncertaintyColumn
     */
    void setUncertaintyColumn(ReportColumnInterface uncertaintyColumn);

    /**
     *
     * @param uncertaintyType
     */
    void setUncertaintyType(String uncertaintyType);

    /**
     *
     * @param units
     */
    void setUnits(String units);

    /**
     *
     * @param xmlCode
     */
    void setUnitsFromXML(String xmlCode);

    /**
     *
     * @param visible
     */
    @Override
    void setVisible(boolean visible);

    /**
     *
     * @param numericString
     * @return
     */
    public static String FormatNumericStringAlignDecimalPoint(String numericString) {
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
     * @param fraction
     * @param isNumeric
     * @return
     */
    public default String[] getReportRecordByColumnSpec(ETFractionInterface fraction, boolean isNumeric) {
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
        if (!isLegacyData() && (fraction instanceof UPbFractionI) &&//
                ((isNeedsPb() && !((UPbFractionI) fraction).hasMeasuredLead())
                || (isNeedsU() && !((UPbFractionI) fraction).hasMeasuredUranium()))) {
            // get default width of column so we can correctly place the "-"
            String defaultContents = ReportColumnInterface.FormatNumericStringAlignDecimalPoint("0");
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
                    if (getRetrieveVariableName().length() == 0) {
                        try {
                            Method meth
                                    = fractionClass.getMethod(//
                                            getRetrieveMethodName(),
                                            new Class[0]);

                            Object o = meth.invoke(fraction, new Object[0]);

                            retVal[0] = o.toString();
                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            System.err.println(e);
                        }
                    } else {
                        try {
                            Method meth
                                    = fractionClass.getMethod(//
                                            getRetrieveMethodName(),
                                            new Class[]{String.class});

                            ValueModel vm = (ValueModel) meth.invoke(fraction, new Object[]{getRetrieveVariableName()});

                            // nov 2014 to show when rhos not calculated
                            if (getRetrieveVariableName().startsWith("rhoR") && vm.getValue().doubleValue() == ReduxConstants.NO_RHO_FLAG) {
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
                                retVal[0] = ReportColumnInterface.FormatNumericStringAlignDecimalPoint(retVal[0]);
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
                                    retVal[1] = ReportColumnInterface.FormatNumericStringAlignDecimalPoint(retVal[1]);
                                    // }
                                }
                            }

                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                            System.err.println("problem formatting " + getRetrieveVariableName() + " for " + fraction.getFractionID() + " >> " + e);
                        }
                    }

                } catch (ClassNotFoundException classNotFoundException) {
                }
            }
        }

        return retVal;
    }

}
