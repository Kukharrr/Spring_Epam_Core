package com.example.epam.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordUpdate {
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
