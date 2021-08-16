package com.jukusoft.anman.base.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * a password service which is responsible for password encoding.
 *
 * @author Justin Kuenzel
 */
@Service
public class PasswordService {

    /**
     * the password encoder.
     */
    private PasswordEncoder passwordEncoder;

    /**
     * constructor.
     *
     * @param passwordEncoder password encoder
     */
    public PasswordService(@Autowired PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * get the password encoder.
     *
     * @return password encoder
     */
    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    /**
     * encode a password.
     *
     * @param password password to encode
     * @param salt salt to use
     *
     * @return encoded password
     */
    public String encodePassword(String password, String salt) {
        return passwordEncoder.encode(password + salt);
    }

    /**
     * generate a password salt.
     *
     * @return password salt
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        return new String(salt);
    }

}
