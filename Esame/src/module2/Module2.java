package module2;

import base.Game;
import base.Node;
import it.kibo.fp.lib.InputData;
import it.kibo.fp.lib.Menu;
import module11.XMLHandler;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.BaseModule.getBaseWorld;
import static base.BaseModule.pressEnterToContinue;

public class Module2 {
    public static void start() throws XMLStreamException, FileNotFoundException {
        int remainingTries = 10;
        List<List<Node>> maps = XMLHandler.getMaps("files/Mappe.xml");
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

            System.out.printf("\n\n\nYou have a total of %d points\n", getTotalPoints(beatenMaps));

            int worldChosen = worldSelectionMenu.choose();
            beatenMaps.putIfAbsent(worldChosen, false);

            switch(worldChosen){
                case 1 -> {
                    Game game = new Game(getBaseWorld());
                    game.start();
                    gameWon = game.wasGameWon();
                }
                case 2 -> {
                    Game game = new Game(maps.get(0));
                    game.start();
                    gameWon = game.wasGameWon();
                }
                case 3 -> {
                    Game game = new Game(maps.get(1));
                    game.start();
                    gameWon = game.wasGameWon();
                }
            }

            if (gameWon) {
                System.out.println("Congratulations, you won!");
                if(!beatenMaps.get(worldChosen)){
                    System.out.printf("You gained %d point!\n",worldChosen);
                    beatenMaps.put(worldChosen,true);
                }
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

    public static int getTotalPoints(Map<Integer, Boolean> beatenMaps) {
        int total = 0;
        for (Integer beatenWorld : beatenMaps.keySet()){
            if(beatenMaps.get(beatenWorld)){
                total += beatenWorld;
            }
        }

        return total;
    }
}
