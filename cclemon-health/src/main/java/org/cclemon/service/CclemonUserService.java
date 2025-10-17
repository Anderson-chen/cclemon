package org.cclemon.service;

import lombok.AllArgsConstructor;
import org.cclemon.entity.CclemonUser;
import org.cclemon.repository.CclemonUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CclemonUserService {

    private final CclemonUserRepository userRepository;

    public Optional<CclemonUser> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<CclemonUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public CclemonUser save(CclemonUser user) {
        return userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}