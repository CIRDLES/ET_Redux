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
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
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
import java.math.RoundingMode;
import java.util.Map;
import java.util.Vector;
import javax.swing.JLayeredPane;
import javax.swing.event.MouseInputListener;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ErrorEllipse;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractDataView extends JLayeredPane implements AliquotDetailsDisplayInterface, MouseInputListener, MouseWheelListener {

    protected static final double ZOOM_FACTOR = 10.0;
    protected static final int minGraphWidthHeight = 100;
    protected int maxGraphWidth = 500;
    protected static final int maxGraphHeight = 750;

    protected double width;
    protected double height;

    /**
     *
     */
    protected double[] myOnPeakData;
    protected double[] myOnPeakDataUpperUnct;
    protected double[] myOnPeakDataLowerUnct;
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
    protected double xAxisMax;
    protected double yAxisMax;
    protected int zoomMinX;
    protected int zoomMinY;
    protected int zoomMaxX;
    protected int zoomMaxY;
    /**
     *
     */
    protected BigDecimal[] ticsYaxis;
    protected BigDecimal[] ticsXaxis;

    protected ValueModel lambda234;
    protected ValueModel lambda238;
    protected ValueModel lambda230;
    protected double lambda238D;
    protected double lambda234D;
    protected double lambda230D;

    protected static Vector<ETFractionInterface> selectedFractions;
    protected Vector<ETFractionInterface> filteredFractions;
    protected Vector<ETFractionInterface> excludedFractions;

    protected boolean eastResizing;
    protected boolean southResizing;

    // <0 = zoom out, 0 = original, >0 = zoom in
    protected int zoomCount;

    protected String imageMode;

    protected boolean showCenters;
    protected boolean showLabels;

    protected int xLocation;

    protected boolean showMe;

    /**
     *
     */
    public AbstractDataView() {
        super();

        this.showMe = true;
    }

    /**
     *
     * @param bounds
     * @param leftMargin
     * @param topMargin
     */
    protected AbstractDataView(Rectangle bounds, int leftMargin, int topMargin) {
        super();
        this.leftMargin = leftMargin;
        this.topMargin = topMargin;

        this.myOnPeakData = null;

        width = bounds.getWidth();
        height = bounds.getHeight();
        graphWidth = (int) width - leftMargin;
        graphHeight = (int) height - topMargin;

        this.ticsYaxis = new BigDecimal[0];
        this.ticsXaxis = new BigDecimal[0];

        this.eastResizing = false;
        this.southResizing = false;

        this.zoomCount = 0;

        putInImageModePan();

        this.showCenters = true;
        this.showLabels = false;

        this.showMe = true;

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
        if (showMe) {
            paintInit(g2d);

            drawBorder(g2d);
        }
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
        AbstractRatiosDataModel physicalConstantsModel;
        if (selectedFractions.size() > 0) {
            physicalConstantsModel = selectedFractions.get(0).getPhysicalConstantsModel();
        } else {
            physicalConstantsModel = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel();
        }

        lambda234 = physicalConstantsModel.getDatumByName(Lambdas.lambda234.getName());
        lambda238 = physicalConstantsModel.getDatumByName(Lambdas.lambda238.getName());
        lambda230 = physicalConstantsModel.getDatumByName(Lambdas.lambda230.getName());

        lambda238D = lambda238.getValue().doubleValue();
        lambda234D = lambda234.getValue().doubleValue();
        lambda230D = lambda230.getValue().doubleValue();

        try {
            preparePanel(doReset);
        } catch (Exception e) {
        }

        validate();
        repaint();
    }

    /**
     *
     * @param doReset
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public abstract void preparePanel(boolean doReset);

    /**
     *
     * @param g2d the value of g2d
     * @param specialYaxisDelta the value of specialYaxisDelta
     */
    protected void drawAxesAndTics(Graphics2D g2d, boolean specialYaxisDelta) {

        // reset the clip bounds to paint axis and numbers
        g2d.setClip(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));

        int yTicLabelFrequency = 1;
        int labeledTicCountYAxis = 0;

        g2d.setPaint(Color.black);
        for (int i = 0; i < ticsYaxis.length; i++) {

            double y = ticsYaxis[i].doubleValue();

            if ((y >= getMinY_Display())
                    && (y <= getMaxY_Display())) {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(getMinX_Display()),
                            mapY(y),
                            mapX(getMinX_Display()) + 7,
                            mapY(y));
                    g2d.draw(ticMark);

                    String intString = "00000" + ticsYaxis[i].toPlainString().replace(".", "");
                    int lastPlace = Integer.parseInt(intString.substring(intString.length() - 4));

                    if (lastPlace % yTicLabelFrequency == 0) {
                        if (labeledTicCountYAxis % yTicLabelFrequency == 0) {

                            TextLayout mLayout
                                    = new TextLayout(
                                            ticsYaxis[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext());

                            Rectangle2D bounds = mLayout.getBounds();

                            float yLabelCenterOffset = (float) mLayout.getBounds().getWidth() / 2f;

                            String ticLabel = "";
                            if (specialYaxisDelta) {
                                ticLabel = ticsYaxis[i].subtract(BigDecimal.ONE).movePointRight(3).setScale(1, RoundingMode.HALF_UP).toPlainString();
                            } else {
                                ticLabel = ticsYaxis[i].toPlainString();
                            }

                            g2d.rotate(
                                    -Math.PI / 2.0,
                                    (float) mapX(getMinX_Display()) - 4f,
                                    (float) mapY(y) + yLabelCenterOffset);
                            g2d.drawString(ticLabel,
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

        // X axis ==============================================================
        if (ticsXaxis.length <= 1) {
            ticsXaxis = new BigDecimal[0];
        }

        int xTicLabelFrequency = 1;
        int labeledTicCountXAxis = 0;

        g2d.setPaint(Color.black);
        for (int i = 0; i < ticsXaxis.length; i++) {

            double x = ticsXaxis[i].doubleValue();

            if ((x >= getMinX_Display())
                    && (x <= getMaxX_Display())) {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(x),
                            mapY(getMinY_Display()),
                            mapX(x),
                            mapY(getMinY_Display()) - 7);
                    g2d.draw(ticMark);

                    String intString = "00000" + ticsXaxis[i].toPlainString().replace(".", "");
                    int lastPlace = Integer.parseInt(intString.substring(intString.length() - 4));

                    if (lastPlace % xTicLabelFrequency == 0) {
                        if (labeledTicCountXAxis % xTicLabelFrequency == 0) {

                            TextLayout mLayout
                                    = new TextLayout(
                                            ticsXaxis[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext());

                            Rectangle2D bounds = mLayout.getBounds();

                            float xLabelCenterOffset = (float) bounds.getWidth() / 2f;

                            g2d.drawString(ticsXaxis[i].toPlainString(),
                                    (float) mapX(x) - xLabelCenterOffset,
                                    (float) mapY(getMinY_Display()) + 12f);
                        }

                        labeledTicCountXAxis++;
                    } else {

                        if (labeledTicCountXAxis > 0) {
                            labeledTicCountXAxis++;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("error at x tic");
                }
            }
        }

        g2d.drawRect(
                leftMargin, topMargin, graphWidth - 1, graphHeight - 1);

    }

    protected double calculateLengthOfStringPlot(Graphics2D g2d, String label) {
        TextLayout mLayout
                = new TextLayout(
                        label, g2d.getFont(), g2d.getFontRenderContext());

        Rectangle2D bounds = mLayout.getBounds();
        return bounds.getWidth();
    }

    /**
     *
     * @param g2d
     */
    protected void drawTicsYAxisInBackground(Graphics2D g2d) {
        // y -axis ticsYaxis
        Stroke savedStroke = g2d.getStroke();

        // ticsYaxis
        if (ticsYaxis != null) {
            for (int i = 0; i < ticsYaxis.length; i++) {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(minX), mapY(ticsYaxis[i].doubleValue()), mapX(maxX), mapY(ticsYaxis[i].doubleValue()));

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
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        zoomMaxX = evt.getX();
        zoomMaxY = evt.getY();

        int myX = evt.getX();
        int myY = evt.getY();

        if (isInImageModeZoom() //
                && (zoomMaxX != zoomMinX) //using != provides for legal inverting of zoom box
                && (zoomMaxY != zoomMinY)) {

            // may 2010 check for bad zooms
            double zMinX = convertMouseXToValue(Math.min(zoomMinX, zoomMaxX));
            double zMinY = convertMouseYToValue(Math.max(zoomMinY, zoomMaxY));
            double zMaxX = convertMouseXToValue(Math.max(zoomMaxX, zoomMinX));
            double zMaxY = convertMouseYToValue(Math.min(zoomMaxY, zoomMinY));

            if ((zMaxX > zMinX) && (zMaxY > zMinY)) {
                minX = zMinX;
                minY = zMinY;
                maxX = zMaxX;
                maxY = zMaxY;

                displayOffsetX = 0.0;
                displayOffsetY = 0.0;

                zoomMaxX = zoomMinX;
                zoomMaxY = zoomMinY;

                putInImageModePan();
            }
        }

        if (eastResizing ^ southResizing) {
            if (eastResizing) {
                this.graphWidth = Math.min(maxGraphWidth, (myX - leftMargin > minGraphWidthHeight) ? myX - leftMargin : minGraphWidthHeight);
            } else {
                this.graphHeight = Math.min(maxGraphHeight, (myY - topMargin > minGraphWidthHeight) ? myY - topMargin : minGraphWidthHeight);
            }
            this.setBounds(xLocation, 0, graphWidth + leftMargin * 2, graphHeight + topMargin * 2);
        }

        if (eastResizing && southResizing) {
            this.graphWidth = Math.min(maxGraphWidth, (myX - leftMargin > minGraphWidthHeight) ? myX - leftMargin : minGraphWidthHeight);
            this.graphHeight = Math.min(maxGraphHeight, (myY - topMargin > minGraphWidthHeight) ? myY - topMargin : minGraphWidthHeight);
            this.setBounds(xLocation, 0, graphWidth + leftMargin * 2, graphHeight + topMargin * 2);
        }

        eastResizing = false;
        southResizing = false;
        setCursor(Cursor.getDefaultCursor());

        repaint();
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseEntered(MouseEvent evt) {
    }

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
        if (mouseInHouse(evt) && !eastResizing && !southResizing) {
            zoomMaxX = evt.getX();
            zoomMaxY = evt.getY();

            if (isInImageModePan()) {
                // prevent all but upper right quadrant
                double calcDisplayOffsetXDelta = convertMouseXToValue(zoomMinX) - convertMouseXToValue(zoomMaxX);
                displayOffsetX += (((minX + displayOffsetX + calcDisplayOffsetXDelta) > 0) ? calcDisplayOffsetXDelta : 0.0);

                double calcDisplayOffsetYDelta = convertMouseYToValue(zoomMinY) - convertMouseYToValue(zoomMaxY);
                displayOffsetY += (((minY + displayOffsetY + calcDisplayOffsetYDelta) > 0) ? calcDisplayOffsetYDelta : 0.0);

                zoomMinX = zoomMaxX;
                zoomMinY = zoomMaxY;

                ticsYaxis = TicGeneratorForAxes.generateTics(getMinY_Display(), getMaxY_Display(), 10);
                ticsXaxis = TicGeneratorForAxes.generateTics(getMinX_Display(), getMaxX_Display(), 10);
            }

            repaint();
        }
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseMoved(MouseEvent evt) {
    }

    protected boolean mouseInHouse(MouseEvent evt) {
        return ((evt.getX() >= leftMargin)
                && (evt.getY() >= topMargin)
                && (evt.getY() < graphHeight + topMargin - 2)
                && (evt.getX() < (graphWidth + leftMargin - 2)));
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
    }

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
            String ellipseLabelFontSize,
            boolean showCenters,
            boolean showLabels) {

        Path2D ellipse = f.getErrorEllipsePath();
        if (svgStyle) {
            // generate file if necessary to handle weakness in Batik
        } else {
            // draw ellipse
            g2d.setStroke(new BasicStroke(borderWeight));
            g2d.setPaint(centerColor);
            g2d.draw(ellipse);

            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
//            g2d.setPaint(fillColor);
            g2d.fill(ellipse);
            //restore composite
            g2d.setComposite(originalComposite);
//            g2d.fill(ellipse);
        }

        // draw ellipse centers
        if (showCenters) {

            float centerXbox = (float) (ellipse.getBounds().x + ellipse.getBounds().width / 2.0 - centerSize / 2.0);
            float centerYbox = (float) (ellipse.getBounds().y + ellipse.getBounds().height / 2.0 - centerSize / 2.0);

            Ellipse2D fractionbox = new Ellipse2D.Double(
                    centerXbox,
                    centerYbox,
                    centerSize,
                    centerSize);
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.fill(fractionbox);
        }

        if (showLabels) {
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
            ValueModel xAxisRatio,
            ValueModel yAxisRatio,
            double ellipseSize) {

        ValueModel correlationCoefficient;
//
//        xAxisRatio = f.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName());
//        yAxisRatio = f.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName());

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

    @Override
    public Map<String, Map<String, String>> getAliquotOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<ETFractionInterface> getDeSelectedFractions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDeSelectedFractions(Vector<ETFractionInterface> deSelectedFractions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getSelectedAliquotOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSelectedFractions(Vector<ETFractionInterface> fractions) {
        selectedFractions = fractions;
    }

    @Override
    public void setFilteredFractions(Vector<ETFractionInterface> filteredFractions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<ETFractionInterface> getSelectedFractions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isShowFilteredEllipses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setShowFilteredEllipses(boolean showFilteredEllipses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isInImageModePan() {
        return imageMode.compareToIgnoreCase("PAN") == 0;
    }

    public boolean isInImageModeZoom() {
        return imageMode.compareToIgnoreCase("ZOOM") == 0;
    }

    public final void putInImageModePan() {
        imageMode = "PAN";
    }

    public void putInImageModeZoom() {
        imageMode = "ZOOM";
    }

    /**
     * @param showCenters the showCenters to set
     */
    public void setShowCenters(boolean showCenters) {
        this.showCenters = showCenters;
    }

    /**
     * @param showLabels the showLabels to set
     */
    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }

    /**
     * @return the showMe
     */
    public boolean isShowMe() {
        return showMe;
    }

    /**
     * @param showMe the showMe to set
     */
    public void setShowMe(boolean showMe) {
        this.showMe = showMe;
    }

    /**
     * @param xLocation the xLocation to set
     */
    public void setxLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    /**
     * @param maxGraphWidth the maxGraphWidth to set
     */
    public void setMaxGraphWidth(int maxGraphWidth) {
        this.maxGraphWidth = maxGraphWidth;
    }
}
