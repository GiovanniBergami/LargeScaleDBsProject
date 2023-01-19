package londonSafeTravel.client.gui;

import londonSafeTravel.schema.document.poi.PointOfInterest;
import londonSafeTravel.schema.document.poi.PointOfInterestOSM;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

public class POIDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel namePOI;
    private JTextField namePOIText;
    private JLabel id_poi;
    private JTable table1;
    private JScrollPane tablesScrollPane;

    PointOfInterest poi;

    public POIDialog(PointOfInterest poi) {
        this.poi = poi;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setMinimumSize(new Dimension(400, 600));

        id_poi.setText("ID: "+poi.poiID);
        namePOIText.setText(poi.name);

        // Show
        if(poi.getType().equals("OSM-POI")) {
            PointOfInterestOSM osm = (PointOfInterestOSM) poi;
            DefaultTableModel tableData = (DefaultTableModel)table1.getModel();

            tableData.addColumn("key");
            tableData.addColumn("value");

            for(Map.Entry<String, String> tag : osm.tags.entrySet())
                tableData.addRow(new String[]{tag.getKey(), tag.getValue()});
        } else
            tablesScrollPane.setVisible(false);


        buttonOK.addActionListener(e -> onOK());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                e -> onOK(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
        );
    }

    private void onOK() {
        // add your code here
        dispose();
    }

}
