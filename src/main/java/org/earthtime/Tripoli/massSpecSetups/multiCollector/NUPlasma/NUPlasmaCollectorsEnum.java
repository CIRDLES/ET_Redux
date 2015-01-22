/*
 * NUPlasmaCollectorsEnum.java
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
package org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma;

import org.earthtime.isotopes.IsotopesEnum;

/**
 *
 * @author James F. Bowring
 */
public enum NUPlasmaCollectorsEnum {
    
        /**
         *
         */
        ExHi("F", IsotopesEnum.U238),
        /**
         *
         */
        H2("F", IsotopesEnum.Th232),
        /**
         *
         */
        Hi("F", null),
        /**
         *
         */
        AX("F", null),
        /**
         *
         */
        L1("F", null),
        /**
         *
         */
        L2("F", null),
        /**
         *
         */
        L3("F", null),
        /**
         *
         */
        L4("F", null),
        /**
         *
         */
        L5("F", null),
        /**
         *
         */
        L6("F", IsotopesEnum.Pb208),
        /**
         *
         */
        L7("F", IsotopesEnum.Pb207),
        /**
         *
         */
        L8("F", IsotopesEnum.Pb206),
        /**
         *
         */
        IC0("IC", IsotopesEnum.Pb204),
        /**
         *
         */
        IC1("IC", null),
        /**
         *
         */
        IC2("IC", IsotopesEnum.Hg202),
        /**
         *
         */
        IC3("IC", null),;
        private final String collectorType;
        private final IsotopesEnum isotope;

        private NUPlasmaCollectorsEnum(String collectorType, IsotopesEnum isotope) {
            this.collectorType = collectorType;
            this.isotope = isotope;
        }

        /**
         *
         * @return
         */
        public static String[] getIonCounterCollectorNames() {
            return new String[]{IC0.name(), IC1.name(), IC2.name(), IC3.name()};
        }

        /**
         * @return the collectorType
         */
        public String getCollectorType() {
            return collectorType;
        }

        /**
         * @return the isotope
         */
        public IsotopesEnum getIsotope() {
            return isotope;
        }
}
