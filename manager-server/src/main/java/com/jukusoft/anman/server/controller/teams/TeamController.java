package com.jukusoft.anman.server.controller.teams;

import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.teams.TeamDTO;
import com.jukusoft.anman.base.teams.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This REST controller is responsible for all teams endpoints.
 *
 * @author Justin Kuenzel
 */
@RestController("/teams")
public class TeamController {

	@Autowired
	private TeamService teamService;

	/**
	 * return a list with all teams which the user belongs to.
	 *
	 * @return list with own teams
	 */
	@GetMapping(path = "/teams/list-own-teams")
	public ResponseEntity<List<TeamDTO>> listOwnTeams() {
		//TODO: check permissions

		return null;
	}

	/**
	 * return a list with all teams which are available for this customer.
	 * The permission "customer.can.see.all.teams" is required to use this endpoint.
	 *
	 * @return list with all teams of the customer
	 */
	@GetMapping(path = "/teams/list-customer-teams")
	public ResponseEntity<List<TeamDTO>> listAllCustomerTeams() {
		//TODO: check permissions

		return null;
	}

	/**
	 * Create a new customer team.
	 */
	@PostMapping(path = "/teams/create-team")
	public ResponseEntity<String> createTeam(@RequestBody TeamDTO team) {
		//TODO: check permissions

		//TODO: get customer of current user
		CustomerEntity customer = null;

		//create teams
		long createdTeamID = teamService.addTeam(customer, team.title(), team.description());

		if (createdTeamID < 0) {
			//the team could not be created
			return new ResponseEntity<>(Long.toString(createdTeamID), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(Long.toString(createdTeamID), HttpStatus.CREATED);
	}

}
