package tn.esprit.pidev.Model;

import java.sql.Date;
import java.util.Arrays;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private String[] roles;  // Changed from String role to String[] roles
    private String specialite;
    private String firstName;
    private String lastName;
    private String photo;
    private String address;
    private Date birthDate;
    private String phoneNumber;
    public static User connecte;

    // Default constructor
    public User() {
    }

    // Constructor for login
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Constructor for basic user creation
    public User(String email, String password, String firstName, String lastName, String[] roles) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roles = roles;
    }

    // Full constructor
    public User(int id, String email, String password, String name, String[] roles, String specialite,
                String firstName, String lastName, String photo, String address, Date birthDate, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.roles = roles;
        this.specialite = specialite;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.address = address;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", roles=" + Arrays.toString(roles) +
                '}';
    }
}