/*
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JLayeredPane;

/**
 * UncertaintyGraphPanel is the bar graph JPanel that visualizes the various
 * contributions and covariances of a fraction's inputs
 *
 * @author James F. Bowring, Michael Jacko
 */
public class UncertaintyGraphPanel
        extends JLayeredPane
        implements MouseListener {

    private UncertaintyZoomLayer zoomLayer;
    private Vector<InputUnctContrib> reducedZL; // vector of drawn contribs: covarying contribs joined into single contrib
    private UncertaintyGraphInfoPanel infoPanel;
    private float centralLineRatio; // location of horizontal line dividing uncertainty and negative covariation
    private boolean showNames;
    private int panelWidth;
    private int panelHeight;
    private int graphWidth;
    private int graphHeight;
    private float covGraphHeight; // pixel height of negative covariance partition of graph
    private float mapZero; // yAboveCenter coordinate for central line
    private int topMargin;
    private int leftMargin;
    private int bottomMargin;
    private int rightMargin;
    private int countOfBars; // number of contributions being displayed (covarying counted as singles)
    private double yRangeGraphCov; // range of negative covariance values graphed
    private double yRangeGraphPos; // range of positive covariance values graphed
    private double barCoverage; // between 0 and 1; bar's width as percentage of xPosition-tick
    private Color inactiveBarColor;
    private Color selectedBarColor;
    private InputUnctContrib selectedBar; // "highlighted" contribution bar
    String end;
    String textType;

    /**
     *
     * @param zoomLayer
     * @param bounds
     */
    public UncertaintyGraphPanel (
            UncertaintyZoomLayer zoomLayer,
            Rectangle bounds ) {

        super();
        this.zoomLayer = zoomLayer;

        showNames = true;

        selectedBarColor = new Color( 255, 255, 150 );
        inactiveBarColor = new Color( 235, 200, 100 );

        setBounds( bounds );
        SetGraphBounds();
        setOpaque( true );
        infoPanel = new UncertaintyGraphInfoPanel( getWidth(), getHeight() );

        setBackground( Color.white );

        addMouseListener( this );

        preparePanel();

        setVisible( true );

        setBounds( bounds );

    }

    /**
     * converts value to an xPosition value on the graph
     *
     * @param xPosition value to be mapped
     * @return
     */
    private int MapX ( double x ) {

        return (int) (x * graphWidth) + leftMargin;
    }

    /**
     * converts value to yAboveCenter value on the graph
     *
     * @param yAboveCenter
     * @return
     */
    private float MapY ( double y ) {
        if ( y >= 0 ) {
            return (float) (((Math.abs( y - yRangeGraphPos ) / yRangeGraphPos)) * graphHeight
                    * centralLineRatio + (double) topMargin); //showNegCov__
        } else {
            return (float) (((Math.abs( yRangeGraphCov - y ) / yRangeGraphCov)) * graphHeight) + MapY( 0 );
        }

    }

    /**
     *
     */
    public void preparePanel () {

        removeAll();

        if ( zoomLayer != null ) {
            reducedZL = zoomLayer.getZoomLayerContents();


            setSize( graphWidth + leftMargin + rightMargin, graphHeight + topMargin + bottomMargin );

            countOfBars = Math.min( 10, reducedZL.size() );

            add( infoPanel );
            infoPanel.setLocation( (getWidth() - infoPanel.getWidth() - 5), topMargin );

            if ( reducedZL.size() > 0 ) {
                infoPanel.update(//
                        reducedZL.get( 0 ).toStringCovNames(),
                        reducedZL.get( 0 ).getTrueUnct() / zoomLayer.getTotalUncertaintyContribs() );
                selectedBar = reducedZL.get( 0 );
            }

            centralLineRatio =
                    (float) getZoomLayer().getCentralRatio();

            yRangeGraphPos = getZoomLayer().getYRangeExcl();
            yRangeGraphCov = getZoomLayer().getMinNegCov();

            centralLineRatio =
                    (float) (1.0 - (Math.abs( yRangeGraphCov ) / (yRangeGraphPos + Math.abs( yRangeGraphCov ))));
            covGraphHeight =
                    (float) graphHeight - (centralLineRatio * (float) graphHeight);

            mapZero = MapY( 0 );
        }

    }

    /**
     *
     */
    public void refreshPanel () {
        preparePanel();
        repaint();
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
    public void paint ( Graphics2D g2d ) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        rh.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHints( rh );

        g2d.setClip( 0, 0, getWidth(), getHeight() );

        g2d.setColor( Color.BLACK );

        g2d.drawLine( MapX( 0 ), (int) mapZero, MapX( 1 ), (int) mapZero );

        // draw IUC (bars)
        int position = 0;
        int counter = 0;
        while ((position < countOfBars) && (counter < reducedZL.size())) {
            PlotIUC( g2d, reducedZL.get( counter ), position );
            position ++;
            counter ++;
        }

        infoPanel.repaint();
    }

    /**
     * draws a reduced IUC (covarying IUCs grouped as one) as a set of
     * rectangular GeneralPaths for the IUC's contribution and positive or
     * negative covariance, if present
     *
     * @param g2d
     * @param IUC
     * @param position
     */
    private void PlotIUC ( Graphics2D g2d, InputUnctContrib IUC, int position ) {

        double positionRatio = (double) position / (countOfBars);
        double nextPR = (double) (position + 1) / (countOfBars);
        g2d.setFont( new Font( textType, Font.BOLD, 14 ) );
        g2d.setStroke( new BasicStroke( 1.0f ) );

        // set measurements
        int width = (int) ((1.0 / (countOfBars)) * barCoverage * graphWidth);
        int spacerWidth = (int) (((width / barCoverage) - width) / 2.0);
        int xPosition = MapX( positionRatio ) + (int) ((1.0 / (countOfBars)) * graphWidth) * (int) ((1.0 - barCoverage) / 2);
        int xSet = xPosition + spacerWidth;
        float yPosition = MapY( IUC.getTrueUnct() );
        float height = mapZero - yPosition;

        //draw vertical lines
        if ( position != 0 ) {
            g2d.setColor( new Color( 240, 240, 240 ) );
            Line2D vertLine = new Line2D.Double( xPosition, topMargin + graphHeight - 2, xPosition, topMargin + 1 );
            g2d.draw( vertLine );
        }

        //create selectionBar
        Path2D sBar = new Path2D.Double( Path2D.WIND_EVEN_ODD, 4 );
        sBar.moveTo( xPosition, topMargin );
        sBar.lineTo( xPosition, graphHeight + topMargin );
        sBar.lineTo( MapX( nextPR ) - 1, graphHeight + topMargin );
        sBar.lineTo( MapX( nextPR ) - 1, topMargin );
        sBar.closePath();
        IUC.setSelectionBar( sBar );

        // draw contrib bar
        int[] barXSet = {xSet, xSet, xSet + width, xSet + width};
        float[] barYSet = {yPosition, yPosition + height, yPosition + height, yPosition};
        Path2D bar = new Path2D.Double( Path2D.WIND_EVEN_ODD, 4 );
        bar.moveTo( barXSet[0], barYSet[0] );
        for (int i = 1; i
                < barXSet.length; i ++) {
            bar.lineTo( barXSet[i], barYSet[i] );
        }

        bar.closePath();
        IUC.setContribBar( bar );
        g2d.setColor( IUC == selectedBar ? selectedBarColor : inactiveBarColor );
        g2d.fill( bar );
        g2d.setColor( Color.BLACK );
        g2d.draw( bar );

        // draws positive covariance as part of bar
        float covHeight = 0f;
        if ( IUC.getCovariance() > 0.0 ) {

            float covY = MapY( Math.abs( IUC.getCovariance() ) );
            covHeight =
                    (int) (((Math.abs( IUC.getCovariance() )) / yRangeGraphPos)
                    * graphHeight * centralLineRatio);

            barXSet = new int[]{xSet, xSet, xSet + width, xSet + width};
            barYSet = new float[]{covY, covY + covHeight, covY + covHeight, covY};
            bar = new Path2D.Double( Path2D.WIND_EVEN_ODD, 4 );
            bar.moveTo( barXSet[0], barYSet[0] );
            for (int i = 1; i
                    < barXSet.length; i ++) {
                bar.lineTo( barXSet[i], barYSet[i] );
            }
            bar.closePath();

            g2d.setColor( IUC == selectedBar ? selectedBarColor : inactiveBarColor );
            g2d.fill( bar );
            g2d.setColor( Color.BLACK );
            g2d.draw( bar );
        }

        // draw negative covariance as sunken bar
        if ( IUC.getCovariance() < 0.0 ) {

            float covRatio = (float) (IUC.getCovariance() / yRangeGraphCov);
            float covarianceHeight = covRatio * covGraphHeight;
            barXSet = new int[]{xSet, xSet, xSet + width, xSet + width};
            barYSet = new float[]{mapZero + covarianceHeight, mapZero, mapZero, mapZero + covarianceHeight};
            bar = new Path2D.Double( Path2D.WIND_EVEN_ODD, 4 );
            bar.moveTo( barXSet[0], barYSet[0] );
            for (int i = 1; i
                    < barXSet.length; i ++) {
                bar.lineTo( barXSet[i], barYSet[i] );
            }
            bar.closePath();

            g2d.setColor( IUC == selectedBar ? selectedBarColor : inactiveBarColor );
            g2d.fill( bar );
            g2d.setColor( Color.BLACK );
            g2d.draw( bar );
        }

        // draw vertical lines representing covariants
        if (  ! (IUC.getCovariants().isEmpty()) ) {
            // april 2009
            // get good count of bars
            int count = (IUC.getPartialDerivative() == 0.0) ? 0 : 1;
            for (InputUnctContrib IUCp : IUC.getCovariants()) {
                if ( IUCp.getPartialDerivative() != 0.0 ) {
                    count ++;
                }
            }
            g2d.setColor( Color.BLACK );
            for (int i = 1; i
                    <= (count); i ++) {
                int divLineX = xSet + (int) ((double) width * ((double) i / (count)));
                if ( (IUC.getTrueUnct() > 0) && (IUC.getCovariance() < 0) ) {
                    covHeight = 0;
                }
                g2d.draw( new Line2D.Float( divLineX, yPosition, divLineX, yPosition + height - covHeight ) );
            }
        }

        // cover over 'zero' line if IUC does not contribute
        if ( Math.abs( IUC.getTrueUnct() ) == 0.0 && IUC.isSelected() == false ) {
            g2d.setColor( new Color( 230, 230, 230 ) );
            g2d.drawLine( xPosition, (int) mapZero, xPosition + (int) (width / barCoverage), (int) mapZero );
        }

        IUC.setSelected( false );
    }

    // preferred method of setting size (setBounds will not change the actual
    // graph's dimensions, only truncate or add white space around)
    /**
     *
     */
    public void SetGraphBounds () {

        graphWidth = (int) (getWidth() * 0.99);
        graphHeight = (int) (getHeight() * 0.95);
        barCoverage = 0.85;
        textType = "SansSerif";
        topMargin = (int) (getHeight() * ((1 - (double) graphHeight / (double) getHeight()) * 0.5));
        leftMargin = (int) (getWidth() * ((1 - (double) graphWidth / (double) getWidth()) * 0.9));
        bottomMargin = (int) (getHeight() * ((1 - (double) graphHeight / (double) getHeight()) * 0.5));
        rightMargin = (int) (getWidth() * ((1 - (double) graphWidth / (double) getWidth()) * 0.1));
    }

    /**
     * method mouseClicked checks to see if one of the IUC bars were clicked
     *
     * @param evt
     */
    public void mouseClicked ( MouseEvent evt ) {
        InputUnctContrib IUCmatch = null;
        boolean match = false;
        Iterator it = reducedZL.iterator();
        while (it.hasNext() && match == false) {
            InputUnctContrib IUCref = ((InputUnctContrib) it.next());
            match =
                    IUCref.getSelectionBar().contains( evt.getX(), evt.getY() ) ? true : false;
            if ( match == true ) {
                IUCmatch = IUCref;
            }

        }
        // if a bar was clicked
        if ( IUCmatch != null ) {
            infoPanel.update(//
                    IUCmatch.toStringCovNames(),
                    IUCmatch.getTrueUnct() / zoomLayer.getTotalUncertaintyContribs() );
            selectedBar =
                    IUCmatch;
            selectedBar.setSelected( true );

            repaint();

        } else {
            // System.out.println("No bar clicked");
        }

    }

    /**
     *
     * @param arg0
     */
    public void mousePressed ( MouseEvent arg0 ) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    public void mouseReleased ( MouseEvent arg0 ) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    public void mouseEntered ( MouseEvent arg0 ) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    public void mouseExited ( MouseEvent arg0 ) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return
     */
    public boolean isShowLambda () {
        return true;
    }

    /**
     *
     * @return
     */
    public boolean isShowNames () {
        return showNames;
    }

    /**
     * @return the panelWidth
     */
    public int getPanelWidth () {
        return panelWidth;
    }

    /**
     * @param panelWidth the panelWidth to set
     */
    public void setPanelWidth ( int panelWidth ) {
        this.panelWidth = panelWidth;
    }

    /**
     * @return the panelHeight
     */
    public int getPanelHeight () {
        return panelHeight;
    }

    /**
     * @param panelHeight the panelHeight to set
     */
    public void setPanelHeight ( int panelHeight ) {
        this.panelHeight = panelHeight;
    }

    /**
     * @return the zoomLayer
     */
    public UncertaintyZoomLayer getZoomLayer () {
        return zoomLayer;
    }

    /**
     * @param zoomLayer the zoomLayer to set
     */
    public void setZoomLayer ( UncertaintyZoomLayer zoomLayer ) {
        this.zoomLayer = zoomLayer;
    }
}
