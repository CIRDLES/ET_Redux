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
package org.earthtime.archivingTools;

import java.io.FileNotFoundException;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.aliquots.AliquotI;
import org.earthtime.exceptions.ETException;
import org.earthtime.samples.SampleInterface;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class GeochronRetrievalUtility {

    /**
     *
     * http://www.geochronportal.org/post_to_search_service.html
     *
     * @param sample
     * @param username
     * @param password
     * @return
     * @throws java.io.FileNotFoundException
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * @throws java.io.IOException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException
     * @throws org.earthtime.XMLExceptions.ETException
     */
    public static String importOneOrMoreGeochronAliquotXMLDataFiles(SampleInterface sample, String username, String password)
            throws FileNotFoundException,
            BadLabDataException,
            IOException,
            BadOrMissingXMLSchemaException,
            ETException {

        String retval = "";

        // ask the user for Aliquot IGSN         
        String aliquotIGSNs = JOptionPane.showInputDialog(//
                null,
                "NOTE: If you need private records, set your GEOCHRON credentials\n"
                + " in the Compilation Sample Manager. \n\n"//
                + "Enter one or more Aliquot IGSN, separated by commas: \n",
                "U-Pb_Redux for Geochron", 1);

        if (aliquotIGSNs != null) {
            String aliquotList[] = aliquotIGSNs.split(",");
            for (int i = 0; i < aliquotList.length; i++) {
                String aliquotIGSN = aliquotList[i].trim();
                if (aliquotIGSN.length() > 0) {
                    retval += retrieveGeochronAliquotFile(sample, aliquotIGSN, username, password) + "\n";
                }
            }
        }

        return retval;
    }

    public static String retrieveGeochronAliquotFile(SampleInterface sample, String aliquotIGSN, String userName, String password) {
        AliquotI myDownAliquot = new UPbReduxAliquot();

        String downloadURL = //
                "http://www.geochron.org/getxml.php?igsn="//
                + aliquotIGSN.toUpperCase().trim()//
                + "&username="//
                + userName//
                + "&password="//
                + password;

        try {
            myDownAliquot
                    = (Aliquot) ((XMLSerializationI) myDownAliquot).readXMLObject(
                            downloadURL, true);
            if (myDownAliquot != null) {
                // xml is added here for consistency and because we test whether aliquot source file is xml ... probably
                // should get rid of xml test and just make it aliquot non-zero length string
                SampleInterface.importAliquotIntoSample(//
                        sample, myDownAliquot, "GeochronDownloadOfAliquot_" + aliquotIGSN.toUpperCase().trim() + ".xml");
                System.out.println("got one " + myDownAliquot.getAnalystName());
            } else {
                return "Missing (or private) aliquot: " + aliquotIGSN;
            }
        } catch (IOException | ETException | BadOrMissingXMLSchemaException ex) {
            myDownAliquot = null;
        }

        return "Found: " + myDownAliquot.getAliquotIGSN();
    }
}
