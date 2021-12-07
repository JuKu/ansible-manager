package com.jukusoft.anman.base.dao;

import com.jukusoft.anman.base.entity.settings.GlobalSettingEntity;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * the DAO for the GlobalSettingEntity.
 *
 * @author Justin Kuenzel
 */
@Repository
public interface GlobalSettingDAO extends PagingAndSortingRepository<GlobalSettingEntity, String> {

    //

}
