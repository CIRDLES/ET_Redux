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
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.dataDictionaries.ReportSpecifications;
import org.earthtime.reports.ReportCategoryInterface;
import org.earthtime.reports.ReportSettingsInterface;

/**
 *
 * @author James F. Bowring
 */
public class ReportSettings implements
        ReportSettingsInterface {

    private static final long serialVersionUID = 3742875572117123821L;
    private transient String reportSettingsXMLSchemaURL;
    /**
     * Each time the report specifications evolve in DataDictionary, this
     * version number is advanced so that any existing analysis will update its
     * report models upon opening in ET_Redux.
     */
    private static transient int CURRENT_VERSION_REPORT_SETTINGS = 298;

    // Fields
    private String name;
    private int version;
    private String isotopeStyle;
    private ReportCategoryInterface fractionCategory;
    private ReportCategoryInterface compositionCategory;
    private ReportCategoryInterface isotopicRatiosCategory;
    private ReportCategoryInterface isotopicRatiosPbcCorrCategory;
    private ReportCategoryInterface datesCategory;
    private ReportCategoryInterface datesPbcCorrCategory;
    private ReportCategoryInterface rhosCategory;
    private ReportCategoryInterface traceElementsCategory;
    private ReportCategoryInterface fractionCategory2;
    protected ArrayList<ReportCategoryInterface> reportCategories;
    private String reportSettingsComment;
    private boolean legacyData;

    /**
     * Creates a new instance of ReportSettings
     */
    public ReportSettings() {
        this(ReduxConstants.NONE, "UPb");
    }

    /**
     * Creates a new instance of ReportSettings
     *
     * @param name
     * @param isotopeStyle the value of isotopeStyle
     */
    public ReportSettings(String name, String isotopeStyle) {

        this.name = name;
        this.version = CURRENT_VERSION_REPORT_SETTINGS;
        this.isotopeStyle = isotopeStyle;
        boolean isotypeStyleIsUPb = (isotopeStyle.compareToIgnoreCase("UPb") == 0);

        this.reportSettingsComment = "";

        this.fractionCategory
                = new ReportCategory(//
                        "Fraction",
                        ReportSpecifications.ReportCategory_Fraction, true);

        this.datesCategory
                = new ReportCategory(//
                        "Dates",
                        isotypeStyleIsUPb
                                ? ReportSpecifications.ReportCategory_Dates//
                                : ReportSpecifications.ReportCategory_Dates, isotypeStyleIsUPb);

        this.datesPbcCorrCategory
                = new ReportCategory(//
                        "PbcCorr Dates",//
                        isotypeStyleIsUPb
                                ? ReportSpecifications.ReportCategory_PbcCorrDates//
                                : ReportSpecifications.ReportCategory_PbcCorrDates, false);

        this.compositionCategory
                = new ReportCategory(//
                        "Composition",//
                        isotypeStyleIsUPb
                                ? ReportSpecifications.ReportCategory_Composition//
                                : ReportSpecifications.ReportCategory_CompositionUTh, true);

        this.isotopicRatiosCategory
                = new ReportCategory(//
                        "Isotopic Ratios",//
                        ReportSpecifications.ReportCategory_IsotopicRatios, true);

        this.isotopicRatiosPbcCorrCategory
                = new ReportCategory(//
                        "PbcCorr Isotopic Ratios",//
                        ReportSpecifications.ReportCategory_PbcCorrIsotopicRatios, false);

        this.rhosCategory
                = new ReportCategory(//
                        "Correlation Coefficients",//
                        ReportSpecifications.ReportCategory_CorrelationCoefficients, true);

        this.traceElementsCategory
                = new ReportCategory(//
                        "Trace Elements",//
                        ReportSpecifications.ReportCategory_TraceElements, false);

        this.fractionCategory2
                = new ReportCategory(//
                        "Fraction",
                        ReportSpecifications.ReportCategory_Fraction2, true);

        legacyData = false;

        assembleReportCategories();

    }

    @Override
    public void assembleReportCategories() {
        setReportCategories(new ArrayList<>());
        getReportCategories().add(getFractionCategory());
        getReportCategories().add(getDatesCategory());
        getReportCategories().add(getDatesPbcCorrCategory());
        getReportCategories().add(getCompositionCategory());
        getReportCategories().add(getIsotopicRatiosCategory());
        getReportCategories().add(getIsotopicRatiosPbcCorrCategory());
        getReportCategories().add(getRhosCategory());
        getReportCategories().add(getTraceElementsCategory());
        getReportCategories().add(getFractionCategory2());

        normalizeReportCategories();
    }

    /**
     *
     * @return
     */
    public static ReportSettingsInterface EARTHTIMEReportSettingsUPb() {
        ReportSettingsInterface EARTHTIME
                = new ReportSettings("EARTHTIME UPb", "UPb");

        return EARTHTIME;
    }

    /**
     *
     * @return
     */
    public static ReportSettingsInterface EARTHTIMEReportSettingsUTh() {
        ReportSettingsInterface EARTHTIME
                = new ReportSettings("EARTHTIME UPb", "UTh");

        return EARTHTIME;
    }

    public static ReportSettingsInterface getReportSettingsModelUpdatedToLatestVersion(ReportSettingsInterface myReportSettingsModel) {
        ReportSettingsInterface reportSettingsModel = myReportSettingsModel;

        if (myReportSettingsModel == null) {
            try {
                reportSettingsModel = ReduxLabData.getInstance().getDefaultReportSettingsModelByIsotopeStyle(myReportSettingsModel.getIsotopeStyle());
            } catch (BadLabDataException badLabDataException) {
            }
        } else {
            // this provides for seamless updates to reportsettings implementation
            // new approach oct 2014
            if (myReportSettingsModel.isOutOfDate()) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"As part of our ongoing development efforts,",
                            "the report settings file you are using is being updated.",
                            "You may lose some report customizations. Thank you for your patience."//,
                        //"If you need to save aliquot copy, please re-export."
                        });
                String myReportSettingsName = myReportSettingsModel.getName();
                reportSettingsModel = new ReportSettings(myReportSettingsName, myReportSettingsModel.getIsotopeStyle());
            }
        }

        //TODO http://www.javaworld.com/article/2077736/open-source-tools/xml-merging-made-easy.html
        return reportSettingsModel;
    }

//  accessors
    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    @Override
    public int getVersion() {
        return version;
    }

    /**
     *
     * @param version
     */
    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategoryInterface getFractionCategory() {
        return fractionCategory;
    }

    /**
     *
     * @param fractionCategory
     */
    @Override
    public void setFractionCategory(ReportCategoryInterface fractionCategory) {
        this.fractionCategory = fractionCategory;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategoryInterface getCompositionCategory() {
        return compositionCategory;
    }

    /**
     *
     * @param compositionCategory
     */
    @Override
    public void setCompositionCategory(ReportCategoryInterface compositionCategory) {
        this.compositionCategory = compositionCategory;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategoryInterface getIsotopicRatiosCategory() {
        return isotopicRatiosCategory;
    }

    /**
     *
     * @param isotopicRatiosCategory
     */
    @Override
    public void setIsotopicRatiosCategory(ReportCategoryInterface isotopicRatiosCategory) {
        this.isotopicRatiosCategory = isotopicRatiosCategory;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategoryInterface getDatesCategory() {
        return datesCategory;
    }

    /**
     *
     * @param datesCategory
     */
    @Override
    public void setDatesCategory(ReportCategoryInterface datesCategory) {
        this.datesCategory = datesCategory;
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

        if (!(reportSettingsModel instanceof ReportSettingsInterface)) {
            return false;
        }

        ReportSettingsInterface myReportSettings = (ReportSettingsInterface) reportSettingsModel;

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

// XML Serialization
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
    @Override
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
    @Override
    public void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        reportSettingsXMLSchemaURL
                = myConfigurator.getResourceURI("URI_ReportSettingsXMLSchema");
    }

    /**
     *
     * @return
     */
    @Override
    public String getReportSettingsComment() {
        return reportSettingsComment;
    }

    /**
     *
     * @param reportSettingsComment
     */
    @Override
    public void setReportSettingsComment(String reportSettingsComment) {
        this.reportSettingsComment = reportSettingsComment;
    }

    /**
     * @return the rhosCategory
     */
    @Override
    public ReportCategoryInterface getRhosCategory() {
        return rhosCategory;
    }

    /**
     * @param rhosCategory the rhosCategory to set
     */
    @Override
    public void setRhosCategory(ReportCategoryInterface rhosCategory) {
        this.rhosCategory = rhosCategory;
    }

    /**
     * @return the fractionCategory2
     */
    @Override
    public ReportCategoryInterface getFractionCategory2() {
        return fractionCategory2;
    }

    /**
     * @param fractionCategory2 the fractionCategory2 to set
     */
    @Override
    public void setFractionCategory2(ReportCategoryInterface fractionCategory2) {
        this.fractionCategory2 = fractionCategory2;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOutOfDate() {
        return this.version < CURRENT_VERSION_REPORT_SETTINGS;
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

        getReportCategories().stream().filter((rc) -> (rc != null)).forEach((rc) -> {
            rc.setLegacyData(legacyData);
        });
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ReportSettingsInterface reportSettings
                = new ReportSettings("Test ReportSettings", "UPb");
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
    @Override
    public ReportCategoryInterface getTraceElementsCategory() {
        return traceElementsCategory;
    }

    /**
     * @param traceElementsCategory the traceElementsCategory to set
     */
    @Override
    public void setTraceElementsCategory(ReportCategoryInterface traceElementsCategory) {
        this.traceElementsCategory = traceElementsCategory;
    }

    /**
     * @return the isotopicRatiosPbcCorrCategory
     */
    @Override
    public ReportCategoryInterface getIsotopicRatiosPbcCorrCategory() {
        return isotopicRatiosPbcCorrCategory;
    }

    /**
     * @param isotopicRatiosPbcCorrCategory the isotopicRatiosPbcCorrCategory to
     * set
     */
    @Override
    public void setIsotopicRatiosPbcCorrCategory(ReportCategoryInterface isotopicRatiosPbcCorrCategory) {
        this.isotopicRatiosPbcCorrCategory = isotopicRatiosPbcCorrCategory;
    }

    /**
     * @return the datesPbcCorrCategory
     */
    @Override
    public ReportCategoryInterface getDatesPbcCorrCategory() {
        return datesPbcCorrCategory;
    }

    /**
     * @param datesPbcCorrCategory the datesPbcCorrCategory to set
     */
    @Override
    public void setDatesPbcCorrCategory(ReportCategoryInterface datesPbcCorrCategory) {
        this.datesPbcCorrCategory = datesPbcCorrCategory;
    }

    /**
     * @return the reportCategories
     */
    public ArrayList<ReportCategoryInterface> getReportCategories() {
        return reportCategories;
    }

    /**
     * @return the reportSettingsXMLSchemaURL
     */
    public String getReportSettingsXMLSchemaURL() {
        return reportSettingsXMLSchemaURL;
    }

    /**
     * @param reportCategories the reportCategories to set
     */
    public void setReportCategories(ArrayList<ReportCategoryInterface> reportCategories) {
        this.reportCategories = reportCategories;
    }

    /**
     * @return the isotopeStyle
     */
    public String getIsotopeStyle() {
        if (isotopeStyle == null) {
            isotopeStyle = "UPb";
        }
        return isotopeStyle;
    }

    /**
     * @param isotopeStyle the isotopeStyle to set
     */
    public void setIsotopeStyle(String isotopeStyle) {
        this.isotopeStyle = isotopeStyle;
    }
}
