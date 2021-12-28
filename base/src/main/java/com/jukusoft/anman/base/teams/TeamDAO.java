package com.jukusoft.anman.base.teams;

import com.jukusoft.anman.base.dao.RefreshDAO;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Justin Kuenzel
 */
public interface TeamDAO extends PagingAndSortingRepository<TeamEntity, Long>, RefreshDAO<TeamEntity, Long> {

	public Optional<TeamEntity> findOneByName(String name);

	public Optional<TeamEntity> findOneById(long id);

	public List<TeamEntity> findAllByCustomer(CustomerEntity customer);

}
