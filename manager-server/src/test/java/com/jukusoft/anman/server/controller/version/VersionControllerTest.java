package com.jukusoft.anman.server.controller.version;

import com.jukusoft.anman.base.version.VersionService;
import com.jukusoft.anman.server.utils.RestTest;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This class tests the {@link VersionController}.
 *
 * @author Justin Kuenzel
 */
@WebMvcTest(controllers = {VersionController.class})
@Import(VersionControllerTest.TestConfig.class)
@ActiveProfiles({"rest-test"})
class VersionControllerTest extends RestTest {

	@TestConfiguration
	public static class TestConfig {

		@Bean
		@Primary
		public VersionService versionService() {
			return new VersionService(Mockito.mock(BuildProperties.class), Mockito.mock(GitProperties.class));
		}

	}

	@Test
	void testGetVersion() throws Exception {
		this.mockMvc.perform(MockMvcRequestBuilders.get("/api/version")).andDo(print()).andExpect(status().isOk())
				.andExpect(result -> {
					JSONObject json = new JSONObject(result.getResponse().getContentAsString());
					assertThat(json.has("version")).isTrue();
					assertThat(json.has("commitId")).isTrue();
					assertThat(json.has("branch")).isTrue();
					assertThat(json.has("commitTime")).isTrue();
				});
	}

}
