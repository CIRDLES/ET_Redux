/*
 * ReportSettings.java
 *
 * Created on September 3, 2008, 9:18 AM
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsAbstract;
import org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsUPb;
import org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsUTh_Carb;
import org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsUTh_Ign;
import org.earthtime.exceptions.ETException;
import org.earthtime.reduxLabData.ReduxLabData;
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
    private static transient int CURRENT_VERSION_REPORT_SETTINGS_UPB = 408;
    private static transient int CURRENT_VERSION_REPORT_SETTINGS_UTH_Carb = 6032;
    private static transient int CURRENT_VERSION_REPORT_SETTINGS_UTH_Ign = 6007;

    // Fields
    private String name;
    private int version;
    private String defaultReportSpecsType;
    private ReportCategoryInterface fractionCategory;
    private ReportCategoryInterface compositionCategory;
    private ReportCategoryInterface isotopicRatiosCategory;
    private ReportCategoryInterface isotopicRatiosPbcCorrCategory;
    private ReportCategoryInterface datesCategory;
    private ReportCategoryInterface datesPbcCorrCategory;
    private ReportCategoryInterface rhosCategory;
    private ReportCategoryInterface traceElementsCategory;
    private ReportCategoryInterface qaQcParametersCategory;
    // 2017 new useries categories
    protected ReportCategoryInterface concentrationAndActivityCategory;
    protected ReportCategoryInterface measuredAtomAndActivityRatiosCategory;
    protected ReportCategoryInterface measuredCorrectedAtomAndActivityRatiosCategory;
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
     * @param defaultReportSpecsType the value of defaultReportSpecsType
     */
    public ReportSettings(String name, String defaultReportSpecsType) {

        this.name = name;
        this.defaultReportSpecsType = defaultReportSpecsType;

        switch (defaultReportSpecsType) {
            case "UPb":
                this.version = CURRENT_VERSION_REPORT_SETTINGS_UPB;
                break;
            case "UTh_Carb":
                this.version = CURRENT_VERSION_REPORT_SETTINGS_UTH_Carb;
                break;
            case "UTh_Ign":
                this.version = CURRENT_VERSION_REPORT_SETTINGS_UTH_Ign;
                break;
        }

        this.reportSettingsComment = "";

        this.fractionCategory
                = new ReportCategory(//
                        "Fraction",
                        ReportSpecificationsAbstract.ReportCategory_Fraction, true);

        this.fractionCategory2
                = new ReportCategory(//
                        "Fraction",
                        ReportSpecificationsAbstract.ReportCategory_Fraction2, true);

        if (isdefaultReportSpecsType_UPb()) {
            this.datesCategory
                    = new ReportCategory(//
                            "Dates",
                            ReportSpecificationsUPb.ReportCategory_Dates, true);

            this.datesPbcCorrCategory
                    = new ReportCategory(//
                            "PbcCorr Dates",//
                            ReportSpecificationsUPb.ReportCategory_PbcCorrDates, false);

            this.compositionCategory
                    = new ReportCategory(//
                            "Composition",//
                            ReportSpecificationsUPb.ReportCategory_Composition, true);

            this.isotopicRatiosCategory
                    = new ReportCategory(//
                            "Isotopic Ratios",//
                            ReportSpecificationsUPb.ReportCategory_IsotopicRatios, true);

            this.isotopicRatiosPbcCorrCategory
                    = new ReportCategory(//
                            "PbcCorr Isotopic Ratios",//
                            ReportSpecificationsUPb.ReportCategory_PbcCorrIsotopicRatios, false);

            this.rhosCategory
                    = new ReportCategory(//
                            "Correlation Coefficients",//
                            ReportSpecificationsUPb.ReportCategory_CorrelationCoefficients, true);

            this.traceElementsCategory
                    = new ReportCategory(//
                            "Trace Elements",//
                            ReportSpecificationsUPb.ReportCategory_TraceElements, false);
            
            this.qaQcParametersCategory
                    = new ReportCategory(//
                            "QA/QC Parameters",//
                            ReportSpecificationsUPb.ReportCategory_QA_QC_Parameters, false);
        } else if (isdefaultReportSpecsType_UTh_Carb()) {
            this.concentrationAndActivityCategory
                    = new ReportCategory(//
                            "Concentration/Activity",
                            ReportSpecificationsUTh_Carb.ReportCategory_USeriesConcentrationActivity, true);
            this.measuredAtomAndActivityRatiosCategory
                    = new ReportCategory(//
                            "Meas. Atom/Activity Ratios",
                            ReportSpecificationsUTh_Carb.ReportCategory_USeriesMeasuredAtomAndActivityRatios, true);
            this.measuredCorrectedAtomAndActivityRatiosCategory
                    = new ReportCategory(//
                            "Meas. Detrital-Corr. Atom/Activity Ratios",
                            ReportSpecificationsUTh_Carb.ReportCategory_USeriesMeasuredDetritalCorrectedAtomAndActivityRatios, true);
            this.datesCategory
                    = new ReportCategory(//
                            "Date and initial 234U",
                            ReportSpecificationsUTh_Carb.ReportCategory_USeriesReportTable, true);

        } else if (isdefaultReportSpecsType_UTh_Ign()) {
            this.datesCategory
                    = new ReportCategory(//
                            "USeries Igneous Outputs",
                            ReportSpecificationsUTh_Ign.ReportCategory_USeriesReportTable, true);
        }

        legacyData = false;

        assembleReportCategories();
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
    public static ReportSettingsInterface EARTHTIMEReportSettingsUTh_Carb() {
        ReportSettingsInterface EARTHTIME
                = new ReportSettings("EARTHTIME UTh", "UTh_Carb");

        return EARTHTIME;
    }

    /**
     *
     * @return
     */
    public static ReportSettingsInterface EARTHTIMEReportSettingsUTh_Ign() {
        ReportSettingsInterface EARTHTIME
                = new ReportSettings("EARTHTIME UTh", "UTh_Ign");

        return EARTHTIME;
    }

    public static ReportSettingsInterface getReportSettingsModelUpdatedToLatestVersion(ReportSettingsInterface myReportSettingsModel) {
        ReportSettingsInterface reportSettingsModel = myReportSettingsModel;

        if (myReportSettingsModel == null) {
            reportSettingsModel = ReduxLabData.getInstance().getDefaultReportSettingsModelBySpecsType(myReportSettingsModel.getDefaultReportSpecsType());
        } else // new approach oct 2014
        {   // this provides for seamless updates to reportsettings implementation
            String myReportSettingsName = myReportSettingsModel.getName();
            if (myReportSettingsModel.isOutOfDate()) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"As part of our ongoing development efforts,",
                            "the report settings file you are using is being updated.",
                            "You may lose some report customizations. Thank you for your patience."//,
                        });

                reportSettingsModel = new ReportSettings(myReportSettingsName, myReportSettingsModel.getDefaultReportSpecsType());
            }
        }

        //TODO http://www.javaworld.com/article/2077736/open-source-tools/xml-merging-made-easy.html
        return reportSettingsModel;
    }

    /**
     *
     * @param filename
     */
    @Override
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("ReportSettings",
                "ReportSettings  "//
                + ReduxConstants.XML_ResourceHeader//
                + getReportSettingsXMLSchemaURL() //

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
        }
    }

    /**
     *
     * @return
     */
    public ReportSettingsInterface deepCopy() {
        ReportSettingsInterface reportSettingsModel = null;

        String tempFileName = "TEMPreportSettings.xml";
        // write out the settings
        serializeXMLObject(tempFileName);

        // read them back in
        try {
            reportSettingsModel = (ReportSettingsInterface) readXMLObject(tempFileName, false);
        } catch (FileNotFoundException | ETException | BadOrMissingXMLSchemaException fileNotFoundException) {
        }

        File tempFile = new File(tempFileName);
        tempFile.delete();

        return reportSettingsModel;
    }

    /**
     *
     * @param reportSettingsModel
     * @return
     * @throws ClassCastException
     */
    @Override
    public int compareTo(ReportSettingsInterface reportSettingsModel)
            throws ClassCastException {
        String reportSettingsModelNameAndVersion
                = reportSettingsModel.getNameAndVersion();
        return this.getNameAndVersion().trim().//
                compareToIgnoreCase(reportSettingsModelNameAndVersion.trim());
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
        boolean retVal = false;
        if (defaultReportSpecsType == null){
            defaultReportSpecsType = "UPb";
        }
        switch (defaultReportSpecsType) {
            case "UPb":
                retVal = isOutOfDateUPb();
                break;
            case "UTh_Carb":
                retVal = isOutOfDateUTh_Carb();
                break;
            case "UTh_Ign":
                retVal = isOutOfDateUTh_Ign();
                break;
        }

        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOutOfDateUPb() {
        return this.version < CURRENT_VERSION_REPORT_SETTINGS_UPB;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOutOfDateUTh_Carb() {
        return this.version < CURRENT_VERSION_REPORT_SETTINGS_UTH_Carb;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOutOfDateUTh_Ign() {
        return this.version < CURRENT_VERSION_REPORT_SETTINGS_UTH_Ign;
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
     * @return the concentrationAndActivityCategory
     */
    public ReportCategoryInterface getConcentrationAndActivityCategory() {
        return concentrationAndActivityCategory;
    }

    /**
     * @param concentrationAndActivityCategory the
     * concentrationAndActivityCategory to set
     */
    public void setConcentrationAndActivityCategory(ReportCategoryInterface concentrationAndActivityCategory) {
        this.concentrationAndActivityCategory = concentrationAndActivityCategory;
    }

    /**
     * @return the measuredAtomAndActivityRatiosCategory
     */
    public ReportCategoryInterface getMeasuredAtomAndActivityRatiosCategory() {
        return measuredAtomAndActivityRatiosCategory;
    }

    /**
     * @param measuredAtomAndActivityRatiosCategory the
     * measuredAtomAndActivityRatiosCategory to set
     */
    public void setMeasuredAtomAndActivityRatiosCategory(ReportCategoryInterface measuredAtomAndActivityRatiosCategory) {
        this.measuredAtomAndActivityRatiosCategory = measuredAtomAndActivityRatiosCategory;
    }

    /**
     * @return the measuredCorrectedAtomAndActivityRatiosCategory
     */
    public ReportCategoryInterface getMeasuredCorrectedAtomAndActivityRatiosCategory() {
        return measuredCorrectedAtomAndActivityRatiosCategory;
    }

    /**
     * @param measuredCorrectedAtomAndActivityRatiosCategory the
     * measuredCorrectedAtomAndActivityRatiosCategory to set
     */
    public void setMeasuredCorrectedAtomAndActivityRatiosCategory(ReportCategoryInterface measuredCorrectedAtomAndActivityRatiosCategory) {
        this.measuredCorrectedAtomAndActivityRatiosCategory = measuredCorrectedAtomAndActivityRatiosCategory;
    }

    /**
     * @return the reportCategories
     */
    @Override
    public ArrayList<ReportCategoryInterface> getReportCategories() {
        return reportCategories;
    }

    /**
     * @return the reportSettingsXMLSchemaURL
     */
    @Override
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
     * @return the defaultReportSpecsType
     */
    @Override
    public String getDefaultReportSpecsType() {
        if (defaultReportSpecsType == null) {
            defaultReportSpecsType = "UPb";
        }
        return defaultReportSpecsType;
    }

    /**
     * @param defaultReportSpecsType the defaultReportSpecsType to set
     */
    @Override
    public void setDefaultReportSpecsType(String defaultReportSpecsType) {
        this.defaultReportSpecsType = defaultReportSpecsType;
    }

    /**
     * @return the isdefaultReportSpecsType_UPb
     */
    @Override
    public boolean isdefaultReportSpecsType_UPb() {
        if (defaultReportSpecsType == null) {
            defaultReportSpecsType = "UPb";
        }
        return (defaultReportSpecsType.compareToIgnoreCase("UPb") == 0);
    }

    /**
     * @return the isdefaultReportSpecsType_UPb
     */
    @Override
    public boolean isdefaultReportSpecsType_UTh_Carb() {
        if (defaultReportSpecsType == null) {
            defaultReportSpecsType = "UTh_Carb";
        }
        return (defaultReportSpecsType.compareToIgnoreCase("UTh_Carb") == 0);
    }

    @Override
    public boolean isdefaultReportSpecsType_UTh_Ign() {
        if (defaultReportSpecsType == null) {
            defaultReportSpecsType = "UTh_Ign";
        }
        return (defaultReportSpecsType.compareToIgnoreCase("UTh_Ign") == 0);
    }
}
