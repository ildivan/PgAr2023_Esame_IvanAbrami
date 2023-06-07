package module11;

import base.Node;

import javax.xml.stream.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that handles reading and writing with XMLHandler files.
 */
public class XMLHandler {
    private static final XMLInputFactory xmlif = XMLInputFactory.newInstance();

    /**
     * Reads cities and connections from specified filepath
     */
    public static List<List<Node>> getMaps(String filepath) throws XMLStreamException, FileNotFoundException {
        List<List<Node>> nodeMap = getNodes(filepath);
        System.out.println(nodeMap);

        List<Map<String, List<String>>> links = readLinksBetweenNodes(filepath);
        for (int i = 0; i < links.size(); i++) {
            var mapLinks = links.get(i);
            for (String id : mapLinks.keySet()) {
                Node node = getNodeFromId(nodeMap.get(i), Integer.parseInt(id));
                for (String linkedNodeId : mapLinks.get(id)) {
                    node.addLink(getNodeFromId(nodeMap.get(i) , Integer.parseInt(linkedNodeId)));
                    getNodeFromId(nodeMap.get(i) , Integer.parseInt(linkedNodeId)).addLink(node);
                }
            }
        }

        return nodeMap;
    }

    /**
     * @param cities A list of cities
     * @param id     The id of the Node to find
     * @return The Node from the list given its id
     */
    public static Node getNodeFromId(List<Node> cities, int id) {
        for (Node Node : cities) {
            if (Node.getId() == id) {
                return Node;
            }
        }
        return null;
    }

    /**
     * Gets the connections between cities as a map that maps ids to lists of ids,
     * Each id is mapped to a list of the ids the Node is connected to.
     *
     * @param filepath Where to look for the links.
     * @return The map containing all connections.
     */
    public static List<Map<String, List<String>>> readLinksBetweenNodes(String filepath)
            throws FileNotFoundException, XMLStreamException {
        XMLStreamReader xmlr;
        xmlr = xmlif.createXMLStreamReader(filepath,
                new FileInputStream(filepath));

        List<Map<String, List<String>>> links = new ArrayList<>();

        String lastId = "1";
        String lastTag = "";
        while (xmlr.hasNext()) {
            if (xmlr.getEventType() == XMLStreamConstants.START_ELEMENT) {
                lastTag = xmlr.getLocalName();

                if(lastTag.equals("MAPPA")){
                    links.add(new HashMap<>());
                }

                for (int i = 0; i < xmlr.getAttributeCount(); i++) {
                    if (xmlr.getAttributeLocalName(i).equals("NODO")) {
                        lastId = xmlr.getAttributeValue(i);
                        links.get(links.size()-1).put(lastId, new ArrayList<>());
                    }
                }
            }else if(xmlr.getEventType() == XMLStreamConstants.CHARACTERS) {
                if (xmlr.getText().trim().length() > 0) {
                    if (lastTag.equals("COLLEGAMENTO")){
                        links.get(links.size()-1).get(lastId).add(xmlr.getText().trim());
                    }
                }
            }
            xmlr.next();
        }

        return links;
    }


    public static List<List<Node>> getNodes(String filepath)
            throws FileNotFoundException, XMLStreamException{
        List<List<Node>> worlds = new ArrayList<>();

        XMLStreamReader xmlr;
        xmlr = xmlif.createXMLStreamReader(filepath,
                new FileInputStream(filepath));

        String lastTag = "";
        String lastId = "";
        while(xmlr.hasNext()) {
            switch (xmlr.getEventType()) {
                case XMLStreamConstants.START_ELEMENT -> {
                    lastTag = xmlr.getLocalName();
                    if (lastTag.equals("MAPPA")){
                        worlds.add(new ArrayList<>());
                    }

                    for (int i = 0; i < xmlr.getAttributeCount(); i++) {
                        if(xmlr.getAttributeLocalName(i).equals("NODO")){
                            lastId = xmlr.getAttributeValue(i);
                        }
                    }
                }
                case XMLStreamConstants.END_ELEMENT -> {
                    if (xmlr.getLocalName().equals("NODO")) {
                        worlds.get(worlds.size()-1).add(new Node(Integer.parseInt(lastId)));
                    }
                }
            }
            xmlr.next();
        }

        return worlds;
    }
    
}

