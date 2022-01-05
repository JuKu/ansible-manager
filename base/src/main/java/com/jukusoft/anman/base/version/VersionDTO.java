package com.jukusoft.anman.base.version;

/**
 * This class represents the current backend version of the system.
 *
 * @author Justin Kuenzel
 */
public record VersionDTO(String version, String commitId, String branch, String commitTime) {

	//

}
