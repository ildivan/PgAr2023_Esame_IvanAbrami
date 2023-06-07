package module3;

import base.Entity;
import base.Game;
import base.Node;
import base.PathFinder;
import it.kibo.fp.lib.InputData;

import java.util.*;

public class GameWithPathAdvice extends Game {

    public GameWithPathAdvice(List<Node> world) {
        super(world);
    }

    @Override
    public void start() {
        Node start = world.get(0);
        Node end = world.get(world.size()-1);
        Node current = start;
        List<Node> visitedNodes = new ArrayList<>();
        visitedNodes.add(start);

        while(true){
            if(current != start){
                Entity monsterFound = monsters.get(current);
                if(monsterFound == null) {
                    System.out.println("Nessun mostro in vista!\n");
                    changePlayerStatistic();
                }else{
                    fightMonster(monsterFound);
                }
            }

            if(current == end || !player.isAlive()){
                return;
            }

            assert current != null;
            List<Node> possibleConnections = getValidConnections(current.getLinks(),visitedNodes,end);

            System.out.println("Dove desideri andare? \n");

            if(possibleConnections.get(0) != end){
                System.out.printf("Nodo %d (consigliato) \n", possibleConnections.get(0).getId());
            }else{
                System.out.printf("Nodo %d (nodo finale **consigliato**)\n", possibleConnections.get(0).getId());
            }

            for(int i = 1; i < possibleConnections.size(); i++) {
                if(possibleConnections.get(i) != end){
                    System.out.printf("Nodo %d\n", possibleConnections.get(i).getId());
                }else{
                    System.out.printf("Nodo %d (nodo finale)\n", possibleConnections.get(i).getId());
                }
            }

            int chosenId;
            do{
                chosenId = InputData.readInteger("Inserisci l'id del nodo: ");
            }while(getNodeFromId(possibleConnections, chosenId) == null);

            current = getNodeFromId(possibleConnections, chosenId);
        }
    }

    @Override
    protected List<Node> getValidConnections
            (List<Node> allConnections, List<Node> invalidConnections, Node end){
        Map<Node,Boolean> arePathsValid = new HashMap<>();
        Map<Node,Integer> pathLengths = new HashMap<>();

        for (Node possibleNextNode : allConnections) {
            PathFinder pf = new PathFinder(possibleNextNode, end, invalidConnections);
            arePathsValid.put(possibleNextNode,pf.isPathPossible());
            pathLengths.put(possibleNextNode, pf.getPathLength());
        }

        List<Node> validConnections = new ArrayList<>(allConnections.stream()
                .filter(x -> !invalidConnections.contains(x) && arePathsValid.get(x))
                .toList());
        validConnections.sort(Comparator.comparing(pathLengths::get));

        return validConnections;
    }
}


