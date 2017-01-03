/*
 * ReportColumn.java
 *
 * Created on September 3, 2008, 9:38 AM
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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

import org.earthtime.dataDictionaries.ReportSpecifications;
import org.earthtime.reports.ReportColumnInterface;

/**
 * A ReportColumn specifies the information shown in a data table column. Each
 * column is part of a single category of columns. Columns can be ordered within
 * the the category and categories can be ordered within the report.
 *
 * @author James F. Bowring
 */
public class ReportColumn implements
        ReportColumnInterface {

    private static final long serialVersionUID = 1474850196549001090L;
    // fields
    private String displayName1;
    private String displayName2;
    private String displayName3;
    private int positionIndex;
    private String units;
    private String retrieveMethodName;
    private String retrieveVariableName;
    private ReportColumnInterface uncertaintyColumn;
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
     * @param amUncertaintyColumn
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

        this.uncertaintyColumn = null;
        this.displayedWithArbitraryDigitCount = false;
        this.countOfSignificantDigits = 2;
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
     * @return
     */
    @Override
    public String getDisplayName1() {
        return displayName1;
    }

    /**
     *
     * @param displayName1
     */
    @Override
    public void setDisplayName1(String displayName1) {
        this.displayName1 = displayName1;
    }

    /**
     *
     * @return
     */
    @Override
    public String getDisplayName2() {
        return displayName2;
    }

    /**
     *
     * @param displayName2
     */
    @Override
    public void setDisplayName2(String displayName2) {
        this.displayName2 = displayName2;
    }

    /**
     *
     * @return
     */
    @Override
    public int getPositionIndex() {
        return positionIndex;
    }

    /**
     *
     * @param positionIndex
     */
    @Override
    public void setPositionIndex(int positionIndex) {
        this.positionIndex = positionIndex;
    }

    /**
     *
     * @return
     */
    @Override
    public String getUnits() {
        return units;
    }

    /**
     *
     * @param units
     */
    @Override
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     *
     * @param xmlCode
     */
    @Override
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
    @Override
    public String getRetrieveMethodName() {
        return retrieveMethodName;
    }

    /**
     *
     * @param retrieveMethodName
     */
    @Override
    public void setRetrieveMethodName(String retrieveMethodName) {
        this.retrieveMethodName = retrieveMethodName;
    }

    /**
     *
     * @return
     */
    @Override
    public String getRetrieveVariableName() {
        return retrieveVariableName;
    }

    /**
     *
     * @param retrieveVariableName
     */
    @Override
    public void setRetrieveVariableName(String retrieveVariableName) {
        this.retrieveVariableName = retrieveVariableName;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportColumnInterface getUncertaintyColumn() {
        return uncertaintyColumn;
    }

    /**
     *
     * @param uncertaintyColumn
     */
    @Override
    public void setUncertaintyColumn(ReportColumnInterface uncertaintyColumn) {
        this.uncertaintyColumn = uncertaintyColumn;
    }

    /**
     *
     * @return
     */
    @Override
    public String getUncertaintyType() {
        return uncertaintyType;
    }

    /**
     *
     * @param uncertaintyType
     */
    @Override
    public void setUncertaintyType(String uncertaintyType) {
        this.uncertaintyType = uncertaintyType;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isDisplayedWithArbitraryDigitCount() {
        return displayedWithArbitraryDigitCount;
    }

    /**
     *
     * @param displayedWithArbitraryDigitCount
     */
    @Override
    public void setDisplayedWithArbitraryDigitCount(boolean displayedWithArbitraryDigitCount) {
        this.displayedWithArbitraryDigitCount = displayedWithArbitraryDigitCount;
    }

    /**
     *
     * @return
     */
    @Override
    public int getCountOfSignificantDigits() {
        return countOfSignificantDigits;
    }

    /**
     *
     * @param countOfSignificantDigits
     */
    @Override
    public void setCountOfSignificantDigits(int countOfSignificantDigits) {
        this.countOfSignificantDigits = countOfSignificantDigits;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isVisible() {
        return visible;
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     *
     * @return
     */
    @Override
    public String getFootnoteSpec() {
        return footnoteSpec;
    }

    /**
     *
     * @param footnoteSpec
     */
    @Override
    public void setFootnoteSpec(String footnoteSpec) {
        this.footnoteSpec = footnoteSpec;
    }

    /**
     * @return the displayName3
     */
    @Override
    public String getDisplayName3() {
        return displayName3;
    }

    /**
     * @param displayName3 the displayName3 to set
     */
    @Override
    public void setDisplayName3(String displayName3) {
        this.displayName3 = displayName3;
    }

    /**
     * @return the alternateDisplayName
     */
    @Override
    public String getAlternateDisplayName() {
        return alternateDisplayName;
    }

    /**
     * @param alternateDisplayName the alternateDisplayName to set
     */
    @Override
    public void setAlternateDisplayName(String alternateDisplayName) {
        this.alternateDisplayName = alternateDisplayName;
    }

    /**
     * @return the needsPb
     */
    @Override
    public boolean isNeedsPb() {
        return needsPb;
    }

    /**
     * @param needsPb the needsPb to set
     */
    @Override
    public void setNeedsPb(boolean needsPb) {
        this.needsPb = needsPb;
    }

    /**
     * @return the needsU
     */
    @Override
    public boolean isNeedsU() {
        return needsU;
    }

    /**
     * @param needsU the needsU to set
     */
    @Override
    public void setNeedsU(boolean needsU) {
        this.needsU = needsU;
    }

    /**
     * @return the legacyData
     */
    @Override
    public boolean isLegacyData() {
        return legacyData;
    }

    /**
     * @param legacyData the legacyData to set
     */
    @Override
    public void setLegacyData(boolean legacyData) {
        this.legacyData = legacyData;
    }

    @Override
    public boolean hasUncertaintyColumn() {
        return uncertaintyColumn != null;
    }

    /**
     * @return the amUncertaintyColumn
     */
    @Override
    public boolean isAmUncertaintyColumn() {
        return amUncertaintyColumn;
    }
}
