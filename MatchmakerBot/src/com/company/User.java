package com.company;

import java.text.ParseException;

public class User {
    private int id;
    private States state;
    private String name;
    private int age;
    private String city;
    private String info;
    //мб таблица для кон.автомата, дабы можно было прямо по ней выводить на экран доступные пути для юзера

    public User(int id) {
        this.id = id;
        this.state = States.START;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setAge(int age){
        this.age = age;
    }

    public void setCity(String city){
        this.city = city;
    }

    public void setInfo(String info){
        this.info = info;
    }

    public States getCurrentState() {
        return this.state;
    }
    public String getName(){return this.name;}
    public int getAge(){return this.age;}
    public String getCity(){return this.city;}
    public String getInfo(){return this.info;}

    public void changeCurrentState(States nextState) {
        //mb validation
        this.state = nextState;
    }
}


