/*
 * AbstractValueModelsPanelView.java
 *
 * Created Mar 4, 2012
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.valueModelPanelViews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModelViews.AbstractValueModelView;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataViews.DataEntryDetectorInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractValueModelsPanelView extends JLayeredPane {

    /**
     *
     */
    protected final AbstractValueModelView[] valueModelViews;

    /**
     *
     */
    protected DataEntryDetectorInterface dataEntryDetector;
    private final JRadioButton absUnctRadioButton;
    private final JRadioButton pctUnctRadioButton;

    /**
     *
     *
     * @param ValueModels the value of ValueModels
     */
    public AbstractValueModelsPanelView ( ValueModel[] ValueModels ) {

        valueModelViews = new AbstractValueModelView[ValueModels.length];

        JLabel ratioLabel = new JLabel( "<html><u>Name</u></html>" );
        ratioLabel.setFont(ReduxConstants.sansSerif_12_Bold );
        ratioLabel.setHorizontalAlignment( SwingConstants.CENTER );
        ratioLabel.setBounds( 0, 2, 100, AbstractValueModelView.PANEL_HEIGHT );
        this.add( ratioLabel );

        JLabel valueLabel = new JLabel( "<html><u>Value</u></html>" );
        valueLabel.setFont(ReduxConstants.sansSerif_12_Bold );
        valueLabel.setHorizontalAlignment( SwingConstants.CENTER );
        valueLabel.setBounds( 110, 2, 150, AbstractValueModelView.PANEL_HEIGHT );
        this.add( valueLabel );

        absUnctRadioButton = new JRadioButton( "<html><u>1\u03C3 ABS</u></html>" );
        absUnctRadioButton.setName( "ABS" );
        absUnctRadioButton.setFont(ReduxConstants.sansSerif_10_Bold );
        absUnctRadioButton.setHorizontalAlignment( SwingConstants.CENTER );
        absUnctRadioButton.setBounds( 300, 2, 75, AbstractValueModelView.PANEL_HEIGHT );
        this.add( absUnctRadioButton );

        pctUnctRadioButton = new JRadioButton( "<html><u>1\u03C3 PCT</u></html>" );
        pctUnctRadioButton.setName( "PCT" );
        pctUnctRadioButton.setFont(ReduxConstants.sansSerif_10_Bold );
        pctUnctRadioButton.setHorizontalAlignment( SwingConstants.CENTER );
        pctUnctRadioButton.setBounds( 370, 2, 75, AbstractValueModelView.PANEL_HEIGHT );
        this.add( pctUnctRadioButton );

        ButtonGroup uncertaintyGroup = new ButtonGroup();
        uncertaintyGroup.add( absUnctRadioButton );
        uncertaintyGroup.add( pctUnctRadioButton );

        ActionListener uncertaintyActionListener = new ActionListener() {
            String lastSelectedName = "ABS";

            public void actionPerformed ( ActionEvent actionEvent ) {
                AbstractButton aButton = (AbstractButton) actionEvent.getSource();
                String name = aButton.getName();

                if (  ! name.equalsIgnoreCase( lastSelectedName ) ) {
                    lastSelectedName = name;

                    for (int i = 0; i < valueModelViews.length; i ++) {
                        valueModelViews[i].toggleShowOneSigmaAsPerCent();
                    }
                }
            }
        };

        absUnctRadioButton.addActionListener( uncertaintyActionListener );
        pctUnctRadioButton.addActionListener( uncertaintyActionListener );

    }

    /**
     *
     */
    public abstract void saveEdits ();

    /**
     *
     *
     */
    protected void initValueModelViews () {

        if ( valueModelViews != null ) {
            this.setBounds( //
                    0, //
                    0, //
                    valueModelViews[0].getWidth(),//
                    AbstractValueModelView.PANEL_HEIGHT * (valueModelViews.length + 1) + 10 );

            for (int i = 0; i < valueModelViews.length; i ++) {

                valueModelViews[i].setBounds(//
                        0, //
                        AbstractValueModelView.PANEL_HEIGHT * (i + 1),//
                        valueModelViews[0].getWidth(),//
                        AbstractValueModelView.PANEL_HEIGHT );

                this.add( valueModelViews[i] );
            }

            if ( valueModelViews[0].isShowOneSigmaAsPerCent() ) {
                pctUnctRadioButton.setSelected( true );
            } else {
                absUnctRadioButton.setSelected( true );
            }
        }
    }

    /**
     *
     * @param g
     */
    @Override
    protected void paintComponent ( Graphics g ) {
        super.paintComponent( g );

        paint( (Graphics2D) g );
    }

    /**
     *
     * @param g2d
     */
    protected void paintInit ( Graphics2D g2d ) {
        RenderingHints rh = g2d.getRenderingHints();
        rh.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        rh.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHints( rh );

        g2d.setPaint( Color.BLACK );
        g2d.setStroke( new BasicStroke( 1.0f ) );
        g2d.setFont(ReduxConstants.sansSerif_12_Bold );

    }

    /**
     *
     * @param g2d
     */
    public void paint ( Graphics2D g2d ) {
        paintInit( g2d );

    }
}
