package tn.esprit.pidev.Model;


import java.util.Objects;

public class Category {
    private int id;
    private String name;
    private String type;

    // Full constructor
    public Category(int id, String name, String type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    // Convenience constructor (without id)
    public Category(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Legacy constructor (type only) — you can remove if you no longer need it
    public Category(String type) {
        this.type = type;
    }

    // No-arg constructor
    public Category() {}

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // toString includes name now
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    // equals considers name
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category that = (Category) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type);
    }

    // hashCode considers name
    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
}
