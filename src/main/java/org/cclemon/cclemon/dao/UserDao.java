package org.cclemon.cclemon.dao;

import org.cclemon.cclemon.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<Users, Long> {

}
