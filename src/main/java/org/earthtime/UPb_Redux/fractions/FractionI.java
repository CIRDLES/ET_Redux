/*
 * FractionI.java
 *
 * Created on August 2, 2007, 10:05 AM
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

package org.earthtime.UPb_Redux.fractions;

import java.math.BigDecimal;
import java.util.Date;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public interface FractionI {
    
    // accessors
    
    /**
     * 
     * @return
     */
    abstract String getSampleName();
    /**
     * 
     * @param sampleName
     */
    abstract void setSampleName(String sampleName);
    
    /**
     * 
     * @return
     */
    abstract String getFractionID();
    /**
     * 
     * @param fractionID
     */
    abstract void setFractionID(String fractionID);
    
    /**
     * 
     * @return
     */
    abstract String getImageURL();
    /**
     * 
     * @param imageURL
     */
    abstract void setImageURL(String imageURL);
    
    /**
     * 
     * @return
     */
    abstract Date getTimeStamp();
    /**
     * 
     * @param timeStamp
     */
    abstract void setTimeStamp(Date timeStamp);
    
    /**
     * 
     * @return
     */
    abstract String getMineralName();
    /**
     * 
     * @param mineralName
     */
    abstract void setMineralName(String mineralName);
    
    /**
     * 
     * @return
     */
    abstract String getSettingType();
    /**
     * 
     * @param settingType
     */
    abstract void setSettingType(String settingType);
    
    /**
     * 
     * @return
     */
    abstract int getNumberOfGrains();
    /**
     * 
     * @param numberOfGrains
     */
    abstract void setNumberOfGrains(int numberOfGrains);
    
    /**
     * 
     * @return
     */
    abstract BigDecimal getEstimatedDate();
    /**
     * 
     * @param estimatedAge
     */
    abstract void setEstimatedDate(BigDecimal estimatedAge);
    
    /**
     * 
     * @return
     */
    abstract boolean isPhysicallyAbraded();
    /**
     * 
     * @param physicallyAbraded
     */
    abstract void setPhysicallyAbraded(boolean physicallyAbraded);
    
    /**
     * 
     * @return
     */
    abstract boolean isLeachedInHFAcid();
    /**
     * 
     * @param leachedInHFAcid
     */
    abstract void setLeachedInHFAcid(boolean leachedInHFAcid);
    
    /**
     * 
     * @return
     */
    abstract boolean isAnnealedAndChemicallyAbraded();
    /**
     * 
     * @param annealedAndChemicallyAbraded
     */
    abstract void setAnnealedAndChemicallyAbraded(boolean annealedAndChemicallyAbraded);
    
    /**
     * 
     * @return
     */
    abstract boolean isChemicallyPurifiedUPb();
    /**
     * 
     * @param chemicallyPurifiedUPb
     */
    abstract void setChemicallyPurifiedUPb(boolean chemicallyPurifiedUPb);
    
    /**
     * 
     * @return
     */
    abstract String getAnalysisFractionComment();
    /**
     * 
     * @param analysisFractionComment
     */
    abstract void setAnalysisFractionComment(String analysisFractionComment);
    
    /**
     * 
     * @return
     */
    abstract String getPbBlankID();
    /**
     * 
     * @param pbBlankID
     */
    abstract void setPbBlankID(String pbBlankID);
    
    /**
     * 
     * @return
     */
    abstract String getTracerID();
    /**
     * 
     * @param tracerID
     */
    abstract void setTracerID(String tracerID);
    
    /**
     * 
     * @return
     */
    abstract AbstractRatiosDataModel getInitialPbModel();
    /**
     * 
     * @param initialPbModel
     */
    abstract void setInitialPbModel(AbstractRatiosDataModel initialPbModel);

    /**
     * 
     * @return
     */
    abstract String getPbCollectorType();
    /**
     * 
     * @param pbCollectorType
     */
    abstract void setPbCollectorType(String pbCollectorType);

    /**
     * 
     * @return
     */
    abstract String getUCollectorType();
    /**
     * 
     * @param uCollectorType
     */
    abstract void setUCollectorType(String uCollectorType);
        
    /**
     * 
     * @return
     */
    abstract ValueModel[] getMeasuredRatios();
    /**
     * 
     * @param measuredRatio
     */
    abstract void setMeasuredRatios(ValueModel[] measuredRatio);
    
    /**
     * 
     * @return
     */
    abstract ValueModel[] getRadiogenicIsotopeRatios();
    /**
     * 
     * @param radiogenicIsotopeRatios
     */
    abstract void setRadiogenicIsotopeRatios(ValueModel[] radiogenicIsotopeRatios);
    
    /**
     * 
     * @return
     */
    abstract ValueModel[] getCompositionalMeasures();
    /**
     * 
     * @param compositionalMeasures
     */
    abstract void setCompositionalMeasures(ValueModel[] compositionalMeasures);
    
    /**
     * 
     * @param ratioName
     * @return
     */
    abstract ValueModel getMeasuredRatioByName(String ratioName);
    
    /**
     *
     * @param myMeasuredRatio
     * @return
     */
    abstract ValueModel getMeasuredRatioByName(MeasuredRatios myMeasuredRatio);
    

    
    
}
