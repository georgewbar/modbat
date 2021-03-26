package modbat.graph;

import modbat.graph.testrequirements.AbstractTestRequirement;
import modbat.graph.testrequirements.EdgePairTestRequirement;
import modbat.graph.testrequirements.EdgeTestRequirement;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

//adapted from https://algorithms.tutorialhorizon.com/weighted-graph-implementation-java/
public class Graph<NT, ET> {
    private Node<NT> root;
    private final Map<Node<NT>, List<Edge<NT, ET>>> adjacencyMap;

    private Map<EdgeTestRequirement<NT, ET>, EdgeTestRequirement<NT, ET>> edgesReqs = new HashMap<>();
    private Map<EdgePairTestRequirement<NT, ET>, EdgePairTestRequirement<NT, ET>> edgePairsReqs = new HashMap<>();

    // log
    private PrintStream out;
    private PrintStream err;

    public Graph() { this(null); }

    public Graph(Node<NT> root) {
        this.root = root;
        this.adjacencyMap = new HashMap<>();
        this.out = null;
        this.err = null;
    }

    public void setRoot(Node<NT> root) {
        this.root = root;
    }

    //for directed graph
    public void addEdge(Node<NT> source, Node<NT> destination) {
        addEdge(source, destination, null);
    }

    public void addEdge(Node<NT> source, Node<NT> destination, ET data) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("either source or destination is null");
        }

        Edge<NT, ET> edge = new Edge<>(source, destination, data);

        if (adjacencyMap.containsKey(source)) {
            adjacencyMap.get(source).add(edge);
        } else {
            LinkedList<Edge<NT, ET>> newList = new LinkedList<>();
            newList.add(edge);
            adjacencyMap.put(source, newList);
        }

        // create a new empty list for destination node
        if (!adjacencyMap.containsKey(destination)) {
            adjacencyMap.put(destination, new LinkedList<>());
        }
    }

    public Node<NT> getRoot() {
        return root;
    }

    public boolean isLeaf(Node<NT> node) {
        if (adjacencyMap.get(node) == null) {
            throw new IllegalArgumentException("node does not exist");
        }

        return adjacencyMap.get(node).isEmpty();
    }

    public Set<Node<NT>> getAllNodes() {
        return adjacencyMap.keySet();
    }

    public List<Edge<NT, ET>> getAllEdges() {
        return adjacencyMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<Edge<NT, ET>> outgoingEdges(Node<NT> from) {
        return adjacencyMap.get(from);
    }

    public List<Edge<NT, ET>> incomingEdges(Node<NT> to) {
        return getAllEdges().stream().filter(edge -> edge.getDestination().equals(to)).collect(Collectors.toList());
    }

    /**
     * Update edge-pair requirements (this includes even individual edges
     * as well. This is done to make edge-pairs test requirements subsume edges test
     * requirements).
     * <p>
     * Note that the edge pairs that already exist are not changed. i.e., if the edge-pairs
     * were covered, this will not change, and if the edge-pairs were not covered, they will
     * remain uncovered. Only new edge-pairs can be added, but the old ones remain the same.
     */
    private void updateEdgePairRequirements() {
        Map<EdgeTestRequirement<NT, ET>, EdgeTestRequirement<NT, ET>> oldReqs = new HashMap<>(edgesReqs);
        // add all edges to to edgesTestRequirements

        // TODO: delete this part later
        // sanity check
        for (Edge<NT, ET> edge : getAllEdges()) {
            EdgeTestRequirement<NT, ET> edgeReq = new EdgeTestRequirement<>(edge);
            edgesReqs.put(edgeReq, edgeReq);
        }

//        if (!edgesReqs.equals(oldReqs)) {
//            System.out.println("The old edge requirements and the new edge requirements are not equal.");
//            throw new IllegalStateException("The old edge requirements and the new edge requirements are not equal.");
//        }

        // add all (incoming, outgoing) pairs in edgePairsTestRequirements
        for (Node<NT> node : getAllNodes()) {
            for (Edge<NT, ET> incoming : incomingEdges(node)) {
                for (Edge<NT, ET> outgoing : outgoingEdges(node)) {
                    EdgePairTestRequirement<NT, ET> edgePair = new EdgePairTestRequirement<>(incoming, outgoing);
                    edgePairsReqs.putIfAbsent(edgePair, edgePair);
                }
            }
        }
    }

    /**
     * Update all test requirements of type:
     * <p>
     * - edge-pair (this includes even individual edges
     * as well. This is done to make edge-pairs test requirements subsume edges test
     * requirements).
     * <p>
     * Update edge-pair requirements. Note that the edge pairs that already exist are
     * not changed. i.e., if the edge-pairs were covered, this will not change, and
     * if the edge-pairs were not covered, they will remain uncovered. Only new edge-pairs
     * can be added, but the old ones remain the same.
     */
    public void updateTestRequirements() {
        updateEdgePairRequirements();
    }

    /**
     * Cover edge-pair test requirements that path covers.
     */
    private void coverEdgePairs(List<Edge<NT, ET>> path) {
        printToOut("path: ");
        printToOut(path.toString()); // todo: comment later
        // cover all edges - This should be printed.
//        printToOut("we are here");
        for (int i = 0; i < path.size(); i++) {
            EdgeTestRequirement<NT, ET> edge = edgesReqs.get(new EdgeTestRequirement<>(path.get(i)));

            if (edge == null) {
                // TODO: throw IllegalStateException later
                printToErr("edge pair: (" + path.get(i - 1) + ", " + path.get(i) + ") does not exist");
            } else {
                edge.setCovered(true);
            }
        }

        // cover all edge-pairs
        for (int i = 1; i < path.size(); i++) {
            EdgePairTestRequirement<NT, ET> edgePair = edgePairsReqs.get(
                    new EdgePairTestRequirement<>(path.get(i - 1), path.get(i))
            );

            if (edgePair == null) {
                // TODO: throw IllegalStateException later
                printToErr("edge pair: (" + path.get(i - 1) + ", " + path.get(i) + ") does not exist");
            } else {
                edgePair.setCovered(true);
            }
        }

//        edgePairsReqs.forEach((key, value) -> printToOut(value.toString()));
    }

    /**
     * Covers test requirements that path covers.
     */
    public void coverTestRequirements(List<Edge<NT, ET>> path) {
        coverEdgePairs(path);
    }

    /**
     * Add edge pair coverage (includes individual edges as well) in a string builder
     */
    private List<String> getEdgePairsCoverageInfo(List<String> stringList) {
        // calculate edges coverage
        int noOfEdgesCovered = edgesReqs.keySet().stream().
                filter(AbstractTestRequirement::isCovered).
                collect(Collectors.toSet()).size();
        int totalNoOfEdges = edgesReqs.size();
        double percentOfEdgesCovered = noOfEdgesCovered * 1.0d / totalNoOfEdges * 100d;
        stringList.add("Edges covered: " + noOfEdgesCovered + ", total edges: " + totalNoOfEdges +
                ", percent: " + percentOfEdgesCovered + " %");

        // calculate edge-pair coverage as well
        int noOfEdgePairsCovered = edgePairsReqs.keySet().stream().
                filter(AbstractTestRequirement::isCovered).
                collect(Collectors.toSet()).size();
        int totalNoOfEdgePairs = edgePairsReqs.size();
        double percentOfEdgePairsCovered = noOfEdgePairsCovered * 1.0d / totalNoOfEdgePairs * 100d;
        stringList.add("Edge-pairs covered: " + noOfEdgePairsCovered + ", total edges: " + totalNoOfEdgePairs +
                ", percent: " + percentOfEdgePairsCovered + " %");

        // calculate edge-pair and edges in total
        int noOfBothCovered = noOfEdgesCovered + noOfEdgePairsCovered;
        int totalOfBoth = totalNoOfEdges + totalNoOfEdgePairs;
        double percentOfBoth = noOfBothCovered * 1.0d / totalOfBoth * 100d;
        stringList.add("Edge-pairs (including edges) covered: " + noOfBothCovered + ", total (both): " + totalOfBoth +
                ", percent: " + percentOfBoth + " %");

        return stringList;
    }

    /**
     * Print coverage info to a given print stream.
     */
    public List<String> getCoverageInfo() {
        List<String> stringList = new ArrayList<>();
        return getEdgePairsCoverageInfo(stringList);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<Node<NT>> keys = adjacencyMap.keySet();
        List<Edge<NT, ET>> list;

        for (Node<NT> node : keys) {
            list = adjacencyMap.get(node);

            for (Edge<NT, ET> data : list) {
                sb.append(data).append("\n");
            }
        }

        return sb.toString();
    }

    public void setOutStream(PrintStream out) {
        this.out = out;
    }

    public void setErrStream(PrintStream err) {
        this.err = err;
    }

    private void printToOut(String line) {
        if (this.out != null) {
            this.out.println(line);
        }
    }

    private void printToErr(String line) {
        if (this.err != null) {
            this.err.println(line);
        }
    }
}
