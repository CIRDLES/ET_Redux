/*
 * ETSerializer.java
 *
 * Created on April 11, 2006, 8:09 PM
 *
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
package org.earthtime.UPb_Redux.utilities;

import java.io.*;
import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.exceptions.ETException;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring
 */
public final class ETSerializer {

    /**
     * Creates a new instance of ETSerializer
     */
    public ETSerializer() {
    }

    /**
     *
     * @param o
     * @param filename
     * @throws org.earthtime.exceptions.ETException
     */
    public static void SerializeObjectToFile(Object o, String filename) throws ETException {
        try {
            // Serialize to a file
            FileOutputStream out = new FileOutputStream(filename);
            try (ObjectOutputStream s = new ObjectOutputStream(out)) {
                s.writeObject(o);
                s.flush();
            }

        } catch (FileNotFoundException ex) {
            throw new ETException(null, "Cannot serialize to: " + filename);
        } catch (IOException ex) {
            throw new ETException(null, "Cannot serialize to: " + filename);
        }
    }

    /**
     *
     * @param filename
     * @return
     */
    public static Object GetSerializedObjectFromFile(String filename) {
        FileInputStream in;
        ObjectInputStream s;
        Object o = null;

        try {
            in = new FileInputStream(filename);
            s = new ObjectInputStream(in);
            o = s.readObject();
        } catch (FileNotFoundException ex) {
            if ((!filename.endsWith(ReduxLabData.getLabDataFileName()))&& (!filename.endsWith(ReduxPersistentState.getPersistentStateFileName()))) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"The file you are attempting to open does not exist:\n"
                            + " " + filename //,
                    });
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    new String[]{"The file you are attempting to open is not compatible with this version of ET_Redux."//,
                    });
        }

        return o;
    }

}
