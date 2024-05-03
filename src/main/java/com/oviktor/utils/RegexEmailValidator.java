package com.oviktor.utils;

import java.util.regex.Pattern;

public class RegexEmailValidator {

        private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

        public static boolean isValidEmailAddress(String email) {
            return RegexEmailValidator.VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches();
        }
    }

