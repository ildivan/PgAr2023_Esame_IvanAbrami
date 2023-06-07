package base;

import it.kibo.fp.lib.InputData;

import java.util.ArrayList;
import java.util.List;

public class BaseModule {
    public static void start() {
        int remainingTries = 10;
        int points = 0;

        do{
            Game game = new Game(getBaseWorld());
            game.start();

            if (game.wasGameWon()) {
                System.out.println("Congratulations, you won!");
                if(points == 0){
                    System.out.println("You gained 1 point!");
                    points += 1;
                }
                boolean tryAgain = InputData.readYesOrNo("Do you want to try again?");
                if(!tryAgain) return;
            } else {
                remainingTries--;
                System.out.printf("You lost! you have %d tries left.\n", remainingTries);
                pressEnterToRestart();
            }
        }while(remainingTries > 0);
    }

    public static List<Node> getBaseWorld(){
        Node start = new Node(0);
        Node node2 = new Node(1);
        Node node3 = new Node(2);
        Node end = new Node (3);

        start.addLink(node2);
        start.addLink(node3);
        node2.addLink(end);
        node3.addLink(end);

        List<Node> baseWorld = new ArrayList<>();
        baseWorld.add(start);
        baseWorld.add(node2);
        baseWorld.add(node3);
        baseWorld.add(end);

        return baseWorld;
    }

    // Pauses the program until user press the return key.
    public static void pressEnterToContinue() {
        System.out.print("\n\nPress Enter to continue...");
        try {
            int ignored = System.in.read();
        } catch (Exception ignored) {}
    }

    // Pauses the program until user press the return key.
    public static void pressEnterToRestart() {
        System.out.print("\n\nPress Enter to restart...");
        try {
            int ignored = System.in.read();
        } catch (Exception ignored) {}
    }
}
