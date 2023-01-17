package londonSafeTravel.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdministrationDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JPanel login;

    public AdministrationDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setMinimumSize(new Dimension(250, 300));
        this.setLocationRelativeTo(null);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    onOK();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() throws Exception {
        boolean userOK = textField1.getText().equals("admin");
        boolean passOK = new String(passwordField1.getPassword()).equals("admin");
        if(userOK && passOK) {
            AnalyticsMap.main(null);
            dispose();
        }
        else
            JOptionPane.showMessageDialog(this.contentPane, "Wrong password", "Wrong password", JOptionPane.ERROR_MESSAGE);

    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        AdministrationDialog dialog = new AdministrationDialog();
        dialog.pack();
        dialog.setVisible(true);
        //System.exit(0);
    }

}
