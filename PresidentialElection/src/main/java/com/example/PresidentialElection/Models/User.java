package com.example.PresidentialElection.Models;

import jakarta.persistence.*;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Entity
@Table(name="users", uniqueConstraints = {@UniqueConstraint(columnNames={"Username"})})
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "Name")
    @NonNull
    private String name;
    @Column(name = "Surname")
    @NonNull
    private String surname;
    @Column(name = "Email")
    @NonNull
    private String email;
    @Column(name = "Phone No.")
    @NonNull
    private String phoneNumber;
    @Column(name = "Username")
    @NonNull
    private String username;
    @Column(name = "Password")
    @NonNull
    private String password;
    @Column(name="Description")
    private String description;
    @Column(name="Voted")
    @NonNull
    private Boolean voted = false;

    public User() {
        voted = false;
    }

    public User(String name, String surname, String email, String phoneNumber, String username, String password, String description, Boolean voted) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
        this.description = description;
        this.voted = voted;
    }

    @NonNull
    public long getId() {
        return id;
    }

    public void setId(@NonNull long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getSurname() {
        return surname;
    }

    public void setSurname(@NonNull String surname) {
        this.surname = surname;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@NonNull String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    @NonNull
    public void setPassword(String password) {
        this.password = password;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public Boolean getVoted() {
        return voted;
    }

    public void setVoted(Boolean voted) {
        this.voted = voted;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", description='" + description + '\'' +
                ", voted=" + voted +
                '}';
    }
}