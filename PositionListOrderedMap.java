package aed.orderedmap;

import java.util.Comparator;
import java.util.Iterator;

import es.upm.aedlib.Entry;
import es.upm.aedlib.Position;
import es.upm.aedlib.indexedlist.ArrayIndexedList;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.positionlist.NodePositionList;

public class PositionListOrderedMap<K,V> implements OrderedMap<K,V> {
    private Comparator<K> cmp;
    private PositionList<Entry<K,V>> elements;
  
    /* Acabar de codificar el constructor */
    public PositionListOrderedMap(Comparator<K> cmp) {
	  this.cmp = cmp;
	  this.elements = new NodePositionList<Entry<K,V>>();
    }

    /* Ejemplo de un posible método auxiliar: */
  
    /* If key is in the map, return the position of the corresponding
     * entry.  Otherwise, return the position of the entry which
     * should follow that of key.  If that entry is not in the map,
     * return null.  Examples: assume key = 2, and l is the list of
     * keys in the map.  For l = [], return null; for l = [1], return
     * null; for l = [2], return a ref. to '2'; for l = [3], return a
     * reference to [3]; for l = [0,1], return null; for l = [2,3],
     * return a reference to '2'; for l = [1,3], return a reference to
     * '3'. */

    private Position<Entry<K,V>> findKeyPlace(K key) {
    	if (key == null) {
    		throw new IllegalArgumentException();
    	}
        Position<Entry<K,V>> posicion = null; // si no hay ninguno mayor que key se devuelve null
        Position<Entry<K, V>> cursor = null;
        if( elements.size() == 1 && cmp.compare(elements.first().element().getKey(), key) > 0) {
            posicion = elements.first();
        }
        else if( keyPosition(key) != null ) {
          posicion = elements.next(keyPosition(key)); //ya esta en la lista y devuelve el que va despues
        }
        else{
        boolean aux = false;
        for (cursor = elements.first(); !aux && cursor != null ; cursor = elements.next(cursor) ) {
        	if (cmp.compare(cursor.element().getKey(), key) > 0) {
        		posicion = cursor;
        		aux = true;
        	}	
        }
        }
        return posicion;
    }

    
    private Position<Entry<K,V>> keyPosition(K key){
    	if (key == null) {
    		throw new IllegalArgumentException();
    	}
        Position<Entry<K,V>> cursor = null; 
        Position<Entry<K,V>> posicion = null;
        boolean aux = false;
        for (cursor = elements.first(); !aux && cursor !=null ; cursor = elements.next(cursor)){
            if (cursor.element().getKey().equals(key)) {
        	   posicion = cursor;
        	   aux = true;
        	}
        }
        return posicion;
    }


    /* Podéis añadir más métodos auxiliares */
  
    public boolean containsKey(K key) {
    	boolean aux = false;
    	if (keyPosition(key)!= null) {
    		aux = true;
    	}
	    return aux;
    }
  
    public V get(K key) {
    	if (keyPosition(key) != null)
    		return keyPosition(key).element().getValue();
    	else
    		return null;
    }
  
    public V put(K key, V value) {
    	EntryImpl map =new EntryImpl(key, value);
    	V v = null;
    	if (keyPosition(key) != null) {
    		v = get(key);
    		remove(key);
    	}
    	if(findKeyPlace(key) == null) {
    		elements.addLast(map);
    	} else 
    		elements.addBefore(findKeyPlace(key), map);
    	
	     return v;
    }
  
    public V remove(K key) {
    	if (key == null) {
    		throw new IllegalArgumentException();
    	}
    	if (keyPosition(key) != null) {
    		V v = keyPosition(key).element().getValue();
    	    elements.remove(keyPosition(key));
    	    return v;
    	} else
    		return null;
    }
  
    public int size() {
    	return elements.size();
    }
  
    public boolean isEmpty() {
    	return elements.isEmpty();
    }
  
    public Entry<K,V> floorEntry(K key) {
        Entry<K,V> returnValue = null; // si la lista esta vacia se queda null
        Position<Entry<K,V>> position = findKeyPlace(key); // Auxiliar porque sabemos que key no esta en elements
        boolean contieneKey = containsKey(key);
        if( contieneKey ) { // Si tienes una que es igual a key, devuelvela
            returnValue =  keyPosition(key).element();
        }
        else if( position == null && elements.last() != null ) { // Si el elemento cuya key es key(this) es el mayor
            returnValue = elements.last().element();
        }
        else if( position != null && elements.prev(position) != null ){ // En otro caso devuelve la posicion
            returnValue = elements.prev(position).element();
       
        }
        return returnValue;
    }

    public Entry<K,V> ceilingEntry(K key) {
        Entry<K,V> returnValue = null; //si la posicion pedida es null, key es el mayor y se queda null
        boolean contieneKey = containsKey(key); 
        Position<Entry<K,V>> position = findKeyPlace(key); // Auxiliar porque sabemos que key no esta en elements
        if( contieneKey ) { // Si tienes una que es igual a key, devuelvela
            returnValue = keyPosition(key).element();
        }
        else if( position != null) {// no contiene a key y existe la posicion pedida
            returnValue = position.element();
        }
        return returnValue;
    }
  
    public Iterable<K> keys() {
    	ArrayIndexedList<K> keys = new ArrayIndexedList<K>();
    	Iterator<Entry<K,V>> e = elements.iterator();
    	for (int i=0; i<elements.size(); i++) {
    		keys.add(i, e.next().getKey());
    	}
    	return keys;
    }
  
    public String toString() {
	return elements.toString();
    }
 
  
}
