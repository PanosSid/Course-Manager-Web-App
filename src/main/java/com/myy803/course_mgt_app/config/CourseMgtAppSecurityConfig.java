package com.myy803.course_mgt_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class CourseMgtAppSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		// bcrypt online https://bcrypt-generator.com/
		
		UserDetails user1 = User.withUsername("panos")
			     .password("$2a$12$dqwy.JCjdYW/qIxlreHgJeEhO5aJN6Ps/xEqyS3sh4pWFmrT73TX6")
			     .roles("USER")
			     .build();
		
		UserDetails user2 = User.withUsername("pvassil")
			     .password("$2a$12$32hHbte0n8Et3pkzLCzDNu84yV0BeFqgAdZhwnlg.71Vrn4n7iGL6")	//password pvassil
			     .roles("USER")
			     .build();
		
		UserDetails user3 = User.withUsername("zarras")
			     .password("$2a$12$9OSXlW2m.IkcUREadBKVF.gsvetPFtNBOP7N1VczexdFOp8fZsG/.") //password zarras
			     .roles("USER")
			     .build();
		
		auth.inMemoryAuthentication().withUser(user1).withUser(user2).withUser(user3);
		
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
		
}






