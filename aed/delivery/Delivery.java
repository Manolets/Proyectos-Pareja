package aed.delivery;

import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.set.PositionListSet;

import java.util.Iterator;

import es.upm.aedlib.graph.DirectedAdjacencyListGraph;
import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.graph.Vertex;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.indexedlist.IndexedList;

public class Delivery<V> {

  DirectedGraph<V, Integer> graph;
  IndexedList<Vertex<V>> verteces;

  // Construct a graph out of a series of vertices and an adjacency matrix.
  // There are 'len' vertices. A negative number means no connection. A
  // non-negative number represents distance between nodes.
  public Delivery(V[] places, Integer[][] gmat) {
    graph = new DirectedAdjacencyListGraph<>();
    verteces = new ArrayIndexedList<>();
    for (V place : places) {
      verteces.add(verteces.size(), graph.insertVertex(place));
    }

    for (int i = 0; i < gmat.length; i++) {
      for (int j = 0; j < gmat[i].length; j++) {
        if (gmat[i][j] != null && gmat[i][j] >= 0)
          graph.insertDirectedEdge(verteces.get(i), verteces.get(j), gmat[i][j]);
      }
    }

  }

  // Just return the graph that was constructed
  public DirectedGraph<V, Integer> getGraph() {
    return graph;
  }

  // Return a Hamiltonian path for the stored graph, or null if there is noe.
  // The list containts a series of vertices, with no repetitions (even if the
  // path can be expanded to a cycle).
  public PositionList<Vertex<V>> tour() {
    Iterator<Vertex<V>> vertexIt = graph.vertices().iterator();
    PositionList<Vertex<V>> result = null;
    while (vertexIt.hasNext() && result == null) {
      result = tour(vertexIt.next(), new PositionListSet<>());
    }
    return result;
  }

  private PositionList<Vertex<V>> tour(Vertex<V> activeV, PositionListSet<Vertex<V>> visited) {
    visited.add(activeV);
    PositionList<Vertex<V>> res = null;
    for (Edge<Integer> edge : graph.outgoingEdges(activeV)) {
      if (!visited.contains(graph.endVertex(edge)) && res == null)
        res = tour(graph.endVertex(edge), visited);
    }
    if (visited.size() == verteces.size())
      res = new NodePositionList<>();

    if (res != null)
      res.addFirst(activeV);
    visited.remove(activeV);

    return res;
  }

  public int length(PositionList<Vertex<V>> path) {
    return 0;
  }

  public String toString() {
    return "Delivery";
  }
}
