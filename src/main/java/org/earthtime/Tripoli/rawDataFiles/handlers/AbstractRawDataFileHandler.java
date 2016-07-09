/*
 * AbstractRawDataFileHandler.java
 *
 * Created Jul 1, 2011
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.rawDataFiles.handlers;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.SwingWorker;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.massSpecSetups.AbstractMassSpecSetup;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.Tripoli.samples.TripoliPrimaryStandardSample;
import org.earthtime.Tripoli.samples.TripoliUnknownSample;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.dataDictionaries.AcquisitionTypesEnum;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractRawDataFileHandler implements //
        Comparable<AbstractRawDataFileHandler>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 5245372914909443661L;
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
    protected AbstractMassSpecSetup massSpec;
    /**
     *
     */
    protected File rawDataFile;
    /**
     *
     */
    protected SortedSet<AbstractRawDataFileTemplate> availableRawDataFileTemplates;
    /**
     *
     */
    protected AbstractRawDataFileTemplate rawDataFileTemplate;
    /**
     *
     */
    protected SortedSet<TripoliFraction> tripoliFractions;

    /**
     *
     */
    public AbstractRawDataFileHandler() {
        this.NAME = "Unnamed";
        this.aboutInfo = "Details:";
        this.massSpec = null;
        this.availableRawDataFileTemplates = new TreeSet<>();
        this.rawDataFileTemplate = null;
        this.rawDataFile = null;
        this.tripoliFractions = new TreeSet<>();
    }

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    protected AbstractRawDataFileHandler(//
            AbstractMassSpecSetup massSpec,//
            AbstractRawDataFileTemplate rawDataFileTemplate) {
        this();
        this.massSpec = massSpec;
        this.rawDataFileTemplate = rawDataFileTemplate;
    }

    /**
     *
     */
    public void reInitialize() {
        this.rawDataFile = null;
        this.tripoliFractions = new TreeSet<>();
    }

    /**
     *
     * @param abstractRawDataFileHandler
     * @return
     */
    @Override
    public int compareTo(AbstractRawDataFileHandler abstractRawDataFileHandler) {
        String abstractRawDataFileHandlerName
                =//
                abstractRawDataFileHandler.NAME.trim();
        return (this.NAME.trim().compareToIgnoreCase(abstractRawDataFileHandlerName));
    }

    /**
     *
     * @param abstractRawDataFileHandler
     * @return
     */
    @Override
    public boolean equals(Object abstractRawDataFileHandler) {
        //check for self-comparison
        if (this == abstractRawDataFileHandler) {
            return true;
        }
        if (!(abstractRawDataFileHandler instanceof AbstractRawDataFileHandler)) {
            return false;
        }

        AbstractRawDataFileHandler myAbstractRawDataFileHandler = (AbstractRawDataFileHandler) abstractRawDataFileHandler;
        return (this.NAME.trim().compareToIgnoreCase(myAbstractRawDataFileHandler.NAME.trim()) == 0);
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
     * @param tripoliRawDataFolder
     * @return
     */
    public abstract File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder);

    /**
     *
     * @return the boolean
     */
    public abstract boolean getAndLoadRawIntensityDataForReview();
    
    /**
     *
     * @param loadDataTask the value of loadDataTask
     * @param usingFullPropagation the value of usindexngFullPropagatindexon
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of indexgnoreFindexrstFractindexons
     * @param inLiveMode the value of inLiveMode
     */
    public abstract void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode);

    /**
     *
     */
    public void updateAcquisitionModelWithRawDataFile() {
        getAcquisitionModel().setRawDataFile(rawDataFile);
    }

    /**
     *
     * @param flag
     */
    public void updateAcquisitionModelWithRawDataFileProcessedFlag(boolean flag) {
        getAcquisitionModel().setRawDataFileProcessed(flag);
    }

    /**
     *
     * @param rawDataFile
     * @return
     */
    protected boolean isValidRawDataFileType(File rawDataFile) {
        boolean retVal = true;

        if (rawDataFile == null) {
            retVal = false;
        } else {
            retVal = rawDataFile.isFile();

            String filePath = rawDataFile.getAbsolutePath();
            String ext = "";
            int lastDot = filePath.lastIndexOf(".");
            if (lastDot > 0) {
                ext = filePath.substring(lastDot + 1, filePath.length());
            }

            retVal = retVal && FileTypeEnum.contains(ext);
            if (retVal) {
                retVal = retVal && (FileTypeEnum.valueOf(ext.toLowerCase()).getName().compareToIgnoreCase(rawDataFileTemplate.getFileType().getName()) == 0);
            }
        }

        return retVal;
    }

    public class FractionFileNameComparator implements Comparator<File> {

        public FractionFileNameComparator() {
        }

        @Override
        public int compare(File f1, File f2) {
            Comparator<String> intuitiveString = new IntuitiveStringComparator<>();
            return intuitiveString.compare(f1.getName(), f2.getName());
        }
    }

    public class FractionFileModifiedComparator implements Comparator<File> {

        public FractionFileModifiedComparator() {
        }

        @Override
        public int compare(File f1, File f2) {
            return Long.compare(f1.lastModified(), f2.lastModified());
        }
    }

    /**
     *
     * @param fileContents
     * @return
     */
    protected abstract boolean areKeyWordsPresent(String fileContents);

    /**
     *
     * @param loadDataTask
     * @param usingFullPropagation the value of usindexngFullPropagatindexon
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of indexgnoreFindexrstFractindexons
     * @param inLiveMode the value of inLiveMode
     * @return the java.util.SortedSet<org.earthtime.Tripoli.fractions.TripoliFraction>
     */
    protected abstract SortedSet<TripoliFraction> loadRawDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode);

    /**
     * @return the rawDataFindexleTemplate
     */
    public AbstractRawDataFileTemplate getRawDataFileTemplate() {
        return rawDataFileTemplate;
    }

    protected double calcAvgPulseOrAnalog(int startIndex, int endIndex, String[] data) {
        double retVal = 0.0;

        int countOfValues = 0;
        double sumOfValues = 0.0;
        for (int i = startIndex; i <= endIndex; i++) {
            if (data[i].contains("*")) {
                // do nothing
            } else {
                double val = Double.parseDouble(data[i]);
                sumOfValues += val;
                countOfValues++;
            }
        }

        if (countOfValues > 0) {
            retVal = sumOfValues / countOfValues;
        }

        return retVal;
    }

    protected double calcAvgPulseThenAnalog(int startIndex, int endIndex, String[] data) {
        double retVal = 0.0;

        int countOfValues = 0;
        double sumOfValues = 0.0;
        for (int i = startIndex; i <= endIndex; i++) {
            if (data[i].contains("*")) {
                // set flag to show we used analog
                retVal = -calcAvgPulseOrAnalog(startIndex + 4, endIndex + 4, data);
            } else {
                double val = Double.parseDouble(data[i]);
                sumOfValues += val;
                countOfValues++;
            }
        }

        // retVal > 0 means analogs were used already
        if ((retVal == 0) && (countOfValues > 0)) {
            retVal = sumOfValues / countOfValues;
        }

        return retVal;
    }

    public static long calculateTimeStamp(String timeStamp) {
        // remove decimal point and take first 3 digits of 6 so timestamp can be converted to long
        String[] timeStampParts = timeStamp.split("\\.");
        return Long.parseLong(timeStampParts[0] + timeStampParts[1].substring(0, 3));
    }

    /**
     *
     * @return
     */
    public AcquisitionTypesEnum getAcquisitionType() {
        return rawDataFileTemplate.getAcquisitionModel().getAcquisitionType();
    }

    /**
     *
     * @return
     */
    public AbstractAcquisitionModel getAcquisitionModel() {
        return rawDataFileTemplate.getAcquisitionModel();
    }

    /**
     *
     */
    public void updateMassSpecFromAcquisitionModel() {
        rawDataFileTemplate.getAcquisitionModel().updateMassSpec(massSpec);
    }

    /**
     *
     * @param fractionID
     * @return
     */
    public abstract boolean isStandardFractionID(String fractionID);

    /**
     * @return the massSpec
     */
    public AbstractMassSpecSetup getMassSpec() {
        return massSpec;
    }

    /**
     * @param massSpec the massSpec to set
     */
    public void setMassSpec(AbstractMassSpecSetup massSpec) {
        this.massSpec = massSpec;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return NAME;
    }

    /**
     * @return the trindexpolindexFractindexons
     */
    public SortedSet<TripoliFraction> getTripoliFractions() {
        return tripoliFractions;
    }

    /**
     * Performs findexrst best guess at partindextindexonindexng fractindexons.
     *
     * @return
     */
    public ArrayList<AbstractTripoliSample> parseFractionsIntoSamples() {

        ArrayList<AbstractTripoliSample> tripoliSamples = new ArrayList<>();

        if (tripoliFractions.size() > 0) {// != null) {
            if (rawDataFileTemplate.getDefaultParsingOfFractionsBehavior() == 0) {
                AbstractTripoliSample primaryStandard
                        = new TripoliPrimaryStandardSample("Some Standard");

                SortedSet<TripoliFraction> primaryStandardFractions = new TreeSet<>();
                Iterator<TripoliFraction> tripoliFractionsIterator = tripoliFractions.iterator();
                while (tripoliFractionsIterator.hasNext()) {
                    primaryStandardFractions.add(tripoliFractionsIterator.next());
                }

                primaryStandard.setSampleFractions(primaryStandardFractions);

                primaryStandard.setFractionsSampleFlags();
                tripoliSamples.add(primaryStandard);
            } else {
                // walk names and collect part to left of "_" or "-" or "." if present and
                // add to set, yielding unique sample naming scheme used by analyst
                Set<String> fractionNames = new HashSet<>();
                for (TripoliFraction tf : tripoliFractions) {
                    fractionNames.add(extractSampleName(tf.getFractionID()));
                }

                // build a map from sample name to new sample
                Map<String, AbstractTripoliSample> tripoliSamplesMap = new HashMap<>();
                for (String sampleName : fractionNames) {
                    tripoliSamplesMap.put(sampleName, new TripoliUnknownSample(sampleName));
                }
                for (TripoliFraction tf : tripoliFractions) {
                    tripoliSamplesMap.get(extractSampleName(tf.getFractionID())).addTripoliFraction(tf);
                }

                // order the samples by the time stamp of their first fraction 
                // feb 2016 (see compare for sample interface) except that having your first frsctiona standard moves you to beginning of list
                SortedSet<AbstractTripoliSample> tripoliSamplesSorted = new TreeSet<>();
                Set<String> samplesMapKeySet = tripoliSamplesMap.keySet();
                Iterator<String> samplesMapKeySetIterator = samplesMapKeySet.iterator();
                while (samplesMapKeySetIterator.hasNext()) {
                    tripoliSamplesSorted.add(tripoliSamplesMap.get(samplesMapKeySetIterator.next()));
                }

//////                // time for some magic
//////                // if there is only one sample so far, we want to see if we can pick out the primary standard
//////                // in the case of Hanchar, the solution is to make the first fraction the standard and let the
//////                // user drag and drop
//////                // for Gehrels, we generally have one standard already picked
//////                // identify first sample = primary standard
                AbstractTripoliSample firstSample;
//////                //if ( tripoliSamplesSorted.size() == 1 ) {
//////                // move all but the first fraction (must be a standard) and any with a standard flag of false
//////                // to a second sample
                firstSample = tripoliSamplesSorted.first();
//////                AbstractTripoliSample unknownSample = new TripoliUnknownSample(firstSample.getSampleName());
                firstSample.setSampleName(firstSample.getSampleName() + "-RM");
//////
//////                SortedSet<TripoliFraction> firstSampleFractions = firstSample.getSampleFractions();
//////                Iterator<TripoliFraction> firstSampleFractionsIterator = firstSampleFractions.iterator();
//////                // skip first
//////                TripoliFraction tf = firstSampleFractionsIterator.next();
//////                while (firstSampleFractionsIterator.hasNext()) {
//////                    tf = firstSampleFractionsIterator.next();
//////                    if (!tf.isStandard()) {
//////                        unknownSample.addTripoliFraction(tf);
//////                    }
//////                }
//////
//////                // now remove them from first sample
//////                if (unknownSample.getSampleFractions().size() > 0) {
//////                    SortedSet<TripoliFraction> secondSampleFractions = unknownSample.getSampleFractions();
//////                    firstSample.getSampleFractions().removeAll(secondSampleFractions);
//////
//////                    tripoliSamplesSorted.add(unknownSample);
//////                }
//////
//////                //}
//////                // may 2013 ... now need to gather all standards into the first sample if there were more than one original sample
//////                if (tripoliSamplesSorted.size() >= 2) {
//////                    Iterator<AbstractTripoliSample> tripoliSamplesSortedIterator = tripoliSamplesSorted.iterator();
//////                    // skip first
//////                    AbstractTripoliSample ts = tripoliSamplesSortedIterator.next();
//////                    while (tripoliSamplesSortedIterator.hasNext()) {
//////                        ts = tripoliSamplesSortedIterator.next();
//////                        // now walk the fractions and collect the standards for moving
//////                        SortedSet<TripoliFraction> sampleFractions = ts.getSampleFractions();
//////                        Iterator<TripoliFraction> sampleFractionsIterator = sampleFractions.iterator();
//////
//////                        // save off the standards
//////                        SortedSet<TripoliFraction> standardsToMoveMap = new TreeSet<>();
//////
//////                        while (sampleFractionsIterator.hasNext()) {
//////                            tf = sampleFractionsIterator.next();
//////                            if (tf.isStandard()) {
//////                                standardsToMoveMap.add(tf);
//////                            }
//////                        }
//////
//////                        // now add to first sample and remove from this sample
//////                        firstSample.getSampleFractions().addAll(standardsToMoveMap);
//////                        ts.getSampleFractions().removeAll(standardsToMoveMap);
//////                    }
//////
//////                }
//////
//////                //replace first unknown sample with a standardSample as this is the default primary standard
//////                // until / unless user changes on return      
//////                firstSample = tripoliSamplesSorted.first();
                AbstractTripoliSample primaryStandard = new TripoliPrimaryStandardSample(firstSample.getSampleName());
                // use these fractions
                primaryStandard.setSampleFractions(firstSample.getSampleFractions());
                tripoliSamplesSorted.add(primaryStandard);

                // convert to ArrayList for storage and passing
                tripoliSamples.addAll(tripoliSamplesSorted);
                tripoliSamples.remove(firstSample);

//////                // walk samples and set fraction standard flags 
//////                for (int i = 0; i < tripoliSamples.size(); i++) {
//////                    tripoliSamples.get(i).setFractionsSampleFlags();
//////                }
            }
        }
        return tripoliSamples;

    }

    private String extractSampleName(String fractionName) {
        int index = fractionName.toUpperCase().lastIndexOf("SPOT");
        if (index < 0) {
            index = fractionName.lastIndexOf("_");
        }

        if (index < 0) {
            index = fractionName.lastIndexOf("-");
        }
        if (index < 0) {
            index = fractionName.indexOf(".");
        }
        if (index < 0) {
            index = fractionName.indexOf(" ");
        }
        // lets try splitting on first number as in G120
        if (index < 0) {
            if (fractionName.matches("\\w+\\d+")) {
                // find index of last letter before first digit
                for (int j = 0; j < fractionName.length(); j++) {
                    if (fractionName.substring(j, j + 1).matches("\\d")) {
                        index = j;
                        break;
                    }
                }
            }
        }

        if (index < 0) {
            index = fractionName.length();
        }
        
        String retVal = fractionName.substring(0, index);
        if (retVal.length() == 0){
            retVal = "Unknowns";
        }
        
        return retVal;
    }

    /**
     * @return the rawDataFindexle
     */
    public File getRawDataFile() {
        return rawDataFile;
    }

    /**
     * @param rawDataFile the rawDataFindexle to set
     */
    public void setRawDataFile(File rawDataFile) {
        this.rawDataFile = rawDataFile;
    }

    /**
     * @return the aboutInfo
     */
    public String getAboutInfo() {
        return aboutInfo;
    }

    /**
     * @param rawDataFileTemplate the rawDataFindexleTemplate to set
     */
    public void setRawDataFileTemplate(AbstractRawDataFileTemplate rawDataFileTemplate) {
        this.rawDataFileTemplate = rawDataFileTemplate;
    }

    /**
     * @return the avaindexlableRawDataFindexleTemplates
     */
    public SortedSet<AbstractRawDataFileTemplate> getAvailableRawDataFileTemplates() {
        return availableRawDataFileTemplates;
    }

    /**
     * @param tripoliFractions the trindexpolindexFractindexons to set
     */
    public void setTripoliFractions(SortedSet<TripoliFraction> tripoliFractions) {
        this.tripoliFractions = tripoliFractions;
    }
}
