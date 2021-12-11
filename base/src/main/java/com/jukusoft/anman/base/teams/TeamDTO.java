package com.jukusoft.anman.base.teams;

/**
 * The data transfer object for the {@link TeamEntity} which is given to the REST api.
 *
 * @author Justin Kuenzel
 */
public record TeamDTO(long teamID, String title, String description) {
	//
}