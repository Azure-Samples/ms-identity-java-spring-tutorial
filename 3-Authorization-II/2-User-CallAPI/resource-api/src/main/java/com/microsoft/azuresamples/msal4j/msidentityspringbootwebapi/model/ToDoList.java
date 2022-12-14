package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class that represents a ToDoList Table
 * Able to store a series of ToDoListItems and can add, delete, edit, or search for ToDoListItems in its store
 */
public class ToDoList {
    
	//Store for ToDoListItem objects
    private HashMap<Integer, ToDoListItem> ItemStore = new HashMap<>();
    
    
	/**
	 * Returns all ToDoListItem objects currently stored
	 */
    public HashMap<Integer, ToDoListItem> get(){
        return ItemStore;
    }
    
    public HashMap<Integer, ToDoListItem> getByUser(String user){
        HashMap<Integer, ToDoListItem> userToDoList = new HashMap<>();
        Iterator iterator = ItemStore.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry)iterator.next();
            ToDoListItem currentTD = (ToDoListItem)entry.getValue();           
            if (currentTD.getOwner().equals(user)) {
                userToDoList.put((Integer)entry.getKey(), currentTD);
            }
        }
        return userToDoList;
    } 
    
    public void add(ToDoListItem tobeadded) {
        int lrgId = 0;
        Iterator toDoIterator = ItemStore.entrySet().iterator();
        while (toDoIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)toDoIterator.next();
            int currId = (int)entry.getKey();
            if (lrgId <= currId) {
                lrgId = currId;
            }
        }
        ItemStore.put(lrgId + 1, tobeadded);
    }
    
    public void delete(Integer id) {
        if (ItemStore.containsKey(id)) {
        	ItemStore.remove(id); 
        }
           
    }
    
    public ToDoListItem getById(Integer id) {
        if (ItemStore.containsKey(id)) {
            return ItemStore.get(id);
        }
        return null;
    }
    
    public void edit(Integer id, ToDoListItem editedItem) {
    	ItemStore.put(id, editedItem);
    }

}
