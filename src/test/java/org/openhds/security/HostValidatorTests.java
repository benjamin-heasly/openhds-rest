package org.openhds.security;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

public class HostValidatorTests {

    private String whitelist = "http://example.com,http://www.example.com";
    private String origin = "http://www.example.com";

    @Test
    public void permittedUrlsNotSpecifiedAllowsAllHosts() {
        assertTrue(HostValidator.isAllowed(origin, null));
    }

    @Test
    public void nullOriginNotPermittedIfHostsAreSpecified() {
        assertFalse(HostValidator.isAllowed(null, whitelist));
    }

    @Test
    public void originPermittedIfHostInWhitelist() {
        assertTrue(HostValidator.isAllowed(origin, whitelist));
    }

    @Test
    public void originNotPermittedIfHostNotInWhitelist() {
        assertFalse(HostValidator.isAllowed(origin + ":8081", whitelist));
    }
}
