package org.openhds.security;

import java.util.Arrays;

class HostValidator {

    static boolean isAllowed(String origin, String permittedUrls) {
        // Allow from everywhere if not specified
        return permittedUrls == null
                || origin != null
                && Arrays.stream(permittedUrls.split(","))
                .filter(
                        addr -> addr.trim().toLowerCase().equals(origin.toLowerCase())
                ).findFirst()
                .isPresent();
    }
}
