package aed.tries;

import java.util.Arrays;
import java.util.Iterator;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.tree.GeneralTree;
import es.upm.aedlib.tree.LinkedGeneralTree;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.positionlist.NodePositionList;


public class DictImpl implements Dictionary {
  // A boolean because we need to know if a word ends in a node or not
  GeneralTree<Pair<Character,Boolean>> tree;
  

  public DictImpl() {
    tree = new LinkedGeneralTree<>();
    tree.addRoot(new Pair<Character,Boolean>(null,false));
  }
  // metodos auxiliares
  
 //Devuelve el nodo cuyo camino desde la raiz contiene
 //la palabra prefix. Si no existe el metodo devuelve null.
  private Position<Pair<Character,Boolean>> findPos(String prefix){
	  int i = 0;
	  Position<Pair<Character,Boolean>> aux2 = tree.root() ;
	  Position<Pair<Character,Boolean>> aux;
	  if(prefix.length()!=0) {  
	    while(i<prefix.length() && (aux = searchChildLabelledBy(prefix.charAt(i),aux2))!=null) {
		  i++;
		  aux2=aux;
	    }
	  }
	  return aux2;  
  }


  //Devuelve el hijo del nodo pos que contiene el caracter ch.
  private Position<Pair<Character,Boolean>> searchChildLabelledBy(char ch, Position<Pair<Character,Boolean>> pos) {
	Position<Pair<Character,Boolean>> res = null;
	if(!tree.isExternal(pos)) {
	  Iterator<Position<Pair<Character, Boolean>>> it = tree.children(pos).iterator();
	  Position<Pair<Character,Boolean>> aux = it.next();
	  boolean sigue = true;
	  while (aux!= null && sigue) {
		if(aux.element().getLeft().equals(ch)) {
			res = aux;
			sigue = false;
		}
	  aux = (it.hasNext()? it.next():null);
	  }
	}
	return res;  
  }
  
 //Anade un hijo al nodo pos conteniendo el elemento pair,
 //respetando el orden alfabetico de los hijos.
  private Position<Pair<Character,Boolean>> addChildAlphabetically (Pair<Character,Boolean> pair , Position<Pair<Character,Boolean>> parent) {

      Position<Pair<Character, Boolean>> res = null;

      if(tree.isExternal(parent)) { // si parent no tiene hijos
          res = tree.addChildFirst(parent, pair);    
      }
      else {
    	  for (Position<Pair<Character,Boolean>> pos : tree.children(parent)) {
    		  if(pos.element().getLeft()>pair.getLeft()) {
    			  res = tree.insertSiblingBefore(pos, pair);
    		  }
    	  }
    	  if(res == null) {
    		  res = tree.addChildLast(parent, pair);
    	  }
      }
      return res;
}
  
  
  
  public void add(String word) {
	  if(word==null || word.length()==0) {
		  throw new IllegalArgumentException();
	  }
      

      Position<Pair<Character,Boolean>> aux2 = tree.root() ;
	  Position<Pair<Character,Boolean>> aux;
	  int i =0;
      while(i<word.length() && (aux = searchChildLabelledBy(word.charAt(i),aux2))!=null) {
		  i++;
		  aux2=aux;
	    }
      if( i != word.length() ){//no contiene word entero
          for( int j = i; word.length()>1 && j < word.length()-1; j++  ) {
              aux2 = addChildAlphabetically(new Pair<Character,Boolean>(word.charAt(j),false), aux2 ); //se anade hasta la penultima letra de word
          }
          addChildAlphabetically(new Pair<Character,Boolean>(word.charAt(word.length()-1),true), aux2 ); //se anade la ultima letra de word
      }else {
          aux2.element().setRight(true);
      }
                             
  }
  
  public void delete(String word) { 
	  if(word==null || word.length()==0) {
		  throw new IllegalArgumentException();
	  }
	  
	  Position<Pair<Character,Boolean>> aux = findPos(word);
	  if (aux!=null) {
	       aux.element().setRight(false);
	  }

	  
  }
  public boolean isIncluded(String word) {
	  if(word==null|| word.length()==0) {
		  throw new IllegalArgumentException();
	  }
	  
	  Position<Pair<Character,Boolean>> aux = findPos(word);
	  return aux!= null && aux.element().getRight(); 
  }
  
  public PositionList<String> wordsBeginningWithPrefix(String prefix) {
      if(prefix==null) {
          throw new IllegalArgumentException();
      }
      PositionList<String> list = new NodePositionList<String>();
      Position<Pair<Character,Boolean>> aux = (prefix.length()==0? tree.root() : findPos(prefix));
     
      evaluateChildren(prefix, aux ,list);
     
       return list; 
      }

private void evaluateChildren(String prefix ,Position<Pair<Character, Boolean>> pos, PositionList<String> list ) {
    
      if(pos.element().getRight()) {
    	  list.addLast(prefix); //si el prefix es una palabra se anade
      }
     for(Position<Pair<Character, Boolean>> child : tree.children(pos)) {
    	 evaluateChildren(prefix + child.element().getLeft(),child,list);
     }

}

}
