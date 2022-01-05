package com.jukusoft.anman.server.controller.version;

import com.jukusoft.anman.base.version.VersionDTO;
import com.jukusoft.anman.base.version.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Justin Kuenzel
 */
@RestController("/api/version")
public class VersionController {

	private final VersionService versionService;

	public VersionController(@Autowired VersionService versionService) {
		this.versionService = versionService;
	}

	@GetMapping(path = "/api/version")
	public ResponseEntity<VersionDTO> getVersion() {
		//TODO: check permissions

		return new ResponseEntity<>(versionService.getVersionInformation(), HttpStatus.OK);
	}

}
