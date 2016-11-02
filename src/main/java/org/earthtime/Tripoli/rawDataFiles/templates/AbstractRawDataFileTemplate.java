/*
 * AbstractRawDataFileTemplate.java
 *
 * Created Jul 1, 2011
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
package org.earthtime.Tripoli.rawDataFiles.templates;

import java.io.Serializable;
import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.massSpecSetups.AbstractMassSpecSetup;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractRawDataFileTemplate implements //
        Comparable<AbstractRawDataFileTemplate>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 635520011046860860L;
    /**
     *
     */
    protected String NAME;
    /**
     *
     */
    protected String aboutInfo;
    /**
     *
     */
    protected FileTypeEnum fileType;
    /**
     *
     */
    protected String startOfFirstLine;
    /**
     *
     */
    protected String startOfDataSectionFirstLine;
    /**
     *
     */
    protected String startOfEachBlockFirstLine;

    /**
     *
     */
    protected String endOfEachBlockLastLine;
    /**
     *
     */
    protected int blockStartOffset;
    /**
     *
     */
    protected int blockSize;
    /**
     *
     */
    protected String[] standardIDs;
    /**
     *
     */
    protected TimeZone timeZone;
    // 0 = no parse, 1 = auto-parse, 2 = follow a provided pattern (not implemented as of feb 2012)
    /**
     *
     */
    protected int defaultParsingOfFractionsBehavior;

    /**
     *
     */
    protected AbstractAcquisitionModel acquisitionModel;

    protected String[] elementsByIsotopicMass;

    protected AbstractMassSpecSetup massSpecSetup;

    /**
     *
     */
    public AbstractRawDataFileTemplate() {
        this.NAME = "Unnamed";
        this.aboutInfo = "Details:";

        this.fileType = FileTypeEnum.txt;
        this.startOfFirstLine = "";
        this.startOfDataSectionFirstLine = "";
        this.startOfEachBlockFirstLine = "";
        this.endOfEachBlockLastLine = "";
        this.blockStartOffset = 0;
        this.blockSize = 0;
        this.standardIDs = new String[0];
        this.timeZone = TimeZone.getDefault();
        this.defaultParsingOfFractionsBehavior = 0;

    }

    /**
     *
     * @param abstractRawDataFileTemplate
     * @return
     */
    @Override
    public int compareTo(AbstractRawDataFileTemplate abstractRawDataFileTemplate) {
        String abstractRawDataFileTemplateName
                = abstractRawDataFileTemplate.NAME.trim();
        return (this.NAME.trim().compareToIgnoreCase(abstractRawDataFileTemplateName));
    }

    /**
     *
     * @param abstractRawDataFileTemplate
     * @return
     */
    @Override
    public boolean equals(Object abstractRawDataFileTemplate) {
        //check for self-comparison
        if (this == abstractRawDataFileTemplate) {
            return true;
        }
        if (!(abstractRawDataFileTemplate instanceof AbstractRawDataFileTemplate)) {
            return false;
        }

        AbstractRawDataFileTemplate myAbstractRawDataFileTemplate = (AbstractRawDataFileTemplate) abstractRawDataFileTemplate;
        return (this.NAME.trim().compareToIgnoreCase(myAbstractRawDataFileTemplate.NAME.trim()) == 0);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     *
     * @return
     */
    public abstract AbstractAcquisitionModel makeNewAcquisitionModel();

    /**
     * @return the fileType
     */
    public FileTypeEnum getFileType() {
        return fileType;
    }

    /**
     * @return the startOfFirstLine
     */
    public String getStartOfFirstLine() {
        return startOfFirstLine;
    }

    /**
     * @return the startOfDataSectionFirstLine
     */
    public String getStartOfDataSectionFirstLine() {
        return startOfDataSectionFirstLine;
    }

    /**
     * @return the startOfEachBlockFirstLines
     */
    public String getStartOfEachBlockFirstLine() {
        return startOfEachBlockFirstLine;
    }

    /**
     * @return the blockSize
     */
    public int getBlockSize() {
        return blockSize;
    }

    /**
     * @return the standardIDs
     */
    public String[] getStandardIDs() {
        return standardIDs;
    }

    /**
     * @return the timeZone
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return NAME;
    }

    public String getNAME() {
        return NAME;
    }

    /**
     * @return the aboutInfo
     */
    public String getAboutInfo() {
        return aboutInfo;
    }

    /**
     * @return the defaultParsingOfFractionsBehavior
     */
    public int getDefaultParsingOfFractionsBehavior() {
        return defaultParsingOfFractionsBehavior;
    }

    /**
     * @return the blockStartOffset
     */
    public int getBlockStartOffset() {
        return blockStartOffset;
    }

    /**
     * @return the acquisitionModel
     */
    public AbstractAcquisitionModel getAcquisitionModel() {
        return acquisitionModel;
    }

    /**
     * @return the endOfEachBlockLastLine
     */
    public String getEndOfEachBlockLastLine() {
        return endOfEachBlockLastLine;
    }

    public boolean standardIsKnown(String standardName) {
        boolean retval = false;
        for (int i = 0; i < standardIDs.length; i++) {
            if (standardIDs[i].startsWith(standardName)) {
//            if (standardName.compareToIgnoreCase(standardIDs[i]) == 0) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    /**
     * @return the elementsByIsotopicMass
     */
    public String[] getElementsByIsotopicMass() {
        return elementsByIsotopicMass;
    }

    public String getStringListOfElementsByIsotopicMass() {
        String retVal = "[";

        for (int i = 0; i < elementsByIsotopicMass.length; i++) {
            retVal += elementsByIsotopicMass[i];
            if (i < (elementsByIsotopicMass.length - 1)) {
                retVal += ", ";
            }
        }
        retVal += "]";

        return retVal;
    }

    /**
     * @return the massSpecSetup
     */
    public AbstractMassSpecSetup getMassSpecSetup() {
        return massSpecSetup;
    }

    /**
     * @param massSpecSetup the massSpecSetup to set
     */
    public void setMassSpecSetup(AbstractMassSpecSetup massSpecSetup) {
        this.massSpecSetup = massSpecSetup;
    }
}
