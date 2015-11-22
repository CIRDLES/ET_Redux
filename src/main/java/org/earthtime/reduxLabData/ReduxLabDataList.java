/*
 * ReduxLabDataList.java
 *
 * Created on October 11, 2007, 7:44 AM
 *
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.reduxLabData;

import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;

/**
 * This class abstracts the lists of models used in Redux Lab Data. The design
 * includes a first element in each list of name "<none>. The second element is
 * the first usable model.
 *
 * @param <listType>
 * @author James F. Bowring
 */
public class ReduxLabDataList<listType> extends ArrayList<listType> {

    // Class variables
    private static final long serialVersionUID = 4256386169879343995L;
    // fields
    private String listTypeName;

    /**
     * Creates a new instance of ReduxLabDataList
     *
     * @param listTypeName
     */
    public ReduxLabDataList(String listTypeName) {
        super();
        this.listTypeName = listTypeName;
    }

    /**
     *
     * @return @throws BadLabDataException
     */
    public listType getFirstElement()
            throws BadLabDataException {
        if (isEmpty()) {
            throw new BadLabDataException(
                    null, "Cannot find any " + listTypeName);
        } else {
            return get(0);
        }
    }

    /**
     *
     * @return @throws BadLabDataException
     */
    public listType getSecondElement()
            throws BadLabDataException {
        if (isEmpty()) {
            throw new BadLabDataException(
                    null, "Cannot find any " + listTypeName);
        } else if (size() > 1) {
            return get(1);
        } else {
            return get(0);
        }
    }

    /**
     *
     * @param elementName
     * @return
     */
    public listType getAnElement(String elementName) /* throws BadLabDataException */ {
        // we look for name of model by walking list
        for (int m = 0; m < size(); m++) {
            if (((ReduxLabDataListElementI) get(m)).getReduxLabDataElementName().equalsIgnoreCase(elementName)) {
                return get(m);
            }
        }

        // else
        if (size() > 1) {
            return get(1);
        } else {
            return get(0);
        }
//        throw new BadLabDataException(
//                null, "Cannot find " + listTypeName + "  " + elementName);
    }

    /**
     *
     * @param elementName
     * @return
     * @throws BadLabDataException
     */
    public boolean removeAnElement(String elementName)
            throws BadLabDataException {
        // we look for name of model by walking list
        // leaving out the first element = #NONE#
        for (int m = 1; m < size(); m++) {

            ReduxLabDataListElementI model = ((ReduxLabDataListElementI) get(m));
            if (model.getReduxLabDataElementName().equalsIgnoreCase(elementName)) {
                remove(m);

                // march 2012
                model.removeSelf();

                return true;
            }
        }
        throw new BadLabDataException(
                null, "Cannot remove " + listTypeName + "  " + elementName);
    }

    /**
     *
     * @param elementName
     * @return
     */
    public boolean containsElementName(String elementName) {
        boolean retval = false;
        //march 2012 backward compatible
        if (elementName.startsWith("<none>")) {
            return true;
        }

        for (Iterator it = iterator(); it.hasNext();) {
            Object element = it.next();
            if (((ReduxLabDataListElementI) element).getReduxLabDataElementName().equalsIgnoreCase(elementName)) {
                retval = true;
            }
        }
        return retval;

    }

    /**
     *
     * @param element
     * @param isVerbose
     * @return
     */
    public boolean registerElement(Object element, boolean isVerbose) {
        boolean retval = false;
        if (element != null) {
            if (containsElementName(((ReduxLabDataListElementI) element).getReduxLabDataElementName())) {
                if (isVerbose) {
                    JOptionPane.showMessageDialog(null,
                            new String[]{listTypeName + " "//
                                + ((ReduxLabDataListElementI) element).getReduxLabDataElementName() + " is already registered with LabData."},
                            "ET Redux Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            } else {
                retval = true;
                if (isVerbose) {
                    JOptionPane.showMessageDialog(null,
                            new String[]{listTypeName + " " //
                                + ((ReduxLabDataListElementI) element).getReduxLabDataElementName() + " is now registered with LabData."},
                            "U-Pb Redux Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
        return retval;
    }

}
