package base;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int id;
    private List<Node> links;

    public Node(int id){
        this.id = id;
        links = new ArrayList<>();
    }

    public void addLink(Node nodeToLink) {
        links.add(nodeToLink);
    }

    public void addLinks(List<Node> nodesToLink) {
        for(Node nodeToLink : nodesToLink){
            addLink(nodeToLink);
        }
    }

    public int getId() {
        return id;
    }

    public List<Node> getLinks() {
        return links;
    }
}
