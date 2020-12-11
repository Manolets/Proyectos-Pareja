package aed.delivery;

import es.upm.aedlib.Position;
import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.graph.DirectedAdjacencyListGraph;
import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.graph.Vertex;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.indexedlist.IndexedList;

import javax.swing.text.html.HTMLDocument;

public class Delivery<V> {

  DirectedGraph<V, Integer> graph;
  IndexedList<Vertex<V>> verteces;

  // Construct a graph out of a series of vertices and an adjacency matrix.
  // There are 'len' vertices. A negative number means no connection. A
  // non-negative
  // number represents distance between nodes.
  public Delivery(V[] places, Integer[][] gmat) {
    graph = new DirectedAdjacencyListGraph<>();
    verteces = new ArrayIndexedList<>();
    for (V place : places) {
      verteces.add(verteces.size(), graph.insertVertex(place));
    }

    for (int i = 0; i < gmat.length; i++) {
      for (int j = 0; j < gmat[i].length; j++) {
        if (gmat[i][j] != null)
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
    return null;
  }

  public int length(PositionList<Vertex<V>> path) {
    int length = 0;
    if(!path.isEmpty()){
      Position<Vertex<V>> cursor = path.first();
      while(!cursor.equals(path.last())) {
        Iterable<Edge<Integer>> edges = graph.outgoingEdges(cursor.element());
        for (Edge<Integer> edge : edges) {
          if (graph.endVertex(edge).equals(path.next(cursor).element()))
            length = edge.element() + length;
        }
        cursor = path.next(cursor);
      }
    }
    return length;
  }

  public String toString() {
    return "Delivery";
  }
}
