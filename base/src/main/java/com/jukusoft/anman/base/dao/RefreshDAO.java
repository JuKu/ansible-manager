package com.jukusoft.anman.base.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * This DAO is used to refresh entity instances, if the database makes some changes.
 * See also: https://dzone.com/articles/adding-entitymanagerrefresh-to-all-spring-data-rep .
 *
 * @author Justin Kuenzel
 */
@NoRepositoryBean
public interface RefreshDAO<T, ID extends Serializable> extends CrudRepository<T, ID> {

	/**
	 * refresh the entity.
	 *
	 * @param t entity to refresh
	 */
	void refresh(T t);

}
