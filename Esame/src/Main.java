import base.Game;
import base.Node;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
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

        Game game = new Game(baseWorld);
        game.start();
    }
}