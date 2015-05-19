package org.openhds.security.model;

import org.hibernate.annotations.GenericGenerator;
import org.openhds.Description;
import org.openhds.domain.model.UuidIdentifiable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
@Table(name = "user")
public class User implements Serializable, UuidIdentifiable {

    static final long serialVersionUID = 23L;

    public User() {
    }

    public User(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(length = 32)
    private String uuid;

    @Description(description = "User's first name")
    private String firstName;

    @Description(description = "User's last name")
    private String lastName;

    @Description(description = "User's full name")
    private String fullName;

    @Description(description = "Description of the user.")
    private String description;

    @Description(description = "The name used for logging into the system.")
    private String username;

    @Description(description = "Password associated with the username.")
    private String password;

    @Description(description = "Set of roles applied to the user.")
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = {@JoinColumn(name = "user_uuid")}, inverseJoinColumns = @JoinColumn(name = "role_name"))
    private Set<Role> roles = new HashSet<Role>();

    @Description(description = "Indicator for signaling some data to be deleted.")
    boolean deleted = false;

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Set<String> getPrivilegeNames() {
        return roles.stream()
                .map(r -> r.getPrivileges())
                .flatMap(p -> p.stream())
                .map(Privilege::toString)
                .collect(toSet());
    }
}
