/*
 * ValueModelUncertSlider.java
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
package org.earthtime.UPb_Redux.beans;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.JPanel;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class ValueModelUncertSlider extends JPanel
        implements
        ValueModelSliderI,
        MouseListener,
        MouseMotionListener,
        Serializable {

    /**
     * 
     */
    public static final String UNCERTAINTY_PROPERTY = "uncertainty";
    private BigDecimal uncertaintyProperty;
    private final PropertyChangeSupport propertySupport;
    private final JPanel valueModelSliderBox;
    private int leftX;
    private int topY;
    private int width;
    private int height;
    private ValueModel valueModel;
    private ValueModel valueModelCopy;
    private BigDecimal currentUncertainty;
    private int sigDigits;
    private BigDecimal pixelSlideRange;
    private int lastX;
    private int nextX;
    private boolean mouseInsideValueModelSliderBox;
    private String units;
    private String[] covaryingTerms;
    private JPanel[] sisterSliders;

    /**
     * 
     * @param name
     * @param covaryingTerms
     * @param sisterSliders
     * @param leftX
     * @param topY
     * @param width
     * @param height
     * @param valueModel
     * @param units
     * @param kwikiUncertChangeListener
     */
    public ValueModelUncertSlider(
            String name,
            String[] covaryingTerms,
            JPanel[] sisterSliders,
            int leftX,
            int topY,
            int width,
            int height,
            ValueModel valueModel,
            String units,
            PropertyChangeListener kwikiUncertChangeListener) {

        this.leftX = leftX;
        this.topY = topY;
        this.width = width;
        this.height = height;
        sigDigits = 2;

        this.valueModel = valueModel;
        valueModelCopy = valueModel.copy();

        this.units = units;

        currentUncertainty = valueModel.getOneSigma();
        uncertaintyProperty = valueModel.getOneSigmaAbs();
        propertySupport = new PropertyChangeSupport(this);
        addPropertyChangeListener(kwikiUncertChangeListener);
        // oct 2010   setUncertaintyProperty(true);

        setOpaque(true);
        setBackground(Color.white);
        addMouseListener(this);

        // detect whether to allow sliding based on uncert mode
        addMouseMotionListener(this);
        setBounds(leftX, topY, width, height);

        valueModelSliderBox = //
                new ValueModelSliderBox(//
                width, height - 3, getCurrentUncertaintyShowValue());

        setName(name);
        this.covaryingTerms = covaryingTerms;
        this.sisterSliders = sisterSliders;

        lastX = 0;
        nextX = 0;

        pixelSlideRange = //
                new BigDecimal(valueModel.getOneSigma().doubleValue() / ((width - valueModelSliderBox.getWidth()) / 2));

    }

    private String getCurrentUncertaintyShowValue() {
        if (valueModel.getUncertaintyType().equalsIgnoreCase("PCT")) {
            return ValueModel.formatBigDecimalForPublicationSigDigMode(//
                    new BigDecimal(currentUncertainty.doubleValue(), ReduxConstants.mathContext15), sigDigits);

        } else {
            return ValueModel.formatBigDecimalForPublicationSigDigMode(new BigDecimal(currentUncertainty.doubleValue(), ReduxConstants.mathContext15).//
                    movePointRight(ReduxConstants.getUnitConversionMoveCount(units)), sigDigits);
        }
    }

    /**
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    /**
     * 
     * @param g2d
     */
    public void paint(Graphics2D g2d) {

        // don't show missing uncts
        if (valueModelCopy.getOneSigma().compareTo(BigDecimal.ZERO) > 0) {
            RenderingHints rh = g2d.getRenderingHints();
            rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHints(rh);

            g2d.setColor(Color.black);
            DrawBounds(g2d);
            g2d.setColor(Color.red);
            g2d.drawLine((width / 2), 0, (width / 2), height);


            ((ValueModelSliderBox) valueModelSliderBox).//
                    setValueString(getCurrentUncertaintyShowValue());

            ((ValueModelSliderBox) valueModelSliderBox).paint(g2d);

        }
    }

    private void DrawBounds(Graphics2D g2d) {

        g2d.drawRect(0, 0, width - 1, height - 1);

    }

    /**
     * 
     * @param evt
     * @return
     */
    public boolean mouseInsideValueModelSliderBox(MouseEvent evt) {
        // this is not very sensitive
        if ((evt.getX() >= valueModelSliderBox.getX()) &&
                (evt.getX() <= (valueModelSliderBox.getX() + valueModelSliderBox.getWidth()))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @return
     */
    public BigDecimal getUncertaintyProperty() {
        return uncertaintyProperty;
    }

    /**
     * 
     * @param doFirePropertyChange
     */
    public void setUncertaintyProperty(Boolean doFirePropertyChange) {
        BigDecimal oldValue = uncertaintyProperty;
        // uncertaintyProperty = ValueModel.CalculateOneSigmaAbs(valueModel, getCurrentUncertainty());

        // change fraction's copy
        valueModel.setOneSigma(getCurrentUncertainty());
        uncertaintyProperty = valueModel.getOneSigmaAbs();

        BigDecimal change;
        try {
            change =getCurrentUncertainty().//
                    subtract(valueModelCopy.getOneSigma()).//
                    divide(valueModelCopy.getOneSigma(), ReduxConstants.mathContext15);
        } catch (Exception e) {
            change = BigDecimal.ZERO;
        }

        setToolTipText(//
                ValueModel.formatBigDecimalForPublicationSigDigMode(//
                getCurrentUncertainty(), sigDigits) //
                + " (current) = " //
                + ValueModel.formatBigDecimalForPublicationSigDigMode(//
                valueModelCopy.getOneSigma(), sigDigits) //
                + " (1-sigma) + " //
                + change.setScale(1, RoundingMode.HALF_UP).toPlainString()//
                + " sigma");


        if (doFirePropertyChange) {
            propertySupport.firePropertyChange(UNCERTAINTY_PROPERTY, oldValue, uncertaintyProperty);
        }
    }

    /**
     * 
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * 
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * single click outside valuebox will reset valuebox to original value
     * @param e
     */
    public void mouseClicked(MouseEvent e) {
        // if (!mouseInsideValueModelSliderBox) {
        resetSliderBox();
    // }
    }

    /**
     * 
     */
    public void resetSliderBox() {
        // here avoid use of setter to prevent reset of true zero
        currentUncertainty = valueModelCopy.getOneSigma();
        setUncertaintyProperty(true);
        ((ValueModelSliderBox) valueModelSliderBox).//
                centerSliderBox(ValueModel.//
                formatBigDecimalForPublicationSigDigMode(getCurrentUncertainty(), sigDigits));
        repaint();
    }

    /**
     * 
     * @param e
     */
    public void mousePressed(MouseEvent e) {
        lastX = e.getX();
        mouseInsideValueModelSliderBox = mouseInsideValueModelSliderBox(e);
    }

    /**
     * 
     * @param e
     */
    public void mouseReleased(MouseEvent e) {
        nextX = lastX;//e.getX();
        if (mouseInsideValueModelSliderBox) {
            // recalculate fraction - fire property
            setUncertaintyProperty(true);
            mouseInsideValueModelSliderBox = false;
        }
    }

    /**
     * 
     * @param e
     */
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param e
     */
    public void mouseExited(MouseEvent e) {
        nextX = lastX;//e.getX();
        if (mouseInsideValueModelSliderBox) {
            // recalculate fraction - fire property
            setUncertaintyProperty(true);
            mouseInsideValueModelSliderBox = false;
        }
    }

    /**
     * 
     * @param e
     */
    public void mouseDragged(MouseEvent e) {
        nextX = e.getX();
        //System.out.println("in at x = " + e.getX() + "  lastx = " + lastX + "  nextx = " + nextX);
        if (mouseInsideValueModelSliderBox) {
            // check for moving past limits of valueModelSlider
            int pos = valueModelSliderBox.getX() + (nextX - lastX);
            if ((pos + valueModelSliderBox.getWidth()) <= (getWidth() + 1) &&
                    (pos >= -1)) {
                valueModelSliderBox.setLocation(//
                        pos,
                        valueModelSliderBox.getY());

                setCurrentUncertainty(getCurrentUncertainty().add(pixelSlideRange.multiply(new BigDecimal(nextX - lastX), ReduxConstants.MCforBigD_5)));

                lastX = nextX;

                repaint();
            }
        }
    }

    // used for moving co-varying uncertainty sliders
    /**
     * 
     * @param oneSigmaAbs
     */
    public void forceCurrentUncertainty(BigDecimal oneSigmaAbs) {

        setCurrentUncertainty(ValueModel.convertOneSigmaAbsToPctIfRequired(valueModel, oneSigmaAbs));

        lastX = (int) (getCurrentUncertainty().doubleValue() / pixelSlideRange.doubleValue());

        valueModelSliderBox.setLocation(lastX, valueModelSliderBox.getY());

        setUncertaintyProperty(false);
        repaint();
    }

    /**
     * 
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the currentUncertainty
     */
    public BigDecimal getCurrentUncertainty() {
        return currentUncertainty;
    }

    /**
     * @param currentUncertainty the currentUncertainty to set
     */
    public void setCurrentUncertainty(BigDecimal currentUncertainty) {
        // prevent division by zero with sliders
        this.currentUncertainty = //
                (BigDecimal) (currentUncertainty.compareTo(BigDecimal.ZERO) <= 0 //
                ? new BigDecimal(0.000001) : currentUncertainty);
    //  this.currentUncertainty = currentUncertainty;
    }

    /**
     * @return the covaryingTerms
     */
    public String[] getCovaryingTerms() {
        return covaryingTerms;
    }

    /**
     * @param covaryingTerms the covaryingTerms to set
     */
    public void setCovaryingTerms(String[] covaryingTerms) {
        this.covaryingTerms = covaryingTerms;
    }

    /**
     * @return the sisterSliders
     */
    public JPanel[] getSisterSliders() {
        return sisterSliders;
    }

    /**
     * @param sisterSliders the sisterSliders to set
     */
    public void setSisterSliders(JPanel[] sisterSliders) {
        this.sisterSliders = sisterSliders;
    }
}
