package com.company;

public class User {
    private int id;
    private States state;
    //мб таблица для кон.автомата, дабы можно было прямо по ней выводить на экран доступные пути для юзера

    public User(int id) {
        this.id = id;
        this.state = States.START;

    }

    public States getCurrentState() {
        return this.state;
    }

    public void changeCurrentState(States nextState) {
        //mb validation
        this.state = nextState;
    }
}


