package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        var user = server.createUser();
        System.out.println(server.getStartMessage());
        while (true) {
            Scanner scanner = new Scanner(System.in);
            String message = scanner.nextLine();
            String answer = server.replyToUser(user, message);
            System.out.println(answer);
        }
    }
}
