package com.example.PresidentialElection.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.lang.NonNull;


@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
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

    @Column(name="Description")
    private String description;

    @Column(name="Number Votes")
    private long nrVotes = 0;

    public Candidate() {}

    public Candidate(@NonNull String name, @NonNull String surname, @NonNull String email, @NonNull String phoneNumber, @NonNull String username, String description, Long nrVotes) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.description = description;
        this.nrVotes = nrVotes;
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

    @NonNull
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public Long getNrVotes() {
        return nrVotes;
    }

    public void setNrVotes(@NonNull Long nrVotes) {
        this.nrVotes = nrVotes;
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", username='" + username + '\'' +
                ", description='" + description + '\'' +
                ", nrVotes=" + nrVotes +
                '}';
    }
}