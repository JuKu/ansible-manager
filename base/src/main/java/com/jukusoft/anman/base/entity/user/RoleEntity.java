package com.jukusoft.anman.base.entity.user;

import com.jukusoft.anman.base.entity.general.AbstractEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "roles", indexes = {
        //@Index(columnList = "email", name = "email_idx"),
}, uniqueConstraints = {
        //@UniqueConstraint(columnNames = "username", name = "username_uqn")
})
@Cacheable//use second level cache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RoleEntity extends AbstractEntity {

    /**
     * unique name (this is not the title, which is shown in frontend!)
     */
    @Size(min = 2, max = 45)
    @Column(name = "name", nullable = false, updatable = true, unique = true)
    private String name;

    @Size(min = 2, max = 255)
    @Column(name = "title", nullable = false, updatable = true)
    private String title;

    @ManyToMany(/*mappedBy = "id", */cascade = {}, fetch = FetchType.LAZY)
    private List<UserEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "roleID", orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<RolePermissionEntity> permissions;

    /**
     * constructor for global role
     *
     * @param name  unique role name
     * @param title role title
     */
    public RoleEntity(@Size(min = 2, max = 45) String name, @Size(min = 2, max = 45) String title) {
        this.name = name;
        this.title = title;
        this.permissions = new HashSet<>();
    }

    public RoleEntity(@Size(min = 2, max = 45) String name) {
        this.name = name;
        this.title = UUID.randomUUID().toString();
        this.permissions = new HashSet<>();
    }

    private RoleEntity() {
        //
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Set<PermissionEntity> listPermissions() {
        return permissions.stream().map(rolePermission -> rolePermission.getPermission()).collect(Collectors.toSet());
    }

    public void addPermission(PermissionEntity permissionEntity) {
        Objects.requireNonNull(permissionEntity);

        RolePermissionEntity rolePermission = new RolePermissionEntity(this, permissionEntity);

        if (this.permissions.contains(rolePermission)) {
            return;
        }

        this.permissions.add(rolePermission);
    }

    public void removePermission(PermissionEntity permissionEntity) {
        RolePermissionEntity rolePermission = new RolePermissionEntity(this, permissionEntity);
        this.permissions.remove(rolePermission);
    }

}
