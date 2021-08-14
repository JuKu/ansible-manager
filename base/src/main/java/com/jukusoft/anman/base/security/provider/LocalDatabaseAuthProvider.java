package com.jukusoft.anman.base.security.provider;

import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.security.AuthProvider;
import com.jukusoft.anman.base.security.ExtendedAccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * local database auth provider, which authenticates users against the local database.
 *
 * @author Justin Kuenzel
 */
@Service
public class LocalDatabaseAuthProvider implements AuthProvider {

	/**
	 * the user dao.
	 */
	private UserDAO userDAO;

	/**
	 * the password encoder.
	 */
	private PasswordEncoder passwordEncoder;

	/**
	 * constructor.
	 *
	 * @param userDAO         user dao
	 * @param passwordEncoder password encoder
	 */
	public LocalDatabaseAuthProvider(@Autowired UserDAO userDAO, @Autowired(required = false) PasswordEncoder passwordEncoder) {
		this.userDAO = userDAO;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public String getName() {
		return "local-database";
	}

	@Override
	public Optional<ExtendedAccountDTO> login(String username, String password) {
		return userDAO.findOneByUsername(username)
				.filter(account -> passwordEncoder.matches(password, account.getPassword()))
				.map(user -> new ExtendedAccountDTO(user.getId(), user.getUsername(), "n/a", "n/a", listRoles(username)));
	}

	@Override
	public Set<String> listRoles(String username) {
		UserEntity userEntity = userDAO.findOneByUsername(username).orElseThrow();
		return userEntity.listRoles().stream().map(role -> role.getName()).collect(Collectors.toSet());
	}

	@Override
	public boolean hasRole(String username, String role) {
		UserEntity userEntity = userDAO.findOneByUsername(username).orElseThrow();
		return userEntity.listRoles().stream()
				.anyMatch(role1 -> role1.getName().equals(role));
	}

}
