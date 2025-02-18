package com.example.epam.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ActivationStatus {
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
