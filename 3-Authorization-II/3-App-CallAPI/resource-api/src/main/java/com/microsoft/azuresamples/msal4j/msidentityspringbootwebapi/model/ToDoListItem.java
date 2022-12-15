package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi.model;

/**
 * @author v-yuhaoyang
 *
 */

public class ToDoListItem {
    
    private int id; 
    
    private String owner;
    
    private String todo;


    public ToDoListItem(String owner, String todo) {
        super();
        this.owner = owner;
        this.todo = todo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

}
