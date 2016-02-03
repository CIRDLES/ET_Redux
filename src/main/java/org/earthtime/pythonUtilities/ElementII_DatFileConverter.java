/*
 * ElementII_DatFileConverter.java
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.pythonUtilities;

import java.io.File;
import org.python.core.*;
import org.python.util.PythonInterpreter;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 * adapted from Python code published by Dr. Philip Wenig and John H. Hartman
 * under the terms of the GNU Lesser General Public License version # 2.1, as
 * published by the Free Software Foundation.
 */
public class ElementII_DatFileConverter {

    private static PythonInterpreter python;

    /**
     *
     * @param file the value of file
     * @param elementsList the value of elementsList
     * @return
     */
    public static String[][] readDatFile(File file, String elementsList) {

        String fileName = file.getAbsolutePath();

        python = new PythonInterpreter();

        python.exec("import struct");
        python.exec("import math");

        python.exec("HDR_INDEX_OFFSET = 33");
        python.exec("HDR_INDEX_LEN = 39");
        python.exec("HDR_TIMESTAMP = 40");
        python.exec("SCAN_NUMBER = 9");
        python.exec("SCAN_DELTA = 7");
        python.exec("SCAN_ACF = 12");
        python.exec("SCAN_PREV_TIME = 18");
        python.exec("SCAN_TIME = 19");

        python.exec("scales = {}");
        python.exec("for i in xrange(0,16):\n"
                + "\tkey = 0x1010 + i\n"
                + "\tscales[key] = math.pow(2, i) * 1.0\n"
        );

        python.exec("for scale in scales.keys():\n"
                + "\tkey = scale & 0xFF0F\n"
                + "\tscales[key] = scales[scale]\n"
        );

        python.exec("def Scale(value, scale, acf):\n"
                + "\tif scale & 0xF00:\n"
                + "\t\t# Don't know what this digit does, flag the result\n"
                + "\t\tsuffix = '*'\n"
                + "\t\tscale = scale & 0xF0FF\n"
                + "\telse:\n"
                + "\t\tsuffix = ''\n"
                + "\tif scale not in scales:\n"
                + "\t\traise Exception(\"Unknown scaling %s (%d)\" % (hex(scale), value))\n"
                + "\tfactor = scales[scale]\n"
                + "\tif scale & 0XF0 == 0:\n"
                + "\t\tfactor *= acf\n"
                + "\tresult = str(value * factor) + suffix\n"
                + "\treturn result\n"
        );

        python.exec("magic = 0x8000");
        python.exec("def Magic(value):\n"
                + "\tglobal magic\n"
                + "\tif value == magic:\n"
                + "\t\treturn True\n"
                + "\telif value == magic + 1:\n"
                + "\t\tmagic += 1\n"
                + "\t\treturn True\n"
                + "\telse:\n"
                + "\t\treturn False\n"
        );

        python.exec("elements = " + elementsList);//[202, 204, 206, 207, 208, 232, 238]");
        python.exec("first = True");

        python.exec("dat = open('" + fileName + "', 'rb')");

        readHeader();
        python.exec("timeStamp = hdr[HDR_TIMESTAMP]");

        python.exec("indexOffset = hdr[HDR_INDEX_OFFSET] + 4");
        python.exec("indexLength = hdr[HDR_INDEX_LEN]");
        python.exec("dat.seek(indexOffset)");
        python.exec("offsets = struct.unpack('<' + indexLength * 'I', dat.read(indexLength * 4))");
        python.exec("scans = len(offsets)");
        python.exec("prev = 0");
        python.exec("for offset in offsets:\n"
                + "\tif prev != 0:\n"
                + "\t\tindexSize = offset - prev\n"
                + "\tprev = offset\n");

        python.exec("count = (indexSize - (22 * 4)) / 2");
        python.exec("dataMatrix = {}");

        python.exec("for offset in offsets:\n"
                + "\tdat.seek(offset)\n"
                + "\tdata = dat.read(indexSize)\n"
                + "\tvals = struct.unpack('<22I%uH' % count, data)\n"
                + "\tscanNumber = vals[SCAN_NUMBER]\n"
                + "\tacf = (vals[SCAN_ACF] * 1.0) / 64.0\n"
                + "\tbase = 72\n"
                + "\tkey = vals[base:base+2]\n"
                + "\tt = vals[SCAN_TIME]/ 1000.0 + timeStamp\n"
                + "\tresult = [str(scanNumber), '%f' % t, '%f' % acf]\n"
                + "\theaders = [\"Scan\", \"Time\", \"ACF\"]\n"
                + "\tpulses = []\n"
                + "\tanalogs = []\n"
                + "\tmysteries = ['']*5\n"
                + "\taverages = [str(t)]\n"
                + "\tindex = base\n"
                + "\ttotal = 0.0\n"
                + "\tn = 0\n"
                + "\tacquisition = 0\n"
                + "\tmass = 0\n"
                + "\treading = 0\n"
                + "\twhile index + 4 < len(vals):\n"
                + "\t\tpulse = None\n"
                + "\t\tanalog = None\n"
                + "\t\tif vals[index:index+2] != key:\n"
                + "\t\t\traise Exception(\"missing key\")\n"
                + "\t\tindex += 2\n"
                //
                + "\t\t# Read the mystery value\n"
                + "\t\tmystery = vals[index+1] * 65536 + vals[index]\n"
                + "\t\tmysteries.append(str(mystery))\n"
                + "\t\tindex += 2\n"
                //
                + "\t\t# Read the pulse count\n"
                + "\t\tscale = vals[index+1]\n"
                + "\t\tpulse = Scale(vals[index], vals[index+1], acf)\n"
                + "\t\tindex += 2\n"
                + "\t\tif vals[index:index+2] != key and not Magic(vals[index+1]):\n"
                //
                + "\t\t\t# Read the analog value\n"
                + "\t\t\tscale = vals[index+1]\n"
                + "\t\t\tanalog = Scale(vals[index], vals[index+1], acf)\n"
                + "\t\t\tindex += 2\n"
                + "\t\tif pulse is None and analog is None:\n"
                + "\t\t\tpass\n"
                + "\t\t\traise Exception(\"scan %d acquisition %d index %d\" % (scanNumber, acquisition, index))\n"
                + "\t\tif pulse is not None:\n"
                + "\t\t\tpulses.append(str(pulse))\n"
                + "\t\tif analog is not None:\n"
                + "\t\t\tanalogs.append(str(analog))\n"
                + "\t\treading += 1\n"
                + "\t\tif Magic(vals[index+1]):\n"
                //    
                + "\t\t\t# End of element\n"
                + "\t\t\tX = vals[index]\n"
                + "\t\t\tresult += pulses + analogs + ['']\n"
                + "\t\t\tmysteries += [''] * (2 + len(analogs))\n"
                + "\t\t\theaders += [\"%dp\" % elements[mass]] * len(pulses) + [\"%da\" % elements[mass]] * len(analogs) + ['']\n"
                + "\t\t\tpulses = []\n"
                + "\t\t\tanalogs = []\n"
                + "\t\t\tindex += 2\n"
                + "\t\t\tmass += 1\n"
                + "\t\t\treading = 0\n"
                //
                + "\t\t# Resynchronize with key. The header/trailer appears to be variable size.\n"
                + "\t\twhile index + 4 < len(vals) and vals[index:index+2] != key:\n"
                + "\t\t\t#raise Exception(\"scan %d acquisition %d index %d\" % (scanNumber, acquisition, index))\n"
                + "\t\t\tindex += 2\n"
                + "\t\tacquisition += 1\n"
                //
                + "\tif first == True:\n"
                //                + "\t\tdataMatrix[0]=headers\n"
                + "\t\tfirst = False\n"
                + "\tdataMatrix[scanNumber - 1]=result\n"
        );

        python.exec("dat.close");
        PyObject dataMatrix = python.get("dataMatrix");

        // remove Python artifacts and split into acquisitions
        String[] data = dataMatrix.toString().replace("{", "").replace("}", "").replace("0: ", "").replace("[", "").replace("]", "").split("[0-9]*[L][\\:][\\ ]");

        // now split each element into a string array
        String[][] dataArray = new String[data.length - 1][];
        for (int i = 1; i < data.length; i++) {
            // replace single quotes with double quotes
            dataArray[i - 1] = data[i].replace("'", "").split(", ");
        }

        return dataArray;
    }

    public static String[][] readDatFile5(File file, String elementsList)
            throws org.python.core.PyException {

        String[][] dataArray = new String[0][];

        String fileName = file.getAbsolutePath();

        python = new PythonInterpreter();

        python.exec("import struct");
        python.exec("import math");

        python.exec("HDR_INDEX_OFFSET = 33");
        python.exec("HDR_INDEX_LEN = 39");
        python.exec("HDR_TIMESTAMP = 40");
        python.exec("SCAN_NUMBER = 9");
        python.exec("SCAN_DELTA = 7");
        python.exec("SCAN_ACF = 12");
        python.exec("SCAN_PREV_TIME = 18");
        python.exec("SCAN_TIME = 19");

        python.exec("scales = {}");
        python.exec("for i in xrange(0,16):\n"
                + "\tkey = 0x1010 + i\n"
                + "\tscales[key] = math.pow(2, i) * 1.0\n"
        );

        python.exec("for scale in scales.keys():\n"
                + "\tkey = scale & 0xFF0F\n"
                + "\tscales[key] = scales[scale]\n"
        );

        python.exec("def Scale(value, scale, acf):\n"
                + "\tif scale & 0xF00:\n"
                + "\t\tsuffix = '*'\n"
                + "\t\tscale = scale & 0xF0FF\n"
                + "\telse:\n"
                + "\t\tsuffix = ''\n"
                //                + "\tif scale not in scales:\n"
                //                + "\t\traise Exception(\"Unknown scaling %s (%d)\" % (hex(scale), value))\n"
                + "\tfactor = scales[scale]\n"
                + "\tif scale & 0XF0 == 0:\n"
                + "\t\tfactor *= acf\n"
                + "\tresult = str(value * factor) + suffix\n"
                + "\treturn result\n"
        );

        python.exec("magic = 0x8000");
        python.exec("def Magic(value):\n"
                + "\tglobal magic\n"
                + "\tif value == magic:\n"
                + "\t\treturn True\n"
                + "\telif value == magic + 1:\n"
                + "\t\tmagic += 1\n"
                + "\t\treturn True\n"
                + "\telse:\n"
                + "\t\treturn False\n"
        );

        // added v5
        python.exec("skips = {'A' : [0,2,0,0], 'B' : [4,0,2,2]}");

        String myElementsList = elementsList;
        if (elementsList.length() == 0) {
            myElementsList = "None";
        }
        python.exec("elements = " + myElementsList);//[202, 204, 206, 207, 208, 232, 238]");
        python.exec("first = True");

        python.exec("dat = open('" + fileName + "', 'rb')");

        readHeader();
        python.exec("timeStamp = hdr[HDR_TIMESTAMP]");

        python.exec("indexOffset = hdr[HDR_INDEX_OFFSET] + 4");
        python.exec("indexLength = hdr[HDR_INDEX_LEN]");
        python.exec("dat.seek(indexOffset)");
        python.exec("offsets = struct.unpack('<' + indexLength * 'I', dat.read(indexLength * 4))");
        python.exec("scans = len(offsets)");
        python.exec("prev = 0");
        python.exec("for offset in offsets:\n"
                + "\tif prev != 0:\n"
                + "\t\tindexSize = offset - prev\n"
                + "\tprev = offset\n");

        python.exec("count = (indexSize - (22 * 4)) / 2");
        python.exec("dataMatrix = {}");

        python.exec("for offset in offsets:\n"
                + "\tdat.seek(offset)\n"
                + "\tdata = dat.read(indexSize)\n"
                + "\tvals = struct.unpack('<22I%uH' % count, data)\n"
                + "\tscanNumber = vals[SCAN_NUMBER]\n"
                + "\tacf = (vals[SCAN_ACF] * 1.0) / 64.0\n"
                + "\tbase = 72\n"
                + "\tkey = vals[base:base+2]\n"
                + "\tt = vals[SCAN_TIME]/ 1000.0 + timeStamp\n"
                + "\tresult = [str(scanNumber), '%f' % t, '%f' % acf]\n"
                + "\theaders = [\"Scan\", \"Time\", \"ACF\"]\n"
                + "\tpulses = []\n"
                + "\tanalogs = []\n"
                + "\tmysteries = ['']*5\n"
                + "\taverages = [str(t)]\n"
                + "\tindex = base\n"
                + "\ttotal = 0.0\n"
                + "\tn = 0\n"
                + "\tacquisition = 0\n"
                + "\tmass = 0\n"
                + "\treading = 0\n"
                + "\twhile index + 4 < len(vals):\n"
                + "\t\tpulse = None\n"
                + "\t\tanalog = None\n"
                // new with v5
                + "\t\tscanFormat = 'A'\n"
                + "\t\tif key is None:\n"
                + "\t\t\tscanFormat = 'A'\n"
                + "\t\t\ttry:\n"
                + "\t\t\t\tScale(0, vals[index + 5], 0)\n"
                + "\t\t\t\tscanFormat = 'A'\n"
                + "\t\t\texcept:\n"
                + "\t\t\t\ttry:\n"
                + "\t\t\t\t\tScale(0, vals[index + 7], 0)\n"
                + "\t\t\t\t\tscanFormat = 'B'\n"
                + "\t\t\t\texcept:\n"
                + "\t\t\t\t\tpass\n"
                + "\t\t\tindex += skips[scanFormat][0]\n"
                + "\t\t\tkey = vals[index:index+2]\n"
                + "\t\tindex += 2\n"
                // skip to pulse data
                + "\t\tindex += skips[scanFormat][1]\n"
                + "\t\tscale = vals[index+1]\n"
                //+ "\t\tpulse = Scale(vals[index], vals[index+1], acf)\n"
                + "\t\tpulse = Scale(vals[index], scale, acf)\n"
                // skip to analog data
                + "\t\tindex += 2\n"
                + "\t\ttry:\n"
                + "\t\t\tscale = vals[index+1]\n"
                + "\t\t\tanalog = Scale(vals[index], scale, acf)\n"
                + "\t\t\tindex += 2\n"
                + "\t\texcept Exception, e:\n"
                + "\t\t\tpass\n"
                + "\t\tif pulse is None and analog is None:\n"
                + "\t\t\traise Exception(\"scan %d acquisition %d index %d\"  % (scanNumber, acquisition, index))\n"
                + "\t\tif pulse is not None:\n"
                + "\t\t\tpulses.append(str(pulse))\n"
                + "\t\tif analog is not None:\n"
                + "\t\t\tanalogs.append(str(analog))\n"
                + "\t\treading += 1\n"
                + "\t\tif Magic(vals[index+1]):\n"
                + "\t\t\tif vals[index+3] in [0x3000, 0xf000]:\n"
                + "\t\t\t\tindex += 4\n"
                + "\t\t\telse:\n"
                + "\t\t\t\tindex += 2\n"
                // End of element
                + "\t\t\tresult += pulses + analogs + ['']\n"
                + "\t\t\tif first:\n"
                + "\t\t\t\tif elements is None:\n"
                + "\t\t\t\t\telement = \"Mass%02d\" % (mass + 1)\n"
                + "\t\t\t\telse:\n"
                + "\t\t\t\t\telement = elements[mass]\n"
                + "\t\t\t\theaders += [\"%sp\" % element] * len(pulses) + [\"%sa\" % element] * len(analogs) + ['']\n"
                + "\t\t\tpulses = []\n"
                + "\t\t\tanalogs = []\n"
                + "\t\t\tindex += skips[scanFormat][3]\n"
                + "\t\t\tmass += 1\n"
                + "\t\t\treading = 0\n"
                + "\t\t\tkey = None\n"
                + "\t\telse:\n"
                + "\t\t\tindex += skips[scanFormat][2]\n"
                + "\t\tacquisition += 1\n"
                + "\tif first == True:\n"
                + "\t\tfirst = False\n"
                + "\tdataMatrix[scanNumber - 1]=result\n"
        );

        python.exec("dat.close");
        PyObject dataMatrix = python.get("dataMatrix");

        // remove Python artifacts and split into acquisitions
        String[] data = dataMatrix.toString().replace("{", "").replace("}", "").replace("0: ", "").replace("[", "").replace("]", "").split("[0-9]*[L][\\:][\\ ]");

        // now split each element into a string array
        dataArray = new String[data.length - 1][];
        for (int i = 1; i < data.length; i++) {
            // replace single quotes with double quotes
            dataArray[i - 1] = data[i].replace("'", "").split(", ");
        }

        return dataArray;
    }

    private static void readHeader() {

        python.exec("dat.seek(0x10)");
        python.exec("fields = 85");
        python.exec("data = dat.read(fields * 4)");
        python.exec("hdr = struct.unpack('<%dI'% fields, data)");

    }

    public static void main(String a[]) {
        String[][] data = null;
        try {
            data = readDatFile5(new File("untSMPABC001.dat"), "");// "[202, 204, 206, 207, 208, 232, 238]");//null);
        } catch (PyException pyException) {
            System.out.println("bad read of fraction " + " message = " + pyException.getMessage());
        }
        
        System.out.println(data[0][0]);
        System.out.println(data[1][0]);
        System.out.println(data[2][0]);
    }

}
