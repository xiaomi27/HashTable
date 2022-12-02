
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

public class HashedDictionary<K,V> implements DictionaryInterface<K, V> {
	
	private int numberOfEntries;
	private static final int DEFAULT_CAPACITY = 37;  
    private static final int MAX_CAPACITY = 10000;
	
    private TableEntry<K, V>[] hashTable;
    private int tableSize; 
    private static final int MAX_SIZE = 2 * MAX_CAPACITY;
    private boolean initialized = false;
    private static double DEFAULT_LOAD_FACTOR = 0.5;
    static DictionaryInterface<String, Integer> alphabet = new Dictionary<String, Integer>();
    
    public int collisionCount=0;
    private boolean hashType;
    private boolean handlingType;
    
	
    public HashedDictionary(boolean loadFactor,boolean hashType,boolean handlingType)
    {
        this(DEFAULT_CAPACITY,loadFactor,hashType,handlingType); 
    }  // constructor
    
	public HashedDictionary(int initialCapacity,boolean loadFactor,boolean hashType,boolean handlingType) {
		checkCapacity(initialCapacity);
		
		if(!loadFactor) { DEFAULT_LOAD_FACTOR=0.8;}
    	this.hashType=hashType;
    	this.handlingType=handlingType;
    	
		
		numberOfEntries = 0; 
		
		int tableSize = getNextPrime(initialCapacity);
		

		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[tableSize];
		hashTable = temp;
		initialized = true;
		
		alphabet.put("a", 1);alphabet.put("b", 2);alphabet.put("c", 3);alphabet.put("d", 4);      //  all these will be used for string calculations
		alphabet.put("e", 5);alphabet.put("f", 6);alphabet.put("g", 7);alphabet.put("h", 8);
		alphabet.put("i", 9);alphabet.put("j", 10);alphabet.put("k", 11);alphabet.put("l", 12);
		alphabet.put("m", 13);alphabet.put("n", 14);alphabet.put("o", 15);alphabet.put("p", 16);
		alphabet.put("q", 17);alphabet.put("r", 18);alphabet.put("s", 19);alphabet.put("t", 20);
		alphabet.put("u", 21);alphabet.put("v", 22);alphabet.put("w", 23);alphabet.put("x", 24);
		alphabet.put("y", 25);alphabet.put("z", 26);
		
		
		
	}  // constructor
	
	private int getHashIndex(K key) {
		
		if(hashCode(key)!=2147483647) {     // overflows will be ignored
			
			int hashIndex = hashCode(key) % hashTable.length;

			if (hashIndex < 0)
				hashIndex = hashIndex + hashTable.length;

			return hashIndex;
		}
		else {  // overflow
			return 2147483647;
		}
		
	}
	
	private int hashCode(K key) {
		
		if(hashType)  // SSF
		{
			int sum=0;
			String word =key.toString().toLowerCase(Locale.ENGLISH);
			
			for(int i=0;i<word.length();i++) {
				sum = sum + alphabet.getValue(Character.toString(word.charAt(i)));
			}
			return sum;
		}
		else   // PAF 
		{
			int sum=0;
			String word =key.toString().toLowerCase(Locale.ENGLISH);
			
			
			for(int i=0;i<word.length();i++) {
				sum = sum + alphabet.getValue(Character.toString(word.charAt(i)))*((int)Math.pow(31, (word.length()-(i+1))));
			}
			
			if(sum<0) {   // overflow  ignore it
				return 2147483647;
			}
			else {
				return sum;
			}
			
		}
		
		
		
	}
	
	public V put(K key, V value) {   // puts key to hashtable
		
		if(getHashIndex(key)!=2147483647) {  // if is not overflow then add it  otherwise ignore
			
			if ((key == null) || (value == null))
				throw new IllegalArgumentException();
			else {
				
				V oldValue;
				int index = getHashIndex(key);
				index = probe(index, key); 
				
				assert (index >= 0) && (index < hashTable.length);
				
				if ((hashTable[index] == null)) { 
					hashTable[index] = new TableEntry<>(key, value);
					numberOfEntries++;
					oldValue = null;
					
				} else { 
					
					
					oldValue = (V) hashTable[index].getDictionary().getValue(value.toString());
					Integer freq= hashTable[index].getDictionary().getValue(value.toString());
					
					if(freq==null)
					{
						hashTable[index].getDictionary().put(value.toString(), 1);
					}
					else {
						freq++;
						hashTable[index].getDictionary().put(value.toString(), freq);
					}
				
				} 
				
				double loadFactor = (double)numberOfEntries/hashTable.length;
				
				if (loadFactor > DEFAULT_LOAD_FACTOR ) {
					resize();
				}
					
				return oldValue;
			} 
		}
		else {
			
			return null;
		}
		
	
	}  
	
	private int locate(int index, K key) {
		
		boolean found = false;
		
		while (!found && (hashTable[index] != null)) {
			
			if (hashTable[index] != null && key.equals(hashTable[index].getKey()))
				found = true; 
			else 
				index = (index + 1) % hashTable.length;   // with Linear probing
		} 
			
		int result = -1;
		if (found)
			result = index;
		
		return result;
	} // end locate
	

	
	private int probe(int index, K key) {
		
		
		
		if(handlingType) {   // linear probing
			
			boolean found = false;
			int removedStateIndex = -1; 
			
			
			while (!found && (hashTable[index] != null)) {
				
				if (hashTable[index]!= null) {
					
					if (key.equals(hashTable[index].getKey()))
						found = true; 
					else {
						index = (index + 1) % hashTable.length;     // linear probing   
						collisionCount++;
					}
						
					
				} 
				else 
				{
					
					if (removedStateIndex == -1)
						removedStateIndex = index;
					index = (index + 1) % hashTable.length; 
				} 
			} // end while
				
			if (found || (removedStateIndex == -1))
				return index; 
			else
				return removedStateIndex; 
		}
		else {					//  Double hashing
			
			boolean found = false;
			int removedStateIndex = -1; 
			int j=0;
			int h=index;
			
			while (!found && (hashTable[index] != null)) {
				
				if (hashTable[index]!= null) {
					
					if (key.equals(hashTable[index].getKey()))
						found = true; 
					else {
						
						index = (h+(j*secondaryHashCode(key)))%hashTable.length;     // double hashing
						collisionCount++;
						j++;
						if(j>hashTable.length) {
							index=-1;break;
						}
					}
						  
					
				} 
				else 
				{
					
					if (removedStateIndex == -1)
						removedStateIndex = index;
					
					index = (h+(j*secondaryHashCode(key)))%hashTable.length;
				} 
				
				
				
				
			} // end while
				
			if (found || (removedStateIndex == -1))
				return index; 
			else
				return removedStateIndex; 
			
			
			
		}
		
		
	} 
	
	private int secondaryHashCode(K key) {
		
		return (31-(hashCode(key)%31));
		
		
	}
	
	
	

	private void resize() {
		
		TableEntry<K, V>[] oldTable = hashTable;
		int oldSize = hashTable.length;
		int newSize = getNextPrime(oldSize + oldSize);
		
		
		@SuppressWarnings("unchecked")
		TableEntry<K, V>[] temp = (TableEntry<K, V>[]) new TableEntry[newSize];
		hashTable = temp;
		numberOfEntries = 0; 
		
		for (int index = 0; index < oldSize; index++) {
			
			if ((oldTable[index] != null)) {
				add2(oldTable[index].getKey(), oldTable[index].getDictionary());
			}
				
		} // end for
	}  // end enlarge
	
    
	public V get(K key) {
		
		V result = null;
		
		int index = getHashIndex(key);
		
		if(index!=2147483647) {
			
			key=(K) key.toString().toLowerCase();
			index = locate(index, key);
			
			if (index != -1) {
				result = (V) hashTable[index].getDictionary();
			}
			
		}
		
		
			
		return result;
	}

	
	public V search(K key) {
		V result = null;

		int index = getHashIndex(key);

		if (index != 2147483647) {

			key = (K) key.toString().toLowerCase();
			index = locate(index, key);

			if (index != -1) {
				hashTable[index].displayDic2();
				result = (V) hashTable[index].getDictionary();
			}
			else
				System.out.println("Not found !!! ");

		}
		else
			System.out.println("Not found !!! ");

		return result;
	}
	
	
	public V remove(K key) {
		
		V removedValue = null;
		int index = getHashIndex(key);
		index = locate(index, key);
		
		if (index != -1) { // Key found; flag entry as removed and return its value
			removedValue = (V)hashTable[index].getDictionary();
			hashTable[index]=null;
			numberOfEntries--;
		} // end if
			// Else key not found; return null
		return removedValue;
	}
	
	
	public Dictionary<String, Integer> add2(K key, Dictionary<String,Integer> dic) {  // this is just for resize function since Table entry has a key and a dictionary ,this is easy for passing dictionaries to new hashtable

		if (getHashIndex(key) != 2147483647) { // if not overflow

			if ((key == null) || (dic == null))
				throw new IllegalArgumentException();
			else {

				Dictionary<String,Integer> oldValue;
				int index = getHashIndex(key);
				index = probe(index, key);

				assert (index >= 0) && (index < hashTable.length);

				if ((hashTable[index] == null)) {
					hashTable[index] = new TableEntry<>(key, dic);
					numberOfEntries++;
					oldValue = null;

				} else {
					oldValue = hashTable[index].getDictionary();
					hashTable[index].setDictionary(dic);
				}

				double loadFactor = (double) numberOfEntries / hashTable.length;

				if (loadFactor > DEFAULT_LOAD_FACTOR) {
					resize();
				}

				return oldValue;
			} // end if
		} else {
			
			return null;
		}

	} // end add
	
	void display() {     // if you want to see all hashtable  run this at main
		for (int i = 0; i < hashTable.length; i++) {
			
			if(hashTable[i] !=null) {
				
				System.out.println("Key: " +hashTable[i].getKey()+"  index"+i +"    tablesize:"+hashTable.length);
				hashTable[i].displayDic();
			}
			
		}
		
	}
    
    
	
	
	private static class TableEntry<K, V> {
		
		
		private K key;
		private Dictionary<String,Integer> dictionary = new Dictionary<String,Integer>();
		
		private States state;
		private enum States {
			CURRENT, REMOVED
		}
		

		private TableEntry(K searchKey, V docName) {
			key = searchKey;
			dictionary.put(docName.toString(), 1);
			
			state = States.CURRENT;
		}
		
		private TableEntry(K searchKey, Dictionary<String,Integer> dic) {
			
			key = searchKey;
			dictionary= dic;
			state = States.CURRENT;
		}

		public K getKey() {
			return key;
		}

		public Dictionary<String, Integer> getDictionary() {
			return dictionary;
		}

		public void setDictionary(Dictionary<String, Integer> dictionary) {
			this.dictionary = dictionary;
		}
		
		public void displayDic() {
			
			Iterator<String> keyIterator =dictionary.getKeyIterator();
			Iterator<Integer> valueIterator =dictionary.getValueIterator();
			
			while(keyIterator.hasNext()) {
				System.out.println("path "+ keyIterator.next()+"   int: "+valueIterator.next() );
			}
			
			
		}
		
		public void displayDic2() {
			
			
			Iterator<String> keyIterator =dictionary.getKeyIterator();
			Iterator<Integer> valueIterator =dictionary.getValueIterator();
			
			System.out.println(dictionary.getSize()+ " documents found");
			
			while(keyIterator.hasNext()) {
				System.out.println(valueIterator.next()+"-"+ keyIterator.next());
			}
		}
	}
    
	private int getNextPrime(int initialCapacity) {
		
		if(isPrime(initialCapacity)) {
			
			return initialCapacity;
			
		}
		else {
			if (initialCapacity <= 1)
	            return 2;
	     
	        int prime = initialCapacity;
	        boolean found = false;
	     
	       
	        while (!found)
	        {
	            prime++;
	     
	            if (isPrime(prime))
	                found = true;
	        }
	     
	        return prime;
			
		}
		
	}

	private boolean isPrime(int n) {
		
		 
        if (n <= 1) return false;
        if (n <= 3) return true;
         
       
        if (n % 2 == 0 || n % 3 == 0) return false;
         
        for (int i = 5; i * i <= n; i = i + 6) {
        	if (n % i == 0 || n % (i + 2) == 0) {
        		return false;
        	}
                
        }
            
         
        return true;
		
	}

	private void checkCapacity(int capacity)
	{
	 if (capacity > MAX_CAPACITY)
	 throw new IllegalStateException("Attempt to create a table whose " + 
	 "capacity exeeds allowed " + 
	 "maximum of " + MAX_CAPACITY);
	}

	

	public V getValue(K key) {
		
		V result = null;

		int index = getHashIndex(key);

		if (index != 2147483647) {

			key = (K) key.toString().toLowerCase();
			index = locate(index, key);

			if (index != -1) {
				hashTable[index].displayDic2();
				result = (V) hashTable[index].getDictionary();
			} else
				System.out.println("Not found !!! ");

		} else
			System.out.println("Not found !!! ");

		return result;
	}

	
	public boolean contains(K key) {
		return get(key)!=null;
	}

	
	

	
	public boolean isEmpty() {
		
		return numberOfEntries==0;
	}

	
	public int getSize() {
		
		return numberOfEntries;
	}

	
	public void clear() {
		
		for (int i = 0; i < hashTable.length; i++) {
			hashTable[i]=null;
		}
		
	}
	
	public Iterator<K> getKeyIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Iterator<V> getValueIterator() {
		// TODO Auto-generated method stub
		return null;
	}
     

} //end 
