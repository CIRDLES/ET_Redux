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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.ReportSpecifications;
import org.earthtime.exceptions.ETException;
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
    private static transient int CURRENT_VERSION_REPORT_SETTINGS = 278;
//    private static transient String activityFootnoteEntry = "";
//    private static transient String thU_MagmaFootnoteEntry = "";
//    // april 2010
//    // no zircons in report = 0; all zircons = 1; mixture = 2
//    private static transient int zirconPopulationType;
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
    protected ArrayList<ReportCategory> reportCategories;
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
                        "Fraction",
                        0,
                        ReportSpecifications.ReportCategory_Fraction, true);

        this.datesCategory
                = new ReportCategory(//
                        "Dates",
                        1,
                        ReportSpecifications.ReportCategory_Dates, true);

        this.datesPbcCorrCategory
                = new ReportCategory(//
                        "PbcCorr Dates",
                        2,//7,
                        ReportSpecifications.ReportCategory_PbcCorrDates, false);

        this.compositionCategory
                = new ReportCategory(//
                        "Composition",
                        3,//2,
                        ReportSpecifications.ReportCategory_Composition, true);

        this.isotopicRatiosCategory
                = new ReportCategory(//
                        "Isotopic Ratios",
                        4,//3,
                        ReportSpecifications.ReportCategory_IsotopicRatios, true);

        this.isotopicRatiosPbcCorrCategory
                = new ReportCategory(//
                        "PbcCorr Isotopic Ratios",
                        5,//6,
                        ReportSpecifications.ReportCategory_PbcCorrIsotopicRatios, false);

        this.rhosCategory
                = new ReportCategory(//
                        "Correlation Coefficients",
                        6,//4,
                        ReportSpecifications.ReportCategory_CorrelationCoefficients, true);

        this.traceElementsCategory
                = new ReportCategory(//
                        "Trace Elements",
                        7,//5,
                        ReportSpecifications.ReportCategory_TraceElements, false);

        this.fractionCategory2
                = new ReportCategory(//
                        "Fraction",
                        8,
                        ReportSpecifications.ReportCategory_Fraction2, true);

        legacyData = false;

        assembleReportCategories();

    }

    @Override
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

    /**
     *
     * @return
     */
    @Override
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
    public String getNameAndVersion() {
        return getName().trim() + " v." + getVersion();
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategory getFractionCategory() {
        return fractionCategory;
    }

    /**
     *
     * @param fractionCategory
     */
    @Override
    public void setFractionCategory(ReportCategory fractionCategory) {
        this.fractionCategory = fractionCategory;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategory getCompositionCategory() {
        return compositionCategory;
    }

    /**
     *
     * @param compositionCategory
     */
    @Override
    public void setCompositionCategory(ReportCategory compositionCategory) {
        this.compositionCategory = compositionCategory;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategory getIsotopicRatiosCategory() {
        return isotopicRatiosCategory;
    }

    /**
     *
     * @param isotopicRatiosCategory
     */
    @Override
    public void setIsotopicRatiosCategory(ReportCategory isotopicRatiosCategory) {
        this.isotopicRatiosCategory = isotopicRatiosCategory;
    }

    /**
     *
     * @return
     */
    @Override
    public ReportCategory getDatesCategory() {
        return datesCategory;
    }

    /**
     *
     * @param datesCategory
     */
    @Override
    public void setDatesCategory(ReportCategory datesCategory) {
        this.datesCategory = datesCategory;
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

    /**
     *
     * @return
     */
    @Override
    public ReportSettings clone() {
        ReportSettings reportSettingsModel = null;

        String tempFileName = "TEMPreportSettings.xml";
        // write out the settings
        serializeXMLObject(tempFileName);

        // read them back in
        try {
            reportSettingsModel = (ReportSettings) readXMLObject(tempFileName, true);
        } catch (FileNotFoundException | ETException | BadOrMissingXMLSchemaException fileNotFoundException) {
        }

        File tempFile = new File(tempFileName);
        tempFile.delete();

        return reportSettingsModel;
    }

// XML Serialization
    /**
     *
     * @return
     */
    @Override
    public XStream getXStreamWriter() {
        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @return
     */
    @Override
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
    public ReportCategory getRhosCategory() {
        return rhosCategory;
    }

    /**
     * @param rhosCategory the rhosCategory to set
     */
    @Override
    public void setRhosCategory(ReportCategory rhosCategory) {
        this.rhosCategory = rhosCategory;
    }

    /**
     * @return the fractionCategory2
     */
    @Override
    public ReportCategory getFractionCategory2() {
        return fractionCategory2;
    }

    /**
     * @param fractionCategory2 the fractionCategory2 to set
     */
    @Override
    public void setFractionCategory2(ReportCategory fractionCategory2) {
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
    @Override
    public ReportCategory getTraceElementsCategory() {
        return traceElementsCategory;
    }

    /**
     * @param traceElementsCategory the traceElementsCategory to set
     */
    @Override
    public void setTraceElementsCategory(ReportCategory traceElementsCategory) {
        this.traceElementsCategory = traceElementsCategory;
    }

    /**
     * @return the isotopicRatiosPbcCorrCategory
     */
    @Override
    public ReportCategory getIsotopicRatiosPbcCorrCategory() {
        return isotopicRatiosPbcCorrCategory;
    }

    /**
     * @param isotopicRatiosPbcCorrCategory the isotopicRatiosPbcCorrCategory to
     * set
     */
    @Override
    public void setIsotopicRatiosPbcCorrCategory(ReportCategory isotopicRatiosPbcCorrCategory) {
        this.isotopicRatiosPbcCorrCategory = isotopicRatiosPbcCorrCategory;
    }

    /**
     * @return the datesPbcCorrCategory
     */
    @Override
    public ReportCategory getDatesPbcCorrCategory() {
        return datesPbcCorrCategory;
    }

    /**
     * @param datesPbcCorrCategory the datesPbcCorrCategory to set
     */
    @Override
    public void setDatesPbcCorrCategory(ReportCategory datesPbcCorrCategory) {
        this.datesPbcCorrCategory = datesPbcCorrCategory;
    }

    /**
     * @return the reportCategories
     */
    public ArrayList<ReportCategory> getReportCategories() {
        return reportCategories;
    }
}
