/*
 * TemplatesForCsvImport.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 *  Licensed under the Apache License,Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.dataDictionaries;

/**
 *
 * @author James F. Bowring
 */
public class TemplatesForCsvImport {
// september 2009 to support batch upload of csv sample files

    /**
     *
     */
    public static final String IDTIMSLegacyDataSampleFieldNames_MIT = //
            "Fraction,206/238,2-sig%,207/235,2-sig%,207/206,2-sig%,"
            + "rho 6/8-7/5,206/204,208/206,Pb*/Pbc,Pb* pg,Pbc pg,conc U,conc Pb,Th/U samp,"
            + "frac mass,age206/238,2-sig,age207/235,2-sig,age207/206,2-sig,age206/238xTh,2-sig,"
            + "age207/235xPa,2-sig,age207/206xTh,2-sig,207/206xPa,2-sig";
    // April 2010 to support batch upload of csv sample files George Gehrels
    /**
     *
     */
    public static final String LAICPMSLegacyDataSampleFieldNames_MC_UA = //
            "Analysis,U(ppm),206/204,U/Th,206/207,1-sig%,207/235,1-sig%,206/238,1-sig%,"
            + "rho 6/8-7/5,"
            + "age206/238,1-sig,age207/235,1-sig,age207/206,1-sig,Best age,1-sig";
    // May 2010 to support Jeff VerVoort at Washington State with single-collector LA-ICP MS
    /**
     *
     */
    public static final String LAICPMSLegacyDataSampleFieldNames_SC_WSU_vA = //
            "Analysis,207Pb/235U,1-sig abs,206Pb/238U,1-sig abs,207Pb/206Pb,1-sig abs,"
            + "Th/U,207Pb/235U,1-sig abs,206Pb/238U,1-sig abs,ignored,ignored,207Pb/206Pb,1-sig abs,"//
            + "ignored,ignored,ignored,ignored,ignored,rho 6/8-7/5";
    // Sept 2010 to support Jeff VerVoort at Washington State with single-collector LA-ICP MS
    /**
     *
     */
    public static final String LAICPMSLegacyDataSampleFieldNames_SC_WSU_vB = //
            "Analysis,207Pb/235U,1-sig abs,206Pb/238U,1-sig abs,207Pb/206Pb,1-sig abs,"
            + "Th/U,207Pb/235U,1-sig abs,206Pb/238U,1-sig abs,207Pb/206Pb,1-sig abs,"//
            + "ignored,ignored,ignored,ignored,ignored,ignored,ignored,ignored,ignored,rho 6/8-7/5";
    // June 2010 to support batch upload of csv sample files Matt Horstwood NIGL
    /**
     *
     */
    public static final String LAICPMSLegacyDataSampleFieldNames_NIGL = //
            "Analysis,Pb(ppm),U(ppm),206/204,1-sig%,207/206,1-sig%,207/235,1-sig%,206/238,1-sig%,"
            + "rho 6/8-7/5,"
            + "age207/206,2-sig-abs,age206/238,2-sig-abs,age207/235,2-sig-abs";
    // July 2012 to support generic UPb isotopic data for Blair and Urs,etc.

    /**
     *
     */
    public static final String GenericUPbIsotopicLegacyDataSampleFieldNames_A = //
            "Sample and fractions,mass(g),# of grains,Pb conc (ppm),U conc (ppm),Th/U,Pb*/Pbc,Pbc (pg),"
            + "206Pb/204Pb,208Pb/206Pb,207Pb/206Pb,2-sig%,207Pb/235Pb,2-sig%,206Pb/238U,2-sig%,rho 6/8-7/5,"
            + "age 207Pb/206Pb,2-sig,age 207Pb/235U,2-sig,age 206Pb/238U,2-sig ";

    public static final String ProjectOfLegacySamplesFieldNames_UCSB_LASS_A = //
            "sample name,mineral type,grain number,spot number,position,Xpos.,Y pos.,"
            + "Pb ppm,U ppm,Th ppm,Th/U,206Pb/204Pb,2s. Abs.,207Pb/206Pb,2s. Abs.,238U/206Pb,2s. Abs.,"
            + "207Pb/235U,2s. Abs.,206Pb/238U,2s. Abs.,7/35 vs. 6/38 Rho,208Pb/232Th,2s. Abs.,"
            + "207Pb/206Pb (Ma),2s abs.,207Pb/235U (Ma),2s abs.,206Pb/238U (Ma),2s abs.,206Pb/238U <Th> (Ma),"
            + "2s abs.,208Pb/232Th (Ma),2s abs.,Si ppm,P ppm,Ca ppm,Ti ppm,Rb ppm,Sr ppm,Y ppm,Zr ppm,"
            + "La ppm,Ce ppm,Pr ppm,Nd ppm,Sm ppm,Eu ppm,Gd ppm,Tb ppm,Dy ppm,Ho ppm,Er ppm,Tm ppm,Yb ppm,Lu ppm,Hf ppm";
}
