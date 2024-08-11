package aed.individual6;

import es.upm.aedlib.graph.Edge;
import es.upm.aedlib.graph.Vertex;

import java.util.Iterator;

import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.map.HashTableMap;


public class Suma {
  public static <E> Map<Vertex<Integer>,Integer> sumVertices(DirectedGraph<Integer,E> g) {
     Map<Vertex<Integer>,Integer> res = new HashTableMap<>();
     int sum = 0;
     for(Vertex<Integer> v : g.vertices()) {
    	PositionList<Vertex<Integer>> p = new NodePositionList<>();
    	sum = v.element();
    	p.addLast(v);
    	res.put(v, sumVerticesRec(g,v,sum,p));
     }
	  return res;
  } 
  private static <E> Integer sumVerticesRec(DirectedGraph<Integer,E> g, Vertex<Integer> v, int sum, PositionList<Vertex<Integer>> p) {
	  Vertex<Integer> end;
	  for(Edge<E> e: g.outgoingEdges(v)) {
		  end = g.endVertex(e);
		  if(!esta(p,end)) {
			 p.addLast(end);
			 sum = end.element() + sumVerticesRec(g,end,sum,p);
		 }
	  }
	  return sum;
  }
  
  private static boolean esta(PositionList<Vertex<Integer>> p, Vertex<Integer> v) {
	  boolean aux = false;
	  for(Vertex<Integer> pos: p) {
		  if(v.equals(pos)) {
			  aux = true;
		  }
	  }
	  return aux;
  } 
}
