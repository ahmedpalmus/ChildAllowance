package com.example.childallowance;

import java.io.Serializable;

public class Child implements Serializable {
    private String id,name, age, image,allergies;
    int buy_limit;

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public int getBuy_limit() {
        return buy_limit;
    }

    public void setBuy_limit(int buy_limit) {
        this.buy_limit = buy_limit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
