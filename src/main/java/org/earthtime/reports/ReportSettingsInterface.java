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
package org.earthtime.reports;

import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reports.ReportSettings;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface ReportSettingsInterface {

    public static ReportSettings getReportSettingsModelUpdatedToLatestVersion(ReportSettings myReportSettingsModel) {
        ReportSettings reportSettingsModel = myReportSettingsModel;
        
        if (myReportSettingsModel == null) {
            try {
                reportSettingsModel = ReduxLabData.getInstance().getDefaultReportSettingsModel();
            } catch (BadLabDataException badLabDataException) {
            }
        } else {
            // this provides for seamless updates to reportsettings implementation
            // new approach oct 2014
            if (myReportSettingsModel.isOutOfDate()) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"As part of our ongoing development efforts,",
                            "the report settings file you are using is being updated.",
                            "You may lose some report customizations. Thank you for your patience."//,
                        //"If you need to save aliquot copy, please re-export."
                        });
                String myReportSettingsName = myReportSettingsModel.getName();
                reportSettingsModel = new ReportSettings(myReportSettingsName);
            }
        }

        //TODO http://www.javaworld.com/article/2077736/open-source-tools/xml-merging-made-easy.html
        return reportSettingsModel;
    }
}
