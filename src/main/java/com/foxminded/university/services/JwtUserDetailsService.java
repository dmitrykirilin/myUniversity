package com.foxminded.university.services;

import com.foxminded.university.model.Role;
import com.foxminded.university.model.User;
import com.foxminded.university.repository.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
public class JwtUserDetailsService implements UserDetailsService{

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                user.getRoles());
    }


    @Transactional
    public boolean add(User user) {
         User userFromDB = findByUsername(user.getUsername());
        if(userFromDB != null){
            return false;
        }
        if(StringUtils.isEmpty(user.getRoles())) {
            user.setRoles(Collections.singleton(Role.USER));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDAO.save(user);
        log.info("Created user success.");
        return true;
    }


    @Transactional
    public boolean addStartedUsers(){
        if(userDAO.findAll().isEmpty()){
            log.info("admin, worker and user creating");
            this.add(new User("admin", "admin", Collections.singleton(Role.ADMIN)));
            this.add(new User("worker", "worker", Collections.singleton(Role.WORKER)));
            this.add(new User("user", "user", Collections.singleton(Role.USER)));

            return true;
        }
        return false;
    }


    public User findByUsername(String username){
        return userDAO.findByUsername(username);
    }

    public List<User> findAll() {
        return userDAO.findAll();
    }
}
