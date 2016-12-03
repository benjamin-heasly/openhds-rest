package org.openhds;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhds.repository.concrete.UserRepository;
import org.openhds.resource.controller.UserRestController;
import org.openhds.security.model.Role;
import org.openhds.security.model.User;
import org.openhds.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserRoleTest {

    @Rule public ExpectedException expectedException = ExpectedException.none();

    private static final String validUsername = "validUser";
    private static final String invalidUsername = "invalidUser";

    private UserRepository repository;

    private UserRestController controller;

    @Before
    public void setUp() {
        repository = mock(UserRepository.class);
        controller = new UserRestController(new UserService(repository));
    }

    @Test
    public void validUser_getUserRoles_returnsRoles() {
        User validUser = new User();
        Role userRole = new Role();
        userRole.setName("test-role");
        Set<Role> roleSet = Sets.newHashSet(userRole);
        validUser.setRoles(roleSet);
        when(repository.findByUsername(validUsername)).thenReturn(Optional.of(validUser));

        Set<Role> validUsername = controller.getUserRoles(UserRoleTest.validUsername);

        assertEquals(roleSet, validUsername);
    }

    @Test
    public void invalidUser_getUserRoles_throwsException() {
        expectedException.expect(NoSuchElementException.class);
        expectedException.expectMessage("No user found with username: invalidUser");
        when(repository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        controller.getUserRoles(invalidUsername);
    }
}
