/*
 * SampleTreeCompilationMode.java
 *
 * Created April 2009
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
package org.earthtime.UPb_Redux.dateInterpretation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.earthtime.UPb_Redux.customJTrees.CheckBoxNode;
import org.earthtime.UPb_Redux.customJTrees.CheckBoxNodeEditor;
import org.earthtime.UPb_Redux.customJTrees.CheckBoxNodeRenderer;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers.SampleDateInterpretationChooserDialog;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelI;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class SampleTreeCompilationMode extends JTree implements SampleTreeI {

    // instance variables
    private SampleInterface sample;
    private SampleTreeChangeI sampleTreeChange;
    private Object lastNodeSelected;

    /**
     * Creates a new instance of SampleTreeCompilationMode
     */
    public SampleTreeCompilationMode () {
        super();
        sample = null;
    }

    /**
     *
     * @param mySample
     */
    public SampleTreeCompilationMode ( SampleInterface mySample ) {
        super( new DefaultMutableTreeNode( mySample ) );
        sample = mySample;

        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        setCellRenderer( renderer );

        setCellEditor( new CheckBoxNodeEditor( this ) );
        setEditable( true );

        setLastNodeSelected( null );
    }

    /**
     *
     */
    @Override
    public void buildTree () {
        this.removeAll();

        // populate tree
        // get a master vector of active fraction names
        Vector<String> activeFractionIDs =//
                sample.getSampleFractionIDs();

        // load the sample date interpretations
        for (int index = 0; index < sample.getSampleDateModels().size(); index ++) {
            DefaultMutableTreeNode sampleDateModelNode =//
                    new DefaultMutableTreeNode(//
                    (SampleDateModel) sample.getSampleDateModels().get( index ) );

            ((DefaultMutableTreeNode) getModel().getRoot()).add( sampleDateModelNode );

            // remove from activefractionIDs any fraction with 0 date
            Vector<String> zeroFractionDates = new Vector<>();
            for (int i = 0; i < activeFractionIDs.size(); i ++) {
                if (  ! ((SampleDateModel) sample.getSampleDateModels().get( index )).//
                        fractionDateIsPositive( sample.getSampleFractionByName( activeFractionIDs.get( i ) ) ) ) {
                    zeroFractionDates.add( activeFractionIDs.get( i ) );
                }
            }
            for (int i = 0; i < zeroFractionDates.size(); i ++) {
                activeFractionIDs.remove( zeroFractionDates.get( i ) );
            }

            PopulateSampleDateModel(
                    activeFractionIDs,
                    sample,
                    sample.getSampleDateModels().get( index ),
                    sampleDateModelNode );
        }


        int row = getRowCount() - 1;
        while (row >= 1) {
            collapseRow( row );
            row --;
        }

        DefaultMutableTreeNode rootNode = ((DefaultMutableTreeNode) getModel().getRoot());
        for (int i = 0; i < rootNode.getChildCount(); i ++) {
            try {
                DefaultMutableTreeNode sampleDateNode = (DefaultMutableTreeNode) rootNode.getChildAt( i );
                expandPath( new TreePath( sampleDateNode.getPath() ) );
            } catch (Exception e) {
            }
        }

        getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );

        //Listen for when the selection changes.
        addTreeSelectionListener( this );
        addMouseListener( this );

        // set sample as default selection
        setSelectionRow( 0 );
    }

    private void PopulateSampleDateModel (
            Vector<String> activeFractionIDs,
            SampleInterface sample,
            ValueModel SAM,
            DefaultMutableTreeNode SAMnode ) {

        DefaultMutableTreeNode sampleDateValue = //
                new DefaultMutableTreeNode(//
                ((SampleDateModel) SAM).ShowCustomDateNode() );
        SAMnode.add( sampleDateValue );

        DefaultMutableTreeNode sampleDateMSWD = //
                new DefaultMutableTreeNode(//
                ((SampleDateModel) SAM).ShowCustomMSWDwithN() );
        SAMnode.add( sampleDateMSWD );

        if ( ((SampleDateModel) SAM).getMethodName().contains( "LowerIntercept" ) ) {
            SAMnode.add( new DefaultMutableTreeNode( "See Upper Intercept Fractions" ) );
        } else {
            DefaultMutableTreeNode sampleDateFractions = //
                    new DefaultMutableTreeNode( "Aliquot Fractions" );
            SAMnode.add( sampleDateFractions );

            // organize fractions by aliquot for the user
            // fractions are in order, just need to extract aliquot
            // create checkbox for each fraction set to whether it is in list
            String saveAliquotName = "";
            DefaultMutableTreeNode aliquotNameNode = new DefaultMutableTreeNode( "NONE" );
            for (String fracID : activeFractionIDs) {
                String aliquotName = sample.getAliquotNameByFractionID( fracID );
                if (  ! aliquotName.equalsIgnoreCase( saveAliquotName ) ) {
                    saveAliquotName = aliquotName;
                    aliquotNameNode = new DefaultMutableTreeNode( aliquotName );
                    sampleDateFractions.add( aliquotNameNode );
                }

                DefaultMutableTreeNode fractionNode = new DefaultMutableTreeNode( fracID );

                fractionNode.setUserObject( //
                        new CheckBoxNode(
                        ((SampleDateModel) SAM).showFractionIdWithDateAndUnct(//
                        sample.getSampleFractionByName( fracID ), "Ma" ),
                        ((SampleDateModel) SAM).includesFractionByName( fracID ),
                        true ) );
                aliquotNameNode.add( fractionNode );
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void valueChanged ( TreeSelectionEvent e ) {
        //Returns the last path element of the selection.
        //This method is useful only when the selection model allows a single selection.
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();

        if ( node == null ) //Nothing is selected.	
        {
            return;
        }

        setLastNodeSelected( node );

        //System.out.println(e.getSource());
        Object nodeInfo = node.getUserObject();

        if ( nodeInfo instanceof Sample ) {
          //  System.out.println( ((SampleInterface) nodeInfo).getSampleName() );
        } else if ( nodeInfo instanceof AliquotInterface ) {
          //  System.out.println(((AliquotInterface) nodeInfo).getAliquotName() );
        } else if ( nodeInfo instanceof ValueModel ) {
          //  System.out.println( ((ValueModelI) nodeInfo).getName() );
        } else if ( nodeInfo instanceof CheckBoxNode ) {
           // System.out.println( nodeInfo.toString() );
            // required for toggling because it allows re-focus
            setSelectionRow( -1 );

        } else {
           // System.out.println( nodeInfo.toString() );
        }

        getSampleTreeChange().sampleTreeChangeCompilationMode( node );


    }

    /**
     *
     * @param value
     * @param selected
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     * @return
     */
    @Override
    public String convertValueToText ( Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus ) {

        Object o = ((DefaultMutableTreeNode) value).getUserObject();

        if ( o instanceof Sample ) {
            return ((SampleInterface) o).getSampleName();
        } else if ( o instanceof AliquotInterface ) {
            return ((AliquotInterface) o).getAliquotName();
        } else if ( o instanceof ValueModel ) {
            String displayName = ((ValueModelI) o).getName();
            if ( ((SampleDateModel) o).isPreferred() ) {
                displayName = "PREFERRED: " + displayName;
            }
            if ( ((SampleDateModel) o).isDisplayedAsGraph() ) {
                displayName = "\u25CF " + displayName;
            }
            return displayName + "        ";

        } else if ( (o instanceof String) && (((String) o).startsWith( "date" )) ) {
            return //                
                    ((SampleDateModel) ((DefaultMutableTreeNode) ((TreeNode) value).//
                    getParent()).getUserObject()).ShowCustomDateNode();

        } else if ( (o instanceof String) && (((String) o).startsWith( "MSWD" )) ) {
            return //                
                    ((SampleDateModel) ((DefaultMutableTreeNode) ((TreeNode) value).//
                    getParent()).getUserObject()).ShowCustomMSWDwithN() + "              ";

        } else {
            return super.convertValueToText(
                    value,
                    selected,
                    expanded,
                    leaf,
                    row,
                    hasFocus );
        }
    }

    /**
     *
     */
    @Override
    public void performLastUserSelection () {
        getSampleTreeChange().sampleTreeChangeCompilationMode( getLastNodeSelected() );//
    }
    // Provide for adding sample age interpretations

    /**
     *
     * @param e
     */
    @Override
    public void mousePressed ( MouseEvent e ) {
        int selRow = getRowForLocation( e.getX(), e.getY() );
        TreePath selPath = getPathForLocation( e.getX(), e.getY() );

        if ( selRow != -1 ) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            final Object nodeInfo = node.getUserObject();
            if (  ! e.isPopupTrigger() && (e.getButton() == MouseEvent.BUTTON1) ) {
            } else if ( (e.isPopupTrigger()) || (e.getButton() == MouseEvent.BUTTON3) ) {
                setSelectionPath( selPath );
                if ( nodeInfo instanceof Sample ) {
                    DialogEditor myEditor =
                            new SampleDateInterpretationChooserDialog(
                            null,
                            true,
                            ((SampleInterface) nodeInfo).determineUnusedSampleDateModels( false ) );

                    myEditor.setSize( 340, 395 );
                    DialogEditor.setDefaultLookAndFeelDecorated( true );

                    myEditor.setVisible( true );

                    // get a master vector of active fraction names
                    Vector<String> activeFractionIDs =//
                            ((SampleInterface) nodeInfo).getSampleFractionIDs();

                    if ( ((SampleDateInterpretationChooserDialog) myEditor).getSelectedModels().size() > 0 ) {
                        DefaultMutableTreeNode sampleDateModelNode = null;

                        ArrayList<Integer> tempNewNodes = new ArrayList<Integer>();
                        for (ValueModel selectedSAM : ((SampleDateInterpretationChooserDialog) myEditor).getSelectedModels()) {

                            ((SampleInterface) nodeInfo).getSampleDateModels().add( selectedSAM );
                            // feb 2013 add in SampleAnalysisTypesEnum  for transition to logbased calcs starting with LAICPMS from raw
                            ((SampleDateModel) selectedSAM).setSampleAnalysisType( SampleAnalysisTypesEnum.valueOf( sample.getSampleAnalysisType().trim() ) );


                            // fix up tree
                            sampleDateModelNode = new DefaultMutableTreeNode( selectedSAM );

                            // remove from activefractionIDs any fraction with 0 date
                            Vector<String> zeroFractionDates = new Vector<>();
                            for (String activeFractionID : activeFractionIDs) {
                                if (! //
                                        ((SampleDateModel) selectedSAM).fractionDateIsPositive(sample.getSampleFractionByName(activeFractionID))) {
                                    zeroFractionDates.add(activeFractionID);
                                }
                            }
                            for (String zeroFractionDate : zeroFractionDates) {
                                activeFractionIDs.remove(zeroFractionDate);
                            }

                            PopulateSampleDateModel(
                                    activeFractionIDs,
                                    ((SampleInterface) nodeInfo),
                                    selectedSAM,
                                    sampleDateModelNode );

                            node.add( sampleDateModelNode );

                            // save node indexes of inserted nodes
                            tempNewNodes.add( node.getChildCount() - 1 );
                        }

                        sample.updateSampleDateModels();

                        int[] newNodes = new int[tempNewNodes.size()];
                        for (int i = 0; i < tempNewNodes.size(); i ++) {
                            newNodes[i] = tempNewNodes.get( i );
                        }

                        ((DefaultTreeModel) getModel()).nodesWereInserted(
                                node, newNodes );//new int[]{node.getChildCount() - 1});

                        // collapse all and expand new date
                        int row = getRowCount() - 1;
                        while (row >= 1) {
                            collapseRow( row );
                            row --;
                        }
                        expandPath( new TreePath(//
                                ((DefaultMutableTreeNode) sampleDateModelNode.getChildAt( 2 )).getPath() ) );

                        getSampleTreeChange().sampleTreeChangeCompilationMode( node );


                    }


                } else if ( nodeInfo instanceof AliquotInterface ) {
                } else if ( nodeInfo instanceof ValueModel ) {
                    //Create the popup menu.
                    JPopupMenu popup = new JPopupMenu();

                    JMenuItem menuItem = new JMenuItem( "Set as Preferred Sample Date Interpretation" );
                    menuItem.addActionListener( new ActionListener() {
                        @Override
                        public void actionPerformed ( ActionEvent arg0 ) {
                            DefaultMutableTreeNode sampleNode =
                                    (DefaultMutableTreeNode) node.getParent();
                            Object sampleNodeInfo = sampleNode.getUserObject();
                            ((SampleInterface) sampleNodeInfo).setPreferredSampleDateModel( (ValueModel) nodeInfo );
                            sample.updateSampleDateModels();

                            // fix tree
                            ((DefaultTreeModel) getModel()).nodeChanged( node );
                            getSampleTreeChange().sampleTreeChangeCompilationMode( node );
                        }
                    } );
                    popup.add( menuItem );

                    menuItem = new JMenuItem( "Delete Sample Date Interpretation" );
                    menuItem.addActionListener( new ActionListener() {
                        @Override
                        public void actionPerformed ( ActionEvent arg0 ) {
                            // delete sample age from aliquot
                            DefaultMutableTreeNode sampleNode =
                                    (DefaultMutableTreeNode) node.getParent();
                            Object sampleNodeInfo = sampleNode.getUserObject();

                            // remove and save sample
                            // first check for special case of lower-upper intercept
                            DefaultMutableTreeNode otherInterceptNode = null;
                            Object otherInterceptNodeInfo = null;
                            int[] indicesOfIntercepts = new int[2];
                            DefaultMutableTreeNode[] nodesOfIntercepts = new DefaultMutableTreeNode[2];

                            if ( ((SampleDateModel) nodeInfo).getMethodName().equalsIgnoreCase( "LowerIntercept" ) ) {
                                // also remove the next node == upper intercept
                                otherInterceptNode = node.getNextSibling();
                                otherInterceptNodeInfo = otherInterceptNode.getUserObject();
                                indicesOfIntercepts[0] = sampleNode.getIndex( node );
                                nodesOfIntercepts[0] = node;
                                indicesOfIntercepts[1] = indicesOfIntercepts[0] + 1;
                                nodesOfIntercepts[1] = node.getNextSibling();
                            }
                            if ( ((SampleDateModel) nodeInfo).getMethodName().equalsIgnoreCase( "UpperIntercept" ) ) {
                                // also remove the previous node == lower intercept
                                otherInterceptNode = node.getPreviousSibling();
                                otherInterceptNodeInfo = otherInterceptNode.getUserObject();
                                indicesOfIntercepts[1] = sampleNode.getIndex( node );
                                nodesOfIntercepts[1] = node;
                                indicesOfIntercepts[0] = indicesOfIntercepts[1] - 1;
                                nodesOfIntercepts[0] = node.getPreviousSibling();
                            }
                            if ( otherInterceptNodeInfo != null ) {
                                // this is the special case where the two intercpt nodes were removed
                                ((SampleInterface) sampleNodeInfo).getSampleDateModels().remove( (ValueModel) nodeInfo );
                                ((SampleInterface) sampleNodeInfo).getSampleDateModels().remove( (ValueModel) otherInterceptNodeInfo );
                                sample.updateSampleDateModels();

                                // fix up tree
                                sampleNode.remove( nodesOfIntercepts[0] );
                                sampleNode.remove( nodesOfIntercepts[1] );
                                ((DefaultTreeModel) getModel()).nodesWereRemoved(
                                        sampleNode,
                                        indicesOfIntercepts,
                                        nodesOfIntercepts );
                            } else {

                                ((SampleInterface) sampleNodeInfo).getSampleDateModels().remove( (ValueModel) nodeInfo );
                                sample.updateSampleDateModels();

                                // fix up tree
                                int indexOfNode = sampleNode.getIndex( node );
                                sampleNode.remove( node );
                                ((DefaultTreeModel) getModel()).nodesWereRemoved(
                                        sampleNode,
                                        new int[]{indexOfNode},
                                        new Object[]{node} );
                            }

                            getSampleTreeChange().sampleTreeChangeCompilationMode( sampleNode );
                        }
                    } );
                    popup.add( menuItem );

                    // added to speed choice of graph for weighted means
                    if ( ((ValueModel) nodeInfo).getName().startsWith( "weighted" ) ) {
                        if ( ((SampleDateModel) nodeInfo).isDisplayedAsGraph() ) {
                            menuItem = new JMenuItem( "Hide Graph" );
                            menuItem.addActionListener( new ActionListener() {
                                public void actionPerformed ( ActionEvent arg0 ) {
                                    ((SampleDateModel) nodeInfo).setDisplayedAsGraph( false );

                                    getSampleTreeChange().sampleTreeChangeCompilationMode( node );
                                }
                            } );
                            popup.add( menuItem );

                        } else {
                            menuItem = new JMenuItem( "Show Graph" );
                            menuItem.addActionListener( new ActionListener() {
                                public void actionPerformed ( ActionEvent arg0 ) {
                                    ((SampleDateModel) nodeInfo).setDisplayedAsGraph( true );

                                    getSampleTreeChange().sampleTreeChangeCompilationMode( node );
                                }
                            } );
                            popup.add( menuItem );
                        }
                    }

                    popup.show( e.getComponent(),
                            e.getX(), e.getY() );

                } else if ( nodeInfo instanceof String ) {
                    System.out.println( "STRING HIT" );
                    if ( ((String) nodeInfo).equalsIgnoreCase( "Aliquot Fractions" ) ) {

                        //Create the popup menu.
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem menuItem = new JMenuItem( "Select None" );
                        menuItem.addActionListener( new ActionListener() {
                            public void actionPerformed ( ActionEvent arg0 ) {
                                DefaultMutableTreeNode sampleDateNode =
                                        (DefaultMutableTreeNode) ((DefaultMutableTreeNode) node).getParent();
                                Object SampleDateNodeInfo = sampleDateNode.getUserObject();
                                ((SampleDateModel) SampleDateNodeInfo).//
                                        setIncludedFractionIDsVector( new Vector<String>() );

                                sample.updateSampleDateModels();

                                // fix tree
                                for (int a = 0; a < node.getChildCount(); a ++) {
                                    DefaultMutableTreeNode aliquotNameNode = //
                                            (DefaultMutableTreeNode) ((DefaultMutableTreeNode) node).getChildAt( a );

                                    for (int c = 0; c < aliquotNameNode.getChildCount(); c ++) {
                                        ((CheckBoxNode) ((DefaultMutableTreeNode) aliquotNameNode.//
                                                getChildAt( c )).getUserObject()).setSelected( false );
                                        ((DefaultTreeModel) getModel()).nodeChanged( aliquotNameNode.getChildAt( c ) );
                                    }
                                    ((DefaultTreeModel) getModel()).nodeChanged( aliquotNameNode );

                                }
                                ((DefaultTreeModel) getModel()).nodeChanged( node );

                                ((SampleDateModel) SampleDateNodeInfo).//
                                        CalculateDateInterpretationForSample();

                                getSampleTreeChange().sampleTreeChangeCompilationMode( sampleDateNode );
                            }
                        } );

                        popup.add( menuItem );

                        menuItem = new JMenuItem( "Select All" );
                        menuItem.addActionListener( new ActionListener() {
                            @Override
                            public void actionPerformed ( ActionEvent arg0 ) {
                                DefaultMutableTreeNode sampleDateNode = //
                                        (DefaultMutableTreeNode) node.getParent();
                                DefaultMutableTreeNode sampleNode = //
                                        (DefaultMutableTreeNode) sampleDateNode.getParent();

                                Object SampleDateNodeInfo = sampleDateNode.getUserObject();
                                Object SampleNodeInfo = sampleNode.getUserObject();

                                ((SampleDateModel) SampleDateNodeInfo).//
                                        setIncludedFractionIDsVector(//
                                        ((SampleInterface) SampleNodeInfo).getSampleFractionIDs() );

                                sample.updateSampleDateModels();

                                // fix tree
                                for (int a = 0; a < node.getChildCount(); a ++) {
                                    DefaultMutableTreeNode aliquotNameNode = //
                                            (DefaultMutableTreeNode) ((DefaultMutableTreeNode) node).getChildAt( a );

                                    for (int c = 0; c < aliquotNameNode.getChildCount(); c ++) {
                                        ((CheckBoxNode) ((DefaultMutableTreeNode) aliquotNameNode.//
                                                getChildAt( c )).getUserObject()).setSelected( true );
                                        ((DefaultTreeModel) getModel()).nodeChanged( aliquotNameNode.getChildAt( c ) );
                                    }
                                    ((DefaultTreeModel) getModel()).nodeChanged( aliquotNameNode );

                                }
                                ((DefaultTreeModel) getModel()).nodeChanged( node );

                                ((SampleDateModel) SampleDateNodeInfo).//
                                        CalculateDateInterpretationForSample();

                                getSampleTreeChange().sampleTreeChangeCompilationMode( sampleDateNode );

                            }
                        } );

                        popup.add( menuItem );

                        popup.show( e.getComponent(),
                                e.getX(), e.getY() );
                    }
                }

            }
        } else {
            // do nothing
        }
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void mouseReleased ( MouseEvent arg0 ) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void mouseEntered ( MouseEvent arg0 ) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void mouseExited ( MouseEvent arg0 ) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void mouseClicked ( MouseEvent arg0 ) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the sampleTreeChange
     */
    @Override
    public SampleTreeChangeI getSampleTreeChange () {
        return sampleTreeChange;
    }

    /**
     * @param sampleTreeChange the sampleTreeChange to set
     */
    @Override
    public void setSampleTreeChange ( SampleTreeChangeI sampleTreeChange ) {
        this.sampleTreeChange = sampleTreeChange;
    }

    /**
     * @return the lastNodeSelected
     */
    public Object getLastNodeSelected () {
        return lastNodeSelected;
    }

    /**
     * @param lastNodeSelected the lastNodeSelected to set
     */
    public void setLastNodeSelected ( Object lastNodeSelected ) {
        this.lastNodeSelected = lastNodeSelected;
    }

    /**
     *
     */
    @Override
    public void performLastUserSelectionOfSampleDate () {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
