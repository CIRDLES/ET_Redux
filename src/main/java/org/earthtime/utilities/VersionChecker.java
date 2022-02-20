/*
 * Copyright 2022 CIRDLES.
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
package org.earthtime.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import org.earthtime.ETRedux;

/**
 *
 * @author bowring
 */
public class VersionChecker {

    public static boolean checkIfCurrentVersion(String runningVersion) throws Exception {

        boolean retVal = true;
        java.net.URL url = null;
        String file = "";
        try {
            url = new java.net.URL("https://raw.githubusercontent.com/CIRDLES/ET_Redux/master/.currentVersion.txt");
            java.net.URLConnection uc;
            uc = url.openConnection();

            uc.setRequestProperty("X-Requested-With", "Curl");
            java.util.ArrayList<String> list = new java.util.ArrayList<>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = reader.readLine();
            retVal = line.trim().compareToIgnoreCase(runningVersion) == 0;
            

        } catch (IOException e) {
            System.out.println("Could not read .currentVersion.txt");
        }
        
        return retVal;
    }
}
