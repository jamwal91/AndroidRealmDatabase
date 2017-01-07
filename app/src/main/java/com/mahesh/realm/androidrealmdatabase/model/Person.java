package com.mahesh.realm.androidrealmdatabase.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by root on 30/12/16.
 */

public class Person extends RealmObject implements Parcelable {

    @PrimaryKey
    private long id;

    private String name;
    private int age;
    private String designation;
    private String location;

    public Person() {
    }

    public Person(String name, int age, String designation, String location) {
        this.name = name;
        this.age = age;
        this.designation = designation;
        this.location = location;
    }

    protected Person(Parcel in) {
        id = in.readLong();
        name = in.readString();
        age = in.readInt();
        designation = in.readString();
        location = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(age);
        dest.writeString(designation);
        dest.writeString(location);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
