/*
 * Copyright 2006-2017 CIRDLES.org.
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
package org.earthtime.aliquots;

import java.math.BigDecimal;
import java.util.Vector;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface AliquotForUPbInterface {

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
     * @param pbBlankNameAndVersion
     * @return
     */
    public default AbstractRatiosDataModel getAPbBlank(String pbBlankNameAndVersion) {
        AbstractRatiosDataModel retVal = null;
        for (AbstractRatiosDataModel pbb : getPbBlanks()) {
            if (pbb.getNameAndVersion().equalsIgnoreCase(pbBlankNameAndVersion.trim())) {
                retVal = pbb;
            }
        }
        return retVal;
    }

    /**
     * @return the bestAgeDivider206_238
     */
    public BigDecimal getBestAgeDivider206_238();

    /**
     * @param bestAgeDivider206_238 the bestAgeDivider206_238 to set
     */
    public void setBestAgeDivider206_238(BigDecimal bestAgeDivider206_238);

    /**
     *
     * @return
     */
    public BigDecimal getCalibrationUnct206_238();

    /**
     *
     * @param calibrationUnct206_238
     */
    public void setCalibrationUnct206_238(BigDecimal calibrationUnct206_238);

    /**
     *
     * @return
     */
    public BigDecimal getCalibrationUnct208_232();

    /**
     *
     * @param calibrationUnct208_232
     */
    public void setCalibrationUnct208_232(BigDecimal calibrationUnct208_232);

    /**
     *
     * @return
     */
    public BigDecimal getCalibrationUnct207_206();

    /**
     *
     * @param calibrationUnct207_206
     */
    public void setCalibrationUnct207_206(BigDecimal calibrationUnct207_206);

    /**
     *
     * @param alphaPbModelName
     * @return
     */
    public default ValueModel getAnAlphaPbModel(String alphaPbModelName) {
        ValueModel retVal = null;
        for (ValueModel apbm : getAlphaPbModels()) {
            if (apbm.getName().equalsIgnoreCase(alphaPbModelName.trim())) {
                retVal = apbm;
            }
        }
        return retVal;
    }

    /**
     * @return the alphaPbModels
     */
    public Vector<ValueModel> getAlphaPbModels();

    /**
     *
     * @param alphaUModelName
     * @return
     */
    public default ValueModel getAnAlphaUModel(String alphaUModelName) {
        ValueModel retVal = null;
        for (ValueModel aum : getAlphaUModels()) {
            if (aum.getName().equalsIgnoreCase(alphaUModelName.trim())) {
                retVal = aum;
            }
        }
        return retVal;
    }

    /**
     * @return the alphaUModels
     */
    public Vector<ValueModel> getAlphaUModels();



}
