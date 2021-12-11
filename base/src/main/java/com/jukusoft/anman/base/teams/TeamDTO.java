package com.jukusoft.anman.base.teams;

/**
 * The data transfer object for the {@link TeamEntity} which is given to the REST api.
 *
 * @author Justin Kuenzel
 */
public class TeamDTO {

	private final long teamID;
	private final String title;
	private final String description;

	public TeamDTO(long teamID, String title, String description) {
		this.teamID = teamID;
		this.title = title;
		this.description = description;
	}

	public long getTeamID() {
		return teamID;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
}
