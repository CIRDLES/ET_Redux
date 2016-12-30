/*
 * Copyright 2006-2016 CIRDLES.org.
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
package org.earthtime.UPb_Redux.dateInterpretation.concordia;

import java.util.Map;
import org.earthtime.UPb_Redux.dialogs.graphManagers.ConcordiaOptionsDialog;
import org.earthtime.dialogs.DialogEditor;

/**
 *
 * @author CIRDLES.org
 */
public interface ConcordiaPlotDisplayInterface {

    /**
     * @return the concordiaFlavor
     */
    String getConcordiaFlavor();

    /**
     *
     * @return
     */
    Map<String, String> getConcordiaOptions();

    public void setConcordiaOptions(Map<String, String> concordiaOptions);

    public default void showConcordiaDisplayOptionsDialog() {
        DialogEditor myConcordiaOptionsDialog
                = new ConcordiaOptionsDialog(
                        null, true,
                        getConcordiaOptions());

        myConcordiaOptionsDialog.setVisible(true);

        setConcordiaOptions(//
                ((ConcordiaOptionsDialog) myConcordiaOptionsDialog).getConcordiaOptions());

        repaint();
    }
    
    public void repaint();
}
