package com.jukusoft.anman.base.git;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/*
 * some tests for the GitService.
 *
 * @author Justin Kuenzel
 */
class GitServiceTest {

	/**
	 * the temporary directory.
	 * On Linux it is "/tmp/repositories", under windows it is "/.temp/repositories".
	 */
	private final String TEMP_DIR = "./temp/repositories/";

	/**
	 * the repository uri to clone.
	 */
	private final String REPO_URL = "https://github.com/JuKu/ansible-manager.git";

	/**
	 * test, that hashed values are the same.
	 * If the hashed values changes, this can be problematic, because the directory names would no longer identified.
	 */
	@Test
	void testHashValues() {
		GitService gitService = createGitService();
		assertEquals("dba821cdc66092bf309f14dd350aa3e64acbcac6261a8a122cef8585a44d1252", gitService.hashValue("example"));
		assertEquals("a955429aa389c9dc34dd2e1db9f9acd14fd4fe8af4c52b9272c4f4ebc02b9790", gitService.hashValue("example2"));
	}

	/**
	 * test the postConstruct() method to verify, if the temporary directory is created automatically.
	 *
	 * @throws IOException if the directory could not be created
	 */
	@Test
	void postConstruct() throws IOException {
		GitService service = createGitService();

		//first, delete temporary directory
		FileUtils.deleteDirectory(new File(TEMP_DIR));
		assertFalse(new File(TEMP_DIR).exists());

		service.postConstruct();

		//verify, that the directory was created
		assertTrue(new File(TEMP_DIR).exists());
	}

	/**
	 * test, if a git repository can be cloned.
	 */
	@Test
	void testCloneRepository() throws IOException {
		String uri = "https://github.com/JuKu/ansible-manager-frontend.git";

		GitService service = createGitService();
		Repository repository = service.clone(uri).orElseThrow();

		assertNotNull(repository);
		assertFalse(repository.isBare());

		//check, if repository directory exists
		assertTrue(new File(service.getRepoPath(uri)).exists());

		//check branch
		assertEquals("master", repository.getBranch());

		//check upstream
		assertEquals(1, repository.getRemoteNames().size());
		assertTrue(repository.getRemoteNames().stream().anyMatch(name -> name.equals("origin")));
	}

	/**
	 * test the getOrClone() method.
	 * This method should clone the repository on first call (if the repository not exists) and else only open and get the repository.
	 *
	 * @throws IOException
	 */
	@Test
	void testGetOrClone() throws IOException {
		GitService service = createGitService();

		//check, if repository directory exists
		assertFalse(new File(service.getRepoPath(REPO_URL)).exists());

		//clone repository, this should clone the repository
		Repository repo = service.getOrClone(REPO_URL).orElseThrow();

		//check, if repository directory exists
		assertTrue(new File(service.getRepoPath(REPO_URL)).exists());

		//cal the method again, this should not clone the repository again
		repo = service.getOrClone(REPO_URL).orElseThrow();
		assertTrue(new File(service.getRepoPath(REPO_URL)).exists());

		//FileUtils.deleteDirectory(new File(TEMP_DIR));
	}

	/**
	 * create an example instance of the git service.
	 *
	 * @return instance of git service
	 */
	protected GitService createGitService() {
		//prepare tests
		if (new File(TEMP_DIR).exists()) {
			//first, delete directory, if exists
			try {
				FileUtils.deleteDirectory(new File(TEMP_DIR));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		GitService gitService = new GitService(TEMP_DIR, "salt");
		gitService.postConstruct();

		//verify, that the temporary directory was created
		assertTrue(new File(TEMP_DIR).exists());

		return gitService;
	}

}
