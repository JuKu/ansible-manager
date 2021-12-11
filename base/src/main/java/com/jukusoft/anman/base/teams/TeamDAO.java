package com.jukusoft.anman.base.teams;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

/**
 * @author Justin Kuenzel
 */
public interface TeamDAO extends PagingAndSortingRepository<TeamEntity, Long> {

	public Optional<TeamEntity> findOneByName(String name);

	public Optional<TeamEntity> findOneById(long id);

}
