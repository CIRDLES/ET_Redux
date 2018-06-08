/*
 * AbstractRawDataView.java
 *
 * Created Jul 6, 2011
 *
 * Copyright 2006 James F. Bowring and Earth-Time.org
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
package org.earthtime.plots;

import Jama.Matrix;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import javax.swing.JLayeredPane;
import javax.swing.event.MouseInputListener;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ErrorEllipse;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.RadRatiosPbcCorrected;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractDataView extends JLayeredPane implements MouseInputListener, MouseWheelListener {

    protected static final double ZOOM_FACTOR = 25.0;

    protected double x;
    protected double y;
    protected double width;
    protected double height;

    /**
     *
     */
    protected double[] myOnPeakData;
    /**
     *
     */
    protected double[] myOnPeakNormalizedAquireTimes;
    /**
     *
     */
    protected int graphWidth;
    /**
     *
     */
    protected int graphHeight;
    /**
     *
     */
    protected int topMargin = 0;
    /**
     *
     */
    protected int leftMargin = 0;
    /**
     *
     */
    protected double minX;
    /**
     *
     */
    protected double maxX;
    /**
     *
     */
    protected double minY;
    /**
     *
     */
    protected double maxY;
    /**
     *
     */
    protected double displayOffsetY = 0;
    /**
     *
     */
    protected double displayOffsetX = 0;
    protected int zoomMinX;
    protected int zoomMinY;
    protected int zoomMaxX;
    protected int zoomMaxY;
    /**
     *
     */
    protected BigDecimal[] tics;

    /**
     *
     */
    public AbstractDataView() {
        super();
    }

    /**
     *
     * @param bounds
     */
    protected AbstractDataView(Rectangle bounds, int leftMargin, int topMargin) {
        super();
        x = bounds.getX();
        y = bounds.getY();
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;

        this.myOnPeakData = null;

        width = bounds.getWidth();
        height = bounds.getHeight();
        graphWidth = (int) width - leftMargin;
        graphHeight = (int) height - topMargin;

        this.tics = null;

        addMeAsMouseListener();
    }

    protected final void addMeAsMouseListener() {
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    /**
     *
     * @param g2d
     */
    protected void paintInit(Graphics2D g2d) {
        g2d.setClip(leftMargin, topMargin, (int) graphWidth, (int) graphHeight);
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                10));

    }

    /**
     *
     * @param g2d
     */
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

        drawBorder(g2d);
    }

    protected void drawBorder(Graphics2D g2d) {
        // fill it in
        g2d.setPaint(Color.white);
        g2d.fillRect(0, 0, (int) width, (int) height);

        // draw border
        g2d.setPaint(Color.black);

    }

    /**
     *
     * @param x
     * @return
     */
    public double mapX(double x) {
        return (((x - getMinX_Display()) / getRangeX_Display()) * graphWidth) + leftMargin;
    }

    /**
     *
     * @param y
     * @return
     */
    protected double mapY(double y) {
        return (((getMaxY_Display() - y) / getRangeY_Display()) * graphHeight) + topMargin;
    }

    /**
     *
     * @param doReset
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public void refreshPanel(boolean doReset) {
        try {
            preparePanel(doReset);
        } catch (Exception e) {
        }

        validate();
        repaint();
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public abstract void preparePanel(boolean doReset);

    protected void drawAxesAndTicks(Graphics2D g2d, double rangeX, double rangeY) {

        // reset the clip bounds to paint axis and numbers
        g2d.setClip(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));

        // determine the axis ticks
        BigDecimal[] tics = TicGeneratorForAxes.generateTics(getMinY_Display(), getMaxY_Display(), 12);
        // trap for bad plot
        if (tics.length <= 1) {
            tics = new BigDecimal[0];
        }
        double minXDisplay = 0.0;
        int yAxisTicWidth = 8;
        int yTicLabelFrequency = 2;
        int labeledTicCountYAxis = 0;

        g2d.setPaint(Color.black);
        for (int i = 0; i < tics.length; i++) {

            double y = tics[i].doubleValue();

            if ((y > getMinY_Display()) // dont print across mappedX axis
                    && (y < getMaxY_Display())) // dont print across top border
            {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(getMinX_Display()),
                            mapY(y),
                            mapX(getMinX_Display()) + 7,
                            mapY(y));
                    g2d.draw(ticMark);

                    String intString = "00000" + tics[i].toPlainString().replace(".", "");
                    int lastPlace = Integer.parseInt(intString.substring(intString.length() - 4));

                    if (lastPlace % yTicLabelFrequency == 0) {
                        if (labeledTicCountYAxis % yTicLabelFrequency == 0) {

                            TextLayout mLayout
                                    = //
                                    new TextLayout(
                                            tics[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext());

                            Rectangle2D bounds = mLayout.getBounds();

                            float yLabelCenterOffset = (float) mLayout.getBounds().getWidth() / 2f;

                            g2d.rotate(
                                    -Math.PI / 2.0,
                                    (float) mapX(getMinX_Display()) - 4f,
                                    (float) mapY(y) + yLabelCenterOffset);
                            g2d.drawString(
                                    tics[i].toPlainString(),
                                    (float) mapX(getMinX_Display()) - 4f,
                                    (float) mapY(y) + yLabelCenterOffset);
                            g2d.rotate(
                                    Math.PI / 2.0,
                                    (float) mapX(getMinX_Display()) - 4f,
                                    (float) mapY(y) + yLabelCenterOffset);
                        }

                        labeledTicCountYAxis++;
                    } else {

                        if (labeledTicCountYAxis > 0) {
                            labeledTicCountYAxis++;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        // draw and label axes
        g2d.setFont(
                new Font("Monospaced", Font.BOLD, 20));
        g2d.drawRect(
                leftMargin, topMargin, graphWidth - 1, graphHeight - 1);

    }

    /**
     *
     * @param g2d
     */
    protected void drawTicsYAxisInBackground(Graphics2D g2d) {
        // y -axis tics
        Stroke savedStroke = g2d.getStroke();

        // tics
        if (tics != null) {
            for (int i = 0; i < tics.length; i++) {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(minX), mapY(tics[i].doubleValue()), mapX(maxX), mapY(tics[i].doubleValue()));

                    g2d.setPaint(new Color(202, 202, 202));//pale gray
                    g2d.setStroke(new BasicStroke(0.5f));
                    g2d.draw(ticMark);
                } catch (Exception e) {
                }
            }
        } else {
            double ticWidth = (maxY - minY) / 10;
            if (ticWidth > 0.0) {
                for (double tic = minY + ticWidth; tic < (maxY * 0.999); tic += ticWidth) {
                    Shape ticMark = new Line2D.Double( //
                            mapX(minX), mapY(tic), mapX(maxX), mapY(tic));
                    g2d.setPaint(new Color(202, 202, 202));//pale gray
                    g2d.setStroke(new BasicStroke(0.5f));
                    g2d.draw(ticMark);
                }
            }
        }

        g2d.setStroke(savedStroke);
    }

    /**
     * @return the displayOffsetY
     */
    public double getDisplayOffsetY() {
        return displayOffsetY;
    }

    /**
     * @param displayOffsetY the displayOffsetY to set
     */
    public void setDisplayOffsetY(double displayOffsetY) {
        this.displayOffsetY = displayOffsetY;
    }

    /**
     * @return the displayOffsetX
     */
    public double getDisplayOffsetX() {
        return displayOffsetX;
    }

    /**
     * @param displayOffsetX the displayOffsetX to set
     */
    public void setDisplayOffsetX(double displayOffsetX) {
        this.displayOffsetX = displayOffsetX;
    }

    /**
     *
     * @return
     */
    public double getMinX_Display() {
        return minX + getDisplayOffsetX();
    }

    /**
     *
     * @return
     */
    public double getMaxX_Display() {
        return maxX + getDisplayOffsetX();
    }

    /**
     *
     * @return
     */
    public double getMinY_Display() {
        return minY + getDisplayOffsetY();
    }

    /**
     *
     * @return
     */
    public double getMaxY_Display() {
        return maxY + getDisplayOffsetY();
    }

    /**
     *
     * @return
     */
    public double getRangeX_Display() {
        return (getMaxX_Display() - getMinX_Display());
    }

    /**
     *
     * @return
     */
    public double getRangeY_Display() {
        return (getMaxY_Display() - getMinY_Display());
    }

    /**
     * @return the myOnPeakData
     */
    public double[] getMyOnPeakData() {
        return myOnPeakData.clone();
    }

    /**
     * @return the myOnPeakNormalizedAquireTimes
     */
    public double[] getMyOnPeakNormalizedAquireTimes() {
        return myOnPeakNormalizedAquireTimes.clone();
    }

    // Mouse events ************************************************************
    /**
     *
     * @param evt
     */
    @Override
    public void mouseClicked(MouseEvent evt) {
        // use mousepressed so can trap events with one-button mac mouse much more easily
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        zoomMinX = evt.getX();
        zoomMinY = evt.getY();
//        if ((evt.getX() >= leftMargin)
//                && (evt.getY() >= topMargin)
//                && (evt.getY() <= graphHeight + topMargin)) {
//
//            if (evt.isPopupTrigger() || evt.getButton() == MouseEvent.BUTTON3) {
//
//                // reset to pan mode
////                graphPanelModeChanger.switchToPanMode();
//
//                //Create the popup menu.
//                JPopupMenu popup = new JPopupMenu();
//
//                // Jan 2011 show coordinates fyi
//                double x = convertMouseXToValue(evt.getX());
//                double y = convertMouseYToValue(evt.getY());
//                DecimalFormat f = new DecimalFormat("0.0000E00");
//
//                JMenuItem menuItem = new JMenuItem("Automatically configure axes");
//                menuItem.addActionListener(new ActionListener() {
//
//                    public void actionPerformed(ActionEvent arg0) {
//                        getCurrentPlotAxesSetup().setUseAutomaticAxisTics(true);
//                        repaint();
//                    }
//                });
//                popup.add(menuItem);
//
//                mySelf = this;
//                menuItem = new JMenuItem("Manually configure axes");
//                menuItem.addActionListener((ActionEvent arg0) -> {
//                    getCurrentPlotAxesSetup().setUseAutomaticAxisTics(false);
//
//                    JDialog myGraphAxisDialog = new GraphAxesDialog( //
//                            null, //
//                            true,
//                            getCurrentPlotAxesSetup(),
//                            mySelf);
//                    myGraphAxisDialog.setVisible(true);
//
//                    // dialog closed
//                    repaint();
//                });
//                popup.add(menuItem);
//
//                // may 2014
//                // check if aliquot vs sample
//                if (showingSingleAliquot) {
//                    DecimalFormat d = new DecimalFormat("###0");
//                    menuItem = new JMenuItem("Toggle Best Date Divider (currently = " + d.format(currentBestDate / 1e6) + " ma)");
//                    menuItem.addActionListener((ActionEvent arg0) -> {
//                        showBestDateDivider206_238 = !showBestDateDivider206_238;
//
//                        repaint();
//                    });
//                    popup.add(menuItem);
//                }
//
//                popup.show(evt.getComponent(), evt.getX(), evt.getY());
//            } else {
//
//                setZoomMinX(evt.getX());
//                setZoomMinY(evt.getY());
//            }
//        } else {
//
//        }
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        zoomMaxX = evt.getX();
        zoomMaxY = evt.getY();

//        if ((evt.getX() >= getLeftMargin())
//                && (evt.getY() >= getTopMargin())
//                && (evt.getY() <= getGraphHeight() + getTopMargin()) && !changingBestDateDivider) {
//
//            setZoomMaxX(evt.getX());
//            setZoomMaxY(evt.getY());
//
//            if (getImageMode().equalsIgnoreCase("ZOOM") //
//                    && (getZoomMaxX() != getZoomMinX()) //using != provides for legal inverting of zoom box
//                    && (getZoomMaxY() != getZoomMinY())) {
//
//                // may 2010 check for bad zooms
//                double zMinX = convertMouseXToValue(Math.min(getZoomMinX(), getZoomMaxX()));
//                double zMinY = convertMouseYToValue(Math.max(getZoomMinY(), getZoomMaxY()));
//                // hack to correct a bug in the reported x,y from mouse release vs mouse click
//                // june 2010 removed this hack
//                double zMaxX = convertMouseXToValue(Math.max(getZoomMaxX(), getZoomMinX()) - 0 * 65);
//                double zMaxY = convertMouseYToValue(Math.min(getZoomMaxY(), getZoomMinY()) + 0 * 60);
//
//                if ((zMaxX > zMinX) && (zMaxY > zMinY)) {
//                    setMinX(zMinX);
//                    setMinY(zMinY);
//                    setMaxX(zMaxX);
//                    setMaxY(zMaxY);
//
//                    setDisplayOffsetX(0.0);
//                    setDisplayOffsetY(0.0);
//
//                    setZoomMaxX(getZoomMinX());
//                    setZoomMaxY(getZoomMinY());
//
//                    // may 2010
//                    currentGraphAxesSetup.setUseAutomaticAxisTics(true);
//                    graphPanelModeChanger.switchToPanMode();
//
//                    repaint();
//                }
//            }
//        } else // set best age divider
//        {
//            if (changingBestDateDivider) {
//                ((AliquotForUPbInterface) curAliquot).setBestAgeDivider206_238(new BigDecimal(currentBestDate));
//                ((UPbReduxAliquot) curAliquot).updateBestAge();
//                reportUpdater.updateReportTable(false, false, "");
//            }
//        }
//
//        changingBestDateDivider = false;
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseEntered(MouseEvent evt) {
//        // this is not very sensitive, so have forced cursor at mode selection below
//        if ((evt.getX() >= getLeftMargin())
//                && (evt.getY() >= getTopMargin())
//                && (evt.getY() <= getGraphHeight() + getTopMargin())) {
//            setCursor(getConcordiaCursor());
//        }
    }

//    /**
//     *
//     * @param evt
//     * @return
//     */
//    public boolean mouseEnteredTitleBox(MouseEvent evt) {
//        boolean retval = false;
//        if (isShowTitleBox()) {
//            // this is not very sensitive, so have forced cursor at mode selection below
//            if ((evt.getX() >= concordiaTitlePanel.getX())
//                    && (evt.getX() <= (concordiaTitlePanel.getX() + concordiaTitlePanel.getWidth()))
//                    && (evt.getY() >= concordiaTitlePanel.getY())
//                    && (evt.getY() <= (concordiaTitlePanel.getY() + concordiaTitlePanel.getHeight()))) {
//                retval = true;
//            } else {
//                retval = false;
//            }
//        }
//        return retval;
//    }
//    /**
//     *
//     * @param evt
//     * @return
//     */
//    public boolean mouseEnteredDateBox(MouseEvent evt) {
//        // this is not very sensitive, so have forced cursor at mode selection below
//        if (preferredDatePanel == null) {
//            return false;
//        } else if ((evt.getX() >= preferredDatePanel.getX())
//                && (evt.getX() <= (preferredDatePanel.getX() + preferredDatePanel.getWidth()))
//                && (evt.getY() >= preferredDatePanel.getY())
//                && (evt.getY() <= (preferredDatePanel.getY() + preferredDatePanel.getHeight()))) {
//            return true;
//        } else {
//            return false;
//        }
//    }
    /**
     *
     * @param evt
     */
    @Override
    public void mouseExited(MouseEvent evt) {

    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseDragged(MouseEvent evt) {
        zoomMaxX = evt.getX();
        zoomMaxY = evt.getY();

        // prevent all but upper right quadrant
        setDisplayOffsetX(Math.max(getDisplayOffsetX() //
                + convertMouseXToValue(zoomMinX) - convertMouseXToValue(zoomMaxX), 0.0));
        setDisplayOffsetY(Math.min(getDisplayOffsetY() //
                + (convertMouseYToValue(zoomMinY) - convertMouseYToValue(zoomMaxY)), minY));

        System.out.println(getDisplayOffsetY() + "\t" + convertMouseXToValue(zoomMinY) + "\t" + convertMouseXToValue(zoomMaxY));
        zoomMinX = zoomMaxX;
        zoomMinY = zoomMaxY;

        repaint();

//        
//        if ((evt.getX() >= leftMargin)
//                && (evt.getY() >= topMargin)
//                && (evt.getY() <= graphHeight + topMargin)) {
//            setZoomMaxX(evt.getX());
//            setZoomMaxY(evt.getY());
//
//            changingBestDateDivider = false;
//            setCursor(concordiaCursor);
//
        // PANNING
//        double xOffsetValue
//                = getDisplayOffsetX() //
//                + (convertMouseXToValue(0) - convertMouseXToValue(evt.getX()));
//        double yOffsetValue
//                = getDisplayOffsetY() //
//                + (convertMouseYToValue(0) - convertMouseYToValue(evt.getY()));
//
//        // test for out of bounds
//        double x = maxX - xOffsetValue;
//        if (((minX + xOffsetValue) >= 0.0)// may 2010 zeroed - 1.0)
//
//                && ((minY + yOffsetValue) >= 0.0)) {
//            setDisplayOffsetX(xOffsetValue);
//            setDisplayOffsetY(yOffsetValue);
        // System.out.println( "move from " + getZoomMinX() + ", " + getZoomMinY() + "  to  " + getZoomMaxX() + ", " + getZoomMaxY() );
//            setZoomMinX(getZoomMaxX());
//            setZoomMinY(getZoomMaxY());
//        }
//            else if (getImageMode().equalsIgnoreCase("TITLE")
//                    && isShowTitleBox()) {
//                int titleLocationX = concordiaTitlePanel.getX();
//                int titleLocationY = concordiaTitlePanel.getY();
//
//                int nextTitleLocationX = titleLocationX + (getZoomMaxX() - getZoomMinX());
//                int nextTitleLocationY = titleLocationY + (getZoomMaxY() - getZoomMinY());
//
//                setZoomMinX(getZoomMaxX());
//                setZoomMinY(getZoomMaxY());
//
//                if (this.contains(
//                        nextTitleLocationX,
//                        nextTitleLocationY)
//                        && this.contains(
//                                (nextTitleLocationX + concordiaTitlePanel.getWidth()),
//                                (nextTitleLocationY + concordiaTitlePanel.getHeight()))) {
//                    concordiaTitlePanel.setLocation(
//                            nextTitleLocationX,
//                            nextTitleLocationY);
//                    setSavedConcordiaTitlePanelX(nextTitleLocationX);
//                    setSavedConcordiaTitlePanelY(nextTitleLocationY);
//                }
//
//            } else if (getImageMode().equalsIgnoreCase("DATE")) {
//                int dateLocationX = preferredDatePanel.getX();
//                int dateLocationY = preferredDatePanel.getY();
//
//                int nextDateLocationX = dateLocationX + (getZoomMaxX() - getZoomMinX());
//                int nextDateLocationY = dateLocationY + (getZoomMaxY() - getZoomMinY());
//
//                setZoomMinX(getZoomMaxX());
//                setZoomMinY(getZoomMaxY());
//
//                if (this.contains(
//                        nextDateLocationX,
//                        nextDateLocationY)
//                        && this.contains(
//                                (nextDateLocationX + preferredDatePanel.getWidth()),
//                                (nextDateLocationY + preferredDatePanel.getHeight()))) {
//                    preferredDatePanel.setLocation(
//                            nextDateLocationX,
//                            nextDateLocationY);
//                    setSavedPreferredDatePanelX(nextDateLocationX);
//                    setSavedPreferredDatePanelY(nextDateLocationY);
//                }
//            }
//
////            repaint();
//        } else if (mouseInBestAgeHandleArea(evt.getX(), evt.getY())) {
//            if (showBestDateDivider206_238) {
//                changingBestDateDivider = true;
//                double nextBestRatio = convertMouseYToValue(evt.getY());
//                double testBestDate = Math.log1p(nextBestRatio) / lambda238.getValue().doubleValue();
//                if ((testBestDate > 0) && (testBestDate < ReduxConstants.MAX_DATE_ANNUM)) {
//                    currentBestDate = testBestDate;
//                }
//                // System.out.println("Best Date = " + nextBestRatio + "  " + currentBestDate);
//                setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));//.WAIT_CURSOR));//.MOVE_CURSOR));
//            }
//        }
//        repaint();
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseMoved(MouseEvent evt) {
//        if ((evt.getX() >= getLeftMargin())
//                && (evt.getY() >= getTopMargin())
//                && (evt.getY() <= getGraphHeight() + getTopMargin())) {
//
//            if (!getImageMode().equalsIgnoreCase("ZOOM")
//                    && mouseEnteredTitleBox(evt)) {
//                setImageMode("TITLE");
//                //  System.out.println("TITLE");
//            } else if (!getImageMode().equalsIgnoreCase("ZOOM")
//                    && mouseEnteredDateBox(evt)) {
//                setImageMode("DATE");
//                //  System.out.println("DATE");
//
//            } else if (getImageMode().equalsIgnoreCase("TITLE")
//                    || getImageMode().equalsIgnoreCase("DATE")) {
//                setImageMode("PAN");
//            } else {
//                setCursor(concordiaCursor);
//            }
//        } else {
//            setCursor(Cursor.getDefaultCursor());
//        }
//
//        //System.out.println("mouse " + evt.getX());
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        // positive notches = zoom out negative = zoomin

        int notches = e.getWheelRotation();
        if (notches < 0) {
            minX += getRangeX_Display() / ZOOM_FACTOR;
            maxX -= getRangeX_Display() / ZOOM_FACTOR;
            minY += getRangeY_Display() / ZOOM_FACTOR;
            maxY -= getRangeY_Display() / ZOOM_FACTOR;

            repaint();

        } else {
            minX -= getRangeX_Display() / ZOOM_FACTOR;
            minX = Math.max(minX, 0.0);

            minY -= getRangeY_Display() / ZOOM_FACTOR;
            minY = Math.max(minY, 0.0);

            // stop zoom out
            if (minX * minY > 0.0) {
                maxX += getRangeX_Display() / ZOOM_FACTOR;
                maxY += getRangeY_Display() / ZOOM_FACTOR;

                repaint();
            }
        }
    }
//
//    /**
//     *
//     * @param x
//     * @return
//     */
//    protected int convertMouseXToValue(int x) {
//        return //
//                (int) Math.round(
//                        (((double) (x - leftMargin)) / (double) graphWidth) //
//                        * getRangeX_Display()//
//                        + getMinX_Display());
//    }
//
//    /**
//     *
//     * @param y
//     * @return
//     */
//    protected double convertMouseYToValue(double y) {
//        return //
//                -1 * (((y - topMargin - 1) * getRangeY_Display() / graphHeight) //
//                - getMaxY_Display());
//    }

    protected double convertMouseXToValue(int x) {
        return //
                ((x - leftMargin) / (double) graphWidth) //
                * getRangeX_Display()//
                + getMinX_Display();
    }

    protected double convertMouseYToValue(int y) {
        return //
                (1.0 - ((double) (y - topMargin) / graphHeight)) //
                * getRangeY_Display()//
                + getMinY_Display();
    }

    protected void plotAFraction(
            Graphics2D g2d,
            boolean svgStyle,
            ETFractionInterface f,
            Color borderColor,
            float borderWeight,
            Color centerColor,
            float centerSize,
            String ellipseLabelFont,
            String ellipseLabelFontSize) {

        Path2D ellipse = f.getErrorEllipsePath();
        if (svgStyle) {
            // generate file if necessary to handle weakness in Batik
        } else {
            // draw ellipse
            g2d.setStroke(new BasicStroke(borderWeight));
            g2d.setPaint(borderColor);
            g2d.draw(ellipse);
        }

        // draw ellipse centers
        if (true) {// (isShowEllipseCenters()) {

            float centerXbox = (float) (ellipse.getBounds().x + ellipse.getBounds().width / 2.0 - centerSize / 2.0);
            float centerYbox = (float) (ellipse.getBounds().y + ellipse.getBounds().height / 2.0 - centerSize / 2.0);

            Ellipse2D fractionbox = new Ellipse2D.Double(
                    centerXbox,
                    centerYbox,
                    centerSize,
                    centerSize);
            g2d.setPaint(centerColor);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.fill(fractionbox);
        }

        if (false) {//(isShowEllipseLabels()) {
            g2d.setPaint(borderColor);
            g2d.setFont(new Font(
                    ellipseLabelFont,
                    Font.BOLD,
                    Integer.parseInt(ellipseLabelFontSize)));

            // locate label based on tilt of ellipse as represented by rho
            float labelY;
            float labelX;
            if (f.getEllipseRho() < 0) {
                labelY = (float) (ellipse.getBounds().getY() //
                        + ellipse.getBounds().getHeight() - (1.0 + f.getEllipseRho()) * ellipse.getBounds().getHeight() / 4.0 + 15f);
                labelX = (float) (ellipse.getBounds().getX() + ellipse.getBounds().getWidth() - 6f);
            } else {
                labelY = (float) (ellipse.getBounds().getY() //
                        + (1.0 - f.getEllipseRho()) * ellipse.getBounds().getHeight() / 2.0);
                labelX = (float) (ellipse.getBounds().getX() + ellipse.getBounds().getWidth() + 2f);
            }

            g2d.drawString(f.getFractionID(), labelX, labelY);
        }

    }

    protected void generateEllipsePathIII(
            ETFractionInterface f,
            double ellipseSize) {

        ValueModel xAxisRatio = null;
        ValueModel yAxisRatio = null;
        ValueModel correlationCoefficient = null;

        xAxisRatio = f.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName());
        yAxisRatio = f.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName());

        correlationCoefficient = new ValueModel(); // fake zero for now

        Path2D ellipse = new Path2D.Double(Path2D.WIND_NON_ZERO);// null;

        double aspectRatio = ((getRangeY_Display() / (double) graphHeight) / (getRangeX_Display() / (double) graphWidth));

        if ((correlationCoefficient.getValue().doubleValue() >= -1.0)
                && (correlationCoefficient.getValue().doubleValue() <= 1.0)) {

            ErrorEllipse ee = new ErrorEllipse(
                    xAxisRatio,
                    yAxisRatio,
                    correlationCoefficient,
                    aspectRatio,
                    ellipseSize);

            int pointCount = 13;

            Matrix ellipseXY = ee.getEllipseControlPoints();

            ellipse.moveTo(
                    mapX(ellipseXY.get(0, 0)),
                    mapY(ellipseXY.get(0, 1)));

            for (int i = 1; i < pointCount; i += 3) {
                ellipse.curveTo(
                        mapX(ellipseXY.get(i, 0)),
                        mapY(ellipseXY.get(i, 1)),
                        mapX(ellipseXY.get(i + 1, 0)),
                        mapY(ellipseXY.get(i + 1, 1)),
                        mapX(ellipseXY.get(i + 2, 0)),
                        mapY(ellipseXY.get(i + 2, 1)));
            }
            ellipse.closePath();

            // june 2010 if any part of bounds in view, then display
            if (ellipse.getBounds().intersects(//
                    leftMargin - 1, topMargin - 1, (int) graphWidth + 2, (int) graphHeight + 2)) {

                f.setErrorEllipsePath(ellipse);
                // used for placing ellipse label
                f.setEllipseRho(correlationCoefficient.getValue().doubleValue());

            } else {
                f.setErrorEllipsePath(null);
            }
        } else {
            // bad boy
            f.setErrorEllipsePath(null);
        }
    }

}
