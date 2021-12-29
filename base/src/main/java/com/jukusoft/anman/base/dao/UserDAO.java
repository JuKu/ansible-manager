package com.jukusoft.anman.base.dao;

import com.jukusoft.anman.base.entity.user.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends CrudRepository<UserEntity, Long>, RefreshDAO<UserEntity, Long> {

	//@Cacheable(cacheNames = "")
	public Optional<UserEntity> findOneByUsername(String username);

	List<UserEntity> findAll();

	//@Modifying(clearAutomatically = true)
	UserEntity save(User entity);

	//@Modifying
	//@CacheEvict
	void deleteAll();

}
