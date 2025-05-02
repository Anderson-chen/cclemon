package org.cclemon.service;

import lombok.AllArgsConstructor;
import org.cclemon.entity.Exercise;
import org.cclemon.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;


    public void insert() {
        var insert = new Exercise();
        insert.setCalorie(1000L);
        exerciseRepository.save(insert);
    }
}
