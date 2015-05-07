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
package org.earthtime.UPb_Redux.dateInterpretation;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Node;
import javax.swing.JComponent;
import org.cirdles.topsoil.app.ErrorEllipseChart;
import org.cirdles.topsoil.app.TSVTable;
import org.cirdles.topsoil.chart.BaseChart;
import org.cirdles.topsoil.chart.Chart;
import org.cirdles.topsoil.chart.JavaFXDisplayable;
import org.cirdles.topsoil.chart.SimpleVariableContext;
import org.cirdles.topsoil.chart.Variable;
import org.cirdles.topsoil.chart.VariableContext;
import org.cirdles.topsoil.data.Dataset;
import org.cirdles.topsoil.data.Entry;
import org.cirdles.topsoil.data.Field;
import org.cirdles.topsoil.data.NumberField;
import org.cirdles.topsoil.data.SimpleDataset;
import org.cirdles.topsoil.data.SimpleEntry;

/**
 *
 * @author bowring
 */
public class TestTopsoil {

    public TestTopsoil() {

        Chart myChart = new ErrorEllipseChart();

        List<Field> myFields = new ArrayList<>();
        myFields.add(new NumberField("Ratio1"));
        myFields.add(new NumberField("Ratio1Unct"));
        myFields.add(new NumberField("Ratio2"));
        myFields.add(new NumberField("Ratio2Unct"));
        myFields.add(new NumberField("rho"));

        List<Entry> myEntries = new ArrayList<>();
        Entry entry1 = new SimpleEntry();
        entry1.set(myFields.get(0), "11.11");
        entry1.set(myFields.get(1), "22.22");
        entry1.set(myFields.get(2), "33.33");
        entry1.set(myFields.get(3), "44.44");
        entry1.set(myFields.get(4), "55.55");
        myEntries.add(entry1);

        Dataset dataset = new SimpleDataset(myFields, myEntries);

        VariableContext vc = new SimpleVariableContext(dataset);
        for (int i = 0; i < 5; i++) {
            vc.addBinding(myChart.getVariables().get(i), myFields.get(i));
        }

        myChart.setData(vc);
        
        JComponent jc = myChart.displayAsJComponent();

    }

}
