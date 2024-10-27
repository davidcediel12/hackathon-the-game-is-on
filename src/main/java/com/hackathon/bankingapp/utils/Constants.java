package com.hackathon.bankingapp.utils;

import java.util.Set;

public class Constants {

    private Constants() {
    }

    public static final Set<String> UNPROTECTED_PATHS = Set.of("api/users/register",
            "api/users/login", "api/auth/password-reset/send-otp",
            "api/auth/password-reset/verify-otp", "api/auth/password-reset");


    public static final String MAIL_PURCHASE_ASSET_INITIAL = """
            Dear Nuwe Test,
            
            You have successfully purchased %.2f units of %s for a total amount of $%.2f.
            
            Current holdings of %s: %.2f units
            
            Summary of current assets:
            """;

    public static final String MAIL_PURCHASE_ASSET_END = """            
            Account Balance: $%.2f
            Net Worth: $%.2f
            
            Thank you for using our investment services.
            
            Best Regards,
            Investment Management Team
            """;

    public static final String ASSET_SUMMARY_LINE = """
            - %s: %.2f units purchased at $%.2f
            """;

}
