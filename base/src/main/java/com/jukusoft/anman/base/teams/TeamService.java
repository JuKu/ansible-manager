package com.jukusoft.anman.base.teams;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.utils.UserHelperService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A service for the {@link TeamEntity}.
 *
 * @author Justin Kuenzel
 */
@Service
public class TeamService {

	private final CustomerDAO customerDAO;

	private final UserDAO userDAO;

	/**
	 * the team data access object (repository).
	 */
	private final TeamDAO teamDAO;

	private final UserHelperService userHelperService;

	/**
	 * default constructor.
	 *
	 * @param teamDAO           team data access object (repository)
	 * @param userHelperService user helper service, e.q. to get the user entity from the userID
	 */
	public TeamService(@Autowired CustomerDAO customerDAO, @Autowired UserDAO userDAO, @Autowired TeamDAO teamDAO, @Autowired UserHelperService userHelperService) {
		this.customerDAO = customerDAO;
		this.userDAO = userDAO;
		this.teamDAO = teamDAO;
		this.userHelperService = userHelperService;
	}

	/**
	 * create a new team.
	 *
	 * @param customer    the customer the team belongs to
	 * @param name        the name of the team
	 * @param description the description of the team
	 */
	@Transactional
	public long addTeam(CustomerEntity customer, String name, String description) {
		TeamEntity team = new TeamEntity(customer, name, description);
		cleanTeamMemberCacheByCustomer(customer.getId());

		return teamDAO.save(team).getId();
	}

	@CacheEvict(cacheNames = "team_dto", key = "'team_dto_'.concat(#teamID)")
	@Transactional
	public void deleteTeam(CustomerEntity customer, long teamID, boolean checkForCustomer) {
		//first, check if the team belongs to this customer, so the user can delete it
		if (checkForCustomer) {
			TeamEntity team = teamDAO.findOneById(teamID).orElseThrow();

			if (team.getCustomer().getId() != customer.getId()) {
				throw new IllegalStateException("this team (id: " + teamID + ") does not belong to customer '" + customer.getName() + "', so it cannot be deleted.");
			}
		}

		teamDAO.deleteById(teamID);

		//clear cache
		this.cleanTeamMemberCacheByCustomer(customer.getId());
	}

	@Cacheable(cacheNames = "team_list_by_customer", key = "'team_list_by_customer_'.concat(#customerID)")
	public List<TeamDTO> listAllTeamsOfCustomer(long customerID) {
		Optional<CustomerEntity> customerOpt = customerDAO.findById(customerID);

		if (customerOpt.isEmpty()) {
			throw new IllegalStateException("customer with id " + customerID + " does not exists");
		}

		return customerOpt.get().getTeams().stream()
				.map(this::mapTeamEntityToDTO)
				.toList();
	}

	@Cacheable(cacheNames = "team_dto", key = "'team_dto_'.concat(#teamID)")
	public Optional<TeamDTO> getTeam(long teamID) {
		return teamDAO.findOneById(teamID).map(this::mapTeamEntityToDTO);
	}

	protected TeamDTO mapTeamEntityToDTO(TeamEntity entity) {
		return new TeamDTO(entity.getId(), entity.getName(), entity.getDescription());
	}

	/**
	 * this method checks, if the user is member of the team
	 *
	 * @param userID
	 * @param teamID
	 *
	 * @return
	 */
	@Cacheable(cacheNames = "team_member_state", key = "'team_member_state_'.concat(#userID).concat('_').concat(#teamID)")
	public boolean checkIfUserIsMemberOfTeam(long userID, long teamID) {
		Optional<UserEntity> userEntityOpt = userHelperService.getUserById(userID);

		if (userEntityOpt.isEmpty()) {
			return false;
		}

		Optional<TeamEntity> teamEntityOpt = getTeamEntityById(teamID);

		if (teamEntityOpt.isEmpty()) {
			return false;
		}

		//check, if user is member of team
		return teamEntityOpt.get().getMembers().contains(userEntityOpt.get());
	}

	@CacheEvict(cacheNames = "team_member_state", key = "'team_member_state_'.concat(#userID).concat('_').concat(#teamID)")
	@Transactional
	public void addUserAsMemberOfTeam(long userID, long teamID) {
		UserEntity userEntity = userHelperService.getUserById(userID).orElseThrow();
		TeamEntity teamEntity = teamDAO.findOneById(teamID).orElseThrow();

		teamEntity.addMember(userEntity);

		cleanTeamMemberCache(userID, teamID);
		cleanTeamsOfUserCache(userID);
	}

	@Cacheable(cacheNames = "teams_of_user", key = "'teams_of_user_'.concat(#userID)")
	public List<TeamDTO> listTeamsOfUser(long userID) {
		UserEntity user = userDAO.findById(userID).orElseThrow(() -> new IllegalArgumentException("user with id '" + userID + "' does not exists"));

		return user.getTeams().stream()
				.map(this::mapTeamEntityToDTO)
				.toList();
	}

	@CacheEvict(cacheNames = "team_member_state", key = "'team_member_state_'.concat(#userID).concat('_').concat(#teamID)")
	public void cleanTeamMemberCache(long userID, long teamID) {
		//
	}

	@CacheEvict(cacheNames = "team_list_by_customer", key = "'team_list_by_customer_'.concat(#customerID)")
	public void cleanTeamMemberCacheByCustomer(long customerID) {
		//
	}

	@CacheEvict(cacheNames = "teams_of_user", key = "'teams_of_user_'.concat(#userID)")
	public void cleanTeamsOfUserCache(long userID) {
		//
	}

	protected Optional<TeamEntity> getTeamEntityById(long teamID) {
		return teamDAO.findOneById(teamID);
	}

}
