/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.earthtime.plots.topsoil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.cirdles.topsoil.app.plot.variable.Variables.RHO;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_X;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_Y;
import static org.cirdles.topsoil.app.plot.variable.Variables.X;
import static org.cirdles.topsoil.app.plot.variable.Variables.Y;

/**
 *
 * @author James F. Bowring
 */
public class TopsoilDataFactory {

    // this test data set is CM2 from ET_Redux work
    public static final double[][] EXAMPLE_CM2_DATASET = new double[][]{
        {0.071813669, 0.011006957, 0.00010654762, 0.0000029902690, 0.525021016},
        {0.072151433, 0.011005053, 0.00007607823, 0.0000027925747, 0.576825349},
        {0.071944887, 0.011003275, 0.00005774879, 0.0000026419337, 0.565467772},
        {0.071935928, 0.011006019, 0.00007001780, 0.0000027907138, 0.593632132},
        {0.071881029, 0.011006746, 0.00011879759, 0.0000029932998, 0.547212036},
        {0.072008073, 0.011000075, 0.00012637628, 0.0000030924856, 0.491113441},
        {0.071909459, 0.011005301, 0.00014366566, 0.0000034129203, 0.53576221},
        {0.072023966, 0.011007749, 0.00006526067, 0.0000027068856, 0.588051902},
        {0.07204976, 0.011005122, 0.00006776661, 0.0000027686638, 0.663026105},
        {0.072067922, 0.011007277, 0.00007005962, 0.0000027064135, 0.547645084},
        {0.072012531, 0.011005595, 0.00007278283, 0.0000027963019, 0.485116139},
        {0.071951025, 0.011001109, 0.00006243122, 0.0000027089483, 0.587402112},
        {0.071984195, 0.011002318, 0.00005824101, 0.0000026286515, 0.547568329},
        {0.072026796, 0.011001577, 0.00009552567, 0.0000028683014, 0.580131768}

    };
    public static final double[][] EXAMPLE_EVOLUTION_DATASET = new double[][]{
        {0.787174467, 1.112997105, 0.002472973, 0.004812142, 0},
        {0.785279872, 1.104535717, 0.003488836, 0.003504504, 0},
        {0.757751874, 1.098862611, 0.003437122, 0.004937997, 0},
        {0.756755971, 1.095577076, 0.002292904, 0.00361392, 0},
        {0.769622435, 1.10493373, 0.003069412, 0.005966043, 0},
        {0.754230241, 1.099870658, 0.004366264, 0.003039218, 0},
        {0.760346901, 1.104336707, 0.004460567, 0.004830971, 0},
        {0.759050757, 1.098264378, 0.002174949, 0.005414218, 0},
        {0.766429941, 1.102943341, 0.007290816, 0.003127906, 0},
        {0.777301225, 1.101947646, 0.003167341, 0.003224137, 0},
        {0.779894193, 1.118173335, 0.003500865, 0.00696614, 0},
        {0.781888816, 1.118372424, 0.003270823, 0.008000935, 0},
        {0.782985109, 1.117974657, 0.00383209, 0.004330706, 0},
        {0.781688601, 1.111205641, 0.004060869, 0.007176835, 0},
        {0.762741024, 1.10792003, 0.002969033, 0.00482314, 0},
        {0.763237957, 1.103739947, 0.004908767, 0.004548051, 0},
        {0.769023512, 1.110209805, 0.003525521, 0.005196072, 0},
        {0.769917231, 1.105831646, 0.002727962, 0.005963751, 0},
        {0.776501596, 1.112997925, 0.003282191, 0.004246044, 0},
        {0.776998549, 1.111107425, 0.003054914, 0.003777516, 0},
        {0.798544256, 1.117575886, 0.001562462, 0.005461398, 0},
        {0.800139952, 1.114788659, 0.002006875, 0.005468221, 0},
        {0.800439225, 1.113992263, 0.003009748, 0.003583907, 0},
        {0.803930031, 1.11817297, 0.002447054, 0.00376547, 0},
        {0.763039792, 1.107123872, 0.003881829, 0.007757655, 0}};

    public static List<Map<String, Object>> prepareWetherillData(double[][] data) {
        List<Map<String, Object>> datumList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Map<String, Object> datum = new HashMap<>();
            datumList.add(datum);
            datum.put(X.getName(), data[i][0]);
            datum.put(SIGMA_X.getName(), data[i][2]);
            datum.put(Y.getName(), data[i][1]);
            datum.put(SIGMA_Y.getName(), data[i][3]);
            datum.put(RHO.getName(), data[i][4]);
            datum.put("Selected", true);
        }

        return datumList;
    }

    public static List<Map<String, Object>> prepareEvolutionData(double[][] data) {
        List<Map<String, Object>> datumList = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            Map<String, Object> datum = new HashMap<>();
            datumList.add(datum);
            datum.put(X.getName(), data[i][0]);
            datum.put(SIGMA_X.getName(), data[i][2]);
            datum.put(Y.getName(), data[i][1]);
            datum.put(SIGMA_Y.getName(), data[i][3]);
            datum.put(RHO.getName(), data[i][4]);
            datum.put("Selected", true);
        }

        return datumList;
    }
}
