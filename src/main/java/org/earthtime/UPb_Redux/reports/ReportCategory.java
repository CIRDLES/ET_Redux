/*
 * ReportCategory.java
 *
 * Created on September 9, 2008, 3:05 PM
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

import org.earthtime.reports.ReportCategoryInterface;
import java.awt.Color;
import org.earthtime.reports.ReportColumnInterface;

/**
 *
 * @author James F. Bowring
 */
public class ReportCategory implements ReportCategoryInterface {

    private static final long serialVersionUID = 5227409808812622714L;

    // Fields
    private String displayName;
    private int positionIndex;
    private ReportColumnInterface[] categoryColumns;
    private Color categoryColor;
    private boolean visible;
    private boolean legacyData;

    /**
     * Creates a new instance of ReportCategory
     */
    public ReportCategory() {
    }

    /**
     * Creates a new instance of ReportCategory
     *
     * @param displayName
     * @param positionIndex
     * @param reportCategorySpecs
     * @param isVisible
     */
    public ReportCategory(
            String displayName, int positionIndex, String[][] reportCategorySpecs, boolean isVisible) {

        this.displayName = displayName;
        this.positionIndex = positionIndex;
        categoryColumns
                = new ReportColumn[reportCategorySpecs.length];
        for (int i = 0; i < categoryColumns.length; i++) {
            categoryColumns[i]
                    = SetupReportColumn(i, reportCategorySpecs);
        }

        this.categoryColor = Color.white;
        this.visible = isVisible;
        this.legacyData = false;

    }

    /**
     *
     * @return
     */
    @Override
    public ReportColumnInterface[] getCategoryColumns() {
        return categoryColumns;
    }

    /**
     *
     * @param categoryColumns
     */
    @Override
    public void setCategoryColumns(ReportColumn[] categoryColumns) {
        this.categoryColumns = categoryColumns;
    }

    /**
     *
     * @return
     */
    @Override
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @param displayName
     */
    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
    public Color getCategoryColor() {
        return categoryColor;
    }

    /**
     *
     * @param categoryColor
     */
    @Override
    public void setCategoryColor(Color categoryColor) {
        this.categoryColor = categoryColor;
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
     */
    @Override
    public void ToggleIsVisible() {
        this.setVisible(!isVisible());
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
        for (ReportColumnInterface categoryColumn : categoryColumns) {
            categoryColumn.setLegacyData(legacyData);
        }
    }
}
