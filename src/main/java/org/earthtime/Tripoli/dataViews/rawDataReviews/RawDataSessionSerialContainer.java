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

package org.earthtime.Tripoli.dataViews.rawDataReviews;

import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class RawDataSessionSerialContainer extends AbstractRawDataView {

    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

    }

    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
