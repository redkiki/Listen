package com.example.listen;

public class Book
{
    private int id;

    private String name;

    public Book(int i,String s)
    {
        this.id=i;
        this.name=s;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

