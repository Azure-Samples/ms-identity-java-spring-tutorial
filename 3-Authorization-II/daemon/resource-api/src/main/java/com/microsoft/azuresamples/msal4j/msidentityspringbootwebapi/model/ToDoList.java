package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ToDoList {
    
    private HashMap<Integer, ToDoListItem> TDL = new HashMap<>();
    
    public HashMap<Integer, ToDoListItem> get(){
        return TDL;
    }
    
    public HashMap<Integer, ToDoListItem> getByUser(String user){
        HashMap<Integer, ToDoListItem> userToDoList = new HashMap<>();
        Iterator iterator = TDL.entrySet().iterator();
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
        Iterator toDoIterator = TDL.entrySet().iterator();
        while (toDoIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)toDoIterator.next();
            int currId = (int)entry.getKey();
            if (lrgId <= currId) {
                lrgId = currId;
            }
        }
        TDL.put(lrgId + 1, tobeadded);
    }
    
    public void delete(Integer id) {
        if (TDL.containsKey(id)) {
            TDL.remove(id); 
        }
           
    }
    
    public ToDoListItem getById(Integer id) {
        if (TDL.containsKey(id)) {
            return TDL.get(id);
        }
        return null;
    }
    
    public void edit(Integer id, ToDoListItem editedItem) {
        TDL.put(id, editedItem);
    }

}
