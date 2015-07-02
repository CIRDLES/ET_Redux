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

import java.awt.Color;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.reports.ReportColumn;
import org.earthtime.reportViews.ReportListItemI;
import org.earthtime.reports.ReportColumnInterface;

/**
 *
 * @author bowring
 */
public interface ReportCategoryInterface extends ReportListItemI, Serializable {

    public default ReportColumn SetupReportColumn(int index, String[][] specs) {
        String displayName1 = specs[index][0];
        ReportColumn retVal = new ReportColumn(//
                displayName1, //specs[index][0], // displayname1
                specs[index][1], // displayname2
                specs[index][2], // displayname3
                index, // positionIndex
                specs[index][3], // units
                specs[index][4], // retrieveMethodName
                specs[index][5], // retrieveMethodParameterName
                specs[index][6], // uncertaintyType
                specs[index][7], // footnoteSpec
                Boolean.valueOf(specs[index][8]), // visible
                false); // amUncertainty

        retVal.setDisplayedWithArbitraryDigitCount(Boolean.valueOf(specs[index][9]));
        retVal.setCountOfSignificantDigits(Integer.parseInt(specs[index][10]));
        retVal.setAlternateDisplayName(specs[index][12]);
        retVal.setNeedsPb(Boolean.valueOf(specs[index][13]));
        retVal.setNeedsU(Boolean.valueOf(specs[index][14]));
        retVal.setLegacyData(isLegacyData());

        // check for need to create uncertainty column
        ReportColumn uncertaintyCol = null;

        if (!specs[index][6].equals("")) {
            uncertaintyCol = new ReportColumn(//
                    "",
                    specs[index][6].equalsIgnoreCase("PCT") ? "" : "\u00B12\u03C3",
                    specs[index][6].equalsIgnoreCase("PCT") ? "\u00B12\u03C3 %" : "abs",
                    //"third",
                    index,
                    specs[index][3],
                    specs[index][4],
                    specs[index][5],
                    specs[index][6],
                    "",
                    Boolean.valueOf(specs[index][11]),// show uncertainty
                    true); // amUncertainty

            uncertaintyCol.setAlternateDisplayName("");
        }

        retVal.setUncertaintyColumn(uncertaintyCol);

        return retVal;
    }

    /**
     *
     * @return
     */
    public default int getCountOfCategoryColumns() {
        int retVal = 0;
        if (isVisible()) {
            for (int i = 0; i < getCategoryColumns().length; i++) {
                if (getCategoryColumns()[i].isVisible()) {
                    retVal++;
                    if (getCategoryColumns()[i].getUncertaintyColumn() != null) {
                        if (getCategoryColumns()[i].getUncertaintyColumn().isVisible()) {
                            retVal++;
                        }
                    }
                }
            }
        }

        return retVal;
    }

    /**
     *
     * @param columnName
     * @param isVisible
     */
    public default void setVisibleCategoryColumn(String columnName, boolean isVisible) {
        for (int i = 0; i < getCategoryColumns().length; i++) {
            if (getCategoryColumns()[i].getRetrieveVariableName().compareToIgnoreCase(columnName) == 0) {
                getCategoryColumns()[i].setVisible(isVisible);
            }
        }
    }

    /**
     *
     * @return
     */
    public default Map<Integer, ReportColumnInterface> getCategoryColumnOrder() {
        Map<Integer, ReportColumnInterface> retVal = new HashMap<>();

        for (int i = 0; i < getCategoryColumns().length; i++) {
            retVal.put(getCategoryColumns()[i].getPositionIndex(), getCategoryColumns()[i]);
        }

        return retVal;
    }

    /**
     *
     */
    @Override
    void ToggleIsVisible();

    /**
     *
     * @return
     */
    Color getCategoryColor();

    /**
     *
     * @return
     */
    ReportColumnInterface[] getCategoryColumns();

    /**
     *
     * @return
     */
    @Override
    String getDisplayName();

    /**
     *
     * @return
     */
    int getPositionIndex();

    /**
     * @return the legacyData
     */
    boolean isLegacyData();

    /**
     *
     * @return
     */
    @Override
    boolean isVisible();

    /**
     *
     * @param categoryColor
     */
    void setCategoryColor(Color categoryColor);

    /**
     *
     * @param categoryColumns
     */
    void setCategoryColumns(ReportColumn[] categoryColumns);

    /**
     *
     * @param displayName
     */
    void setDisplayName(String displayName);

    /**
     * @param legacyData the legacyData to set
     */
    void setLegacyData(boolean legacyData);

    /**
     *
     * @param positionIndex
     */
    @Override
    void setPositionIndex(int positionIndex);

    /**
     *
     * @param visible
     */
    @Override
    void setVisible(boolean visible);
}
