package com.jukusoft.anman.base.security;

import java.util.Collection;
import java.util.List;

/*
 * this interface is for auth provider plugins which can extend the application with new authenticaton mechanism like LDAP.
 *
 * @author Justin Kuenzel
 */
public interface AuthProvider {

    /**
     * get name of authentication provider.
     *
     * @return name of authentication provider
     */
    public String getName();

    /**
     * try to login user.
     *
     * @param username username
     * @param password password
     *
     * @return true, if credentials are correct, else false
     */
    public boolean login(String username, String password);

    /**
     * list all roles of user.
     *
     * @return all roles of user
     */
    public Collection<String> listRoles(String username);

    /**
     * check, if the user has a specific role.
     *
     * @param username username
     * @param role requested role
     *
     * @return true, if the user has this role
     */
    public boolean hasRole(String username, String role);

}
