package com.jukusoft.anman.base.teams;

import com.jukusoft.anman.base.entity.general.AbstractEntity;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.ArrayList;
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
	private CustomerEntity customer;

	/**
	 * team name, for example a company division name.
	 */
	@Size(min = 2, max = 255)
	@Column(name = "name", unique = false, nullable = false, updatable = true)
	@NotEmpty(message = "name is required")
	private String name;

	/**
	 * the description of the team.
	 */
	@Size(min = 2, max = 900)
	@Column(name = "description", unique = false, nullable = false, updatable = true)
	@NotEmpty(message = "description is required")
	private String description;

	/**
	 * a list of all team members.
	 */
	@ManyToMany(/*mappedBy = "teams",*/ cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
	@JoinTable(
			name = "team_members",
			joinColumns = @JoinColumn(name = "team_id"),
			inverseJoinColumns = @JoinColumn(name = "user_id"))
	private List<UserEntity> members;

	/**
	 * default constructor.
	 *
	 * @param customer the customer, where the teams belongs to
	 * @param name the name of the team
	 * @param description the description of the team
	 */
	public TeamEntity(CustomerEntity customer, String name, String description) {
		this.customer = customer;
		this.name = name;
		this.description = description;
	}

	/**
	 * private constructor, required by spring jpa.
	 */
	private TeamEntity() {
		//
	}

	public CustomerEntity getCustomer() {
		return customer;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public List<UserEntity> getMembers() {
		return members;
	}

	public void addMember(UserEntity user) {
		this.members.add(user);
	}

	@PrePersist
	public final void prePersist1() {
		if (this.members == null) {
			this.members = new ArrayList<>();
		}
	}

}
