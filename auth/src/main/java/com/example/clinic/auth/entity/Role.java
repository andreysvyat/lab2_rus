package com.example.clinic.auth.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public enum Role  {
    ADMIN("ADMIN"),
    USER("USER");

    private String string;

}
