/*
 * CommonLeadLossCorrectionSchemeB1.java
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
package org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes;

import java.math.BigDecimal;
import java.util.SortedMap;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class CommonLeadLossCorrectionSchemeB1 extends AbstractCommonLeadLossCorrectionScheme {

    // Class variables
    private static final long serialVersionUID = -4795592431036788024L;
    private static CommonLeadLossCorrectionSchemeB1 instance = null;

    private CommonLeadLossCorrectionSchemeB1() {
        super("B1", false);
    }

    /**
     *
     * @return
     */
    public static CommonLeadLossCorrectionSchemeB1 getInstance() {
        if (instance == null) {
            instance = new CommonLeadLossCorrectionSchemeB1();
        }
        return instance;
    }

    /**
     *
     * @param parameters the value of parameterz
     * @param staceyKramerCorrectionParameters the value of
     * staceyKramerCorrectionParameters
     * @param useStaceyKramer the value of useStaceyKramer
     * @param r238_235sVM the value of parameters
     * @param lambda235VM the value of r238_235s
     * @param lambda238VM the value of lambda235
     * @return
     */
    @Override
    public ValueModel calculatePbCorrectedAge(SortedMap<String, ValueModel> parameters, SortedMap<String, BigDecimal> staceyKramerCorrectionParameters, boolean useStaceyKramer, ValueModel r238_235sVM, ValueModel lambda235VM, ValueModel lambda238VM) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        /* To perform the common Pb correction, calculate the following quantities:

         First, add the 206/204, 207/204, and 208/204 ratios to the ‘Fractionation-correct Unknowns’ tab.

         Let upperPhi_r206_207 be the data dictionary term for the variable Φ(t) = log(stdtrue) - log(stdmeas) calculated 
         for the standard 206/207 measurements, interpolated at the time of the present unknown.  

         upperPhi_r206_204 = upperPhi_r206_207 * log(m206/m204)/log(m206/m207)
         upperPhi_r207_204 = upperPhi_r206_207 * log(m207/m204)/log(m206/m207)
         upperPhi_r208_204 = upperPhi_r206_207 * log(m208/m204)/log(m206/m207)

         where m204, m206, m207, and m208 are the atomic masses of 204Pb, 206Pb, 207Pb, and 208Pb, respectively.

         Now fractionation-correct the measured 206/204, 207/204, and 208/204 mean/intercepts for the current unknown using the formula
         log(unkcorr) = log(unkmeas) + upperPhi_rX
         where X is the ratio of interest.

         You should now have the fractionation-corrected 206/204, 207/204, and 208/204 log-ratios.  
         Use the exponential function to transform these into isotope ratios, and you already also additionally 
         have the fractionation-corrected unknown 206/207, 206/238, and 208/232 ratios from Section 10.

         Now perform the following calculations for Pbc-corrected ratios (rX_X_PbcCorr)

         r207_206_PbcCorr = (r207_204fc - r207_204c)/(r206_204fc - r206_204c)
         r206_238_PbcCorr = r206_238fc * (1 - r206_204c / r206_204fc)
         r238_206_PbcCorr = 1/r206_238_PbCorr (note: no date for this ratio, used for T-W plot)
         r208_232_PbcCorr = r208_232fc * (1 - r208_204c / r208_204fc)
         r207_235_PbcCorr = r206_238fc * r238_235s / r206_207fc * (1 - r207_204c / r207_204fc)*/
        // for now (oct 2014) nothing done here and this is done at fraction since just algebra
    }

    /**
     *
     * @param r206_204c
     * @param r206_204fc
     * @param r207_204c
     * @param r207_204fc
     * @param r206_238fc
     * @param r208_204c
     * @param r208_232fc
     * @param r208_204fc
     * @param r238_235s
     * @param r206_207fc
     * @return
     */
    protected static BigDecimal calculateOneSigmaAbsUncertainty(//
            ValueModel r206_204c, ValueModel r206_204fc, ValueModel r207_204c, ValueModel r207_204fc, //
            ValueModel r206_238fc, ValueModel r208_204c, ValueModel r208_232fc, ValueModel r208_204fc, //
            ValueModel r238_235s, ValueModel r206_207fc) {//

//        double dR76pbcc__dR207_204fc = -1.0 / (r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue());
//        double dR76pbcc__dR207_204c = 1.0 / (r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue());
//        double dR76pbcc__dR206_204fc = (r207_204c.getValue().doubleValue() - r207_204fc.getValue().doubleValue()) / Math.pow(r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue(), 2);
//        double dR76pbcc__dR206_204c = -(r207_204c.getValue().doubleValue() - r207_204fc.getValue().doubleValue()) / Math.pow(r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue(), 2);
//
//        double dR68pbcc__dR206_204fc = (r206_204c.getValue().doubleValue() * r206_238fc.getValue().doubleValue()) / Math.pow(r206_204fc.getValue().doubleValue() ,2);
//        double dR68pbcc__dR206_204c = -r206_238fc.getValue().doubleValue() / r206_204fc.getValue().doubleValue();
//        double dR68pbcc__dR206_238fc = 1.0 - r206_204c.getValue().doubleValue() / r206_204fc.getValue().doubleValue();
//
//        double dR86pbcc__dR206_204fc = -r206_204c.getValue().doubleValue() / (r206_238fc.getValue().doubleValue() * Math.pow(r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue() , 2));
//        double dR86pbcc__dR206_204c = r206_204fc.getValue().doubleValue() / (r206_238fc.getValue().doubleValue() * Math.pow(r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue() , 2));
//        double dR86pbcc__dR206_238fc = r206_204fc.getValue().doubleValue() / (Math.pow(r206_238fc.getValue().doubleValue() , 2) * (r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue()));
//
//        double dR82pbcc__dR208_204fc = (r208_204c.getValue().doubleValue() * r208_232fc.getValue().doubleValue()) / Math.pow(r208_204fc.getValue().doubleValue() , 2);
//        double dR82pbcc__dR208_204c = -r208_232fc.getValue().doubleValue() / r208_204fc.getValue().doubleValue();
//        double dR82pbcc__dR208_232fc = 1 - r208_204c.getValue().doubleValue() / r208_204fc.getValue().doubleValue();
//
//        double dR75pbcc__dR207_204fc = (r207_204c.getValue().doubleValue() * r238_235s.getValue().doubleValue() * r206_238fc.getValue().doubleValue()) //
//                / (r206_207fc.getValue().doubleValue() * Math.pow(r207_204fc.getValue().doubleValue() , 2));
//        double dR75pbcc__dR207_204c = -(r238_235s.getValue().doubleValue() * r206_238fc.getValue().doubleValue()) //
//                / (r206_207fc.getValue().doubleValue() * r207_204fc.getValue().doubleValue());
//        double dR75pbcc__dR206_238fc = -(r238_235s.getValue().doubleValue() * (r207_204c.getValue().doubleValue() //
//                / r207_204fc.getValue().doubleValue() - 1)) / r206_207fc.getValue().doubleValue();
//        double dR75pbcc__dR206_207fc = (r238_235s.getValue().doubleValue() * r206_238fc.getValue().doubleValue() //
//                * (r207_204c.getValue().doubleValue() / r207_204fc.getValue().doubleValue() - 1)) / Math.pow(r206_207fc.getValue().doubleValue() , 2);
//        double dR75pbcc_dR238_235s = -(r206_238fc.getValue().doubleValue() * (r207_204c.getValue().doubleValue() //
//                / r207_204fc.getValue().doubleValue() - 1)) / r206_207fc.getValue().doubleValue();
//
//        PbcCorrectionDetails.dR76pbcc__dR207_204fc = dR76pbcc__dR207_204fc;
//        PbcCorrectionDetails.dR76pbcc__dR207_204c = dR76pbcc__dR207_204c;
//        PbcCorrectionDetails.dR76pbcc__dR206_204fc = dR76pbcc__dR206_204fc;
//        PbcCorrectionDetails.dR76pbcc__dR206_204c = dR76pbcc__dR206_204c;
//        PbcCorrectionDetails.dR68pbcc__dR206_204fc = dR68pbcc__dR206_204fc;
//        PbcCorrectionDetails.dR68pbcc__dR206_204c = dR68pbcc__dR206_204c;
//        PbcCorrectionDetails.dR68pbcc__dR206_238fc = dR68pbcc__dR206_238fc;
//        PbcCorrectionDetails.dR86pbcc__dR238_206fc = dR86pbcc__dR206_204fc;
//        PbcCorrectionDetails.dR86pbcc__dR206_204c = dR86pbcc__dR206_204c;
//        PbcCorrectionDetails.dR86pbcc__dR206_204fc = dR86pbcc__dR206_238fc;
//        PbcCorrectionDetails.dR82pbcc__dR208_204fc = dR82pbcc__dR208_204fc;
//        PbcCorrectionDetails.dR82pbcc__dR208_204c = dR82pbcc__dR208_204c;
//        PbcCorrectionDetails.dR82pbcc__dR208_232fc = dR82pbcc__dR208_232fc;
//        
//        PbcCorrectionDetails.dR75pbcc__dR207_204fc = dR75pbcc__dR207_204fc;
//        PbcCorrectionDetails.dR75pbcc__dR207_204c = dR75pbcc__dR207_204c;
//        PbcCorrectionDetails.dR75pbcc__dR206_238fc = dR75pbcc__dR206_238fc;
//        PbcCorrectionDetails.dR75pbcc__dR206_207fc = dR75pbcc__dR206_207fc;
//        PbcCorrectionDetails.dR75pbcc_dR238_235s = dR75pbcc_dR238_235s;
//        
        
        return null;
    }

}
