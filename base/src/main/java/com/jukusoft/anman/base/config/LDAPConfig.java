package com.jukusoft.anman.base.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/*
 * ldap configuration.
 *
 * @author Justin Kuenzel
 */
@Configuration
public class LDAPConfig {

	/**
	 * the logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(LDAPConfig.class);

	/**
	 * the ldap url, e.q. "ldap://localhost:389/dc=localdomain,dc=local".
	 *
	 * See also: https://stackoverflow.com/questions/52153346/how-to-configure-spring-to-use-external-ldap-server
	 */
	@Value("${ldap.url}")
	private String ldapUrl;

	/**
	 * the ldap base (suffix).
	 */
	@Value("${ldap.base}")
	private String ldapBase;

	/**
	 * the ldap principal (admin user).
	 */
	@Value("${ldap.principal}")
	private String ldapPricipal;

	/**
	 * the ldap password.
	 */
	@Value("${ldap.password}")
	private String ldapPassword;

	@Value("${ldap.users.ou}")
	private String usersOU;

	@Value("${ldap.user.object}")
	private String userOUType;

	@Value("${ldap.validate.ssl.certificates}")
	private boolean validateSSLCertificates;

	/**
	 * set the ldap context source, if ldap is enabled.
	 *
	 * @return ldap context source
	 */
	@Bean
	@Conditional(LDAPCondition.class)
	public LdapContextSource ldapContextSource() {
		if (!validateSSLCertificates) {
			LOGGER.info("disable SSL certification validation. To enable set ldap.validate.ssl.certificates=true in application.yml");
			LDAPConfig.trustSelfSignedSSL();
		}

		LOGGER.info("ldap is enabled, create ldap context source, ldap url: {}", ldapUrl);
		LdapContextSource contextSource = new LdapContextSource();

		contextSource.setUrl(ldapUrl);
		contextSource.setBase(ldapBase);
		contextSource.setUserDn(ldapPricipal);
		contextSource.setPassword(ldapPassword);

		//set default timeout to 5 seconds
		Map<String, Object> baseEnv = new HashMap<>();
		baseEnv.put("com.sun.jndi.ldap.connect.timeout", "5000");
		baseEnv.put("com.sun.jndi.ldap.read.timeout", "5000");
		contextSource.setBaseEnvironmentProperties(baseEnv);

		return contextSource;
	}

	/**
	 * create a ldap template bean.
	 *
	 * @return ldap template
	 */
	@Bean
	@Conditional(LDAPCondition.class)
	public LdapTemplate ldapTemplate() {
		LdapTemplate ldapTemplate = new LdapTemplate(ldapContextSource());

		//set default timeout to 5 seconds
		ldapTemplate.setDefaultTimeLimit(5000);

		return ldapTemplate;
	}

	/**
	 * get ldap base, e.q. "dc=localdomain,dc=local"
	 *
	 * @return ldap base
	 */
	public String getLdapBase() {
		return ldapBase;
	}

	@Bean(name = "ldap_users_ou")
	public String getUsersOU() {
		return usersOU;
	}

	/**
	 * get the user ou type, e.q. "cn", "uid" or "uuid"
	 *
	 * @return user ou type
	 */
	public String getUserOUType() {
		return userOUType;
	}

	/**
	 * disable SSL certificate validation.
	 *
	 * see also: https://stackoverflow.com/questions/17143858/disabling-ssl-certificate-validation-for-active-directory-server-using-spring-ld
	 */
	protected static void trustSelfSignedSSL() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
					//
				}

				public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
					//
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};
			ctx.init(null, new TrustManager[]{tm}, null);
			SSLContext.setDefault(ctx);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
