/** A PHPArray is a hybrid of a hash table and a linked list.
* It allows hash table access, indexed integer access, and
* sequential access.
* @author Sherif Khattab
*
**/

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.*;
import java.io.*;

public class PHPArray<V> implements Iterable<V> {
  private static final int INIT_CAPACITY = 15;

  private int N;           // number of key-value pairs in the symbol table
  private int M;           // size of linear probing table
  private Node<V>[] entries;  // the table
  private Node<V> head;       // head of the linked list
  private Node<V> tail;       // tail of the linked list
  private Node<V> curr;       // current of the linked newList

  // create an empty hash table - use 16 as default size
  public PHPArray() {
    this(INIT_CAPACITY);
  }

  // create a PHPArray of given capacity
  public PHPArray(int capacity) {
    M = capacity;
    @SuppressWarnings("unchecked")
    Node<V>[] temp = (Node<V>[]) new Node[M];
    entries = temp;
    head = tail = null;
    N = 0;
  }

  public Iterator<V> iterator() {
    return new MyIterator();
  }

  public void put(int key, V val){
    put(Integer.toString(key), val);
  }
  public V get(int key) {
      return get(Integer.toString(key));
  }
  public void unset(int key) {
      unset(Integer.toString(key));
  }
  // insert the key-value pair into the symbol table
  public void put(String key, V val) {
    if (val == null) unset(key);

    // double table size if 50% full
    if (N >= M/2) resize(2*M);

    // linear probing
    int i;
    for (i = hash(key); entries[i] != null; i = (i + 1) % M) {
      // update the value if key already exists
      if (entries[i].key.equals(key)) {
        entries[i].value = val; return;
      }
    }
    // found an empty entry
    entries[i] = new Node<V>(key, val);
    //insert the node into the linked list
    //Insert the node into the doubly linked list in O(1) time
    if(head == null){
      head = entries[i];
      tail = head;
      curr = head;
    }
    else{
    tail.next = entries[i];
    entries[i].prev = tail;
    tail = entries[i];
    }
    curr = head;
    N++;
  }

  // return the value associated with the given key, null if no such value
  public V get(String key) {
    for (int i = hash(key); entries[i] != null; i = (i + 1) % M)
      if (entries[i].key.equals(key))
        return entries[i].value;
    return null;
  }

  // resize the hash table to the given capacity by re-hashing all of the keys
  private void resize(int capacity) {
    PHPArray<V> temp = new PHPArray<V>(capacity);

    //rehash the entries in the order of insertion
    Node<V> current = head;
    while(current != null){
        temp.put(current.key, current.value);
        current = current.next;
    }
    entries = temp.entries;
    head    = temp.head;
    tail    = temp.tail;
    M       = temp.M;
  }

  // rehash a node while keeping it in place in the linked list
  private void rehash(Node<V> node){
    if(node.value == null) unset(node.key);
    int i;
    for (i = hash(node.key); entries[i] != null; i = (i + 1) % M) {
        if (entries[i].key.equals(node.key)) {
            entries[i].value =node.value;
            return;
        }
    }
    entries[i] = node;
  }
  // delete the key (and associated value) from the symbol table
  public void unset(String key) {
    if (get(key) == null) return;

    // find position i of key
    int i = hash(key);
    while (!key.equals(entries[i].key)) {
      i = (i + 1) % M;
    }

    // delete node from hash table
    Node<V> toDelete = entries[i];
    entries[i] = null;
    // TODO: delete the node from the linked list in O(1)
    if(head == null){
      return;
    }

    else if(head == tail){
      head = null;
      tail = null;
      return;
    }
    else if(toDelete == head){
      head = head.next;
      head.next.prev = head;
      curr = head;
      return;
    }
    else if(toDelete == tail){
      tail = tail.prev;
      tail.next = null;
      return;
    }

   else{
    toDelete.prev.next = toDelete.next;
    toDelete.next.prev = toDelete.prev;
  }
    // rehash all keys in same cluster
    i = (i + 1) % M;
    while (entries[i] != null) {
      // delete and reinsert
      Node<V> nodeToRehash = entries[i];
      entries[i] = null;
      rehash(nodeToRehash);
      i = (i + 1) % M;
    }

    N--;

    // halves size of array if it's 12.5% full or less
    if (N > 0 && N <= M/8) resize(M/2);
  }
  // hash function for keys - returns value between 0 and M-1
  private int hash(String key) {
    return (key.hashCode() & 0x7fffffff) % M;
  }
  //An inner class to store nodes of a doubly-linked list
  //Each node contains a (key, value) pair
  //Uses a comparable to compare Node <V> objects
  private class Node<V> implements Comparable <Node<V>>{
    private String key;
    private V value;
    private Node<V> next;
    private Node<V> prev;

    Node(String key, V value){
      this(key, value, null, null);
    }
    //Compare nodes by value
    public int compareTo(Node<V> t){
      Comparable val = (Comparable)this.value;
      return val.compareTo(t.value);
    }

    Node(String key, V value, Node<V> next, Node<V> prev){
      this.key = key;
      this.value = value;
      this.next = next;
      this.prev = prev;
    }
  }

  private class MyCompare implements Comparator <Node<V>> {
    public int compare(Node<V> x ,Node<V> y){
      return x.compareTo(y);
    }
  }


  public class MyIterator implements Iterator<V> {
    private Node<V> current;

    public MyIterator() {
      current = head;
    }

    public boolean hasNext() {
      return current != null;
    }

    public V next() {
      V result = current.value;
      current = current.next;
      return result;
    }
}
  public static class Pair<V>{
    String key;
    public V value;
  }
  //method to return pairs
   public Pair<V> each(){
     Pair<V> returnPair;
     if(this.curr == null){
       return null;
     }
     else{
       returnPair = new Pair<V>();
       returnPair.key = this.curr.key;
       returnPair.value = this.curr.value;
       curr = this.curr.next;
       return returnPair;
     }
   }
   //sets the current to the head for reset
   public void reset(){
     this.curr = head;
   }
   //puts keys into arraylist
   public ArrayList<String> keys(){
     Node <V> start = head;
     ArrayList<String> returnStrings = new ArrayList<String>();
     while(start != null){
       returnStrings.add(start.key);
       start = start.next;
     }
     return returnStrings;
   }
   //puts values into arraylist
   public ArrayList<V> values(){
     ArrayList<V> returnValues = new ArrayList<V>();
     Node <V> start = head;
     while(start != null){
       returnValues.add(start.value);
       start = start.next;
     }
     return returnValues;
   }
   public int length(){
     return N;
 }
 //method to show the raw table of entries
 public void showTable(){
     System.out.println("\tRaw Hash Table Contents");
     for(int x = 0; x < entries.length; x++){
        System.out.print(x + " : ");
       if(entries[x] != null){
         System.out.println(entries[x].key + " Value: " + entries[x].value);
       }
       else{
         System.out.println("null");
       }
     }
   }
   //sorts the arraylist by value
   public void sort(){
     ArrayList<Integer> newList = (ArrayList<Integer>)values();
     Collections.sort(newList);
     PHPArray<V> newArray = new PHPArray<>(M);
     for(int x = 0; x < newList.size(); x ++){
       newArray.put(x, (V)newList.get(x));
     }
     head = newArray.head;
     tail = newArray.tail;
     curr = head;
     entries = newArray.entries;
   }
   //sorts the arraylist keeping keys the same
   public void asort(){
     Node<V> cur = head;
     ArrayList<Node<V>> myList = new ArrayList<Node<V>>();
      while(cur != null){
       myList.add(cur);
       cur = cur.next;
     }
     Collections.sort(myList, new MyCompare());
     PHPArray<V> newArray = new PHPArray<V>(M);
     for(int x = 0; x < myList.size(); x++){
       cur = myList.get(x);
       newArray.put(cur.key, (V)cur.value);
     }
     head = newArray.head;
     tail = newArray.tail;
     curr = head;
     entries = newArray.entries;
   }

   //flips the array
   public PHPArray <String> array_flip() throws ClassCastException{
     Node <V> curr = head;
     ArrayList<String> keys = (ArrayList<String>)keys();
     ArrayList<String> values = (ArrayList<String>)values();
     PHPArray<String> newArray = new PHPArray<String>(M);
     if(curr.value instanceof String == true){
     while(curr != null){
       newArray.put((curr.value).toString(), curr.key);
       curr = curr.next;
     }
   }
     else{
       throw new ClassCastException("Cannot convert class java.lang.Integer to String");
     }
     return newArray;
   }
//for extra credit, changes the arrays key case
   public void array_change_key_case(){
     Node <V> curr = head;
     ArrayList<String> keys = (ArrayList<String>)keys();
     ArrayList<String> values = (ArrayList<String>)values();
     PHPArray<V> newArray = new PHPArray<V>(M);
     String transfer = curr.key;
     for(int x = 0; x < keys.size(); x++){
       if(curr != null){
       transfer = curr.key;
       transfer = transfer.toUpperCase();
       curr.key = transfer;
       curr = curr.next;
      }
     }
     curr = head;
     for(int x = 0; x < keys.size(); x ++){
       if(curr != null)
      newArray.put(curr.key, curr.value);
      curr = curr.next;
     }
     head = newArray.head;
     tail = newArray.tail;
     curr = head;
     entries = newArray.entries;
   }
}
