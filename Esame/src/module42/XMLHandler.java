package module42;

import CodiceFiscale.person.Person;
import CodiceFiscale.person.Sex;

import javax.xml.stream.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XML {
    private static final XMLInputFactory xmlif = XMLInputFactory.newInstance();
    private static final XMLOutputFactory xmlof = XMLOutputFactory.newInstance();

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

    //Search for fiscal codes in the given filepath and returns them as a list of strings.
    public static List<String> getFiscalCodes(String filepath)
            throws FileNotFoundException, XMLStreamException {
        return readFromFile(filepath,"codice").get("codice");
    }

    //Search for people data in the given filepath and returns them as a list of Person instances.
    public static List<Person> getPeople(String filepath)
            throws FileNotFoundException, XMLStreamException {
        //The list of tags to search for in the file.
        String[] tags = {"nome","cognome","sesso","comune_nascita","data_nascita"};
        //Maps every tag to the list of strings found in the file inside that tag.
        Map<String, List<String>> peopleData = readFromFile(filepath, tags);

        ArrayList<Person> people = new ArrayList<>();
        List<String> names = peopleData.get(tags[0]);
        List<String> surnames = peopleData.get(tags[1]);
        List<String> sexes = peopleData.get(tags[2]);
        List<String> citiesOfBirth = peopleData.get(tags[3]);
        List<String> datesOfBirth = peopleData.get(tags[4]);

        for (int i = 0; i < names.size(); i++) {
            //An exception will be thrown in the constructor if the sex is null
            Sex sex = (sexes.get(i).equals("M") ? Sex.M : (sexes.get(i).equals("F") ? Sex.F : null));
            people.add(new Person(
                    names.get(i), surnames.get(i), sex, citiesOfBirth.get(i), datesOfBirth.get(i))
            );
        }
        return people;
    }

    //Generic method to read from a XML file given the file path
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

    //Writes to file the people with their fiscal codes,
    //then writes the list of invalid fiscal codes
    //and the list of unmatched fiscal codes.
    public static void writeOutput(List<Person> people,List<String> invalid, List<String> unmatched, String filepath){
        XMLStreamWriter xmlw;
        try {
            xmlw = xmlof.createXMLStreamWriter(new FileOutputStream(filepath), "utf-8");
            xmlw.writeStartDocument("utf-8", "1.0");
            xmlw.writeStartElement("output");

            writePeopleSection(xmlw,people);//Writes the section 'persone'
            writeCodesSection(xmlw,invalid,unmatched);//Writes the section 'codici'

            xmlw.writeEndElement();
            xmlw.writeEndDocument();
            xmlw.flush();
            xmlw.close();
        } catch (Exception e) {
            System.out.println("Errore nella scrittura");
        }
    }

    private static void writePeopleSection(XMLStreamWriter xmlw, List<Person> people) throws XMLStreamException {
        xmlw.writeStartElement("persone");
        xmlw.writeAttribute("numero",Integer.toString(people.size()));
        for (int i = 0; i < people.size(); i++) {
            writePerson(xmlw,people.get(i),i);
        }
        xmlw.writeEndElement();
    }

    //Writes to file the XML structure of a single person
    private static void writePerson(XMLStreamWriter xmlw, Person person, int id) throws XMLStreamException {
        xmlw.writeStartElement("persona");
        xmlw.writeAttribute("id", Integer.toString(id));

        writeTag(xmlw,"nome",person.getName());
        writeTag(xmlw,"cognome",person.getSurname());
        writeTag(xmlw,"sesso",person.getSex().toString());
        writeTag(xmlw,"comune_nascita",person.getCityOfBirth());
        writeTag(xmlw,"data_nascita",person.getDateOfBirth());
        writeTag(xmlw,"codice_fiscale",person.getFiscalCode().getCode());

        xmlw.writeEndElement();
    }

    private static void writeCodesSection(XMLStreamWriter xmlw, List<String> invalid, List<String> unmatched)
            throws XMLStreamException {
        xmlw.writeStartElement("codici");
        writeFiscalCodeSeries(xmlw,invalid, "invalidi");
        writeFiscalCodeSeries(xmlw,unmatched, "spaiati");
        xmlw.writeEndElement();
    }

    //Writes to file the list of fiscal code's XML structure
    private static void writeFiscalCodeSeries(XMLStreamWriter xmlw, List<String> series, String name)
            throws XMLStreamException {
        xmlw.writeStartElement(name);
        xmlw.writeAttribute("numero",Integer.toString(series.size()));
        for (String code : series){
            writeTag(xmlw,"codice",code);
        }
        xmlw.writeEndElement();
    }

    //Writes xml tag that does not have any sub-tags or any attributes.
    private static void writeTag(XMLStreamWriter xmlw, String name, String value) throws XMLStreamException {
        xmlw.writeStartElement(name);
        xmlw.writeCharacters(value);
        xmlw.writeEndElement();
    }
}
