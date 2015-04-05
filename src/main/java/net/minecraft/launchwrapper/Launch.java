package net.minecraft.launchwrapper;

import java.util.HashMap;

public class Launch {

    public static HashMap<String, Object> blackboard = new HashMap<>();

    public static void main(String args[]) {
        System.out.println("blackboard="+blackboard);

        System.out.println("TODO: load tweakers");
    }
}
