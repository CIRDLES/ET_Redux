/*
 * ETSerializer.java
 *
 * Created on April 11, 2006, 8:09 PM
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
     * @param serializableObject
     * @param fileName
     * @throws org.earthtime.exceptions.ETException
     */
    public static void SerializeObjectToFile(Object serializableObject, String fileName) throws ETException {
//        try {
//            // Serialize to a file
//            FileOutputStream out = new FileOutputStream(fileName);
//            try (ObjectOutputStream s = new ObjectOutputStream(out)) {
//                s.writeObject(serializableObject);
//                s.flush();
//            }
//
//        } catch (FileNotFoundException ex) {
//            throw new ETException(null, "Cannot serialize to: " + fileName);
//        } catch (IOException ex) {
//            throw new ETException(null, "Cannot serialize to: " + fileName);
//        }

        // https://dzone.com/articles/fast-java-file-serialization
        // Sept 2018 speedup per Rayner request
        ObjectOutputStream objectOutputStream = null;
        try {
            RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
            FileOutputStream fos = new FileOutputStream(raf.getFD());
            objectOutputStream = new ObjectOutputStream(fos);
            objectOutputStream.writeObject(serializableObject);
        } catch (IOException ex) {
            throw new ETException("Cannot serialize object of " + serializableObject.getClass().getSimpleName() + " to: " + fileName);

        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException iOException) {
                }
            }
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
            if ((!filename.endsWith(ReduxLabData.getLabDataFileName())) && (!filename.endsWith(ReduxPersistentState.getPersistentStateFileName()))) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"The file you are attempting to open does not exist:\n"
                            + " " + filename //,
                    });
            }
        } catch (IOException | ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null,
                    new String[]{"The file you are attempting to open is not compatible with this version of ET_Redux."//,
                    });

            System.out.println(ex.getMessage());
        }

        return o;
    }

}
