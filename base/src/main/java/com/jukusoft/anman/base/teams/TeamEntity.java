package com.jukusoft.anman.base.teams;

import com.jukusoft.anman.base.entity.general.AbstractEntity;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * This entity represents a single team.
 *
 * @author Justin Kuenzel
 */
@Entity
@Table(name = "teams", indexes = {
		//@Index(columnList = "email", name = "email_idx"),
}, uniqueConstraints = {
		//@UniqueConstraint(columnNames = "username", name = "username_uqn")
})
@Cacheable//use second level cache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TeamEntity extends AbstractEntity {

	@ManyToOne(optional = false, cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", nullable = false, updatable = false)//don't use an extra table, use join column instead
	@NotEmpty(message = "customer cannot be null")
	private CustomerEntity customer;

	/**
	 * team name, for example a company division name.
	 */
	@Size(min = 2, max = 255)
	@Column(name = "name", unique = false, nullable = false, updatable = true)
	@NotEmpty(message = "name is required")
	private String name;

	/**
	 * a list of all team members.
	 */
	@ManyToMany(/*mappedBy = "id", */cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
	private List<UserEntity> members;

}
