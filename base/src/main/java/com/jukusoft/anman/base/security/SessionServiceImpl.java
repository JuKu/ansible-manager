package com.jukusoft.anman.base.security;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.PermissionEntity;
import com.jukusoft.anman.base.entity.user.RoleEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.authentification.jwt.SessionService;
import com.jukusoft.authentification.jwt.account.IAccount;
import com.jukusoft.authentification.jwt.account.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * the session service implementation to get the user object from the username.
 *
 * @author Justin Kuenzel
 */
@Service
public class SessionServiceImpl implements SessionService<UserEntity> {

    @Autowired
    private UserDAO userDAO;

    @Override
    @Transactional
    public UserAccount findUser(String username) {
        Objects.requireNonNull(username);

        if (username.isEmpty()) {
            throw new IllegalStateException("username does not exists: " + username);
        }

        //find user
        Optional<UserEntity> userOptional = userDAO.findOneByUsername(username);
        UserEntity user = userOptional.orElseThrow(() -> new IllegalStateException("username does not exists, but jwt signature is correct: " + username));

        return new UserAccount(user.getId(), user.getUsername(), getGrantedAuthorities(listPermissionsOfUser(user)));
    }

    @Override
    public Set<String> listPermissionsOfUser(IAccount user) {
        Set<String> permissions = new HashSet<>();

        UserEntity userEntity = userDAO.findById(user.getUserID()).orElseThrow();

        for (RoleEntity role : userEntity.listRoles()) {
            for (PermissionEntity permissionEntity : role.listPermissions()) {
                permissions.add(permissionEntity.getToken());
            }
        }

        return permissions;
    }

    @Override
    @Cacheable(cacheNames = "granted_authorities")
    public List<GrantedAuthority> getGrantedAuthorities(Set<String> privileges) {
        return privileges.stream()
                .map(permission -> new SimpleGrantedAuthority(permission))
                .collect(Collectors.toList());
    }

}
