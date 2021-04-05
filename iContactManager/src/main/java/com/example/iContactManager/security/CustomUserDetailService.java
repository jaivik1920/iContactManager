package com.example.iContactManager.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.iContactManager.Entity.User;
import com.example.iContactManager.Repository.UserRepo;

public class CustomUserDetailService implements UserDetailsService {
	
	@Autowired
	private UserRepo userrepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		User user=userrepo.getUserByUserName(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("User not FOund");
		}
		
		CustomUserDetail customUserDetail=new CustomUserDetail(user);
		
		return customUserDetail;
	}

}
