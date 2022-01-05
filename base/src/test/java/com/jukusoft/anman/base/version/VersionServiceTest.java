package com.jukusoft.anman.base.version;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * This class tests the {@link VersionService}.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest(properties="git.build.version=1.0.0-SNAPSHOT")
@ActiveProfiles({"test"})
class VersionServiceTest {

	@Autowired
	private VersionService versionService;

	@Test
	void testConstructor() {
		VersionService service = new VersionService(Mockito.mock(BuildProperties.class), Mockito.mock(GitProperties.class));
		assertThat(service).isNotNull();
	}

	@Test
	void testGetVersionInformation() {
		BuildProperties buildProperties = Mockito.mock(BuildProperties.class);
		//when(buildProperties.getVersion()).thenReturn("1.0.0-SNAPSHOT");

		assertThat(versionService.getVersionInformation()).isNotNull();
		assertThat(versionService.getVersionInformation().version()).isNotNull();
		assertThat(versionService.getVersionInformation().version()).isNotEmpty();
		assertThat(versionService.getVersionInformation().version()).isEqualTo("1.0.0-SNAPSHOT");
	}

}
