/*
 * Copyright 2015 CIRDLES.
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
package org.earthtime.archivingTools.forSESAR;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.earthtime.UPb_Redux.utilities.BrowserControl;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class CoordinateSystemConversions {

    private final static BigDecimal sixty = new BigDecimal(60);

    public static BigDecimal[] convertDecimalCoordinateToDMS(BigDecimal coordDecimal) {
        BigDecimal[] coordDMS = new BigDecimal[3];

        BigDecimal[] degreeMinute = coordDecimal.abs().multiply(sixty, new MathContext(6)).setScale(0, RoundingMode.FLOOR).divideAndRemainder(sixty, new MathContext(6));
        // only degrees canhave negative value
        coordDMS[0] = (coordDecimal.signum() < 0) ? degreeMinute[0].negate() : degreeMinute[0];
        coordDMS[1] = degreeMinute[1];

        BigDecimal[] degreex60Minute = coordDecimal.abs().multiply(sixty).multiply(sixty).divideAndRemainder(sixty);
        coordDMS[2] = degreex60Minute[1].setScale(4, RoundingMode.HALF_UP);

        return coordDMS;
    }

    public static BigDecimal convertDMSCoordinateToDecimal(BigDecimal[] coordDMS) {
        BigDecimal coordDecimal = null;

        coordDecimal = coordDMS[0].abs()//
                .add(coordDMS[1].divide(sixty, 6, RoundingMode.HALF_UP))//
                .add(coordDMS[2].divide(sixty, 6, RoundingMode.HALF_UP).divide(sixty, 6, RoundingMode.HALF_UP)).setScale(6, RoundingMode.HALF_UP);

        if (coordDMS[0].signum() < 0) {
            coordDecimal = coordDecimal.negate();
        }

        return coordDecimal;
    }

    public static void launchGoogleMapsForLatLong(BigDecimal latDecimal, BigDecimal longDecimal) {
        String displayString = latDecimal.setScale(6, RoundingMode.HALF_UP).toPlainString() + "," + longDecimal.setScale(6, RoundingMode.HALF_UP).toPlainString();
        BrowserControl.displayURL(//
                "https://maps.googleapis.com/maps/api/staticmap?center" //
                + displayString + "&markers=color:red%7Clabel:S%7C" //
                + displayString + "&zoom=11&size=400x400");
    }

}
