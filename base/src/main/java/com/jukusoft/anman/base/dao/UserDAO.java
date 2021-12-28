package com.jukusoft.anman.base.dao;

import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.teams.TeamEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDAO extends CrudRepository<UserEntity, Long>, RefreshDAO<UserEntity, Long> {

    public Optional<UserEntity> findOneByUsername(String username);
    List<UserEntity> findAll();

}
