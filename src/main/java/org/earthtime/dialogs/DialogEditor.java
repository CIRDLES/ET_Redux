/*
 * DialogEditor.java
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
package org.earthtime.dialogs;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.math.BigDecimal;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainDocument;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.utilities.BrowserControl;

/**
 *
 * @author James F. Bowring
 */
public abstract class DialogEditor extends JDialog {

    /**
     *
     */
    public static boolean amOpen = false;
    private final static Font myScienceFont = new Font("Calibri"//Arial"
            /*
             * "Monospaced"
             */, Font.PLAIN, 13);

    /**
     * Creates new form DialogEditor
     *
     * @param parent
     * @param modal
     */
    public DialogEditor(java.awt.Frame parent, boolean modal) {
        super(parent, modal);

        JDialog.setDefaultLookAndFeelDecorated(true);
        initComponents();
    }

    public void initDialogContent(){
        
    }
    
    /**
     *
     * @param preferredWidth
     * @param preferredHeight
     */
    protected void setSizeAndCenter(int preferredWidth, int preferredHeight) {
        preferredHeight += (BrowserControl.isMacOS() ? 0 : 25);
        super.setSize(preferredWidth, preferredHeight);
        super.setPreferredSize(new Dimension(preferredWidth, preferredHeight));

        //Get the screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        //Calculate the frame location
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;

        //Set the new frame location centered
        setLocation(x, y);
    }

    /**
     *
     */
    public static class UnDoAbleDocument extends PlainDocument {

        /**
         *
         */
        public final UndoManager undo = new UndoManager();
        private JTextComponent textComp;

        /**
         *
         * @param textComp
         * @param editable
         */
        public UnDoAbleDocument(JTextComponent textComp, boolean editable) {
            this(textComp);
            textComp.setEditable(editable);
            if (editable) {
                textComp.setBackground(ReduxConstants.myEditingWhiteColor);
            } else {
                textComp.setBackground(ReduxConstants.myNotEditingGreyColor);
            }

            textComp.addFocusListener(new SelectTextFocusListener());
        }

        /**
         *
         * @param textComp
         */
        public UnDoAbleDocument(JTextComponent textComp) {
            super();

            this.textComp = textComp;

            // http://javaalmanac.com/egs/javax.swing.undo/UndoText.html
            // Listen for undo and redo events
            addUndoableEditListener(new UndoableEditListener() {

                @Override
                public void undoableEditHappened(UndoableEditEvent evt) {
                    undo.addEdit(evt.getEdit());
                }
            });

            // Create an undo action and add it to the text component
            textComp.getActionMap().put("Undo",
                    new AbstractAction("Undo") {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            try {
                                if (undo.canUndo()) {
                                    undo.undo();
                                }
                            } catch (CannotUndoException e) {
                            }
                        }
                    });

            // Bind the undo action to ctl-Z
            textComp.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");

            // Create a redo action and add it to the text component
            textComp.getActionMap().put("Redo",
                    new AbstractAction("Redo") {

                        @Override
                        public void actionPerformed(ActionEvent evt) {
                            try {
                                if (undo.canRedo()) {
                                    undo.redo();
                                }
                            } catch (CannotRedoException e) {
                            }
                        }
                    });

            // Bind the redo action to ctl-Y
            textComp.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        }

        /**
         *
         */
        public void unFill() {

            try {
                if (undo.canUndo()) {
                    undo.undo();
                    undo.undo();
                }
            } catch (CannotUndoException e) {
            }
        }

        /**
         * @return the textComp
         */
        public JTextComponent getTextComp() {
            return textComp;
        }

        /**
         * @param textComp the textComp to set
         */
        public void setTextComp(JTextComponent textComp) {
            this.textComp = textComp;
        }
    }

    //http://java.sun.com/developer/JDCTechTips/2001/tt1120.html
    /**
     *
     */
    public static class DoubleDocument extends UnDoAbleDocument {

        double maxValue;
        private JTextField textF;

        /**
         *
         * @param textF
         */
        public DoubleDocument(JTextField textF) {
            super(textF, true);
            textF.setFont(myScienceFont);
            maxValue = 0.0;
        }

        /**
         *
         * @param textF
         * @param editable
         */
        public DoubleDocument(JTextField textF, boolean editable) {
            super(textF, editable);
            textF.setFont(myScienceFont);
            maxValue = 0.0;
        }

        // may 2010 to handle trapping maximum dates
        /**
         *
         * @param textF
         * @param maxValue
         * @param editable
         */
        public DoubleDocument(JTextField textF, double maxValue, boolean editable) {
            super(textF, editable);
            this.textF = textF;
            textF.setFont(myScienceFont);
            this.maxValue = maxValue;
        }

        /**
         *
         * @param offset
         * @param string
         * @param attributes
         * @throws BadLocationException
         */
        @Override
        public void insertString(int offset,
                String string, AttributeSet attributes)
                throws BadLocationException {

            if (string != null) {
                String newValue;
                int length = getLength();
                if (length == 0) {
                    newValue = string;
                } else {
                    String currentContent
                            = getText(0, length);
                    StringBuilder currentBuffer
                            = new StringBuilder(currentContent);
                    currentBuffer.insert(offset, string);
                    newValue = currentBuffer.toString();
                }
                try {
                    double tempVal = Double.parseDouble(newValue);

//                    if ((maxValue > 0.0)){//jan 2014 removed positive constraint && (tempVal > maxValue)) {
                    // jan 2015 repalced to original
                    if ((maxValue > 0.0) && (tempVal > maxValue)) {
                        string = Double.toString(maxValue);
                        offset = 0;
                        textF.setText("");
                    }
                    super.insertString(offset, string,
                            attributes);
                } catch (NumberFormatException ex) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
        // public String toString() {
        // }

    }

    /**
     *
     */
    public static class IntegerDocument extends UnDoAbleDocument {

        /**
         *
         * @param textF
         */
        public IntegerDocument(JTextField textF) {
            super(textF, true);
            textF.setFont(myScienceFont);
        }

        /**
         *
         * @param textF
         * @param editable
         */
        public IntegerDocument(JTextField textF, boolean editable) {
            super(textF, editable);
            textF.setFont(myScienceFont);
        }

        /**
         *
         * @param offset
         * @param string
         * @param attributes
         * @throws BadLocationException
         */
        @Override
        public void insertString(int offset,
                String string, AttributeSet attributes)
                throws BadLocationException {

            if (string != null) {
                String newValue;
                int length = getLength();
                if (length == 0) {
                    newValue = string;
                } else {
                    String currentContent
                            = getText(0, length);
                    StringBuilder currentBuffer
                            = new StringBuilder(currentContent);
                    currentBuffer.insert(offset, string);
                    newValue = currentBuffer.toString();
                }
                try {
                    Integer.parseInt(newValue);
                    super.insertString(offset, string,
                            attributes);
                } catch (NumberFormatException ex) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
        // public String toString() {
        // }
    }

    /**
     *
     */
    public static class BigDecimalDocument extends UnDoAbleDocument {

        boolean editable = false;

        /**
         *
         * @param textF
         */
        public BigDecimalDocument(JTextField textF) {
            this(textF, true);
        }

        /**
         *
         * @param textF
         * @param editable
         */
        public BigDecimalDocument(JTextField textF, boolean editable) {
            super(textF, editable);
            this.editable = editable;
            textF.setFont(myScienceFont);
            textF.setCaretPosition(0);
        }

        /**
         *
         * @return
         */
        public boolean isEditable() {
            return editable;
        }

        /**
         *
         * @param offset
         * @param string
         * @param attributes
         * @throws BadLocationException
         */
        @Override
        public void insertString(int offset,
                String string, AttributeSet attributes)
                throws BadLocationException {

            if (string != null) {
                // to handle e and E
                string = string.toUpperCase();
                String newValue;
                int length = getLength();
                if (length == 0) {
                    newValue = string;
                } else {
                    String currentContent
                            = getText(0, length);
                    StringBuilder currentBuffer
                            = new StringBuilder(currentContent);
                    currentBuffer.insert(offset, string);
                    newValue = currentBuffer.toString();
                }
                try {
                    // test for signs and scientific notation by allowing the letter e or E or - or +
                    if ((newValue.startsWith(".") //
                            || newValue.startsWith("-")//
                            || newValue.startsWith("+"))) {
                        if (newValue.length() > 1) {
                            BigDecimal test = new BigDecimal(newValue, ReduxConstants.mathContext15);
                        } else {
                            // do nothing
                        }
                    } else if ((newValue.indexOf("E") >= 0 //
                            || newValue.indexOf("E-") >= 0//
                            || newValue.indexOf("E+") >= 0)) {
                        if ((newValue.length() - newValue.indexOf("E")) > 2) {
                            BigDecimal test = new BigDecimal(newValue, ReduxConstants.mathContext15);
                        } else {
                            // do nothing
                        }
                    } else {
                        BigDecimal test = new BigDecimal(newValue, ReduxConstants.mathContext15);
                    }
                    super.insertString(offset, string,
                            attributes);
                } catch (NumberFormatException ex) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
    }

    /**
     *
     */
    public static class SelectTextFocusListener implements FocusListener {

        /**
         * Creates a new instance of SelectTextFocusListener
         */
        public SelectTextFocusListener() {
        }

        /**
         *
         * @param focusEvent
         */
        @Override
        public void focusGained(FocusEvent focusEvent) {
            // ((JTextComponent) focusEvent.getSource()).setSelectionStart( 0 );
            ((JTextComponent) focusEvent.getSource()).setCaretPosition(0);
//            if ( ((JTextComponent) focusEvent.getSource()).isEditable() ) {
//                ((JTextComponent) focusEvent.getSource()).setSelectionEnd(
//                        ((JTextComponent) focusEvent.getSource()).getText().length() );
//            } else {
//                ((JTextComponent) focusEvent.getSource()).setSelectionEnd( 0 );
//            }
        }

        /**
         *
         * @param focusEvent
         */
        @Override
        public void focusLost(FocusEvent focusEvent) {
            // revised nov 2010 to differentiate //added sep 2010 to handle accidentally blanked out number items
            JTextComponent temp = ((JTextComponent) focusEvent.getSource());

            if (temp.getText().length() == 0) {
                if ((temp.getDocument() instanceof DoubleDocument)//
                        ||//
                        (temp.getDocument() instanceof BigDecimalDocument)) {
                    temp.setText("0.0");
                } else if ((temp.getDocument() instanceof IntegerDocument)) {
                    temp.setText("0");
                }
            }

            temp.setCaretPosition(0);
        }
    }

    /**
     *
     * @param width
     * @param height
     */
    @Override
    public void setSize(int width, int height) {
        if (BrowserControl.isMacOS()) {
            super.setSize(width, height);
        }
        if (BrowserControl.isWindowsPlatform()) {
            super.setSize(width, height + 25);
        }

        //Get the screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        //Calculate the frame location
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;

        //Set the new frame location
        setLocation(x, y);
    }

    /**
     *
     */
    public void close() {
        setVisible(false);
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
