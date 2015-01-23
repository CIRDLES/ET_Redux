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

import java.awt.Window;
import static java.awt.Dialog.ModalityType.APPLICATION_MODAL;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.VALUE_PROPERTY;

/**
 *
 * @author John Zeringue
 */
public class ETExceptionDialog extends JDialog {
    
    private static final String OK = "OK";
    private static final Object[] OPTIONS = new String[]{OK};
    
    private final ETException exception;
    private final JOptionPane optionPane;
    
    public ETExceptionDialog(ETException exception) {
        this(exception, null);
    }
    
    public ETExceptionDialog(ETException exception, Window owner) {
        super(owner, "EARTHTIME Warning", APPLICATION_MODAL);
        this.exception = exception;
        
        optionPane = makeOptionPane();
        configureOptionPane();
        
        configureThis();
    }
    
    private JOptionPane makeOptionPane() {
        return new JOptionPane(exception.getMessage(), WARNING_MESSAGE);
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
        
        // grow to fit optionPane
        pack();
        
        // center on the screen/owner
        setLocationRelativeTo(getOwner());
    }
    
    public static void main(String[] args) {
        ETException exception = new ETException("Var Unct Correlations yield Var Unct covariance matrix NOT positive definite.");
        
        new ETExceptionDialog(exception).setVisible(true);
        JOptionPane.showMessageDialog(null, exception.getMessage(), "EARTHTIME Warning", WARNING_MESSAGE);
    }
    
}
