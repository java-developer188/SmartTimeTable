package com.fast.timetable.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fast.timetable.entity.Login;
import com.fast.timetable.repository.LoginRepository;

@Service
public class LoginService {

	@Autowired
	LoginRepository loginRepository;

	public boolean saveGCMToken(long studentId, String gcmToken) {
		Login login = loginRepository.findByStudentId(studentId);
		if (login != null) {
			login.setToken(gcmToken);
			loginRepository.save(login);
			return true;
		} else {
			return false;
		}

	}

//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		 Student user = loginRepository.findByUsername(username);
//	        if (user == null) {
//
//	            throw new UsernameNotFoundException(username);
//	        }
//	        Login login  = loginRepository.findByStudentId(user.getId());
//	        return new CurrentUser(user,login);
//	}

}
