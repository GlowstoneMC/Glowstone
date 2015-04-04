package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.exceptions.AuthenticationException;

public class YggdrasilUserAuthentication {

    public void setUsername(String username) {

    }

    public void setPassword(String password) {

    }

    public void logIn() throws AuthenticationException {
        System.out.println("Login");
    }

    public Profile getSelectedProfile() {
        return null;
    }

    public String getAuthenticatedToken() {
        return null;
    }

    public Object getUserProperties() {
        return null;
    }

    public class Profile {
        public String getName() {
            return "profile";
        }

        public Object getId() {
            return null;
        }
    }
}
