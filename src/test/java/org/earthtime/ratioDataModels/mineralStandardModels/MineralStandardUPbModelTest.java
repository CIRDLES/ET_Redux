/*
 * MineralStandardUPbModel_Test_04092014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on April 9, 2014.
 *
 *Version History:
 *April 9 2014: File Created. Mostly unfinished tests which are commented out.
 *              This is due to persistence issues and other problems that are
 *              described in more detail elsewhere. The tests that are already
 *              completed should be looked over upon a solution to the problems
 *              faced here, as they may not be correct afterwards, or now.
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.ratioDataModels.mineralStandardModels;


import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.MineralStandardUPbConcentrationsPPMEnum;
import org.earthtime.dataDictionaries.MineralStandardUPbRatiosEnum;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import static org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel.createInstance;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
/**
 *
 * @author patrickbrewer
 */
public class MineralStandardUPbModelTest {

    
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////        
    
    
    

    
    
    
    
    
    
    ////////////////////
    ////Method Tests////
    ////////////////////     
    
    /**
     * Test of initializeNewRatiosAndRhos method, of class MineralStandardUPbModel.
     */
    @Test
    public void test_InitializeNewRatiosAndRhos() {
        //Tests for rhos differences before and after function call
        System.out.println("Testing MineralStandardUPbModel's initializeNewRatiosAndRhos(boolean updateOnly)");
        boolean updateOnly = false;
        AbstractRatiosDataModel instance = MineralStandardUPbModel.getEARTHTIMESriLankaStandardModelInstance();
        
        Map<String,BigDecimal> hello = new HashMap();
        hello.put("1", new BigDecimal("1.0"));
        hello.put("3", new BigDecimal("3.0"));
        hello.put("2", new BigDecimal("2.0"));
        instance.setRhosVarUnct(hello);
        
        String b4=instance.getRhosVarUnct().toString();
        
        instance.initializeNewRatiosAndRhos(updateOnly);
        
        String afta=instance.getRhosVarUnct().toString();
        
        if(b4.equals(afta))
            fail("Rhos before and after initialization should not be the same");
    }

    /**
     * Test of getNoneInstance method, of class MineralStandardUPbModel.
*/   
    @Test
    public void test_GetNoneInstance() {
        System.out.println("Testing MineralStandardUPbModel's getNoneInstance()");
        AbstractRatiosDataModel result = MineralStandardUPbModel.getNoneInstance();
        
        String correlations=result.getDataCorrelationsVarUnct().ToStringWithLabels();
        String covariances=result.getDataCovariancesVarUnct().ToStringWithLabels();
        
        String rhos="{rhoR206_207r__r238_235s=0, rhoR206_208r__r208_232r=0, rhoR206_207r__r208_232r=0, rhoR206_208r__r238_235s=0, rhoR206_238r__r238_235s=0, rhoR208_232r__r238_235s=0, rhoR206_238r__r208_232r=0, rhoR206_207r__r206_238r=0, rhoR206_208r__r206_238r=0, rhoR206_207r__r206_208r=0}";
        String realRhos=result.getRhosVarUnct().toString();
        
        assertEquals(true, result.isImmutable());
        assertEquals(0, result.hashCode());
        assertEquals("MATRIX#=Correlations   \n", correlations);
        assertEquals("MATRIX#=Covariances    \n", covariances);
        assertEquals("Placeholder model", result.getComment());
        assertEquals("Placeholder model", result.getReference());
        assertEquals("No Lab", result.getLabName());
        assertEquals("#NONE#", result.getModelName());
        assertEquals(1, result.getVersionNumber());
        assertEquals(0, result.getMinorVersionNumber());
        assertEquals(rhos,realRhos);        
    }
 
    
    
    
    
    
    
    ////////////////////////
    ////Unfinished Tests////
    ////////////////////////     
    
    
    
    
    
    /**
     * Test of getEARTHTIMESriLankaStandardModelInstance method, of class MineralStandardUPbModel.
     * 
    @Test
    public void test_GetEARTHTIMESriLankaStandardModelInstance() {
        System.out.println("Testing getEARTHTIMESriLankaStandardModelInstance");

        AbstractRatiosDataModel result = MineralStandardUPbModel.getEARTHTIMESriLankaStandardModelInstance();
        
        String correlations=result.getDataCorrelations().ToStringWithLabels();
        String covariances=result.getDataCovariances().ToStringWithLabels();
        
        String rhos="{rhoR206_207r__r238_235s=0, rhoR206_208r__r238_235s=0, rhoR206_207r__r206_208r=0, rhoR208_232r__r238_235s=0, rhoR206_238r__r238_235s=0, rhoR206_208r__r208_232r=0, rhoR206_238r__r208_232r=0, rhoR206_207r__r206_238r=0, rhoR206_208r__r206_238r=0, rhoR206_207r__r208_232r=0}";
        
        String realRhos=result.getRhos().toString();
        
    
        System.out.println(covariances);
        
        assertEquals("EARTHTIME SriLanka Standard", result.getModelName());
        assertEquals(1, result.getVersionNumber());
        assertEquals(0, result.getMinorVersionNumber());
        assertEquals("EARTHTIME", result.getLabName());
        assertEquals("2012-04-01", result.getDateCertified());
        assertEquals("Gehrels, G. E., V. A. Valencia, and J. Ruiz (2008), \nEnhanced precision, accuracy, efficiency, and spatial resolution of U-Pb ages by laser ablation.", result.getReference());
        assertEquals("EARTHTIME-supplied model. 208Pb/232Th is calculated from measured 206Pb/238U using EARTHTIME physical constants v.1 and assuming concordance between the two systems.", result.getComment());
        assertEquals(true, result.isImmutable());
        assertEquals(0, result.hashCode());

        
        assertEquals("MATRIX#=Correlations   r206_207r              r206_208r              r206_238r              r208_232r              r238_235s              \n" +
"r206_207r              1.000000000E00         0.000000000E00         -4.006712157E-02       0.000000000E00         0.000000000E00         \n" +
"r206_208r              0.000000000E00         1.000000000E00         0.000000000E00         0.000000000E00         0.000000000E00         \n" +
"r206_238r              -4.006712157E-02       0.000000000E00         1.000000000E00         0.000000000E00         0.000000000E00         \n" +
"r208_232r              0.000000000E00         0.000000000E00         0.000000000E00         1.000000000E00         0.000000000E00         \n" +
"r238_235s              0.000000000E00         0.000000000E00         0.000000000E00         0.000000000E00         1.000000000E00         \n" +
"", correlations);
        
     //   assertEquals("MATRIX#=Covariances    \n", covariances);        
        
        assertEquals(rhos,realRhos);        


    }
   
    */ 
    
    
    
    
    
    
    
     /**
     * Test of MineralStandardUPbModel() method, of class MineralStandardUPbModel.
    
    @Test
    public void test_constructor_0(){
	System.out.println("Testing MineralStandardUPbModel()");
        //Tests if default values are correct. This omits some of the correlation and covariance models.
        MineralStandardUPbModel instance = new MineralStandardUPbModel("model",2,1,"lab","date","reference","comment","mineralstandard","mineral",InitialPbModelET.getNoneInstance() );

        
        
    }    
    
    
    */
    
        
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Test of getInstance method, of class MineralStandardUPbModel.
     * 
    @Test
    public void test_GetInstance() {
        System.out.println("Testing getInstance");
        String modelName = "";
        int versionNumber = 0;
        int minorVersionNumber = 0;

        
        AbstractRatiosDataModel result = MineralStandardUPbModel.getInstance(modelName, versionNumber, minorVersionNumber);
        
        assertEquals(modelName, result.getModelName());
        assertEquals(versionNumber, result.getVersionNumber());
        assertEquals(minorVersionNumber, result.getMinorVersionNumber());

    }*/    
        
   
    
    
    /**
     * Test of createInstance method, of class MineralStandardUPbModel.
  
  * 
    @Test
    public void test_CreateInstance() {
        System.out.println("Testing createInstance");
        String modelName = "";
        int versionNumber = 0;
        int minorVersionNumber = 0;
        String labName = "";
        String dateCertified = "";
        String reference = "";
        String comment = "";
        ValueModel[] ratios = null;
        Map<String, BigDecimal> rhos = null;
        String mineralStandardName = "";
        String mineralName = "";
        AbstractRatiosDataModel initialPbModelET = null;
        AbstractRatiosDataModel result = MineralStandardUPbModel.createInstance(modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment, ratios, rhos, mineralStandardName, mineralName, initialPbModelET);
     
        
        
        assertEquals(modelName, result.getModelName());
        assertEquals(versionNumber, result.getVersionNumber());
        assertEquals(minorVersionNumber, result.getMinorVersionNumber());
        
    }
*/
    /**
     * Test of createNewInstance method, of class MineralStandardUPbModel.
 
    @Test
    public void test_CreateNewInstance() {
        System.out.println("Testing createNewInstance");
        AbstractRatiosDataModel expResult = null;
        AbstractRatiosDataModel result = MineralStandardUPbModel.createNewInstance();
        assertEquals(expResult, result);

        
    }
*/
    
    
    

    
    
    


    /**
     * Test of getEARTHTIMEPeixeStandardModelInstance method, of class MineralStandardUPbModel.
    @Test
    public void test_GetEARTHTIMEPeixeStandardModelInstance() {
        System.out.println("Testing getEARTHTIMEPeixeStandardModelInstance");
        AbstractRatiosDataModel expResult = null;
        AbstractRatiosDataModel result = MineralStandardUPbModel.getEARTHTIMEPeixeStandardModelInstance();
        assertEquals(expResult, result);

        
    }
*/
    /**
     * Test of getEARTHTIMEPlesoviceStandardModelInstance method, of class MineralStandardUPbModel.
    @Test
    public void test_GetEARTHTIMEPlesoviceStandardModelInstance() {
        System.out.println("Testing getEARTHTIMEPlesoviceStandardModelInstance");
        AbstractRatiosDataModel expResult = null;
        AbstractRatiosDataModel result = MineralStandardUPbModel.getEARTHTIMEPlesoviceStandardModelInstance();
        assertEquals(expResult, result);

        
    }
*/
    /**
     * Test of getArrayListOfModels method, of class MineralStandardUPbModel.
     
    @Test
    public void test_GetArrayListOfModels() {
        System.out.println("Testing getArrayListOfModels");
        ArrayList<AbstractRatiosDataModel> expResult = null;
        ArrayList<AbstractRatiosDataModel> result = MineralStandardUPbModel.getArrayListOfModels();
        assertEquals(expResult, result);

* 
    }
*/
    /**
     * Test of readResolve method, of class MineralStandardUPbModel.
    
    @Test
    public void test_ReadResolve() {
        System.out.println("Testing readResolve");
        MineralStandardUPbModel instance = null;
        Object expResult = null;
        Object result = instance.readResolve();
        assertEquals(expResult, result);

    }
*/
    /**
     * Test of cloneModel method, of class MineralStandardUPbModel.
    @Test
    public void test_CloneModel() {
        System.out.println("Testing cloneModel");
        MineralStandardUPbModel instance = null;
        AbstractRatiosDataModel expResult = null;
        AbstractRatiosDataModel result = instance.cloneModel();
        assertEquals(expResult, result);

        
    }
*/
    /**
     * Test of removeSelf method, of class MineralStandardUPbModel.
     
    @Test
    public void test_RemoveSelf() {
        System.out.println("Testing removeSelf");
        MineralStandardUPbModel instance = null;
        instance.removeSelf();

        
    }
*/
    /**
     * Test of initializeModel method, of class MineralStandardUPbModel.
    @Test
    public void test_InitializeModel() {
        System.out.println("Testing initializeModel");
        MineralStandardUPbModel instance = null;
        instance.initializeModel();
    
        
    }
*/
    /**
     * Test of calculateApparentDates method, of class MineralStandardUPbModel.
  
    @Test
    public void test_CalculateApparentDates() {
        System.out.println("Testing calculateApparentDates");
        MineralStandardUPbModel instance = null;
        instance.calculateApparentDates();

        
    }
*/
    /**
     * Test of listFormattedApparentDatesHTML method, of class MineralStandardUPbModel.
    
    @Test
    public void test_ListFormattedApparentDatesHTML() {
        System.out.println("Testing listFormattedApparentDatesHTML");
        MineralStandardUPbModel instance = null;
        String expResult = "";
        String result = instance.listFormattedApparentDatesHTML();
        assertEquals(expResult, result);

        
    }
*/
    /**
     * Test of customizeXstream method, of class MineralStandardUPbModel.
     
    @Test
    public void test_CustomizeXstream() {
        System.out.println("Testing customizeXstream");
        XStream xstream = null;
        MineralStandardUPbModel instance = null;
        instance.customizeXstream(xstream);

        
    }
*/
    /**
     * Test of getMineralStandardName method, of class MineralStandardUPbModel.
     
    @Test
    public void test_GetMineralStandardName() {
        System.out.println("Testing getMineralStandardName");
        MineralStandardUPbModel instance = null;
        String expResult = "";
        String result = instance.getMineralStandardName();
        assertEquals(expResult, result);
   
        
    }
*/
    /**
     * Test of getMineralName method, of class MineralStandardUPbModel.
     
    @Test
    public void test_GetMineralName() {
        System.out.println("Testing getMineralName");
        MineralStandardUPbModel instance = null;
        String expResult = "";
        String result = instance.getMineralName();
        assertEquals(expResult, result);

        
    }
*/
    /**
     * Test of getClassNameAliasForXML method, of class MineralStandardUPbModel.
     
    @Test
    public void test_GetClassNameAliasForXML() {
        System.out.println("Testing getClassNameAliasForXML");
        MineralStandardUPbModel instance = null;
        String expResult = "";
        String result = instance.getClassNameAliasForXML();
        assertEquals(expResult, result);

    }
*/
    /**
     * Test of hasInitialPb method, of class MineralStandardUPbModel.
     
    @Test
    public void test_HasInitialPb() {
        System.out.println("Testing hasInitialPb");
        MineralStandardUPbModel instance = null;
        boolean expResult = false;
        boolean result = instance.hasInitialPb();
        assertEquals(expResult, result);

    }
*/
    /**
     * Test of getInitialPbModelET method, of class MineralStandardUPbModel.
     
    @Test
    public void test_GetInitialPbModelET() {
        System.out.println("Testing getInitialPbModelET");
        MineralStandardUPbModel instance = null;
        AbstractRatiosDataModel expResult = null;
        AbstractRatiosDataModel result = instance.getInitialPbModelET();
        assertEquals(expResult, result);

    }
*/
    /**
     * Test of getApparentDates method, of class MineralStandardUPbModel.
  
    @Test
    public void test_GetApparentDates() {
        System.out.println("Testing getApparentDates");
        MineralStandardUPbModel instance = null;
        ValueModel[] expResult = null;
        ValueModel[] result = instance.getApparentDates();
        assertArrayEquals(expResult, result);


    }
*/
    /**
     * Test of getParDerivTerms method, of class MineralStandardUPbModel.
     
    @Test
    public void test_GetParDerivTerms() {
        System.out.println("Testing getParDerivTerms");
        MineralStandardUPbModel instance = null;
        ConcurrentMap<String, BigDecimal> expResult = null;
        ConcurrentMap<String, BigDecimal> result = instance.getParDerivTerms();
        assertEquals(expResult, result);

* 
    }
*/
    /**
     * Test of setMineralStandardName method, of class MineralStandardUPbModel.
     
    @Test
    public void test_SetMineralStandardName() {
        System.out.println("Testing setMineralStandardName");
        String mineralStandardName = "";
        MineralStandardUPbModel instance = null;
        instance.setMineralStandardName(mineralStandardName);

        
    }*/

    /**
     * Test of setMineralName method, of class MineralStandardUPbModel.
     
    @Test
    public void test_SetMineralName() {
        System.out.println("Testing setMineralName");
        String mineralName = "";
        MineralStandardUPbModel instance = null;
        instance.setMineralName(mineralName);

    }
*/
    /**
     * Test of setInitialPbModelET method, of class MineralStandardUPbModel.
    
    @Test
    public void test_SetInitialPbModelET() {
        System.out.println("Testing setInitialPbModelET");
        AbstractRatiosDataModel initialPbModelET = null;
        MineralStandardUPbModel instance = null;
        instance.setInitialPbModelET(initialPbModelET);

* 
    }
 */

    
   /**
     * Integration Test of class MineralStandardUPbModel
     * Testing the writing and reading of a file
     */
    
    private static final ValueModel[] myConcentrationsPPM = new ValueModel[2];
    
    @Before
    public void setUp()
    {
                // dec 2014 for LAICPMS
        myConcentrationsPPM[0] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concU238ppm.getName(), //
                new BigDecimal("564"), //
                "ABS", //
                new BigDecimal("10"), BigDecimal.ZERO);

        myConcentrationsPPM[1] = new ValueModel(//
                MineralStandardUPbConcentrationsPPMEnum.concTh232ppm.getName(), //
                new BigDecimal("150"), //
                "ABS", //
                new BigDecimal("10"), BigDecimal.ZERO);
    }
    
    @Test
    public void testSerialization() throws Exception
    {
        try{
            ValueModel[] myTestRatios = new ValueModel[3];
            myTestRatios[0] = new ValueModel(//
                    //
                    "r206_204c", //
                    BigDecimal.ZERO, //
                    "ABS", //
                    new BigDecimal(0.06298816629854530000 / 2.0), BigDecimal.ZERO);
            myTestRatios[1] = new ValueModel(//
                    //
                    "r207_204c", //
                    BigDecimal.ZERO, //
                    "ABS", //
                    new BigDecimal(0.92376003656586900000 / 2.0), BigDecimal.ZERO);
            myTestRatios[2] = new ValueModel(//
                    //
                    "r208_204c", //
                    BigDecimal.ZERO, //
                    "ABS", //
                    new BigDecimal(0.00040104065069202200 / 2.0), BigDecimal.ZERO);

            Map<String, BigDecimal> myTestCorrelations = new HashMap<>();
            myTestCorrelations.put("rhoR206_204c__r207_204c", new BigDecimal(-0.0400671215735759));
            myTestCorrelations.put("rhoR206_204c__r208_204c", new BigDecimal(-0.0400671215735759));
            myTestCorrelations.put("rhoR207_204c__r208_204c", new BigDecimal(-0.0400671215735759));
            AbstractRatiosDataModel initialPbModel1 = //
                    InitialPbModelET.createInstance("initialPbModel1", 1, 0, "Test Lab", "2012-04-01", "NO REF", "NO COMMENT", myTestRatios, myTestCorrelations);

            myTestRatios = new ValueModel[4];
            myTestRatios[0] = new MineralStandardUPbRatioModel(//
                    MineralStandardUPbRatiosEnum.r206_207r.getName(), //
                    new BigDecimal(16.9432435810912), //
                    "ABS", //
                    new BigDecimal(0.06298816629854530000 / 2.0),//
                    true);
            myTestRatios[1] = new MineralStandardUPbRatioModel(//
                    MineralStandardUPbRatiosEnum.r206_208r.getName(), //
                    new BigDecimal(27.80), //
                    "ABS", //
                    new BigDecimal(0.92376003656586900000 / 2.0),//
                    true);
            myTestRatios[2] = new MineralStandardUPbRatioModel(//
                    MineralStandardUPbRatiosEnum.r206_238r.getName(), //
                    new BigDecimal(0.09130), //
                    "ABS", //
                    new BigDecimal(0.00040104065069202200 / 2.0),//
                    true);
            myTestRatios[3] = new MineralStandardUPbRatioModel(//
                    MineralStandardUPbRatiosEnum.r238_235s.getName(), //
                    new BigDecimal(137.818), //
                    "ABS", //
                    new BigDecimal(0.04500000000000000000 / 2.0),//
                    true);

            myTestCorrelations = new HashMap<String, BigDecimal>();
            myTestCorrelations.put("rhoR206_207r__r206_238r", new BigDecimal(-0.0400671215735759));

            AbstractRatiosDataModel sriLanka1 = createInstance(//
                    "SriLanka", 1, 0, "Test Lab", "2000-01-01", "NO REF", "NO COMMENT", myTestRatios, myTestCorrelations, myConcentrationsPPM, "Sri Lanka", "zircon", initialPbModel1);

            //Throws Exception
            ETSerializer.SerializeObjectToFile(sriLanka1, "MineralStandardUPbModelTEST.ser");

            AbstractRatiosDataModel sriLanka2 = //
                    (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile("MineralStandardUPbModelTEST.ser");

            String testFileName = "MineralStandardUPbModelTEST.xml";
            sriLanka2.serializeXMLObject(testFileName);

            //Throws Exception
            sriLanka2.readXMLObject(testFileName, false);

            String results = 
                    "MATRIX#=Correlations   r206_207r              r206_208r              r206_238r              r238_235s              \n" +
                    "r206_207r              1.000000000E00         0.000000000E00         -4.006712157E-02       0.000000000E00         \n" +
                    "r206_208r              0.000000000E00         1.000000000E00         0.000000000E00         0.000000000E00         \n" +
                    "r206_238r              -4.006712157E-02       0.000000000E00         1.000000000E00         0.000000000E00         \n" +
                    "r238_235s              0.000000000E00         0.000000000E00         0.000000000E00         1.000000000E00         \n" +
                    "";
            assertEquals(results, sriLanka2.getDataCorrelationsVarUnct().ToStringWithLabels());

            results =
                    "MATRIX#=Covariances    r206_207r              r206_208r              r206_238r              r238_235s              \n" +
                    "r206_207r              9.918772734E-04        0.000000000E00         -2.530320384E-07       0.000000000E00         \n" +
                    "r206_208r              0.000000000E00         2.133331513E-01        0.000000000E00         0.000000000E00         \n" +
                    "r206_238r              -2.530320384E-07       0.000000000E00         4.020840088E-08        0.000000000E00         \n" +
                    "r238_235s              0.000000000E00         0.000000000E00         0.000000000E00         5.062500000E-04        \n" +
                    "";
            assertEquals(results, sriLanka2.getDataCovariancesVarUnct().ToStringWithLabels());
        }finally{
            cleanFiles();
        }
    }
    
    /**
     * Delete the files created previously
     * @throws Exception 
     */       
    public void cleanFiles() throws Exception
    {
        File file = new File("MineralStandardUPbModelTEST.ser"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'MineralStandardUPbModelTEST.ser' couldn't be deleted");
        }
        
        file = new File("MineralStandardUPbModelTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'MineralStandardUPbModelTEST.xml' couldn't be deleted");
        }
    } 
    
}
