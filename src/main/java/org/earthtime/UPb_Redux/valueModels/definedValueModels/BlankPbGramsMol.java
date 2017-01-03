/*
 * BlankPbGramsMol.java
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.valueModels.definedValueModels;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.Intermediates;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;

/**
 *
 * @author James F. Bowring
 */
public class BlankPbGramsMol extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 3306066041280801837L;
    private final static String NAME = Intermediates.blankPbGramsMol.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private AbstractRatiosDataModel pbBlank;
    private AbstractRatiosDataModel physicalConstants;

    /** Creates a new instance of BlankPbGramsMol */
    public BlankPbGramsMol () {
        super( NAME, UNCT_TYPE );
        this.pbBlank =  PbBlankICModel.createNewInstance();
        this.physicalConstants = PhysicalConstantsModel.createNewInstance();
    }

    /**
     * 
     * @param pbBlank
     * @param physicalConstants
     */
    public BlankPbGramsMol ( AbstractRatiosDataModel pbBlank, AbstractRatiosDataModel physicalConstants ) {
        this();
        this.pbBlank = pbBlank;
        this.physicalConstants = physicalConstants;
    }

    /**
     * 
     * @param inputValueModels
     * @param parDerivTerms
     */
    @Override
    public void calculateValue (
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms ) {

        setValue(//
                ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol204" ).getValue()//
                .add(
                pbBlank.getDatumByName( "r206_204b" ).getValue()//
                .multiply( ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol206" ).getValue() ) ).//

                add(
                pbBlank.getDatumByName( "r207_204b" ).getValue()//
                .multiply( ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol207" ).getValue() ) ).//

                add(
                pbBlank.getDatumByName( "r208_204b" ).getValue()//
                .multiply( ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol208" ).getValue() ) ) );

       setValueTree(//
                ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol204" ).getValueTree()//
                .add(
                pbBlank.getDatumByName( "r206_204b" ).getValueTree()//
                .multiply( ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol206" ).getValueTree() ) ).//

                add(
                pbBlank.getDatumByName( "r207_204b" ).getValueTree()//
                .multiply( ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol207" ).getValueTree() ) ).//

                add(
                pbBlank.getDatumByName( "r208_204b" ).getValueTree()//
                .multiply( ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol208" ).getValueTree() ) ) );

        try {
            BigDecimal dBlankPbGramsMol__dR206_204b = //
                    ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol206" ).getValue();
            parDerivTerms.put( "dBlankPbGramsMol__dR206_204b", dBlankPbGramsMol__dR206_204b );
        } catch (Exception e) {
            parDerivTerms.put( "dBlankPbGramsMol__dR206_204b", BigDecimal.ZERO );
        }
        try {
            BigDecimal dBlankPbGramsMol__dR207_204b = //
                    ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol207" ).getValue();
            parDerivTerms.put( "dBlankPbGramsMol__dR207_204b", dBlankPbGramsMol__dR207_204b );
        } catch (Exception e) {
            parDerivTerms.put( "dBlankPbGramsMol__dR207_204b", BigDecimal.ZERO );
        }
        try {
            BigDecimal dBlankPbGramsMol__dR208_204b = //
                    ((PhysicalConstantsModel)physicalConstants).getAtomicMolarMassByName( "gmol208" ).getValue();
            parDerivTerms.put( "dBlankPbGramsMol__dR208_204b", dBlankPbGramsMol__dR208_204b );
        } catch (Exception e) {
            parDerivTerms.put( "dBlankPbGramsMol__dR208_204b", BigDecimal.ZERO );
        }
    }
}
