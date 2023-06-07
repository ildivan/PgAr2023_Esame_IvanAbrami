package module42;

import base.*;
import it.kibo.fp.lib.InputData;
import it.kibo.fp.lib.RandomDraws;
import module42.fiscalcode.FiscalCodeChecker;
import module42.person.Person;

import java.util.*;

public class PapersPleaseGame {
    private int currentMoney;
    private int moneyToGet;
    private List<Node> world;
    private List<Person> people;
    private Map<String,String> cityCodes;
    private boolean gameWon;

    public PapersPleaseGame(List<Node> world, List<Person> people, Map<String, String> cityCodes) {
        this.world = world;
        this.people = people;
        this.cityCodes = cityCodes;
        gameWon = false;
    }

    public void start() {
        Node start = world.get(0);
        Node end = world.get(world.size()-1);
        Node current = start;

        int currentYear = 2023;
        int currentMonth = 6;
        int currentDay = 7;

        currentMoney = 1100;
        moneyToGet = 2200;

        List<Node> visitedNodes = new ArrayList<>();
        visitedNodes.add(start);
        FiscalCodeChecker checker = new FiscalCodeChecker(cityCodes);

        while(true){
            if(current != start){
                if(current == end){
                    System.out.printf("Devi pagare %d sbleuri per vincere!\n",moneyToGet);
                    if(currentMoney >= moneyToGet){
                        System.out.println("Per fortuna li avevi, hai vinto!");
                        gameWon = true;
                    }else{
                        System.out.println("Non hai abbastanza soldi, hai perso!");
                    }
                    BaseModule.pressEnterToContinue();
                    return;
                }

                Person personFound = people.get(RandomDraws.drawInteger(0,people.size()-1));
                people.remove(personFound);

                boolean isCodeValid = checker.isValid(personFound.getFiscalCode());
                boolean isIdExpired = !isIdValid(String.format("%d-%d-%d",currentYear,currentMonth,currentDay),personFound.getDateOfExpiration());
                boolean isDataCorrect = !isIdExpired && isCodeValid;

                System.out.println("____________________________________________________\n");
                System.out.printf("Oggi è il %d-%d-%d\n",currentYear,currentMonth,currentDay);
                System.out.printf("Hai %d sbleuri al momento.\n", currentMoney);
                System.out.println("Hai incontrato una nuova persona da controllare!\n");

                printPerson(personFound);

                boolean areInformationsCorrectAccordingToUser = InputData.readYesOrNo("I dati sono corretti?");

                if(areInformationsCorrectAccordingToUser){
                    if(!isDataCorrect){
                        System.out.println("I dati non erano corretti, riceverai 300 sbleuri di multa!");
                        currentMoney -= 300;
                    }else{
                        System.out.println("Avevi ragione, erano corretti! Puoi Continuare!");
                    }
                }else{
                    if(!isDataCorrect){
                        System.out.println("Avevi ragione non sono corretti!");
                        int randMoney = RandomDraws.drawInteger(250,500);
                        System.out.printf("La persona tenta di corromperti con %d sbleuri.",randMoney);
                        boolean acceptedMoney = InputData.readYesOrNo("Accetti di essere corrotto?");

                        if(acceptedMoney){
                            int randChance = RandomDraws.drawInteger(1,100);
                            if(randChance <= 20){
                                System.out.println("Era un poliziotto in borghese! Hai perso");
                                BaseModule.pressEnterToContinue();
                                return;
                            }else{
                                System.out.println("Hai ricevuto i soldi.");
                            }
                        }

                    }else{
                        System.out.println("Avevi ragione, erano corretti! Puoi Continuare!");
                    }
                }

                if(doesDayExistsInMonth(currentDay+1,currentMonth)){
                    currentDay++;
                }else{
                    if(currentMonth < 12){
                        currentDay = 1;
                        currentMonth++;
                    }else{
                        currentDay = 1;
                        currentMonth = 1;
                        currentYear++;
                    }
                }
            }

            assert current != null;
            List<Node> possibleConnections = getValidConnections(current.getLinks(),visitedNodes,end);

            System.out.println("Dove desideri andare domani? \n");
            for(Node possibleNextNode : possibleConnections) {
                if(possibleNextNode != end){
                    System.out.printf("Nodo %d\n", possibleNextNode.getId());
                }else{
                    System.out.printf("Nodo %d (nodo finale)\n", possibleNextNode.getId());
                }
            }

            int chosenId;
            do{
                chosenId = InputData.readInteger("Inserisci l'id del nodo: ");
            }while(getNodeFromId(possibleConnections, chosenId) == null);

            visitedNodes.add(current);
            current = getNodeFromId(possibleConnections, chosenId);
        }
    }

    private void printPerson(Person personFound) {
        System.out.printf("Nome: %s\n",personFound.getName());
        System.out.printf("Cognome: %s\n",personFound.getSurname());
        System.out.printf("Sesso: %s\n",personFound.getSex());
        System.out.printf("Data di nascita: %s\n",personFound.getDateOfBirth());
        System.out.printf("Codice fiscale: %s\n",personFound.getFiscalCode());
        System.out.printf("Data di scadenza id: %s\n",personFound.getDateOfExpiration());
    }


    private List<Node> getValidConnections
            (List<Node> allConnections, List<Node> invalidConnections, Node end){
        Map<Node,Boolean> arePathsValid = new HashMap<>();
        for (Node possibleNextNode : allConnections) {
            PathFinder pf = new PathFinder(possibleNextNode, end, invalidConnections);
            arePathsValid.put(possibleNextNode,pf.isPathPossible());
        }
        return allConnections.stream()
                .filter(x -> !invalidConnections.contains(x) && arePathsValid.get(x))
                .toList();
    }

    private static Node getNodeFromId(List<Node> nodes, int id) {
        for (Node node : nodes){
            if(node.getId() == id)
                return node;
        }
        return null;
    }

    public boolean wasGameWon(){
        return gameWon;
    }

    //Checks if the day of birth exists in the month of birth,
    //since the year of birth is not determined uniquely, february is considered
    //to always have 28 days.
    private boolean doesDayExistsInMonth(int day,int month) {
        int daysInTheMonth; //The number of days in the month of birth.
        switch (month) {
            case 1, 3, 5, 7,10, 8, 12 -> daysInTheMonth = 31;
            case 4, 6, 9,  11 -> daysInTheMonth = 30;
            case 2 -> daysInTheMonth = 28;
            default -> daysInTheMonth = 0;
        }

        return day <= daysInTheMonth;
    }

    private boolean isIdValid(String todayDate, String expirationDate){
        String[] dividedTodayDate = todayDate.split("-");
        int currentYear = Integer.parseInt(dividedTodayDate[0]);
        int currentMonth = Integer.parseInt(dividedTodayDate[1]);
        int currentDay = Integer.parseInt(dividedTodayDate[2]);

        String[] dividedExpirationDate = todayDate.split("-");
        int expirationYear = Integer.parseInt(dividedExpirationDate[0]);
        int expirationMonth = Integer.parseInt(dividedExpirationDate[1]);
        int expirationDay = Integer.parseInt(dividedExpirationDate[2]);

        if(expirationYear > currentYear){
            return true;
        }else if (expirationYear == currentYear){
            if(expirationMonth > currentMonth){
                return true;
            }else if(expirationMonth == currentMonth) {
                return expirationDay >= currentDay;
            }
            return false;
        }
        return false;
    }
}
