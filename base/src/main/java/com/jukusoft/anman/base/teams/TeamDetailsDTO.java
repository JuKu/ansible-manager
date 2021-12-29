package com.jukusoft.anman.base.teams;

import java.util.List;

/**
 * A data transfer object with all overview details of one specific team.
 *
 * @author Justin Kuenzel
 */
public record TeamDetailsDTO(long teamID, String title, String description, long memberCount, long customerID, String customerName, List<String> memberList) {
	//
}
