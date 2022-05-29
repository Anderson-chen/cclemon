package org.cclemon.cclemon.controller;

import java.util.List;

import org.cclemon.cclemon.dao.UserDao;
import org.cclemon.cclemon.entity.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * UserApiController
 */
@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    UserDao userDao;

    @GetMapping("/users")
    public List<Users> findAll() {

        return userDao.findAll();
    }

    @PostMapping("/user")
    public Users create(@RequestBody Users user) {

        return userDao.save(user);
    }

    @PutMapping("/user")
    public Users update(@RequestBody Users user) {

        return userDao.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        
        userDao.deleteById(id);
    }

}