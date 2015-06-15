/*
 * TripoliSessionInterface.java
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
package org.earthtime.Tripoli.sessions;

import Jama.Matrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.SortedSet;
import org.earthtime.Tripoli.dataModels.DownholeFractionationDataModel;
import org.earthtime.Tripoli.dataModels.MaskingSingleton;
import org.earthtime.Tripoli.dataModels.sessionModels.AbstractSessionForStandardDataModel;
import org.earthtime.Tripoli.dataModels.sessionModels.SessionCorrectedUnknownsSummary;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.massSpecSetups.AbstractMassSpecSetup;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.FractionSelectionTypeEnum;
import org.earthtime.dataDictionaries.FractionationTechniquesEnum;
import org.earthtime.dataDictionaries.IncludedTypeEnum;
import org.earthtime.dataDictionaries.RadRatios;
import org.earthtime.dataDictionaries.RawRatioNames;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author samuelbowring
 */
public interface TripoliSessionInterface extends TripoliSessionFractionationCalculatorInterface, Serializable {

    /**
     *
     * @return
     */
    FractionationTechniquesEnum getFractionationTechnique ();

    /**
     *
     * @param fractionationTechnique
     */
    void setFractionationTechnique ( FractionationTechniquesEnum fractionationTechnique );

    /**
     *
     */
    @Override
    void applyCorrections ();

    /**
     *
     * @return  
     */
    public boolean prepareMatrixJfPlotting (  );

    /**
     *
     * @param fractionSelectionTypeEnum the value of fractionSelectionTypeEnum
     * @return the boolean
     */
    public boolean prepareMatrixJfMapFractionsByType (FractionSelectionTypeEnum fractionSelectionTypeEnum);

    /**
     *
     * @param fitFunctionType
     * @return
     */
    public Matrix getMatrixJfPlottingActiveStandards ( FitFunctionTypeEnum fitFunctionType );

    /**
     *
     * @param estimatedPlottingPointsCount
     */
    public void setEstimatedPlottingPointsCount ( int estimatedPlottingPointsCount );

    /**
     *
     * @return
     */
    public double[] getTimesForPlotting ();
    
    /**
     *
     * @return
     */
    public String getCommonLeadCorrectionHighestLevelFromMasspec();

    /**
     *
     */
    @Override
    void calculateDownholeFitSummariesForPrimaryStandard ();

//    /**
//     *
//     */
//    @Override
//    void calculateSessionFitFunctionsForPrimaryStandard ();

    /**
     *
     */
    void clearAllFractionsOfLocalYAxis ();

    /**
     * @return the downholeFractionationAlphaDataModels
     */
    SortedMap<RawRatioNames, DownholeFractionationDataModel> getDownholeFractionationAlphaDataModels ();

    /**
     *
     * @return
     */
    AbstractMassSpecSetup getMassSpec ();

    /**
     * @return the rawDataFileHandler
     */
    AbstractRawDataFileHandler getRawDataFileHandler ();

    /**
     *
     * @return
     */
    SortedMap<RawRatioNames, AbstractSessionForStandardDataModel> getCurrentSessionForStandardsFractionation ();

    /**
     * @return the tripoliFractions
     */
    SortedSet<TripoliFraction> getTripoliFractions ();

    /**
     *
     * @param selection
     * @param visibility
     * @return
     */
    SortedSet<TripoliFraction> getTripoliFractionsFiltered ( FractionSelectionTypeEnum selection, IncludedTypeEnum visibility );

    /**
     *
     * @param fractionSelectionType
     */
    void includeAllFractions ( FractionSelectionTypeEnum fractionSelectionType );

    /**
     *
     */
    void includeAllAquisitions ();

    /**
     *
     * @param leftShadeCount the value of leftShadeCount
     */
    void processRawData (int leftShadeCount);
    void postProcessDataForCommonLeadLossPreparation();

    /**
     * @param rawDataFileHandler the rawDataFileHandler to set
     */
    void setRawDataFileHandler ( AbstractRawDataFileHandler rawDataFileHandler );

    /**
     * @param tripoliFractions the tripoliFractions to set
     */
    void setTripoliFractions ( SortedSet<TripoliFraction> tripoliFractions );

    /**
     *
     * @return
     */
    public ArrayList<AbstractTripoliSample> getTripoliSamples ();

    /**
     *
     * @param tripoliSamples
     */
    public void setTripoliSamples ( ArrayList<AbstractTripoliSample> tripoliSamples );

    /**
     *
     */
    public void updateFractionsToSampleMembership ();

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getPrimaryMineralStandard ();

    /**
     *
     * @param primaryMineralStandard
     */
    public void setPrimaryMineralStandard ( AbstractRatiosDataModel primaryMineralStandard );

    /**
     *
     */
    public void refreshMaskingArray ();

    /**
     *
     */
    public void applyMaskingArray ();

    /**
     *
     */
    public void reFitAllFractions ();

    /**
     *
     * @param setOD
     */
    public void setODforAllFractionsAllRatios ( boolean setOD );
    
    /**
     *
     * @return
     */
    public SortedMap<RadRatios, SessionCorrectedUnknownsSummary> getSessionCorrectedUnknownsSummaries ();
    
    /**
     *
     * @return
     */
    public MaskingSingleton getMaskingSingleton();
    
    /**
     *
     * @return
     */
    public boolean isDataProcessed();
    
    public void prepareForReductionAndCommonLeadCorrection();
    
    public void setLeftShadeCount(int leftShadeCount);
    
    public boolean isFitFunctionsUpToDate();
    
    public void setFitFunctionsUpToDate(boolean fitFunctionsUpToDate);

}
