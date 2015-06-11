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
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;

/**
 * Designed for Uranium Lead
 * @author James F. Bowring
 */
public interface FractionI extends ETFractionInterface {
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
    abstract boolean isFractionationCorrectedU();

    /**
     * @param fractionationCorrectedU the fractionationCorrectedU to set
     */
    public void setFractionationCorrectedU(boolean fractionationCorrectedU);

    /**
     *
     * @return
     */
    abstract boolean isFractionationCorrectedPb();

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
     * @return the staceyKramersCorrelationCoeffs
     */
    public BigDecimal getStaceyKramersCorrelationCoeffs();

    /**
     * @return the staceyKramersOnePctUnct
     */
    public BigDecimal getStaceyKramersOnePctUnct();

    /**
     *
     * @return
     */
    public boolean isZircon();

    /**
     *
     * @param zircon
     */
    public void setZircon(boolean zircon);

    /**
     * @param staceyKramersOnePctUnct the staceyKramersOnePctUnct to set
     */
    public void setStaceyKramersOnePctUnct(BigDecimal staceyKramersOnePctUnct);

    /**
     * @param staceyKramersCorrelationCoeffs the staceyKramersCorrelationCoeffs
     * to set
     */
    public void setStaceyKramersCorrelationCoeffs(BigDecimal staceyKramersCorrelationCoeffs);

    /**
     *
     * @return
     */
    public String getAlphaPbModelID();

    /**
     *
     * @param alphaPbModelID
     */
    public void setAlphaPbModelID(String alphaPbModelID);

    /**
     *
     * @return
     */
    public String getAlphaUModelID();

    /**
     *
     * @param alphaUModelID
     */
    public void setAlphaUModelID(String alphaUModelID);
        
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
     */
    public default void calculateStaceyKramersInitialPbModelValues() {
        if (getInitialPbModel() instanceof StaceyKramersInitialPbModelET) {

            AbstractRatiosDataModel physicalConstantsModel;
            try {
                physicalConstantsModel = //
                        ReduxLabData.getInstance().getAPhysicalConstantsModel(getPhysicalConstantsModelID());

                ((StaceyKramersInitialPbModelET) getInitialPbModel()).calculateRatios(
                        getEstimatedDate(),
                        physicalConstantsModel.getDatumByName(Lambdas.lambda238.getName()).getValue(),
                        physicalConstantsModel.getDatumByName(Lambdas.lambda235.getName()).getValue(),
                        physicalConstantsModel.getDatumByName(Lambdas.lambda232.getName()).getValue());

                // set ratio uncertainties based on pct uncertainty
                ((StaceyKramersInitialPbModelET) getInitialPbModel()).calculateUncertaintiesAndRhos(getStaceyKramersOnePctUnct(),
                        getStaceyKramersCorrelationCoeffs(), BigDecimal.ZERO, BigDecimal.ZERO);
            } catch (BadLabDataException badLabDataException) {
            }

            getInitialPbModel().initializeModel();
        }
    }
}
