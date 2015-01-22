/*
 * AbstractTester.java
 *
 * This file exists so that methods of AbstractMatrixModel can be tested. All
 * of the implemented methods here are simply here in order to prevent errors
 * and are intended to be tested later in the implementations.
 *
 * Created on February 17, 2014.
 *
 *Version History:
 *February 17 2014 : File Created. Constructor tests completed.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.ratioDataModels;

import com.thoughtworks.xstream.XStream;
import java.util.HashMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.matrices.matrixModels.CorrelationMatrixModel;
import org.earthtime.matrices.matrixModels.CovarianceMatrixModel;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author Patrick Brewer
 */
public class AbstractTester extends AbstractRatiosDataModel{
    
    /**
     *
     */
    protected AbstractTester () {
        this.XMLSchemaURL = "";
        this.modelName = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.versionNumber = 1;
        this.minorVersionNumber = 0;
        this.labName = ReduxConstants.DEFAULT_OBJECT_NAME;

        this.dateCertified = DateHelpers.defaultEarthTimeDateString();

        this.reference = "None";
        this.comment = "None";

        this.ratios = new ValueModel[0];
        this.rhos = new HashMap<>();
        this.dataCovariancesVarUnct = new CovarianceMatrixModel();
        this.dataCorrelationsVarUnct = new CorrelationMatrixModel();
        this.immutable = false;
    }    
    
    /**
     *
     * @param modelName
     * @param versionNumber
     * @param minorVersionNumber
     * @param labName
     * @param dateCertified
     * @param reference
     * @param comment
     */
    protected AbstractTester (String modelName, int versionNumber, int minorVersionNumber, String labName, String dateCertified, String reference, String comment ) {
        this();
        this.modelName = modelName.trim();
        this.versionNumber = versionNumber;
        this.minorVersionNumber = minorVersionNumber;
        this.labName = labName;
        this.dateCertified = dateCertified;
        this.reference = reference;
        this.comment = comment;
    }
                
    
        
        
        //Generated to Avoid Errors
    
    

        @Override
        public AbstractRatiosDataModel cloneModel() {
        AbstractRatiosDataModel myModel = new AbstractTester(this.modelName,//
                this.versionNumber, 
                this.minorVersionNumber,
                this.labName, //
                this.dateCertified, //
                this.reference, //
                this.comment );

        myModel.initializeModel(cloneData(), cloneRhosVarUnct(), null );

        return myModel;
        }

    /**
     *
     * @param updateOnly
     */
    @Override
        public void initializeNewRatiosAndRhos(boolean updateOnly) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected void customizeXstream(XStream xstream) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getClassNameAliasForXML() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void removeSelf() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        
        }
    
}
