package com.jukusoft.anman.base.dao;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.Serializable;

/**
 * This is a custom implementation of the {@link RefreshDAO} DAO.
 * This DAO adds support for refresh entities.
 * See also: https://dzone.com/articles/adding-entitymanagerrefresh-to-all-spring-data-rep .
 *
 * @author Justin Kuenzel
 */
public class RefreshDAOImpl<T, ID extends Serializable> extends SimpleJpaRepository<T,ID> implements RefreshDAO<T, ID> {

	private final EntityManager entityManager;

	public RefreshDAOImpl(JpaEntityInformation entityInformation,
						  EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
	}

	@Override
	@Transactional
	public void refresh(T t) {
		entityManager.refresh(t);
	}

}
