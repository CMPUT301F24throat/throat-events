package com.example.pickme.models;

import com.example.pickme.repositories.UserRepository;

/**
 * Class that represents an admin in the app
 * Responsibilities:
 * Models an administrator in the users collection as an extension of user
 **/

public class Admin extends User {

    public Admin(UserRepository userRepository, String userId) {
        super(userRepository, userId);
    }
}
