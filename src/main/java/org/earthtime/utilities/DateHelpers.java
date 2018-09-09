/*
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
package org.earthtime.utilities;

import java.util.GregorianCalendar;

/**
 *
 * @author bowring
 */
public final class DateHelpers {

    /**
     * 
     * @return
     */
    static public String defaultEarthTimeDateString() {
        String dateCertified = String.format("%04d", GregorianCalendar.getInstance().get(GregorianCalendar.YEAR));
        dateCertified += "-" + String.format("%02d", GregorianCalendar.getInstance().get(GregorianCalendar.MONTH) + 1);// month is 0-based
        dateCertified += "-" + String.format("%02d", GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH));

        return dateCertified;
    }
}
