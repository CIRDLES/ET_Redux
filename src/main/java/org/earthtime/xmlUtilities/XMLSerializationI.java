/*
 * XMLSerializationI.java
 *
 * Created on August 6, 2007, 1:53 PM
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

package org.earthtime.xmlUtilities;

import java.io.FileNotFoundException;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.exceptions.ETException;

/**
 *
 * @author James F. Bowring
 */
public interface XMLSerializationI {
    
    
    // XML Serialization
//    /**
//     * 
//     * @return
//     */
//    abstract XStream getXStreamWriter();
//    /**
//     * 
//     * @return
//     */
//    abstract XStream getXStreamReader();
//    
//    /**
//     * 
//     * @param xstream
//     */
//    abstract void customizeXstream(XStream xstream);
    
//    /**
//     * 
//     */
//    abstract void setClassXMLSchemaURL();
//    
    /**
     * 
     * @param filename
     */
    abstract void serializeXMLObject(String filename) ;
    
    /**
     * 
     * @param filename
     * @param doValidate
     * @return
     * @throws FileNotFoundException
     * @throws ETException
     * @throws BadOrMissingXMLSchemaException
     */
    public Object readXMLObject(String filename, boolean doValidate)
    throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException;
   
}
