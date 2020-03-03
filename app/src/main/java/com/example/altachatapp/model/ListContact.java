package com.example.altachatapp.model;

import java.util.ArrayList;


public class ListContact {
    private ArrayList<FetchContacts> listContact;

    public ArrayList<FetchContacts> getListContact() {
        return listContact;
    }

    public ListContact(){
        listContact = new ArrayList<>();
    }

    public String getAvataById(String id){
        for(FetchContacts contact: listContact){
            if(id.equals(contact.id)){
                return contact.avata;
            }
        }
        return "";
    }

    public void setListContact(ArrayList<FetchContacts> listContact) {
        this.listContact = listContact;
    }
}
