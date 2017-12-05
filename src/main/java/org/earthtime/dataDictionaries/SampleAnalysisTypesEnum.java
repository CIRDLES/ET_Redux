/*
 * SampleAnalysisTypesEnum.java
 *
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
package org.earthtime.dataDictionaries;

/**
 * Created July 2011 as part of major refactoring to control magic strings
 *
 * @author James F. Bowring
 */
public enum SampleAnalysisTypesEnum {

    // Analysis Types
    /**
     *
     */
    IDTIMS("IDTIMS", "UPb", "UPb"),
    /**
     *
     */
    LAICPMS("LAICPMS", "UPb", "UPb"),
    LAICPMS_MCUA("LAICPMS_MCUA", "UPb", "UPb"),
    LAICPMS_SCWSU_vB("LAICPMS_SCWSU_vB", "UPb", "UPb"),
    LAICPMS_NIGL("LAICPMS_NIGL", "UPb", "UPb"),
    LAICPMS_SCWSU_vA("LAICPMS_SCWSU_vA", "UPb", "UPb"),
    LAICPMS_SCWSU_vV("LAICPMS_SCWSU_vV", "UPb", "UPb"),
    LAICPMSMC("LAICPMSMC", "UPb", "UPb"),
    LAICPMS_UH("LAICPMS_UH", "UPb", "UPb"),
    SHRIMP("SHRIMP", "UPb", "UPb"),
    /**
     *
     */
    LASS("LASS", "UPb", "UPb"),
    USERIES_CARB("USERIES_CARB", "UTh", "UTh_Carb"),//Carbonate"),
    USERIES_IGN("USERIES_IGN", "UTh", "UTh_Ign"),//Igneous"),
    /**
     *
     */
    GENERIC_UPB("GENERIC_UPB", "UPb", "UPb"),
    /**
     * Used for PROJECTs or COMPILATIONs samples
     */
    COMPILED("COMPILED", "UPb", "UPb"),
    /**
     * Used for PROJECTs or COMPILATIONs samples
     */
    TRIPOLIZED("TRIPOLIZED", "UPb", "UPb");

    private final String name;
    private final String isotypeSystem;
    private final String defaultReportSpecsType;

    private SampleAnalysisTypesEnum(String name, String isotypeSystem, String defaultReportSpecsType) {
        this.name = name;
        this.isotypeSystem = isotypeSystem;
        this.defaultReportSpecsType = defaultReportSpecsType;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return the isotypeSystem
     */
    public String getIsotypeSystem() {
        return isotypeSystem;
    }

    /**
     * @return the defaultReportSpecsType
     */
    public String getDefaultReportSpecsType() {
        return defaultReportSpecsType;
    }
    
    public boolean isFamilyLAICPMS(){
        return name.startsWith("LAICPMS");
    }

}
