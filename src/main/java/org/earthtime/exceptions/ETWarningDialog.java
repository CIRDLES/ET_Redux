/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.exceptions;

import static java.awt.Dialog.ModalityType.TOOLKIT_MODAL;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.VALUE_PROPERTY;

/**
 *
 * @author John Zeringue
 */
public class ETWarningDialog extends JDialog {

    private static final String OK = "OK";
    private static final Object[] OPTIONS = new String[]{OK};

    private final String message;
    private final JOptionPane optionPane;

    /**
     * Added to replace the following:
     *
     * <pre>try {
     *    throw new ETException(null, "Duplicate Fraction ID, please use another.");
     *} catch (ETException ex) {
     *}</pre>
     *
     * @param message
     */
    public ETWarningDialog(String message) {
        super(null, "EARTHTIME Warning", TOOLKIT_MODAL);
        this.message = message;

        optionPane = makeOptionPane();
        configureOptionPane();

        configureThis();
    }

    public ETWarningDialog(ETException ex) {
        this(ex.getMessage());
    }

    private JOptionPane makeOptionPane() {
        return new JOptionPane(message, WARNING_MESSAGE);
    }

    private void configureOptionPane() {
        optionPane.setOptions(OPTIONS);
        optionPane.setInitialValue(OK);

        optionPane.addPropertyChangeListener(event -> {
            String property = event.getPropertyName();
            Object value = optionPane.getValue();

            if (VALUE_PROPERTY.equals(property) && OK.equals(value)) {
                dispose();
            }
        });
    }

    private void configureThis() {
        setContentPane(optionPane);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);

        // grow to fit optionPane
        pack();

        // center on the screen/owner
        setLocationRelativeTo(getOwner());
    }

//    public static void main(String[] args) {
//        ETException ex = new ETException("Var Unct Correlations yield Var Unct covariance matrix NOT positive definite.");
//
//        new ETWarningDialog(ex).setVisible(true);
//        JOptionPane.showMessageDialog(null, ex.getMessage(), "EARTHTIME Warning", WARNING_MESSAGE);
//    }

}
