/*
 * MathMachine.java
 *
 * Created on 18 October 2010
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
package org.earthtime.UPb_Redux.expressions;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.filters.FractionXMLFileFilter;
import org.earthtime.UPb_Redux.valueModels.ValueModel;



/**
 *
 * @author James F. Bowring
 */
public final class MathMachine {

    // fields
    // DataDictionary element name, path
    private static Map<String, String> expFiles = new HashMap<String, String>();
    private static String rootDirectoryName;

    /**
     * 
     */
    public MathMachine () {
    }

    /**
     * 
     * @param variable
     * @param rootDirectoryName
     * @param createNewExpressionFilesXML
     */
    public static void outputExpressionFilesXML (//
            ValueModel variable,
            String rootDirectoryName,
            boolean createNewExpressionFilesXML ) {

        // drive ExpTree output from here

        if ( createNewExpressionFilesXML ) {
            MathMachine.expFiles = new HashMap<String, String>();
            MathMachine.rootDirectoryName = rootDirectoryName;
        }

        File rootDirectory = new File( "." + File.separator + rootDirectoryName );

        if ( rootDirectory.exists() ) {
            if ( createNewExpressionFilesXML ) {
                // find and delete all .xml files
                File[] expressionFilesXML = rootDirectory.listFiles( new FractionXMLFileFilter() );
                for (File f : expressionFilesXML) {
                    f.delete();
                }
            }
        } else {
            rootDirectory.mkdir();
        }

//        createPresentationFile( variable.getValueTree(), variable.differenceValueCalcs());


    }

    /**
     * 
     * @param variableTree
     * @param compare
     * @return
     */
    public static String createPresentationFile ( ExpTree variableTree, String compare ) {

        String elementName = variableTree.getNodeName();
        String pmml = variableTree.treeToPresentation( 1, false ); //
        String pmmlValues = variableTree.treeToPresentation( 1, true );

        String filePath = "";
        if ( expFiles.containsKey( elementName ) ) {
            filePath = expFiles.get( elementName );
        } else {

            File file = null;
            Writer output = null;
            String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

            xmlString += "<?xml-stylesheet href=\"xml_plus_xhtml.css\" type=\"text/css\"?>";
            xmlString += "<page xmlns:html=\"http://www.w3.org/1999/xhtml\" updated=\"jan 2007\">";

 
            xmlString += "\n<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">\n"//
                    + "<mi>" + variableTree.getNodeNameMathML() + "</mi> <mo id='equals'>=</mo>" //
                    + "<mn>" + formattedValue(variableTree.getNodeValue()) + "</mn> <mo id='equals'>=</mo>" //
                    + pmml //
                    + "</math>";

            xmlString += "<br />&#160;<br />&#160;<br />";
            
            xmlString += "\n<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">\n"//
                    + "<mi>" + variableTree.getNodeNameMathML() + "</mi> <mo id='equals'>=</mo>" //
                    + "<mn>" + formattedValue(variableTree.getNodeValue()) + "</mn> <mo id='equals'>=</mo>" //
                    + pmmlValues
                    + "</math>";

            xmlString += "<br />&#160;<br />&#160;<br />";
            
            xmlString += "\n<math xmlns=\"http://www.w3.org/1998/Math/MathML\" display=\"block\">\n"//
                    + "<mi>" + compare + "</mi>"
                    + "</math>";



         //   xmlString += "<br/>" + compare + "<br/>";

            xmlString += "</page>";

            try {

                //create XML file with Presentation MathML in it
                filePath = elementName + ".xml" ;
                file = new File("." + File.separator + rootDirectoryName + File.separator + filePath);
                file.delete();

                output = new BufferedWriter( new FileWriter( file ) );
                output.write( xmlString );

                output.flush();
                output.close();


                expFiles.put( elementName, filePath );

                //System.out.println( "Presentation MathML file " + filePath + " created!" );
            } catch (IOException iOException) {
            }

        }
        return filePath;

    }

    /**
     * 
     * @param value
     * @return
     */
    protected static String formattedValue ( BigDecimal value ) {
        NumberFormat formatter = new DecimalFormat( "0.0000000000E0" );

        String retVal = "";

        if (value.setScale( 0, RoundingMode.DOWN).compareTo( value) == 0){
            retVal = value.toPlainString();
        } else {
            retVal = formatter.format( value.doubleValue() ); //
        }

        return retVal;
    }
}
