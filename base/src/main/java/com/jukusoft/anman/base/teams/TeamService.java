package com.jukusoft.anman.base.teams;

import com.jukusoft.anman.base.entity.general.CustomerEntity;
import org.springframework.stereotype.Service;

/**
 * A service for the {@link TeamEntity}.
 *
 * @author Justin Kuenzel
 */
@Service
public class TeamService {

	/**
	 * the team data access object (repository).
	 */
	private final TeamDAO teamDAO;

	/**
	 * default constructor.
	 *
	 * @param teamDAO team data access object (repository).
	 */
	public TeamService(TeamDAO teamDAO) {
		this.teamDAO = teamDAO;
	}

	/**
	 * create a new team.
	 *
	 * @param customer the customer the team belongs to
	 * @param name the name of the team
	 * @param description the description of the team
	 */
	public long addTeam(CustomerEntity customer, String name, String description) {
		TeamEntity team = new TeamEntity(customer, name, description);
		return teamDAO.save(team).getId();
	}

	public void deleteTeam(CustomerEntity customer, long teamID, boolean checkForCustomer) {
		//first, check if the team belongs to this customer, so the user can delete it
		if (checkForCustomer) {
			TeamEntity team = teamDAO.findOneById(teamID).orElseThrow();

			if (team.getCustomer().getId() != customer.getId()) {
				throw new IllegalStateException("this team (id: " + teamID + ") does not belong to customer '" + customer.getName() + "', so it cannot be deleted.");
			}
		}

		teamDAO.deleteById(teamID);
	}

}
