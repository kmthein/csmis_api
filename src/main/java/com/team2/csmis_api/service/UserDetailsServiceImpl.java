package com.team2.csmis_api.service;

import com.team2.csmis_api.entity.User;
import com.team2.csmis_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepo;
    @Override
    public User loadUserByUsername(String staffId) throws UsernameNotFoundException {
        return userRepo.findByStaffId(staffId);
    }
}
