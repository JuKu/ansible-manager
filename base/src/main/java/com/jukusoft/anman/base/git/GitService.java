package com.jukusoft.anman.base.git;

import com.google.common.hash.Hashing;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/*
 * a service which is responsible for git repositories, e.q. which holds the playbooks.
 *
 * @author Justin Kuenzel
 */
@Service
public class GitService {

	/**
	 * the logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);

	/**
	 * the base directory for git repositories to clone.
	 */
	private String baseDir;

	/**
	 * a salt for the directory hashing.
	 */
	private String hashSalt;

	/**
	 * the constructor.
	 *
	 * @param baseDir the base dir of the directory, ending with "/"
	 * @param hashSalt a salt value
	 */
	public GitService(@Value("${git.repositories.basepath}") String baseDir, @Value("${git.repositories.hashsalt}") String hashSalt) {
		this.baseDir = baseDir;
		this.hashSalt = hashSalt;
	}

	/**
	 * clone a git repository.
	 *
	 * @param uri https uri to git repository
	 *
	 * @return
	 */
	public Optional<Git> clone(String uri) {
		return clone(uri, "master");
	}

	/**
	 * clone a git repository.
	 *
	 * @param uri https uri to git repository
	 * @param branch the git branch
	 *
	 * @return
	 */
	public Optional<Git> clone(String uri, String branch) {
		String dirPath = getRepoPath(uri);
		LOGGER.info("clone new git repository: {}, output path: {}", uri, dirPath);

		try {
			Git git = Git.cloneRepository()
					.setURI(uri)
					.setDirectory(new File(dirPath))
					.setBranch(branch)
					.call();

			LOGGER.info("git repository cloned successfully: {}", uri);

			return Optional.of(git);
		} catch (GitAPIException e) {
			LOGGER.warn("GitAPIException while cloning git repository: {}", uri, e);
			return Optional.empty();
		}
	}

	/**
	 * clone a git repository from a uri, if the repository does not exists local.
	 *
	 * @param uri https uri to git repository
	 *
	 * @return optional instance of git repository
	 */
	public Optional<Git> getOrClone(String uri) {
		return getOrClone(uri, "master");
	}

	/**
	 * clone a git repository from a uri, if the repository does not exists local.
	 *
	 * @param uri https uri to git repository
	 * @param branch the branch to clone or checkout
	 *
	 * @return optional instance of git repository
	 */
	public Optional<Git> getOrClone(String uri, String branch) {
		LOGGER.debug("getOrClone: {}, branch: {}", uri, branch);

		String dirPath = getRepoPath(uri);
		File repoDir = new File(dirPath);

		if (repoDir.exists()) {
			try {
				Git git = Git.open(repoDir);
				return Optional.of(git);
			} catch (IOException e) {
				LOGGER.warn("IOException while opening git repository: {}", repoDir.getAbsolutePath(), e);
				return Optional.empty();
			}
		} else {
			return clone(uri, branch);
		}
	}

	/**
	 * get the repository path.
	 *
	 * @param repositoryURI the https uri to the repository
	 *
	 * @return path to local repository directory
	 */
	public String getRepoPath(String repositoryURI) {
		return baseDir + File.pathSeparator + hashValue(repositoryURI);
	}

	/**
	 * hash a value with the Guava library.
	 *
	 * @param value the value to hash, e.q. a directory name
	 *
	 * @return hashed value
	 */
	protected String hashValue(String value) {
		return Hashing.sha256()
				.hashString(value + hashSalt, StandardCharsets.UTF_8)
				.toString();
	}

	/**
	 * this method is called from spring after bean initialization.
	 * It is responsible for creating the git repositories directory, if not exists.
	 */
	@PostConstruct
	protected void postConstruct() {
		Objects.requireNonNull(baseDir);

		//create temporary directory for git repositories
		new File(baseDir).mkdirs();
	}

}
