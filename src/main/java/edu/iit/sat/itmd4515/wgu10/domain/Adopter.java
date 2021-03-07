/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.iit.sat.itmd4515.wgu10.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

/**
 *
 * @author wenganGu
 */
@Entity
public class Adopter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    // bi-directional relationship - non-owning (inverse) side
    @OneToMany(mappedBy = "adopter")
    private List<Appointment> appointments = new ArrayList<>();

    // bi-directional relationship - owning side
    @ManyToMany
    @JoinTable(name = "ADOPTED_PETS",
            joinColumns = @JoinColumn(name = "ADOPTER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PET_ID"))
    private List<Pet> adoptedPets = new ArrayList<>();

    public Adopter() {
    }

    public Adopter(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
    }

    // helper methods for collections
    public void addAdoptedPet(Pet p) {
        if (!this.adoptedPets.contains(p)) {
            this.adoptedPets.add(p);
        }
        if (!p.getOwners().contains(this)) {
            p.getOwners().add(this);
        }
    }

    public void removeAdoptedPet(Pet p) {
        if (this.adoptedPets.contains(p)) {
            this.adoptedPets.remove(p);
        }
        if (p.getOwners().contains(this)) {
            p.getOwners().remove(this);
        }
    }

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public List<Pet> getAdoptedPets() {
        return adoptedPets;
    }

    public void setAdoptedPets(List<Pet> adoptedPets) {
        this.adoptedPets = adoptedPets;
    }

    @Override
    public String toString() {
        return "Adopter{" + "id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", birthDate=" + birthDate + '}';
    }

}
