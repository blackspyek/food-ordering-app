package com.food.backend.utils.classes;

public class MessageUtil {

    public static String userNotFoundMessage(Long id) {
        return "User with id " + id + " does not exist";
    }

    public static String userDeletedMessage(Long id) {
        return "User with id " + id + " has been deleted";
    }

    public static String userUpdatedMessage(Long id) {
        return "User with id " + id + " has been updated";
    }

    public static String userFoundMessage(Long id) {
        return "User with id " + id + " found";
    }

}