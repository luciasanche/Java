package aed.hashtable;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Arrays;

import es.upm.aedlib.Entry;
import es.upm.aedlib.EntryImpl;
import es.upm.aedlib.map.Map;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.InvalidKeyException;
import es.upm.aedlib.Pair;


/**
 * A hash table implementing using open addressing to handle key collisions.
 */
public class HashTable<K,V> implements Map<K,V> {
  Entry<K,V>[] buckets;
  int size;

  public HashTable(int initialSize) {
    this.buckets = createArray(initialSize); 
    this.size = 0;
  }

  /**
   * Add here the method necessary to implement the Map api, and
   * any auxilliary methods you deem convient.
   */

  // Examples of auxilliary methods: IT IS NOT REQUIRED TO IMPLEMENT THEM
  
  @SuppressWarnings("unchecked") 
  private Entry<K,V>[] createArray(int size) {
   Entry<K,V>[] buckets = (Entry<K,V>[]) new Entry[size];
   return buckets;
  }

  // Returns the bucket index of an object
  private int index(Object obj) {
	 int index=Math.abs(obj.hashCode());
	 
	 if( index>=buckets.length)
		 return index % buckets.length;
	 else 
		 return index;    
  }
 
  
  public boolean isEmpty() {
      boolean res = true;
      for( int i = 0; i < buckets.length && res; i++ ) {
          res = (buckets[i] == null)? true :false;
      }
      return res;
  }

  // Returns the index where an entry with the key is located,
  // or if the key is not in the array, -1 is returned.
  private int search(Object obj) {
	  if (obj == null) {
		  throw new InvalidKeyException();
	  }
      if( isEmpty() ) return -1;
	  int aux = -1;
	  for(int j=0; j < buckets.length ; j++ ) {
		    if(buckets[j]!= null && buckets[j].getKey().equals(obj)) {
		    	  aux=j;
		    }
	 }
		    
	 return aux;

}
//Returns the index where an entry with the key is located,
 // or if no such entry exists, the "next" bucket with no entry,
 // or if all buckets stores an entry, -1 is returned.
 //Para saber donde colocar key
 private int search2(Object obj) {
   int i;
   int aux = -1;
   boolean sigo = true;
   if(containsKey(obj)) {
	   aux=search(obj);
   }else {
   for(i = index(obj); sigo && i < buckets.length ; i++) {
	   if(buckets[i] == null) {
		   aux=i;
	       sigo = false;
	   }  
   }
   for(i=0; sigo && i<index(obj); i++) {
	   if(buckets[i] == null) {
		   aux=i;
	       sigo = false;
	   }
   }
   }
    
   return aux;
 }

  // Doubles the size of the bucket array, and inserts all entries present
  // in the old bucket array into the new bucket array, in their correct
  // places. Remember that the index of an entry will likely change in
  // the new array, as the size of the array changes.
  private void rehash() {
	  Entry<K, V>[] aux = buckets;
	  buckets = createArray(buckets.length * 2);
	  size=0;
      for( int i = 0; i < aux.length && aux[i]!=null; i++ ) {
    	  put(aux[i].getKey(),aux[i].getValue());
      }
      
  }
  
  public V put(K key, V value) { 
	  if (key == null) {
		  throw new InvalidKeyException();
	  }
	  Entry<K, V> obj = new EntryImpl<K,V>(key,value);
	  V val = null;
	  int i = search2(key);
	  if(containsKey(key)) {
		  val = get(key);
		  buckets[i]=obj;
		  
	  }else if(i!= -1) {
		  buckets[i]=obj;
		  size++;
	  }else {
		  rehash();
		  put(key,value);
	  }
	 return val;
	  
  }




  public V remove(K key) {
	 // si key es null lanza la excepcion
	  if (key == null ) { 
		  throw new InvalidKeyException();
	  }
	 
	  int index = search(key);
	  V val =get(key);
	  if(!containsKey(key)) {
		  return null;
	  }else {
	  buckets [index] = null;
	  size--;
	  //colapsar los huecos
	  int start=index;
	  int indexp;
	  int i = (index + 1) % buckets.length;
	  while(i!=start && buckets[i]!=null) {
		  indexp = index(buckets[i]);
		  if((i>=indexp && indexp<=index && index<i)|| indexp>i && (index>=indexp || index<i)) { 
			  buckets[index]=buckets[i];
			  buckets[i]=null;
			  index = i;
		  }
		  i=(i+1)%buckets.length;
	  }
	  
	  return val;
	  }
  }

@Override
public Iterator<Entry<K, V>> iterator() {
	return entries().iterator();
}

@Override
public boolean containsKey(Object obj) throws InvalidKeyException {
	if (obj == null ) { 
		  throw new InvalidKeyException();
	  }
	if(search(obj)!=-1) {
		return true;
	}else 
	    return false;
}

@Override
public Iterable<Entry<K, V>> entries() {
	PositionList<Entry<K, V>> lista = new NodePositionList<Entry<K,V>>();
	for(int i=0; i<buckets.length ; i++) {
		if(buckets[i]!= null) {
		lista.addLast(buckets[i]);
		}
	}
	return lista;
}

@Override
public V get(K key) throws InvalidKeyException {
	 if (key == null) { 
		  throw new InvalidKeyException();
	  }
	 
	 if(search(key)==-1)
		 return null;
	 else
	     return buckets[search(key)].getValue();
}

@Override
public Iterable<K> keys() {
	PositionList<K> lista = new NodePositionList<K>();
	for(int i=0;i<buckets.length; i++) {
		if(buckets[i]!= null) {
		   lista.addLast(buckets[i].getKey());
		}
	}
	return lista;
}

@Override
public int size() {
		return size;
}
  
}

