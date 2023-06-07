package module42;



import base.Game;
import base.Node;
import it.kibo.fp.lib.InputData;
import it.kibo.fp.lib.Menu;
import module3.GameWithPathAdvice;
import module42.person.Person;

import javax.xml.stream.XMLStreamException;
import java.awt.print.Paper;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.BaseModule.getBaseWorld;
import static base.BaseModule.pressEnterToContinue;

public class Module42 {
    public static void start() throws XMLStreamException, FileNotFoundException {
            int remainingTries = 10;
        List<List<Node>> maps = XMLHandler.getMaps("files/Mappe.xml");
        List<Person> people = XMLHandler.getPeople("files/Papers Please/PersoneID.xml");
        Map<String,String> cityCodes = XMLHandler.getCityCodes("files/Papers Please/Comuni.xml");

            Map<Integer, Boolean> beatenMaps = new HashMap<>();

            do{
                String worldSelectionMenuTitle = "Select a Map";
                String[] entries = {
                        "Base world",
                        "World 2",
                        "World 3"
                };

                Menu worldSelectionMenu
                        = new Menu(worldSelectionMenuTitle,entries, false, true , true);

                boolean gameWon = false;

                int worldChosen = worldSelectionMenu.choose();

                switch(worldChosen){
                    case 1 -> {
                        PapersPleaseGame game = new PapersPleaseGame(getBaseWorld(),people,cityCodes);
                        game.start();
                        gameWon = game.wasGameWon();
                    }
                    case 2 -> {
                        PapersPleaseGame game = new PapersPleaseGame(maps.get(0),people,cityCodes);
                        game.start();
                        gameWon = game.wasGameWon();
                    }
                    case 3 -> {
                        PapersPleaseGame game = new PapersPleaseGame(maps.get(1),people,cityCodes);
                        game.start();
                        gameWon = game.wasGameWon();
                    }
                }

                if (gameWon) {
                    System.out.println("Congratulations, you won!");
                    boolean tryAgain = InputData.readYesOrNo("Do you want to try again?");
                    if(!tryAgain) return;
                } else {
                    remainingTries--;
                    System.out.printf("You lost! you have %d tries left.\n", remainingTries);
                    if(remainingTries>0){
                        boolean tryAgain = InputData.readYesOrNo("Do you want to try again?");
                        if(!tryAgain) return;
                    }else{
                        pressEnterToContinue();
                    }
                }
            }while(remainingTries > 0);
        }
}

