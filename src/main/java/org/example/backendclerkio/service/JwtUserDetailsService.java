package org.example.backendclerkio.service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private UserService userService;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<org.example.backendclerkio.entity.User> users = List.of(userService.findByUserEmail(email).get());
        System.out.println("users from database: length: " + users.size());
        if(users.size()==1) {
            System.out.println("found the user in Database: " + users.get(0).getUserEmail());
                return new User(
                        email,
                        users.get(0).getPasswordHash(),
                        new ArrayList<>());
        }   else    {
                throw new UsernameNotFoundException("User not found with email: " + email);
        }
    }
}