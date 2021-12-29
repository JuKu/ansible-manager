package com.jukusoft.anman.server.controller.teams;

import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.teams.TeamDTO;
import com.jukusoft.anman.base.teams.TeamService;
import com.jukusoft.anman.base.utils.UserHelperService;
import com.jukusoft.anman.server.controller.SuccessResponseDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This REST controller is responsible for all teams endpoints.
 *
 * @author Justin Kuenzel
 */
@RestController("/teams")
public class TeamController {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

	private final TeamService teamService;

	private final UserHelperService userHelperService;

	/**
	 * default constructor.
	 *
	 * @param teamService       instance of team service to manage teams
	 * @param userHelperService user helper service to get the current user
	 */
	public TeamController(TeamService teamService, UserHelperService userHelperService) {
		this.teamService = teamService;
		this.userHelperService = userHelperService;
	}

	/**
	 * return a list with all teams which the user belongs to.
	 *
	 * @return list with own teams
	 */
	@GetMapping(path = "/teams/list-own-teams")
	public ResponseEntity<List<TeamDTO>> listOwnTeams() {
		return new ResponseEntity<>(teamService.listTeamsOfUser(userHelperService.getCurrentUserID()), HttpStatus.OK);
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

		return new ResponseEntity<>(teamService.listAllTeamsOfCustomer(userHelperService.getCurrentCustomerID()), HttpStatus.OK);
	}

	/**
	 * Create a new customer team.
	 */
	@PutMapping(path = "/teams/create-team")
	public ResponseEntity<String> createTeam(@RequestBody TeamDTO team) {
		//TODO: check permissions

		CustomerEntity customer = userHelperService.getCurrentCustomer();

		//create teams
		long createdTeamID = teamService.addTeam(customer, userHelperService.getCurrentUser(), team.title(), team.description());

		if (createdTeamID < 0) {
			//the team could not be created
			return new ResponseEntity<>(Long.toString(createdTeamID), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>(Long.toString(createdTeamID), HttpStatus.CREATED);
	}

	/**
	 * delete a customer team.
	 *
	 * @param teamID the team id
	 *
	 * @return a response entity with the HTTP status code
	 */
	@DeleteMapping(path = "/teams/delete-team/{teamID}")
	public ResponseEntity<String> deleteTeam(@PathVariable(name = "teamID") long teamID) {
		//TODO: check permissions

		teamService.deleteTeam(userHelperService.getCurrentCustomer(), teamID, true);

		return new ResponseEntity<>("success", HttpStatus.OK);
	}

	@PostMapping(path = "/teams/add-member")
	public ResponseEntity<SuccessResponseDTO> addTeamMember(@RequestParam("teamID") long teamID, @RequestParam("memberID") long newMemberID) {
		//TODO: check permissions

		LOGGER.info("try to add team member to team, teamID: {}, memberID: {}", teamID, newMemberID);

		//check, if the team belongs to the same customer
		if (!teamService.checkForSameCustomer(userHelperService.getCurrentCustomer(), teamID)) {
			LOGGER.warn("Security violation - the requested team (to add team member) does not belongs to the customer");
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}

		teamService.addUserAsMemberOfTeam(teamID, newMemberID);
		return new ResponseEntity<>(new SuccessResponseDTO(true), HttpStatus.OK);
	}

	@DeleteMapping(path = "/teams/remove-member")
	public ResponseEntity<SuccessResponseDTO> removeTeamMember(@RequestParam("teamID") long teamID, @RequestParam("memberID") long memberIDToRemove) {
		//TODO: check permissions

		LOGGER.info("try to remove team member from team, teamID: {}, memberID: {}", teamID, memberIDToRemove);

		//check, if the team belongs to the same customer
		if (!teamService.checkForSameCustomer(userHelperService.getCurrentCustomer(), teamID)) {
			LOGGER.warn("Security violation - the requested team (to add team member) does not belongs to the customer");
			return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
		}

		teamService.removeUserAsMemberOfTeam(memberIDToRemove, teamID);
		return new ResponseEntity<>(new SuccessResponseDTO(true), HttpStatus.OK);
	}

}
