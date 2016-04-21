/*
 * Copyright 2006-2016 CIRDLES.org.
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
package org.earthtime.Tripoli.rawDataFiles.handlers.shrimp;

import java.io.Serializable;
import org.earthtime.dataDictionaries.IsotopeNames;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class IsotopeRatioModelSHRIMP //
        implements Serializable {

    // Class variables
//    private static final long serialVersionUID = 3111511502335804607L;

    private IsotopeNames numerator;
    private IsotopeNames denominator;

    public IsotopeRatioModelSHRIMP(IsotopeNames numerator, IsotopeNames denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public boolean numeratorAtomicRatioLessThanDenominator(){
        return (numerator.getAtomicMass() < denominator.getAtomicMass());
    }

    /**
     * @return the numerator
     */
    public IsotopeNames getNumerator() {
        return numerator;
    }

    /**
     * @param numerator the numerator to set
     */
    public void setNumerator(IsotopeNames numerator) {
        this.numerator = numerator;
    }

    /**
     * @return the denominator
     */
    public IsotopeNames getDenominator() {
        return denominator;
    }

    /**
     * @param denominator the denominator to set
     */
    public void setDenominator(IsotopeNames denominator) {
        this.denominator = denominator;
    }
    
    

}
