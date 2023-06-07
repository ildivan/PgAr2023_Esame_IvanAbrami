package base;

import java.util.*;

/**
 * Class that implements the A* algorithm.
 */
public class PathFinder {
    private Node start;
    private Node end;
    private List<Node> nodesToIgnore;
    private Map<Node, Double> gCosts;
    private Map<Node, Node> previousCities;
    private boolean calculated;

    /**
     * Constructor for the pathfinder.
     *
     * @param start The start node
     * @param end   The end node
     */
    public PathFinder(Node start, Node end, List<Node> nodesToIgnore) {
        this.start = start;
        this.end = end;
        this.nodesToIgnore = nodesToIgnore;
        gCosts = new HashMap<>();
        previousCities = new HashMap<>();
        calculated = false;
    }

    /**
     * Travels through the graph and finds the optimal path
     */
    private void calculateOptimalRoute() {
        List<Node> openSet = new ArrayList<>(); // List of nodes to process
        List<Node> closedSet = new ArrayList<>(nodesToIgnore); // List of nodes already processed

        // Sets start node information
        openSet.add(start);
        gCosts.put(start, 0.0);

        while (!openSet.isEmpty()) {

            Node currentNode = openSet.get(0);
            // Gets the node in the openSet with the lowest g cost
            for (Node Node : openSet) {
                if (gCosts.get(Node) < gCosts.get(currentNode)) {
                    currentNode = Node;
                }
            }

            // Update openSet and closedSet for the currentNode, as currentNode is now considered processed.
            openSet.remove(currentNode);
            closedSet.add(currentNode);


            for (Node neighbor : currentNode.getLinks()) {
                if (closedSet.contains(neighbor)) // If a neighbor is in closedSet it should not be considered
                    continue;

                double neighborGCost = gCosts.get(currentNode) + 1;

                // Checks if the neighbor has a lower gCost than it previously had,
                // or if the node was not previously checked, thus not having a g cost.
                if (!openSet.contains(neighbor) || neighborGCost < gCosts.get(neighbor)) {
                    gCosts.put(neighbor, neighborGCost);
                    previousCities.put(neighbor, currentNode);

                    // If the node was not previously checked, the node is put in the openSet
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
    }


    /**
     * Returns the optimal route as a stack, with the start node on top.
     */
    public List<Node> getOptimalRoute() {
        if(!calculated){
            calculateOptimalRoute();
        }

        if(previousCities.get(end) == null){
            List<Node> specialRoute = new ArrayList<>();
            if(start == end){
                specialRoute.add(end);
            }
            return specialRoute;
        }

        List<Node> route = new ArrayList<>(); // Stack representing a path

        Node current = end;
        while (current != start) {
            route.add(current);
            current = previousCities.get(current);
        }
        route.add(start);

        Collections.reverse(route);
        return route;
    }

    /**
     * Returns length of the path, the number of cities to go from start to end.
     */
    public int getPathLength() {
        return getOptimalRoute().size();
    }

    public boolean isPathPossible() {
        return getPathLength() != 0;
    }
}
