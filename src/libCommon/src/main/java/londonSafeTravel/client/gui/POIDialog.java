package londonSafeTravel.client.gui;

import londonSafeTravel.schema.document.poi.PointOfInterest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class POIDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel namePOI;
    private JTextArea namePOIText;
    private JLabel id_poi;

    PointOfInterest poi;

    public POIDialog(PointOfInterest poi) {
        this.poi = poi;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setMinimumSize(new Dimension(400, 600));

        id_poi.setText("ID: "+poi.poiID);
        namePOIText.setText(poi.name);


        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

}
