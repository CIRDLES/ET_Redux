/*
 * MathMachineII.java
 *
 * Created on 18 October 2010
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
package org.earthtime.UPb_Redux.expressions;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.filters.FractionXMLFileFilter;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.fractions.FractionInterface;

/**
 *
 * @author James F. Bowring
 */
public final class MathMachineII {

    // fields
    // DataDictionary element name, path
    private static Map<String, String> expFiles = new HashMap<String, String>();
    private static String rootDirectoryName;
    private static ArrayList<Fraction> fractionsTemp = new ArrayList<Fraction>();

    /**
     *
     */
    public SortedSet<String> valueModelsSelected = new TreeSet<String>();

    /**
     *
     * @param names
     */
    public MathMachineII(SortedSet<String> names) {
        valueModelsSelected = null;
    }

    /**
     *
     * @param variable
     * @param rootDirectoryName
     * @param createNewExpressionFilesXML
     */
//    public static void outputExpressionFilesXML(//
//            ValueModel variable,
//            String rootDirectoryName,
//            boolean createNewExpressionFilesXML) throws IOException {
//
//        // drive ExpTreeII output from here
//
//        if (createNewExpressionFilesXML) {
//            MathMachineII.expFiles = new HashMap<String, String>();
//            MathMachineII.rootDirectoryName = rootDirectoryName;
//        }
//
//        File rootDirectory = new File("." + File.separator + rootDirectoryName);
//
//        if (rootDirectory.exists()) {
//            if (createNewExpressionFilesXML) {
//                // find and delete all .xml files
//                File[] expressionFilesXML = rootDirectory.listFiles(new FractionXMLFileFilter());
//                for (File f : expressionFilesXML) {
//                    f.delete();
//                }
//            }
//        } else {
//            rootDirectory.mkdir();
//        }
//
//        createPresentationFile(variable.getValueTree(), variable.differenceValueCalcs());
//    }
    /**
     *
     * @param variable
     * @param rootDirectoryName
     * @param createNewExpressionFilesXML
     * @throws java.io.IOException
     */
    public static void outputExpressionFilesXML(//
            ValueModel variable,
            String rootDirectoryName,
            boolean createNewExpressionFilesXML) throws IOException {

        // drive ExpTreeII output from here

        if (createNewExpressionFilesXML) {
            MathMachineII.expFiles = new HashMap<String, String>();
            MathMachineII.rootDirectoryName = rootDirectoryName;
        }

        File rootDirectory = new File("." + File.separator + rootDirectoryName);


        if (rootDirectory.exists()) {
            if (createNewExpressionFilesXML) {
                // find and delete all .xml files
                File[] expressionFilesXML = rootDirectory.listFiles(new FractionXMLFileFilter());
                for (File f : expressionFilesXML) {
                    f.delete();
                }
            }
        } else {
            rootDirectory.mkdir();
        }
        createPresentationFileVMs(variable.getValueTree(), variable.differenceValueCalcs());

    }

    /**
     *
     * @param variables
     * @param rootDirectoryName
     * @param createNewExpressionFilesXML
     * @throws IOException
     */
    public static void outputExpressionFilesXML(//
            ValueModel[][] variables,
            String rootDirectoryName,
            boolean createNewExpressionFilesXML) throws IOException {

        // drive ExpTreeII output from here

        if (createNewExpressionFilesXML) {
            MathMachineII.expFiles = new HashMap<String, String>();
            MathMachineII.rootDirectoryName = rootDirectoryName;
        }

        File rootDirectory = new File("." + File.separator + rootDirectoryName+File.separator);


        if (rootDirectory.exists()) {
            if (createNewExpressionFilesXML) {
                // find and delete all .xml files
                File[] expressionFilesXML = rootDirectory.listFiles(new FractionXMLFileFilter());
                for (File f : expressionFilesXML) {
                    f.delete();
                }
            }
        } else {
            rootDirectory.mkdir();
        }

        FileOutputStream outputDirectoryDest = new FileOutputStream(rootDirectory+".zip");
        ZipOutputStream output = new ZipOutputStream(outputDirectoryDest);
        int BUFFER = 2048;
        byte[] data = new byte[BUFFER];
        

        for (ValueModel[] vms : variables) {
            for (ValueModel valueModel : vms) {
                FileInputStream input = new FileInputStream("."
                        + File.separator
                        + rootDirectoryName
                        + File.separator
                        + createPresentationFileVMs(valueModel.getValueTree(), valueModel.differenceValueCalcs()));
                ZipEntry entry = new ZipEntry(valueModel.getName());
                output.putNextEntry(entry);
                BufferedInputStream in = new BufferedInputStream(input, BUFFER);
                int count;
                while ((count = in.read(data, 0, BUFFER)) != -1) {
                    output.write(data, 0, count);
                }
                output.closeEntry();
                in.close();
            }
        }
        output.close();
    }

    /**
     *
     * @param variableTree
     * @param compare
     * @return
     * @throws java.io.IOException
     */
//    public static String createPresentationFile(ExpTreeII variableTree, String compare) throws IOException {
//        String elementName = variableTree.getNodeName();
////        String pmml = variableTree.treeToMathJaxPresentation(1, false); //
////        String pmmlValues = variableTree.treeToMathJaxPresentation(1, true);
//        String pmml = variableTree.treeToPresentation(1, false); //
//        String pmmlValues = variableTree.treeToPresentation(1, true);
//        File rootDirectory = new File("." + File.separator + rootDirectoryName);
//
//        String filePath = "";
//        if (expFiles.containsKey(elementName)) {
//            filePath = expFiles.get(elementName);
//        } else {
//
//            File file = null;
//            Writer output = null;
//
//            String macOrWindowsOS = "";
//            if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
//                macOrWindowsOS = "HoverPreview.js";
//            } else if (System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
//                macOrWindowsOS = "./HoverPreview.js";
//
//            }
//            //******************
//            String htmlString = "<!DOCTYPE html>"
//                    + "<html>\n"
//                    + " <head>\n"
//                    + "<title>UPb_Redux</title>\n"
//                    + "<script type=\"text/javascript\" src=\"" + macOrWindowsOS + "\">\n"
//                    + "</script>\n"
//                    + "<!-- The items here are the scripts and style sheets for the custom context menu. This menu"
//                    + "replaces the one for the entire document.-->"
//                    /**
//                     * The jqcontextmenu.js and .css files along with the implementation of the custom context menu
//                     *have been taken from www.dynamicdrive.com/dynamicindex1/contextmenu.htm
//                     *and modified by Jason Daniel.
//                     **/
//                    + "<link rel=\"stylesheet\" type=\"text/css\" href=\"./jqcontextmenu.css\" />"
//                    + "<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js\"></script>"
//                    + "<script type=\"text/javascript\" src=\"./jqcontextmenu.css\">"
//                    /**
//                     * *********************************************
//                     * jQuery Context Menu- (c) Dynamic Drive DHTML code library
//                     * (www.dynamicdrive.com) This notice MUST stay intact for
//                     * legal use Visit Dynamic Drive at
//                     * http://www.dynamicdrive.com/ for this script and 100s
//                     * more *********************************************
//                     */
//                    + "</script><script type=\"text/javascript\">"
//                    //Usage: $(elementselector).addcontextmenu('id_of_context_menu_on_page')
//                    //To apply context menu to entire document, use: $(document).addcontextmenu('id_of_context_menu_on_page')
//
//                    + "jQuery(document).ready(function($){"
//                    + "$(document).addcontextmenu('contextmenu1')" //apply context menu to all images on the page
//                    + "})"
//                    + "</script>"
//                    + "</head>\n"
//                    + "<body>\n";
//
//            htmlString += "<math display=\"block\">\n"//                    
//                    + variableTree.getNodeNameMathML()
//                    + "<mo id='equals'>=</mo>\n" //
//                    + "<mn>\n"
//                    + formattedValue(variableTree.getNodeValue())
//                    + "</mn>\n"
//                    + "<mo id='equals'>=</mo>\n" //
//                    + pmml //
//                    + ""
//                    + "\n</math>\n";
//
//            htmlString += "<br /><br /><br />";
//
//            htmlString += "\n<math display=\"block\">\n"//                    
//                    + variableTree.getNodeNameMathML()
//                    + " <mo id='equals'>=</mo>\n" //
//                    + "<mn>\n"
//                    + formattedValue(variableTree.getNodeValue()) + "</mn>\n"
//                    + " <mo id='equals'>=</mo>\n" //
//                    + pmmlValues
//                    + "</math>\n"
//                    + "<ul id=\"contextmenu1\" class=\"jqcontextmenu\">"
//                    + "<li> <b> Click a term below to open a window.</b></li>";
//            for (String s : expFiles.values()) {
//                htmlString += "<li><a href = '" + s + "'>" + s.substring(0 ,s.length()-5) + "</a></li>";
//            }
//
//
////            "<li><a onclick=\"openWindow('"+variableTree.getNodeNameMathML()+".html','"
////                                        +variableTree.getNodeNameMathML()+"')>"
////                                        +variableTree.getNodeNameMathML()+"</a></li>";
//
//            htmlString += "</ul>"
//                    + " </body>\n"
//                    + "</html> ";
//
//            //******************
//
//            try {
//
//                //create XML file with Presentation MathML in it
//                filePath = elementName + ".html";
//                file = new File("." + File.separator + rootDirectoryName + File.separator + filePath);
//                file.delete();
//
//                output = new BufferedWriter(new FileWriter(file));
//                output.write(htmlString);
//
//                output.flush();
//                output.close();
//
//
//                expFiles.put(elementName, filePath);
//
//                //System.out.println( "Presentation MathML file " + filePath + " created!" );
//            } catch (IOException iOException) {
//            }
//
//
//
//
//        }
//        return filePath;
//    }
    public static String createPresentationFileVMs(ExpTreeII variableTree, String compare) throws IOException {
        String elementName = variableTree.getNodeName();
        String pmml = variableTree.treeToPresentation(1, false); //
        String pmmlValues = variableTree.treeToPresentation(1, true);

        String filePath = "";
        if (expFiles.containsKey(elementName)) {
            filePath = expFiles.get(elementName);
        } else {

            File file = null;
            Writer output = null;
            String htmlString = "<!DOCTYPE html>\n"
                    + "<head></head>\n"
                    + "<body>\n";

            htmlString += "<math display=\"block\">\n"//
                    + "<mtext>\n"
                    + variableTree.getNodeName()
                    + "</mtext>\n"
                    + "<mo >=</mo>\n" //
                    + "<mn>\n"
                    + formattedValue(variableTree.getNodeValue())
                    + "</mn>\n"
                    + "<mo >=</mo>\n" //
                    + pmml //
                    + ""
                    + "\n</math>\n";

            htmlString += "<br /><br /><br />";

            htmlString += "\n<math display=\"block\">\n"//
                    + "<mtext>\n"
                    + variableTree.getNodeName()
                    + "</mtext>\n"
                    + " <mo >=</mo>\n" //
                    + "<mn>\n"
                    + formattedValue(variableTree.getNodeValue()) + "</mn>\n"
                    + " <mo >=</mo>\n" //
                    + pmmlValues
                    + "</math>\n"
                    + " </body>\n"
                    + "</html>\n";

            //create XML file with Presentation MathML in it
            filePath = elementName + ".html";
            file = new File("." + File.separator + rootDirectoryName + File.separator + filePath);
            file.delete();

            output = new BufferedWriter(new FileWriter(file));
            output.write(htmlString);
            output.flush();
            output.close();

            expFiles.put(elementName, filePath);
        }
        return filePath;
    }

    /**
     *
     * @param value
     * @return
     */
    protected static String formattedValue(BigDecimal value) {
        NumberFormat formatter = new DecimalFormat("0.0000000000E0");

        String retVal = "";

        if (value.setScale(0, RoundingMode.DOWN).compareTo(value) == 0) {
            retVal = value.toPlainString();
        } else {
            retVal = formatter.format(value.doubleValue()); //
        }

        return retVal;
    }

    //*************************************

    /*
     * Creates an index HTML page of valuemodels @param SortedSet<String> vms a
     * sorted set of value models @param rootDirectoryName a name for the
     * directory of the file @param creatNewMMLIndex true to create or falsee to
     * not
     */

    /**
     *
     * @param vms
     * @param rootDirectoryName
     * @param creatNewMMLIndex
     * @param myFraction
     * @param aliquot
     * @throws IOException
     */
    
    public static void CreateHTMLIndexFile(
            ArrayList<ValueModel> vms,
            String rootDirectoryName,
            boolean creatNewMMLIndex,
            UPbFraction myFraction,
            AliquotInterface aliquot) throws IOException {

        //gets fractions from UPbFractionDialogue
        fractionsTemp.clear();
        for (Fraction f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            if (!((FractionInterface) f).isRejected()) {
                fractionsTemp.add(f);
            }
        }
        //Inits
        File file = null;
        Writer output = null;

        String indexHTML = "";
        String filePath = "Index.html";

        if (creatNewMMLIndex) {
            MathMachineII.expFiles = new HashMap<String, String>();
            MathMachineII.rootDirectoryName = rootDirectoryName;
        }

        File rootDirectory = new File("." + File.separator + rootDirectoryName);

        if (rootDirectory.exists()) {
            if (creatNewMMLIndex) {
                // find and delete all .xml files
                File[] expressionFilesXML = rootDirectory.listFiles(new FractionXMLFileFilter());
                for (File f : expressionFilesXML) {
                    f.delete();
                }
            }
        } else {
            rootDirectory.mkdir();
        }

        indexHTML += "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n"
                + " <html>\n"
                + " <head>\n"
                + "<title>\n"
                + "Index For ValueModels\n"
                + "</title>\n"
                + "<meta httpequiv = \"Content-Type\" content = \"text/html; charset = MacRoman\">\n"
                + "</head>\n"
                + "<body onload=\"initContext\">\n"
                + " <h2 align = \"center\">\n"
                + "Index Page of chosen ValueModels:\n"
                + "</h2>\n"
                + "<a href=\"DiffCalc.html\">\n"
                + "DIFFERENCE CALCULATIONS PAGE.\n"
                + "</a>\n"
                + "<p>\n"
                + "Choose one of the following from the list of chosen valuemodels.\n"
                + "</p>\n"
                + "<div id = \"valuemodels\">\n"
                + "<h2>\n"
                + "This is my test page\n"
                + "</h2>\n"
                + "<p>\n"
                + "This page will hold, as an index,  all of chosen ValueModels\n"
                + "</p>\n"
                + "<br/>";

        //Creates shortcutlinks when the are more than 3 tables on the page
        if (vms.size() > 3) {
            indexHTML += "<h3 id=\"top\">\n"
                    + " Click on the name below to go to its table:\n"
                    + " </h3>\n"
                    + "<ul>\n";
            for (ValueModel name : vms) {
                indexHTML += "<li>\n"
                        + "<a href=\"#" + name.getName() + "\">\n" + name.getName() + "\n</a>\n"
                        + "</li>\n";
            }
        }
        indexHTML += "</ul>\n";

        //Creates hyperlink & Table
        int counter = 0;
        for (ValueModel vmsNames : vms) {

            indexHTML += "<table border = 2 id =" + vmsNames.getName() + ">\n"
                    + "<tr>\n"
                    + "<th>\n"
                    + " <a href = \" " + vmsNames.getName() + ".html\">\n"
                    + vmsNames.getName() + "\n"
                    + "</a>\n"
                    + "</th>\n"
                    + "<td>\n"
                    + "ExpTree Value\n"
                    + "</td>\n"
                    + "<td>\n"
                    + "Presentation Value\n"
                    + "</td>\n"
                    + "<td>\n"
                    + "Difference Value\n"
                    + "</td>\n"
                    + "</tr>\n"; //First row with Name and Three separate value names

            //Where actual table is created
            for (Fraction a : fractionsTemp) {
                indexHTML += "<tr>\n"
                        + "<th>\n"
                        + a.getFractionID()
                        + "\n</th> \n"
                        + "<td>\n"
                        + vmsNames.getValue()
                        + "\n</td>\n"
                        + "<td>\n"
                        + vmsNames.getValue()
                        + "\n</td>\n"
                        + vmsNames.getValue()
                        + "\n</td>\n"
                        + "</tr>\n";
            }
            indexHTML += "</table>\n";
            if (counter > 1) {
                indexHTML += " <a href = \"#top\">\n"
                        + "Back to Top\n"
                        + "</a>\n";
            }
            indexHTML += "<br>\n"
                    + "<br>\n";
            counter++;
        }
        indexHTML += "</div>\n"
                + "</body>\n"
                + "</html>\n";

        try {
            file = new File("." + File.separator + rootDirectoryName + File.separator + filePath);
            file.delete();
            output = new BufferedWriter(new FileWriter(file));
            output.write(indexHTML);
            output.flush();
            output.close();
        } catch (IOException ex) {
        }

        for (ValueModel vm : vms) {
            createPresentationFileVMs(vm.getValueTree(), vm.differenceValueCalcs());
        }
    }
    //*************************************

    /*
     * Returns if an Expression Variable is contained in directory
     */

    /**
     *
     * @param keyName
     * @return
     */
    
    public static boolean directoryContains(String keyName) {

        boolean exists = false;
        if (expFiles.containsKey(keyName)) {
            exists = true;
        }
        return exists;
    }
}
