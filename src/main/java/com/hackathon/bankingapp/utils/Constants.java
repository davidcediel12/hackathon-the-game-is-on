package com.hackathon.bankingapp.utils;

import java.util.Set;

public class Constants {

    private Constants(){}

    public static final Set<String> UNPROTECTED_PATHS = Set.of("api/users/register",
            "api/users/login");
}
