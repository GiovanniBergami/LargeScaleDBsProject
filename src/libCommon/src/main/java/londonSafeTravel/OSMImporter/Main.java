package londonSafeTravel.OSMImporter;

import java.io.File;
import java.util.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import londonSafeTravel.schema.graph.Point;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class Main {
    public static void main(String[] args) {
        try{
            File inputFile=new File("greater-london-latest.osm");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nList = doc.getElementsByTagName("node");  //Non so se va bene
            List points = new ArrayList();
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("Point id: " + eElement.getAttribute("id"));
                    System.out.println("Latitude: " + eElement.getAttribute("lat"));
                    System.out.println("Longitude: " + eElement.getAttribute("lon"));
                    int id=Integer.parseInt(eElement.getAttribute("id"));
                    int lat=Integer.parseInt(eElement.getAttribute("lat"));
                    int lon=Integer.parseInt(eElement.getAttribute("lon"));
                    Point p=new Point(id,lat,lon);
                    points.add(p);
                    }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}