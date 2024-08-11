package aed.delivery;

import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.Node;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.graph.DirectedAdjacencyListGraph;
import es.upm.aedlib.graph.Vertex;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.indexedlist.IndexedList;
import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.map.HashTableMap;
import es.upm.aedlib.set.HashTableMapSet;
import es.upm.aedlib.set.Set;
import java.util.Iterator;

public class Delivery<V> {

    
  private DirectedGraph<V,Integer> g;
  private Integer[][] gmat;
  private V[] places;
  private IndexedList<Vertex<V>> vertices;
  private IndexedList <Boolean> visited;
  private PositionList<Vertex<V>> hamilt;
    
  // Construct a graph out of a series of vertices and an adjacency matrix.
  // There are 'len' vertices. A negative number means no connection. A non-negative
  // number represents distance between nodes.
  public Delivery(V[] places, Integer[][] gmat) {
      g = new DirectedAdjacencyListGraph<>();
      vertices = new ArrayIndexedList<>();
      visited = new ArrayIndexedList<>();
      for( int i = 0; i < places.length; i++ ) {
          vertices.add(i,g.insertVertex(places[i]));
          visited.add(i, false);
       }
      for( int i = 0; i < gmat.length ; i++ ) {
          for( int j = 0; j < gmat[i].length ; j++ ) {
              if( gmat[i][j] != null ) {
                  g.insertDirectedEdge(vertices.get(i), vertices.get(j), gmat[i][j]);
              }
          }
      }
  }
  
  // Just return the graph that was constructed
  public DirectedGraph<V, Integer> getGraph() {
    return g;
  }

  // Return a Hamiltonian path for the stored graph, or null if there is noe.
  // The list containts a series of vertices, with no repetitions (even if the path
  // can be expanded to a cycle).
  public PositionList<Vertex<V>> tour() {
	     if( vertices.get(0) == null || g == null || g.size() == 0 ) {
	          return null;
	     }
	     PositionList<Vertex<V>> path = new NodePositionList<>();
	     Iterator<Vertex<V>> it = g.vertices().iterator();
	     boolean pathFound = false;
	     while( it.hasNext() && !pathFound ) {
	         Vertex<V> v = it.next();
	         int index = vertices.indexOf(v);
	         if( g.outDegree(v) != 0 ) {
	             path.addLast(v);
	             visited.set(index, true);
	             if( tourRec(path,visited)) {pathFound = true; }
	             else {
	                 path.remove(path.last());
	                 visited.set(index,false);
	             }
	         }
	     }
	     if( pathFound ) {return hamilt;}
	     return null;
	  }

	  private boolean tourRec( PositionList<Vertex<V>> path, IndexedList<Boolean> visited2){
	      Iterator<Edge<Integer>> it = g.outgoingEdges(path.last().element()).iterator();
	      if( path.size() == vertices.size() ) {
	          hamilt = path;
	          return true;
	          }
	      if( !it.hasNext() ) {return false;}
	      boolean pathFound = false;
	          while (it.hasNext() && !pathFound ) {
	              Vertex<V> child = g.endVertex( it.next() );
	              int index = vertices.indexOf(child);
	              if( !visited2.get(index)) {
	                  path.addLast(child);
	                  visited2.set(index, true);
	                  if( tourRec(path,visited2) ) {
	                    
	                      pathFound = true;
	                  }
	                  else {
	                          path.remove(path.last());
	                          visited2.set(index, false);
	                  }
	              } 
	      }
	      if(pathFound) return true;
	      return false;
	  }


  public int length(PositionList<Vertex<V>> path) {
     int sum = 0;
     Position<Vertex<V>> antes = path.first();
     Position<Vertex<V>> despues = path.next(antes);
	 while(despues != null) {
	   sum += sumRec(antes, despues);
	   antes = despues;
	   despues = path.next(despues);
     }
     return sum;
  }
  
  private int sumRec(Position<Vertex<V>> antes,  Position<Vertex<V>> despues) {
	  int sum = 0;
	  Iterable<Edge<Integer>> list = g.outgoingEdges(antes.element());
      boolean aux = false;
      if(g.outDegree(antes.element()) == 1) {
    	 return list.iterator().next().element();
      }
      Iterator<Edge<Integer>> itE = list.iterator(); 
      Edge<Integer> e;
      while(itE.hasNext() && !aux) { 
    	  e = itE.next();
    	  if(g.endVertex(e) == despues.element()) {
			 sum = e.element();
			 aux = true;			
    	  }
	   }
	  
	  return sum;
  }

  public String toString() {
    return "Delivery";
  }
}