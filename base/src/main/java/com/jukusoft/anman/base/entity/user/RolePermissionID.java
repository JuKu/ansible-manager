package com.jukusoft.anman.base.entity.user;

import java.io.Serializable;

public class RolePermissionID implements Serializable {

    private long roleID;
    private String permissionID;

    public RolePermissionID(long roleID, String permissionID) {
        this.roleID = roleID;
        this.permissionID = permissionID;
    }

    public RolePermissionID() {
        //
    }

    public long getRoleID() {
        return roleID;
    }

    public void setRoleID(long roleID) {
        this.roleID = roleID;
    }

    public String getToken() {
        return permissionID;
    }

    public void setToken(String token) {
        this.permissionID = token;
    }

}
