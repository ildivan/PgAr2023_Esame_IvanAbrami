package base;

import it.kibo.fp.lib.InputData;
import it.kibo.fp.lib.RandomDraws;

import java.util.*;

public class Game {
    protected Player player;
    protected List<Node> world;
    protected Map<Node, Entity> monsters;

    private boolean gameWon;

    public Game(List<Node> world) {
        this.world = world;
        player = new Player();
        monsters = new HashMap<>();

        //Around a third of the nodes will have a monster
        for (int i = 1; i < world.size(); i++) {
            int rand = RandomDraws.drawInteger(1,100);
            if(rand <= 33){
                monsters.put(world.get(i), new Monster());
            }
        }

        //The last node will have Cammo
        monsters.put(world.get(world.size()-1), new Cammo());

        gameWon = false;
    }

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

            current = getNodeFromId(possibleConnections, chosenId);
        }
    }

    protected void changePlayerStatistic() {
        Random random = new Random();
        if(random.nextBoolean()){
            player.modifyHealth(-5,10);
            System.out.printf("La vita del player è cambiata a %d\n", player.getHealth());
        }else{
            player.modifyAttack(-3,3);
            System.out.printf("L'attacco del player è cambiato a %d\n", player.getAttack());
        }
    }

    protected void fightMonster(Entity monsterFound) {
        if(monsterFound instanceof Monster) {
            System.out.println("Trovato un mostro comune!\n");
            Entity.combat(player,monsterFound);
        }else if(monsterFound instanceof Cammo) {
            System.out.println("Hai raggiunto Cammo, il boss finale!\n");
            Entity.combat(player,monsterFound);
            if(player.isAlive()) gameWon = true;
        }
    }

    protected List<Node> getValidConnections
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

    protected static Node getNodeFromId(List<Node> nodes, int id) {
        for (Node node : nodes){
            if(node.getId() == id)
                return node;
        }
        return null;
    }

    public boolean wasGameWon(){
        return gameWon;
    }
}
