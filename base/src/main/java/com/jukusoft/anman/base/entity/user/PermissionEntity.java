package com.jukusoft.anman.base.entity.user;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * this table contains a list with all possible permissions
 */
@Entity
@Table(name = "permissions", indexes = {
        //@Index(columnList = "email", name = "email_idx"),
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = "token", name = "token_uqn")
})
@Cacheable//use second level cache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PermissionEntity implements Serializable {

    public static enum PermissionType {
        /*
         * a global permission for all users and all customers (e.q. super admin)
         */
        GLOBAL,

        /**
         * a project local permission, which is only set for a specific project
         */
        PROJECT_LOCAL,

        /**
         * a customer local permission, which is only set for a specific customer (customer-dependent permission)
         */
        CUSTOMER_LOCAL
    }

    @Id
    @Size(max = 50)
    @Column(name = "token", nullable = false, updatable = false)
    private String token;

    @Size(min = 2, max = 90)
    @Column(name = "title", nullable = false, updatable = true, unique = true)
    private String title;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "perm_type", nullable = false, updatable = false)
    private PermissionType type;

    public PermissionEntity(@Size(max = 50) String token, @Size(min = 2, max = 45) String title, PermissionType type) {
        this.token = token;
        this.title = title;
        this.type = type;
    }

    protected PermissionEntity() {
        //
    }

    public String getToken() {
        return token;
    }

    public String getTitle() {
        return title;
    }

    public PermissionType getType() {
        return type;
    }

}
