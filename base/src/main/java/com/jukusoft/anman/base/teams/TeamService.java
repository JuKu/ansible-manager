package com.jukusoft.anman.base.teams;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.utils.UserHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * A service for the {@link TeamEntity}.
 *
 * @author Justin Kuenzel
 */
@Service
public class TeamService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

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
	@Caching(evict = {
			@CacheEvict(cacheNames = "teams_of_user", key = "'teams_of_user_'.concat(#firstTeamOwner.getUserID())"),
			@CacheEvict(cacheNames = "team_list_by_customer", key = "'team_list_by_customer_'.concat(#customer.getId())")
	})
	public long addTeam(CustomerEntity customer, UserEntity firstTeamOwner, String name, String description) {
		LOGGER.info("create a new team with name: '{}' (customer: {}, creator user: {})", name, customer.getId(), firstTeamOwner.getUserID());

		TeamEntity team = new TeamEntity(customer, name, description);

		long teamID = teamDAO.save(team).getId();

		//fetch the team entity after save from database, to get the current state
		team = teamDAO.findOneById(teamID).orElseThrow();

		//every team required at least one team member - so we add a user to this team
		team.addMember(firstTeamOwner);
		teamDAO.save(team);

		cleanTeamMemberCacheByCustomer(customer.getId());
		cleanTeamsOfUserCache(firstTeamOwner.getUserID());

		return teamID;
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "team_dto", key = "'team_dto_'.concat(#teamID)"),
			@CacheEvict(cacheNames = "team_list_by_customer", key = "'team_list_by_customer_'.concat(#customer.getId())")
	})
	@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	public void deleteTeam(CustomerEntity customer, long teamID, boolean checkForCustomer) {
		LOGGER.info("delete team with id: {}", teamID);

		//first, check if the team belongs to this customer, so the user can delete it
		if (checkForCustomer) {
			if (!checkForSameCustomer(customer, teamID)) {
				throw new IllegalStateException("this team (id: " + teamID + ") does not belong to customer '" + customer.getName() + "', so it cannot be deleted.");
			}
		}

		//clear cache of users which are members of this team
		deleteAllTeamMembers(teamID);

		//delete team from customer to avoid caching issues
		TeamEntity team = teamDAO.findOneById(teamID).orElseThrow();
		CustomerEntity customer1 = team.getCustomer();
		customer1.removeTeam(team);
		customerDAO.save(customer1);

		teamDAO.deleteById(teamID);

		//clear cache
		this.cleanTeamMemberCacheByCustomer(customer.getId());

	}

	@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	protected boolean checkForSameCustomer(CustomerEntity customer, long teamID) {
		TeamEntity team = teamDAO.findOneById(teamID).orElseThrow();

		return team.getCustomer().getId() == customer.getId();
	}

	@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	protected void deleteAllTeamMembers(long teamID) {
		TeamEntity team = teamDAO.findOneById(teamID).orElseThrow();

		//clear cache of users which are members of this team
		for (UserEntity member : team.getMembers()) {
			cleanTeamsOfUserCache(member.getUserID());
			cleanTeamMemberCache(member.getUserID(), teamID);

			//TODO: this is a quick & dirty fix. Fix the caching instead.
			userDAO.refresh(member);
			member.removeTeam(team);
			member = userDAO.save(member);

			team.removeMember(member);
		}

		teamDAO.save(team);
	}

	@Cacheable(cacheNames = "team_list_by_customer", key = "'team_list_by_customer_'.concat(#customerID)")
	@Transactional
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
		teamDAO.save(teamEntity);

		cleanTeamMemberCache(userID, teamID);
		cleanTeamsOfUserCache(userID);
	}

	@CacheEvict(cacheNames = "team_member_state", key = "'team_member_state_'.concat(#userID).concat('_').concat(#teamID)")
	@Transactional
	public void removeUserAsMemberOfTeam(long userID, long teamID) {
		UserEntity userEntity = userHelperService.getUserById(userID).orElseThrow();
		TeamEntity teamEntity = teamDAO.findOneById(teamID).orElseThrow();

		teamEntity.removeMember(userEntity);
		teamDAO.save(teamEntity);

		cleanTeamMemberCache(userID, teamID);
		cleanTeamsOfUserCache(userID);
	}

	@Cacheable(cacheNames = "teams_of_user", key = "'teams_of_user_'.concat(#userID)")
	@Transactional
	public List<TeamDTO> listTeamsOfUser(long userID) {
		UserEntity user = userDAO.findById(userID).orElseThrow(() -> new IllegalArgumentException("user with id '" + userID + "' does not exists"));

		return user.getTeams().stream()
				.map(this::mapTeamEntityToDTO)
				.toList();
	}

	@CacheEvict(cacheNames = "team_member_state", key = "'team_member_state_'.concat(#userID).concat('_').concat(#teamID)")
	public void cleanTeamMemberCache(long userID, long teamID) {
		LOGGER.debug("clean team member cache for user: {} and team: {}", userID, teamID);
	}

	@CacheEvict(cacheNames = "team_list_by_customer", key = "'team_list_by_customer_'.concat(#customerID)")
	public void cleanTeamMemberCacheByCustomer(long customerID) {
		LOGGER.debug("clean team member cache by customer: {}", customerID);
	}

	@CacheEvict(cacheNames = "teams_of_user", key = "'teams_of_user_'.concat(#userID)")
	public void cleanTeamsOfUserCache(long userID) {
		LOGGER.debug("clean teams of user cache: {}", userID);
	}

	@Caching(evict = {
			@CacheEvict(cacheNames = "team_member_state", allEntries = true),
			@CacheEvict(cacheNames = "team_list_by_customer", allEntries = true),
			@CacheEvict(cacheNames = "teams_of_user", allEntries = true)
	})
	public void cleanAllCaches() {
		LOGGER.info("Clean all TeamService caches");
	}

	protected Optional<TeamEntity> getTeamEntityById(long teamID) {
		return teamDAO.findOneById(teamID);
	}

}
