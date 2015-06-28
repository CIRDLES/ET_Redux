/*
 * MaskingShade.java
 *
 * Created Jul 28, 2011
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
package org.earthtime.Tripoli.beans;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JLayeredPane;
import javax.swing.event.MouseInputListener;
import org.earthtime.Tripoli.dataViews.overlayViews.MaskingShadeTargetInterface;

/**
 *
 * @author James F. Bowring
 */
public class MaskingShade extends JLayeredPane implements MouseInputListener {

    /**
     *
     */
    public final static int PULL_FROM_LEFT = -1;
    /**
     *
     */
    public final static int PULL_FROM_RIGHT = 1;
    /**
     *
     */
    private int WIDTH_OF_PULLTAB = 20;
    private int HEIGHT_OF_PULLTAB = 30;
    private int currentMouseX;
    private int currentMouseY;
    private int pressedMouseX;
    private int pressedMouseY;
    private boolean mouseIsPressedInPullTab;
    private final MaskingShadeTargetInterface maskingShadeTarget;
    private final boolean showPullTab;
    private final int pullFrom;
    private final int countOfMaskedTimeSlots;
    private int maskingShadeTargetWidth;

    /**
     *
     * @param maskingShadeTarget
     * @param showPullTab
     * @param pullFrom
     * @param countOfMaskedTimeSlots
     */
    public MaskingShade(//
            MaskingShadeTargetInterface maskingShadeTarget, //
            boolean showPullTab,
            int pullFrom, //
            int countOfMaskedTimeSlots) {
        super();

        this.maskingShadeTarget = maskingShadeTarget;
        this.countOfMaskedTimeSlots = countOfMaskedTimeSlots;
        this.showPullTab = showPullTab;
        this.pullFrom = pullFrom;

        setBounds(calculateBounds());

        this.currentMouseX = 0;
        this.currentMouseY = 0;
        this.mouseIsPressedInPullTab = false;

        addMeAsMouseImputListener();
    }

    private Rectangle calculateBounds() {
        int x = 0;
        int y = 0;
        int w;
        if (pullFrom == PULL_FROM_LEFT) {
            w = (int) Math.max(WIDTH_OF_PULLTAB, //
                    WIDTH_OF_PULLTAB //* (countOfMaskedTimeSlots < 0 ? 1 : 2)//
                    + maskingShadeTarget.mapX(
                            (countOfMaskedTimeSlots < 0 ? //
                                    -1 //
                                    : countOfMaskedTimeSlots)));
        } else {
            x = maskingShadeTarget.getWidth() - (int) Math.max(WIDTH_OF_PULLTAB, //
                    WIDTH_OF_PULLTAB * (countOfMaskedTimeSlots < 0 ? 0 : 1)//
                    + maskingShadeTarget.mapX(
                            (countOfMaskedTimeSlots < 0 ? //
                                    -1 //
                                    : countOfMaskedTimeSlots)));
            w = WIDTH_OF_PULLTAB * (countOfMaskedTimeSlots < 0 ? 1 : 2) //
                    + (int) maskingShadeTarget.mapX(
                            (countOfMaskedTimeSlots < 0 ? //
                                    0 //
                                    : countOfMaskedTimeSlots));

        }
        int h = maskingShadeTarget.getHeight();

        return new Rectangle(x, y, w, h);
    }

    private void addMeAsMouseImputListener() {
        addMouseMotionListener(this);
        addMouseListener(this);
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
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        Composite originalComposite = g2d.getComposite();
        g2d.setPaint(Color.gray);

        Shape shade;

        if (pullFrom == PULL_FROM_LEFT) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));

            // pulltab
            if (showPullTab) {
                Shape pullTabOutline = new Arc2D.Double( //
                        (double) (getWidth() - WIDTH_OF_PULLTAB * 1.5),//
                        (double) calculateTopOfPullTab(),//
                        (double) WIDTH_OF_PULLTAB,//
                        (double) HEIGHT_OF_PULLTAB,
                        270., 180., Arc2D.CHORD);

                g2d.fill(pullTabOutline);
            }

            // body in line with pulltab
            shade = new Rectangle2D.Double(//
                    getX(), 0, getWidth() - WIDTH_OF_PULLTAB, getHeight());
        } else {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));

            // pulltab
            if (showPullTab) {
                Shape pullTabOutline = new Arc2D.Double( //
                        (double) WIDTH_OF_PULLTAB / 2,
                        (double) calculateTopOfPullTab(),//
                        (double) WIDTH_OF_PULLTAB,//
                        (double) HEIGHT_OF_PULLTAB,
                        90., 180., Arc2D.CHORD);

                g2d.fill(pullTabOutline);

            }

            // body in line with pulltab
            shade = new Rectangle2D.Double(//
                    WIDTH_OF_PULLTAB, 0, getWidth(), getHeight());

        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f));
        g2d.fill(shade);
        g2d.setComposite(originalComposite);

    }

    private int calculateTopOfPullTab() {
        return getHeight() - HEIGHT_OF_PULLTAB;
    }

    private boolean mouseInPullTab() {

        boolean mouseInPullTab;

        if (pullFrom == PULL_FROM_LEFT) {
            mouseInPullTab = (currentMouseY > calculateTopOfPullTab())//
                    && //
                    (currentMouseY < calculateTopOfPullTab() + HEIGHT_OF_PULLTAB)//
                    && //
                    (currentMouseX > getWidth() - WIDTH_OF_PULLTAB)//
                    && //
                    (currentMouseX < getWidth()//
                    && //
                    showPullTab);

        } else {
            mouseInPullTab = (currentMouseY > calculateTopOfPullTab())//
                    && //
                    (currentMouseY < calculateTopOfPullTab() + HEIGHT_OF_PULLTAB)//
                    && //
                    (currentMouseX > - 3 * WIDTH_OF_PULLTAB)//
                    && //
                    (currentMouseX < 3 * WIDTH_OF_PULLTAB//
                    && //
                    showPullTab);

        }
        return mouseInPullTab;
    }

    private void setShadeCover(int width) {
        if (pullFrom == PULL_FROM_LEFT) {
            setBounds(0, 0, width, getHeight());
        } else {
            //setBounds( Math.max( WIDTH_OF_PULLTAB, maskingShadeTargetWidth - width ), 0, width, getHeight() );
            setBounds(maskingShadeTargetWidth - width, 0, width, getHeight());
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        currentMouseX = e.getX();
        currentMouseY = e.getY();
        pressedMouseX = e.getX();
        pressedMouseY = e.getY();

        maskingShadeTargetWidth = maskingShadeTarget.getWidth();

        mouseIsPressedInPullTab = mouseInPullTab();
    }

    private void processMouseExitedPullTab() {
        mouseIsPressedInPullTab = false;
        if (pullFrom == PULL_FROM_LEFT) {
            setShadeCover(maskingShadeTarget.provideShadeXFromLeft(currentMouseX));//- WIDTH_OF_PULLTAB / 2 ) );
        } else {
            setShadeCover(/* WIDTH_OF_PULLTAB +*/maskingShadeTarget.provideShadeXFromRight(getWidth() - currentMouseX));//+ WIDTH_OF_PULLTAB));// - getWidth() )  );
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (mouseInPullTab()) {
            processMouseExitedPullTab();
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if (mouseIsPressedInPullTab) {
            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (pullFrom == PULL_FROM_LEFT) {
                setShadeCover(Math.max(WIDTH_OF_PULLTAB, getWidth() + (currentMouseX - pressedMouseX)));
                pressedMouseX = currentMouseX;
            } else {
                setShadeCover(Math.max(WIDTH_OF_PULLTAB, getWidth() + (pressedMouseX - currentMouseX)));
                pressedMouseX = WIDTH_OF_PULLTAB / 2;
            }

            if (!mouseInPullTab()) {
                processMouseExitedPullTab();
                setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        currentMouseX = e.getX();
        currentMouseY = e.getY();

        if (mouseInPullTab()) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }

    }

    /**
     * @param WIDTH_OF_PULLTAB the WIDTH_OF_PULLTAB to set
     */
    public void setWIDTH_OF_PULLTAB(int WIDTH_OF_PULLTAB) {
        this.WIDTH_OF_PULLTAB = WIDTH_OF_PULLTAB;
    }

    /**
     * @param HEIGHT_OF_PULLTAB the HEIGHT_OF_PULLTAB to set
     */
    public void setHEIGHT_OF_PULLTAB(int HEIGHT_OF_PULLTAB) {
        this.HEIGHT_OF_PULLTAB = HEIGHT_OF_PULLTAB;
    }
}
