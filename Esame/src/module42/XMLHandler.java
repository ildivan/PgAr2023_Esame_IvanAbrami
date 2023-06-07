package module42;

import base.Node;
import module42.person.Person;
import module42.person.Sex;

import javax.xml.stream.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLHandler {
    private static final XMLInputFactory xmlif = XMLInputFactory.newInstance();

    //Search for city names and codes in the given filepath and returns them as a map.
    public static Map<String, String> getCityCodes(String filepath)
            throws FileNotFoundException, XMLStreamException {
        Map<String,List<String>> cityData = readFromFile(filepath,"nome","codice");
        List<String> cityNames = cityData.get("nome");
        List<String> cityCodes = cityData.get("codice");

        Map<String, String> cityCodesMap = new HashMap<>();
        for (int i = 0; i < cityNames.size(); i++) {
            cityCodesMap.put(cityNames.get(i), cityCodes.get(i));
        }

        return cityCodesMap;
    }


    //Search for people data in the given filepath and returns them as a list of Person instances.
    public static List<Person> getPeople(String filepath)
            throws FileNotFoundException, XMLStreamException {
        //The list of tags to search for in the file.
        String[] tags = {"nome","cognome","sesso","comune_nascita","data_nascita","codice_fiscale","data_scadenza_id"};
        //Maps every tag to the list of strings found in the file inside that tag.
        Map<String, List<String>> peopleData = readFromFile(filepath, tags);

        ArrayList<Person> people = new ArrayList<>();
        List<String> names = peopleData.get(tags[0]);
        List<String> surnames = peopleData.get(tags[1]);
        List<String> sexes = peopleData.get(tags[2]);
        List<String> citiesOfBirth = peopleData.get(tags[3]);
        List<String> datesOfBirth = peopleData.get(tags[4]);
        List<String> fiscalCodes = peopleData.get(tags[5]);
        List<String> datesOfExpiration = peopleData.get(tags[6]);

        for (int i = 0; i < names.size(); i++) {
            //An exception will be thrown in the constructor if the sex is null
            Sex sex = (sexes.get(i).equals("M") ? Sex.M : (sexes.get(i).equals("F") ? Sex.F : null));
            people.add(new Person(
                    names.get(i), surnames.get(i), sex,
                    citiesOfBirth.get(i), datesOfBirth.get(i),
                    fiscalCodes.get(i), datesOfExpiration.get(i))
            );
        }
        return people;
    }

    //Generic method to read from a XMLHandler file given the file path
    // and the list of tags containing data to search for.
    public static Map<String,List<String>> readFromFile(String filepath,String...tagNames)
            throws FileNotFoundException, XMLStreamException{
        Map<String,List<String>> data = new HashMap<>();
        //Initialize all tags with empty array lists.
        for (String tag : tagNames) {
            data.put(tag,new ArrayList<>());
        }

        XMLStreamReader xmlr;
        xmlr = xmlif.createXMLStreamReader(filepath,
                new FileInputStream(filepath));

        String lastTag = "";
        while(xmlr.hasNext()) {
            switch (xmlr.getEventType()) {
                case XMLStreamConstants.START_ELEMENT -> lastTag = xmlr.getLocalName();
                case XMLStreamConstants.CHARACTERS -> {
                    if (xmlr.getText().trim().length() > 0) {
                        for (String tag : tagNames) {
                            if (tag.equals(lastTag)){ //Checks if the text found is inside one of the searched tags
                                data.get(tag).add(xmlr.getText()); //Adds the text to the list corresponding to the tag
                                break;
                            }
                        }
                    }
                }
            }
            xmlr.next();
        }

        return data;
    }

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
