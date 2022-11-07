package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ToDoList {
    
    private HashMap<Integer, ToDoListItem> todolist = new HashMap<>();
    
    public HashMap<Integer, ToDoListItem> get(){
        return todolist;
    } 
    
    public void add(ToDoListItem tobeadded) {
        int lrgId = 0;
        Iterator toDoIterator = todolist.entrySet().iterator();
        while (toDoIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)toDoIterator.next();
            int currId = (int)entry.getKey();
            if (lrgId <= currId) {
                lrgId = currId;
            }
        }
        todolist.put(lrgId + 1, tobeadded);
    }
    
    public void delete(Integer id) {
        if (todolist.containsKey(id)) {
            todolist.remove(id); 
        }
           
    }
    
    public ToDoListItem getOne(Integer id) {
        if (todolist.containsKey(id)) {
            return todolist.get(id);
        }
        return null;
    }
    
    public void update(Integer id, ToDoListItem editedItem) {
        ToDoListItem attemptEditItem = todolist.get(id);
        if (!attemptEditItem.equals(editedItem)) {
            todolist.replace(id, editedItem);
        }
    }
}
