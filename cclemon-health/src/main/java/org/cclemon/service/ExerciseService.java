package org.cclemon.service;

import lombok.AllArgsConstructor;
import org.cclemon.entity.Exercise;
import org.cclemon.repository.CclemonUserRepository;
import org.cclemon.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    private final CclemonUserRepository cclemonUserRepository;

    public Exercise insert() throws InterruptedException {
        var user = cclemonUserRepository.findById(10L);
        var insert = new Exercise();
        insert.setCalorie(1000L);
        insert.setName("balls" + UUID.randomUUID());
        insert.setUnit("份");
        insert.setUser(user.orElseThrow());
        return exerciseRepository.save(insert);
    }
}
