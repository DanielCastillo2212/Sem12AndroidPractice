package com.example.vj20231.entities;

public class Contact {
    public int id;
    public String nameContact;
    public String numberContact;
    public String imgContact;

    public Contact(int id, String nameContact, String numberContact, String imgContact) {
        this.id = id;
        this.nameContact = nameContact;
        this.numberContact = numberContact;
        this.imgContact = imgContact;
    }

    public Contact() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameContact() {
        return nameContact;
    }

    public void setNameContact(String nameContact) {
        this.nameContact = nameContact;
    }

    public String getNumberContact() {
        return numberContact;
    }

    public void setNumberContact(String numberContact) {
        this.numberContact = numberContact;
    }

    public String getImgContact() {
        return imgContact;
    }

    public void setImgContact(String imgContact) {
        this.imgContact = imgContact;
    }
}

