package com.jukusoft.anman.base.entity.general;

import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.teams.TeamEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This entity represents a single customer (a company).
 * This is the highest structural division.
 * Every customer can have multiple projects and teams.
 *
 * @author Justin Kuenzel
 */
@Entity
@Table(name = "customers", indexes = {
}, uniqueConstraints = {
		//@UniqueConstraint(columnNames = "username", name = "username_uqn")
})
@Cacheable//use second level cache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CustomerEntity extends AbstractEntity {

	/**
	 * customer name, for example the company name.
	 */
	@Size(min = 2, max = 45)
	@Column(name = "name", unique = true, nullable = false, updatable = true)
	@NotEmpty(message = "name is required")
	private String name;

	/**
	 * a list with all users which belongs to this customer (company).
	 */
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Column(name = "customer")
	private List<UserEntity> users;

	/**
	 * a list with all teams which belongs to this customer (company).
	 */
	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@Column(name = "customer")
	private Set<TeamEntity> teams = new HashSet<>();

	/**
	 * a flag, if the customer is deletable.
	 * This flag is required, because the admin "customer" (the first customer) cannot be deleted, because it contains the super admin.
	 */
	@Column(name = "deletable", nullable = false)
	private boolean deletable = true;

	/**
	 * a flag, if the customer is editable.
	 * This flag is required, because the admin "customer" (the first customer) cannot be edited, because it contains the super admin.
	 */
	@Column(name = "editable", nullable = false)
	private boolean editable = true;

	/**
	 * default constructor.
	 *
	 * @param name unique name of the customer
	 */
	public CustomerEntity(@Size(min = 2, max = 45) @NotEmpty(message = "name is required") String name) {
		this.name = name;
	}

	/**
	 * default constructor.
	 */
	protected CustomerEntity() {
		//
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (!this.editable) {
			throw new IllegalStateException("The customer '" + this.getName() + "' is not editable");
		}

		this.name = name;
	}

	public List<UserEntity> getUsers() {
		return users;
	}

	public List<TeamEntity> getTeams() {
		return teams.stream().toList();
	}

	public void addTeam(TeamEntity team) {
		this.teams.add(team);
	}

	public void removeTeam(TeamEntity team) {
		this.teams.remove(team);
	}

	public boolean isDeletable() {
		return deletable;
	}

	public void setDeletable(boolean deletable) {
		this.deletable = deletable;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@PrePersist
	public final void prePersist1() {
		if (this.teams == null) {
			this.teams = new HashSet<>();
		}
	}

}
