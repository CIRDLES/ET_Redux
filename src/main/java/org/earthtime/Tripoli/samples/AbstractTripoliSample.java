/*
 * AbstractTripoliSample.java
 *
 * Created Jan 11, 2012
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
package org.earthtime.Tripoli.samples;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.TreeSet;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.AbstractCommonLeadLossCorrectionScheme;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractTripoliSample implements //
        Comparable<AbstractTripoliSample>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -3499278795384175527L;
    // Instance variables
    /**
     *
     */
    protected String sampleName;
    /**
     *
     */
    protected boolean primaryStandard;
    /**
     *
     */
    protected boolean secondaryStandard;
    /**
     *
     */
    protected AbstractRatiosDataModel mineralStandardModel;
    /**
     *
     */
    protected SortedSet<TripoliFraction> sampleFractions;
    protected ValueModel sampleR238_235s;

    /**
     *
     * @param sampleName
     */
    public AbstractTripoliSample(String sampleName) {
        this.sampleName = sampleName;
        this.primaryStandard = false;
        this.secondaryStandard = false;
        this.mineralStandardModel = null;
        this.sampleFractions = new TreeSet<>();
        // nov 2014 default for now
        this.sampleR238_235s = new ValueModel("r238_235s", new BigDecimal(137.818), "ABS", new BigDecimal(0.022), BigDecimal.ZERO);
    }

    /**
     *
     * @param ts
     * @return
     */
    @Override
    public int compareTo(AbstractTripoliSample ts) {
        long tsTimeStamp = 0l;
        long myTsTimeStamp = 0l;

        int returnValue = 0;
        try {
            tsTimeStamp = ts.getSampleFractions().first().getPeakTimeStamp();
            myTsTimeStamp = this.getSampleFractions().first().getPeakTimeStamp();
        } catch (Exception e) {
            returnValue = -1;
        }

        // force reference material (standard) to beginning of list
        if (ts.getSampleFractions().first().isStandard()) {
            returnValue = 1;
        } else if (this.getSampleFractions().first().isStandard()) {
            returnValue = -1;
        } else {
            returnValue = ((Long) myTsTimeStamp).compareTo((Long) tsTimeStamp);
        }

        return returnValue;
    }

    /**
     *
     */
    public void setFractionsSampleFlags() {
        for (TripoliFraction tf : sampleFractions) {
            tf.setStandard(primaryStandard);
            tf.setSecondaryReferenceMaterial(secondaryStandard);
        }
    }

    /**
     *
     * @param tripoliFraction
     */
    public void addTripoliFraction(TripoliFraction tripoliFraction) {
        sampleFractions.add(tripoliFraction);
    }

    /**
     *
     * @param tripoliFraction
     */
    public void removeTripoliFraction(TripoliFraction tripoliFraction) {
        sampleFractions.remove(tripoliFraction);
    }

    /**
     *
     * @param commonLeadLossCorrectionScheme
     */
    public void setCommonLeadLossCorrectionSchemaForAllFractions(AbstractCommonLeadLossCorrectionScheme commonLeadLossCorrectionScheme) {
        for (TripoliFraction tf : sampleFractions) {
            tf.setCommonLeadLossCorrectionScheme(commonLeadLossCorrectionScheme);
        }
    }

    /**
     *
     */
    public void setDefaultInitialPbModelForAllFractions() {
        for (TripoliFraction tf : sampleFractions) {
            if (tf.getInitialPbModelET() == null) {
                tf.setInitialPbModelET(StaceyKramersInitialPbModelET.getStaceyKramersInstance());
            }
        }
    }

    /**
     *
     * @param initialPbModelET
     */
    public void setInitialPbModelForAllFractions(AbstractRatiosDataModel initialPbModelET) {
        for (TripoliFraction tf : sampleFractions) {
            if (tf.getCommonLeadLossCorrectionScheme().getName().compareToIgnoreCase("A2") != 0) {
                tf.setInitialPbModelET(initialPbModelET);
            } else {
                tf.setInitialPbModelET(StaceyKramersInitialPbModelET.getStaceyKramersInstance());
            }
        }
    }

    /**
     *
     * @param staceyKramersOnePctUnct
     */
    public void setStaceyKramersOnePctUnctForAllFractions(BigDecimal staceyKramersOnePctUnct) {
        for (TripoliFraction tf : sampleFractions) {
            tf.setSkOneSigmaVarUnctPct(staceyKramersOnePctUnct);
        }
    }

    /**
     *
     * @param staceyKramersRho
     */
    public void setStaceyKramersCorrelationCoeffsForAllFractions(BigDecimal staceyKramersRho) {
        for (TripoliFraction tf : sampleFractions) {
            tf.setSkRhoVarUnct(staceyKramersRho);
        }
    }

    /**
     *
     * @param staceyKramersEstimatedDate
     */
    public void setStaceyKramersstaceyKramersEstimatedDateForAllFractions(BigDecimal staceyKramersEstimatedDate) {
        for (TripoliFraction tf : sampleFractions) {
            tf.setSkEstimatedDate(staceyKramersEstimatedDate);
        }
    }

    /**
     * @return the sampleName
     */
    public String getSampleName() {
        return sampleName;
    }

    /**
     * @param sampleName the sampleName to set
     */
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    /**
     * @return the primaryStandard
     */
    public boolean isPrimaryReferenceMaterial() {
        return primaryStandard;
    }

    /**
     * @param primaryStandard the primaryStandard to set
     */
    public void setPrimaryReferenceMaterial(boolean primaryStandard) {
        this.primaryStandard = primaryStandard;
    }

    /**
     * @return the sampleFractions
     */
    public SortedSet<TripoliFraction> getSampleFractions() {
        return sampleFractions;
    }

    /**
     * @param sampleFractions the sampleFractions to set
     */
    public void setSampleFractions(SortedSet<TripoliFraction> sampleFractions) {
        this.sampleFractions = sampleFractions;

    }

    /**
     * @return the secondaryStandard
     */
    public boolean isSecondaryReferenceMaterial() {
        return secondaryStandard;
    }

    /**
     * @param secondaryStandard the secondaryStandard to set
     */
    public void setSecondaryReferenceMaterial(boolean secondaryStandard) {
        this.secondaryStandard = secondaryStandard;
    }

    /**
     * @return the mineralStandardModel
     */
    public AbstractRatiosDataModel getMineralStandardModel() {
        return mineralStandardModel;
    }

    /**
     * @param mineralStandardModel the mineralStandardModel to set
     */
    public void setMineralStandardModel(AbstractRatiosDataModel mineralStandardModel) {
        this.mineralStandardModel = mineralStandardModel;
    }

    @Override
    // used for list population
    public String toString() {
        return sampleName;// + (getSampleFractions().first().isStandard() ? " (primary ref mat)" : "");
    }

    /**
     * @return the sampleR238_235s
     */
    public ValueModel getSampleR238_235s() {
        return sampleR238_235s;
    }

    /**
     * @param sampleR238_235s the sampleR238_235s to set
     */
    public void setSampleR238_235s(ValueModel sampleR238_235s) {
        this.sampleR238_235s = sampleR238_235s;
    }
}
