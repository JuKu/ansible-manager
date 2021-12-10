package com.jukusoft.anman.base.dao;

import com.jukusoft.anman.base.entity.general.CustomerEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * @author Justin Kuenzel
 */
public interface CustomerDAO extends PagingAndSortingRepository<CustomerEntity, Long> {

	public Optional<CustomerEntity> findOneByName(String username);

}
