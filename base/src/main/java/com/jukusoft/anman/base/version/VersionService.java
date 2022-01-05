package com.jukusoft.anman.base.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * This service provides version information about the backend system.
 * See also: https://www.baeldung.com/spring-git-information .
 *
 * @author Justin Kuenzel
 */
@Service
public class VersionService {

	private final BuildProperties buildProperties;
	private final GitProperties gitProperties;

	@Value("${git.build.version}")
	private String versionName;

	@Value("${git.branch}")
	private String branch;

	@Value("${git.commit.id.abbrev}")
	private String commitId;

	@Value("${git.commit.time}")
	private String commitTime;

	public VersionService(@Autowired final BuildProperties buildProperties, @Autowired final GitProperties gitProperties) {
		Objects.requireNonNull(buildProperties);
		Objects.requireNonNull(gitProperties);

		this.buildProperties = buildProperties;
		this.gitProperties = gitProperties;

		//this.versionName = buildProperties.getVersion();
	}

	/**
	 * get the current version information for the api endpoint.
	 *
	 * @return version dto
	 */
	public VersionDTO getVersionInformation() {
		Objects.requireNonNull(versionName);
		return new VersionDTO(versionName, commitId, branch, commitTime);
	}

}
