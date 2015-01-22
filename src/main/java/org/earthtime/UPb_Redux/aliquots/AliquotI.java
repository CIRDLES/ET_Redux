/*
 * AliquotI.java
 *
 * Created on July 23, 2007, 12:52 PM
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

package org.earthtime.UPb_Redux.aliquots;

import java.util.Vector;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public interface AliquotI {
    
    // Field accessors
    /**
     * 
     * @return
     */
    abstract String getSampleIGSN();
    /**
     * 
     * @param sampleIGSN
     */
    abstract void setSampleIGSN(String sampleIGSN);
    
    /**
     * 
     * @return
     */
    abstract String getAliquotIGSN();
    /**
     * 
     * @param aliquotIGSN
     */
    abstract void setAliquotIGSN(String aliquotIGSN);
    
    /**
     * 
     * @return
     */
    abstract String getLaboratoryName();
    /**
     * 
     * @param laboratoryName
     */
    abstract void setLaboratoryName(String laboratoryName);
    
    /**
     * 
     * @return
     */
    abstract String getAnalystName();
    /**
     * 
     * @param analystName
     */
    abstract void setAnalystName(String analystName);
    
    /**
     * 
     * @return
     */
    abstract String getAliquotReference();
    /**
     * 
     * @param aliquotReference
     */
    abstract void setAliquotReference(String aliquotReference);

    /**
     * 
     * @return
     */
    abstract String getAliquotInstrumentalMethod();
    /**
     * 
     * @param aliquotInstrumentalMethod
     */
    abstract void setAliquotInstrumentalMethod(String aliquotInstrumentalMethod);
    
    /**
     * 
     * @return
     */
    abstract String getAliquotInstrumentalMethodReference();
    /**
     * 
     * @param aliquotInstrumentalMethodReference
     */
    abstract void setAliquotInstrumentalMethodReference(String aliquotInstrumentalMethodReference);

    /**
     * 
     * @return
     */
    abstract String getAliquotComment();
    /**
     * 
     * @param aliquotComment
     */
    abstract void setAliquotComment(String aliquotComment);

    /**
     * 
     * @return
     */
    abstract Vector<ValueModel> getSampleDateModels() ;
    /**
     * 
     * @param sampleAgeModels
     */
    abstract void setSampleDateModels(Vector<ValueModel> sampleAgeModels);
    
    /**
     * 
     * @return
     */
    abstract AbstractRatiosDataModel getPhysicalConstants();
    /**
     * 
     * @param physicalConstants
     */
    abstract void setPhysicalConstants(AbstractRatiosDataModel physicalConstants);
   
    /**
     * 
     * @return
     */
    abstract Vector<AbstractRatiosDataModel> getPbBlanks();
    /**
     * 
     * @param pbBlanks
     */
    abstract void setPbBlanks(Vector<AbstractRatiosDataModel> pbBlanks);

    /**
     * 
     * @return
     */
    abstract Vector<AbstractRatiosDataModel> getTracers();
    /**
     * 
     * @param tracers
     */
    abstract void setTracers(Vector<AbstractRatiosDataModel> tracers);
    
    /**
     * 
     * @return
     */
    abstract Vector<AbstractRatiosDataModel> getMineralStandardModels();
    /**
     * 
     * @param MineralStandards
     */
    abstract void setMineralStandardModels(Vector<AbstractRatiosDataModel> MineralStandards);
    
        
}

