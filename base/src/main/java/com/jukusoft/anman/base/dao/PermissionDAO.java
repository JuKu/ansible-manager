package com.jukusoft.anman.base.dao;

import com.jukusoft.anman.base.entity.user.PermissionEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionDAO extends PagingAndSortingRepository<PermissionEntity, String> {
}
