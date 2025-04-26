package org.example.models;

import java.util.Objects;

public class Category_equipement {
    private int id;
    private String type;

    public Category_equipement(int id,String type) {
        this.id = id;
        this.type = type;
    }
    public Category_equipement(String type) {
        this.type=type;
    }
    public Category_equipement(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Category_equipement{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category_equipement that = (Category_equipement) o;
        return id == that.id && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
