/*
 * RatioNamePrettyPrinter.java
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
 *
 * @author James F. Bowring
 */
public class RatioNamePrettyPrinter {

    /**
     *
     * @param ratioName
     * @return
     */
    public static String makePrettyHTMLString(String ratioName) {
        return "<html>" + makePrettyForHTMLString(ratioName) + "</html>";
    }

    /**
     *
     * @param ratioName
     * @return
     */
    public static String makePrettyForHTMLString(String ratioName) {
        String prettyString = ratioName.trim();

        if (ratioName.startsWith("r")) {
            prettyString = prettyString.replaceAll("_", "/ ");

            if (ratioName.endsWith("r")) {
                prettyString = prettyString.replaceAll("r", "");
                prettyString = formatUPbTh(prettyString);
                prettyString = prettyString.replaceAll("Pb", "Pb*");
            } else {// ( ratioName.endsWith( "s" ) ) {
                prettyString = prettyString.replaceAll("[b-t]", "");
                prettyString = formatUPbTh(prettyString);
            }
        }

        // tracer case uses mol/g
        if ((ratioName.startsWith("conc")) && (ratioName.endsWith("t"))) {
            prettyString = prettyString.replaceAll("conc", "");
            prettyString = prettyString.replaceAll("Pb", "");
            prettyString = prettyString.replaceAll("U", "");
            prettyString = prettyString.replaceAll("t", "");
            prettyString = formatUPbTh(prettyString);
            prettyString += "&nbsp;&nbsp;mol/g";
        } else if (ratioName.startsWith("conc")) {
            // used for MineralStandardModel ppm concentrations
            prettyString = prettyString.replaceAll("conc", "conc. ");
            prettyString += "&nbsp;&nbsp;ppm";

        }

        if (ratioName.startsWith("lambda")) {
            prettyString = prettyString.replaceAll("lambda", "");
            prettyString = formatLambda(prettyString);
        }

        return prettyString;
    }

    private static String formatUPbTh(String prettyString) {
        String retValPretty = prettyString;
        retValPretty = retValPretty.replaceAll("20(\\d)", "<sup>20$1</sup>Pb");
        retValPretty = retValPretty.replaceAll("23([1,3-9])", "<sup>23$1</sup>U");
        retValPretty = retValPretty.replaceAll("23([2])", "<sup>23$1</sup>Th");

        return retValPretty;
    }

    private static String formatLambda(String prettyString) {
        return "\u03BB" + "<sub>" + prettyString + "</sub>";
    }

    /**
     *
     * @param arg
     */
    public static void main(String[] arg) {

        System.out.println(makePrettyHTMLString("r206_204r"));
        System.out.println(makePrettyHTMLString("r206_238r"));
        System.out.println(makePrettyHTMLString("r208_232r"));
        System.out.println(makePrettyHTMLString("r238_235s"));
    }
}
