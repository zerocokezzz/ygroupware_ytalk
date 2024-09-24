package com.ygroupware.ytalk.service;

import com.ygroupware.ytalk.entity.Members;
import com.ygroupware.ytalk.repository.MembersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private MembersRepository membersRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {

        Members members = membersRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found: " + id));

        return User.builder()
                .username(members.getId())
                .password(members.getPassword())
                .authorities(members.getRole())
                .build();
    }

}