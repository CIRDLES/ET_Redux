/*
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.UTh_Redux.aliquots;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.AnalysisImage;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquotXMLConverter;
import org.earthtime.UPb_Redux.fractions.AnalysisFraction;
import org.earthtime.UPb_Redux.fractions.AnalysisFractionXMLConverter;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.samples.SESARSampleMetadata;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferencedXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.archivingTools.AnalysisImageInterface;
import org.earthtime.archivingTools.AnalysisImageXMLConverter;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.AnalysisImageTypes;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.physicalConstants.PhysicalConstantsXMLConverter;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelETXMLConverter;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModelXMLConverter;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModel;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModelXMLConverter;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModelXMLConverter;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModelXMLConverter;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reportViews.ReportRowGUIInterface;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class UThReduxAliquot implements //
        AliquotInterface, ReduxAliquotInterface, ReportRowGUIInterface, XMLSerializationI, Serializable {

    // Class variables
    private static final long serialVersionUID = 8000633948734353979L;
    // Instance variables
    private transient String aliquotUThXMLSchemaURL;
    private transient boolean selectedInDataTable;
    /**
     * SESAR produces IGSN and eventually we will tie to their database.
     */
    protected String sampleIGSN;
    /**
     * Lab's local name for the Aliquot.
     */
    private String aliquotName;
    protected String aliquotIGSN;
    private int aliquotNumber;
    private String laboratoryName;
    private String analystName;
    private String aliquotReference;
    private String aliquotInstrumentalMethod;
    private String aliquotInstrumentalMethodReference;
    private String aliquotComment;
    protected Vector<ValueModel> sampleDateModels;
    private AbstractRatiosDataModel physicalConstantsModel;
    private Vector<AbstractRatiosDataModel> mineralStandardModels;
    private Vector<FractionI> analysisFractions;
    private ReduxConstants.ANALYSIS_PURPOSE analysisPurpose;
    private String keyWordsCSV;
    private boolean compiled;
    private Vector<ETFractionInterface> aliquotFractions;
    private transient ReduxLabData myReduxLabData;

    public UThReduxAliquot() {
    }

    /**
     *
     * @param aliquotNumber
     * @param aliquotName
     * @param physicalConstantsModel
     * @param reduxLabData
     * @param physicalConstants
     * @param compiled
     * @param mySESARSampleMetadata
     */
    public UThReduxAliquot(
            int aliquotNumber,
            String aliquotName,
            AbstractRatiosDataModel physicalConstantsModel,
            boolean compiled,
            SESARSampleMetadata mySESARSampleMetadata) {

        this.aliquotIGSN = ReduxConstants.DEFAULT_ALIQUOT_IGSN;
        this.aliquotName = aliquotName;

        this.aliquotNumber = aliquotNumber;

        this.physicalConstantsModel = physicalConstantsModel;

        this.compiled = compiled;

        this.sampleDateModels = new Vector<>();
        this.mineralStandardModels = new Vector<>();
        this.aliquotFractions = new Vector<>();

        this.sampleIGSN = "NONE";
        this.laboratoryName = ReduxLabData.getInstance().getLabName();
        this.analystName = ReduxLabData.getInstance().getAnalystName();

//        this.automaticDataUpdateMode = false;
//
//        this.containingSampleDataFolder = null;
//
//        this.mySESARSampleMetadata = mySESARSampleMetadata;
        this.myReduxLabData = ReduxLabData.getInstance();
    }

    @Override
    public String getAliquotName() {
        return aliquotName;
    }

    @Override
    public void setAliquotName(String aliquotName) {
        this.aliquotName = aliquotName;
    }

    @Override
    public String getSampleIGSN() {
        return sampleIGSN;
    }

    @Override
    public void setSampleIGSN(String sampleIGSN) {
        this.sampleIGSN = sampleIGSN;
    }

    @Override
    public String getAliquotIGSN() {
        return aliquotIGSN;
    }

    @Override
    public void setAliquotIGSN(String aliquotIGSN) {
        this.aliquotIGSN = aliquotIGSN;
    }

    @Override
    public String getLaboratoryName() {
        return laboratoryName;
    }

    @Override
    public void setLaboratoryName(String laboratoryName) {
        this.laboratoryName = laboratoryName;
    }

    @Override
    public String getAnalystName() {
        return analystName;
    }

    @Override
    public void setAnalystName(String analystName) {
        this.analystName = analystName;
    }

    @Override
    public String getAliquotReference() {
        return aliquotReference;
    }

    @Override
    public void setAliquotReference(String aliquotReference) {
        this.aliquotReference = aliquotReference;
    }

    @Override
    public String getAliquotInstrumentalMethod() {
        return aliquotInstrumentalMethod;
    }

    @Override
    public void setAliquotInstrumentalMethod(String aliquotInstrumentalMethod) {
        this.aliquotInstrumentalMethod = aliquotInstrumentalMethod;
    }

    @Override
    public String getAliquotInstrumentalMethodReference() {
        return aliquotInstrumentalMethodReference;
    }

    @Override
    public void setAliquotInstrumentalMethodReference(String aliquotInstrumentalMethodReference) {
        this.aliquotInstrumentalMethodReference = aliquotInstrumentalMethodReference;
    }

    @Override
    public String getAliquotComment() {
        return aliquotComment;
    }

    @Override
    public void setAliquotComment(String aliquotComment) {
        this.aliquotComment = aliquotComment;
    }

    @Override
    public Vector<ValueModel> getSampleDateModels() {
        return sampleDateModels;
    }

    @Override
    public void setSampleDateModels(Vector<ValueModel> sampleDateModels) {
        this.sampleDateModels = sampleDateModels;
    }

    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        return physicalConstantsModel;
    }

    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        this.physicalConstantsModel = physicalConstantsModel;

        // all existing fractions must be updated
        if (getAliquotFractions() != null) {
            for (int i = 0; i < getAliquotFractions().size(); i++) {
                getAliquotFractions().get(i).setPhysicalConstantsModel(physicalConstantsModel);
            }
        }
    }

    @Override
    public Vector<AbstractRatiosDataModel> getTracers() {
        return null;
    }

    @Override
    public void setTracers(Vector<AbstractRatiosDataModel> tracers) {
    }

    @Override
    public Vector<AbstractRatiosDataModel> getMineralStandardModels() {
        return mineralStandardModels;
    }

    @Override
    public void setMineralStandardModels(Vector<AbstractRatiosDataModel> MineralStandardModels) {
        this.mineralStandardModels = MineralStandardModels;
    }

    @Override
    public ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose() {
        return analysisPurpose;
    }

    /**
     *
     * @param analysisPurpose
     */
    @Override
    public void setAnalysisPurpose(ReduxConstants.ANALYSIS_PURPOSE analysisPurpose) {
        this.analysisPurpose = analysisPurpose;
    }

    @Override
    public String getKeyWordsCSV() {
        return keyWordsCSV;
    }

    @Override
    public void setKeyWordsCSV(String keyWordsCSV) {
        this.keyWordsCSV = keyWordsCSV;
    }

    @Override
    public Vector<FractionI> getAnalysisFractions() {
        return analysisFractions;
    }

    @Override
    public Vector<ETFractionInterface> getAliquotFractions() {
        return aliquotFractions;
    }

    @Override
    public void setAliquotFractions(Vector<ETFractionInterface> aliquotFractions) {
        this.aliquotFractions = aliquotFractions;
    }

    @Override
    public boolean isCompiled() {
        return compiled;
    }

    @Override
    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    /**
     *
     * @return
     */
    @Override
    public int getAliquotNumber() {
        return aliquotNumber;
    }

    /**
     *
     * @param aliquotNumber
     */
    public void setAliquotNumber(int aliquotNumber) {
        this.aliquotNumber = aliquotNumber;
    }

    @Override
    public void setMyReduxLabData(ReduxLabData myReduxLabData) {
        this.myReduxLabData = myReduxLabData;
    }

    /**
     * @return the selectedInDataTable
     */
    @Override
    public boolean isSelectedInDataTable() {
        return selectedInDataTable;
    }

    /**
     * @param selectedInDataTable the selectedInDataTable to set
     */
    @Override
    public void setSelectedInDataTable(boolean selectedInDataTable) {
        this.selectedInDataTable = selectedInDataTable;
    }

    /**
     *
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void reduceData(boolean inLiveMode) {
        //TODO: Reduce Useries
    }

    @Override
    public AnalysisImageInterface getAnalysisImageByType(AnalysisImageTypes imageType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // XML Serialization**************************************************************************************************************************************************
    /**
     *
     */
    public void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        aliquotUThXMLSchemaURL
                = myConfigurator.getResourceURI("aliquotUThXMLSchemaURL");
    }

    /**
     * Creates XStream for writing XML
     *
     * @return XStream object
     * @see XStream
     */
    public XStream getXStreamWriter() {

        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @param xstream
     */
    public void customizeXstream(XStream xstream) {

        xstream.registerConverter(new UThReduxAliquotXMLConverter());
        xstream.registerConverter(new PhysicalConstantsModelXMLConverter());
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new ValueModelReferencedXMLConverter());
        xstream.registerConverter(new MeasuredRatioModelXMLConverter());
        xstream.registerConverter(new SampleDateModelXMLConverter());

        xstream.registerConverter(new AnalysisFractionXMLConverter());

        xstream.registerConverter(new MineralStandardUPbRatioModelXMLConverter());

        xstream.alias("Aliquot", UThReduxAliquot.class);

        xstream.alias("AnalysisFraction", AnalysisFraction.class);
        xstream.alias("MineralStandardUPbModel", MineralStandardUPbModel.class);
        xstream.alias("PhysicalConstantsModel", PhysicalConstantsModel.class);

        xstream.alias("ValueModel", ValueModel.class);
        xstream.alias("ValueModelReferenced", ValueModelReferenced.class);
        xstream.alias("MeasuredRatioModel", MeasuredRatioModel.class);
        xstream.alias("SampleDateModel", SampleDateModel.class);
        xstream.alias("SampleDateInterceptModel", SampleDateInterceptModel.class);
        xstream.alias("MineralStandardUPbRatioModel", MineralStandardUPbRatioModel.class);

        xstream.alias("SESARSampleMetadata", SESARSampleMetadata.class);

        setClassXMLSchemaURL();
    }

    /**
     *
     * @return
     */
    @Override
    public String serializeXMLObject() {
        prepareUPbReduxAliquotForXMLSerialization();

        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("Aliquot",
                "Aliquot  "//
                + ReduxConstants.XML_ResourceHeader//
                + aliquotUThXMLSchemaURL///
                + "\"");

        return xml;
    }

    /**
     *
     * @param filename
     */
    @Override
    public void serializeXMLObject(String filename) {

        String xml = serializeXMLObject();

        try {
            try (FileWriter outFile = new FileWriter(filename)) {
                PrintWriter out = new PrintWriter(outFile);

                // Write xml to file
                out.println(xml);
                out.flush();
                out.close();
            }

        } catch (IOException e) {
        }
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
     *
     * @param filename
     * @param doValidate
     * @return
     * @throws FileNotFoundException
     * @throws ETException
     * @throws BadOrMissingXMLSchemaException
     */
    @Override
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException,
            ETException,
            FileNotFoundException,
            BadOrMissingXMLSchemaException {

        AliquotInterface myAliquot = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode = true;

            XStream xstream = getXStreamReader();

            if (doValidate) {
                isValidOrAirplaneMode = URIHelper.validateXML(reader, filename, aliquotUThXMLSchemaURL);
            }

            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myAliquot = (AliquotInterface) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }
            } else {
                throw new ETException(null, "XML data file does not conform to schema.");
            }
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return myAliquot;
    }

    /**
     * Extracts unique Tracers and PbBlanks from fractions and also processes
     * fractions into ANalysisFractions for publication.
     *
     */
    public synchronized void prepareUPbReduxAliquotForXMLSerialization() {

        getAnalysisFractions().clear();

        // note fractions and SampleDateModels are already unique
        Collections.sort(aliquotFractions, ETFractionInterface.FRACTION_ID_ORDER);
        if (sampleDateModels != null) {
            Collections.sort(sampleDateModels);
        }

        Iterator it = getAliquotFractions().iterator();
        while (it.hasNext()) {
            FractionI fraction = (FractionI) it.next();
            if (!fraction.isRejected()) {
                getAnalysisFractions().add(new AnalysisFraction(fraction, isCompiled()));
            }
        }

    }

    // END  XML Serialization***********************************************************************************************************END
}
