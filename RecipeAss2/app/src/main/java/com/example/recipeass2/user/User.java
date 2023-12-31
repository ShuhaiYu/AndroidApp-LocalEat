package com.example.recipeass2.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "user_table")
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "email")
    private String email;
    @NonNull
    @ColumnInfo(name = "password")

    private String password;

    @Embedded(prefix = "address_")
    private Address address;

    // Constructor, getters and setters
    @Ignore
    public User(@NonNull String email, @NonNull String password, Address address) {
        this.email = email;
        this.password = password;
        this.address = address;
    }

    public User() {

    }

    public void setEmail(@NonNull String email) {
        this.email = email;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @NonNull
    public String getPassword() {
        return password;
    }


    public Address getAddress() {
        return address;
    }
}



