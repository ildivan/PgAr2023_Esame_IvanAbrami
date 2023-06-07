package module11;

import base.Game;
import base.Node;
import it.kibo.fp.lib.InputData;
import it.kibo.fp.lib.RandomDraws;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.util.List;

import static base.BaseModule.*;

public class Module11 {
    public static void start() throws XMLStreamException, FileNotFoundException {
        int remainingTries = 10;
        List<List<Node>> maps = XMLHandler.getMaps("files/Mappe.xml");

        do{
            boolean gameWon = false;
            int rand = RandomDraws.drawInteger(1,100);
            if(rand <= 33){
                Game game = new Game(getBaseWorld());
                game.start();
                gameWon = game.wasGameWon();
            }else if(rand <= 66){
                Game game = new Game(maps.get(0));
                game.start();
                gameWon = game.wasGameWon();
            }else{
                Game game = new Game(maps.get(1));
                game.start();
                gameWon = game.wasGameWon();
            }

            if (gameWon) {
                System.out.println("Congratulations, you won!");
                boolean tryAgain = InputData.readYesOrNo("Do you want to try again?");
                if(!tryAgain) return;
            } else {
                remainingTries--;
                System.out.printf("You lost! you have %d tries left.\n", remainingTries);
                if(remainingTries>0){
                    pressEnterToRestart();
                }else{
                    pressEnterToContinue();
                }
            }
        }while(remainingTries > 0);
    }
}
