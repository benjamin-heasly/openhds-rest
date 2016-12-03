package org.openhds.service.impl;

import org.openhds.errors.model.ErrorLog;
import org.openhds.repository.concrete.PrivilegeRepository;
import org.openhds.repository.concrete.RoleRepository;
import org.openhds.repository.concrete.UserRepository;
import org.openhds.security.model.Privilege;
import org.openhds.security.model.Role;
import org.openhds.security.model.User;
import org.openhds.security.model.UserDetailsWrapper;
import org.openhds.service.contract.AbstractUuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Created by Wolfe on 7/1/2015.
 */
@Component
public class UserService extends AbstractUuidService<User, UserRepository> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    //For default users
    private final PasswordEncoder weakPasswordEncoder = new BCryptPasswordEncoder(4);


    @Autowired
    public UserService(UserRepository userRepository) {
        super(userRepository);
    }

    @Override
    public User makePlaceHolder(String id, String name) {
        User user = new User();
        user.setUuid(id);
        user.setUsername(name);
        user.setPasswordHash(name);
        user.setFirstName(name);
        return user;
    }


    public User findByUsername(String username) {
        Optional<User> user = repository.findByUsername(username);

        if(user.isPresent()) {
            return user.get();
        } else {
            throw new NoSuchElementException("No user found with username: " + username);
        }
    }

    public Role findRoleByName(String name) {
        return roleRepository.findByName(name).get();
    }

    public Privilege createOrUpdate (Privilege privilege) {
        return privilegeRepository.save(privilege);
    }

    public Role createOrUpdate(Role role) {
        return roleRepository.save(role);
    }

    public long countPrivileges() {
        return privilegeRepository.count();
    }

    public long countRoles() {
        return roleRepository.count();
    }

    public Privilege findPrivilegeByGrant(Privilege.Grant grant) {
        return privilegeRepository.findByGrant(grant).get();
    }

    public User getCurrentUser() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (null == authentication) {
            return getUnknownEntity();
        }

        Object principal = authentication.getPrincipal();
        if (null == principal || !(principal instanceof UserDetailsWrapper)) {
            return getUnknownEntity();
        }

        UserDetailsWrapper wrapper = (UserDetailsWrapper) principal;
        return wrapper.getUser();
    }

    public User recordUser(User user, String password) {
        user.setPasswordHash(passwordEncoder.encode(password));
        return createOrUpdate(user);
    }

    /**
     *
     * @param user
     * @param password
     * @return created user
     * WARNING: This should only be used for default testing/setup accounts
     */
    public User recordUserWeakPassword(User user, String password) {
        user.setPasswordHash(weakPasswordEncoder.encode(password));
        return createOrUpdate(user);
    }
}
