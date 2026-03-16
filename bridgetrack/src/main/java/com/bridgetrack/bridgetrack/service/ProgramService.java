package com.bridgetrack.bridgetrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.model.Program;
import com.bridgetrack.bridgetrack.repository.ProgramRepository;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public Program create(Program program) {
        programRepository.findByProgramNameIgnoreCase(program.getProgramName())
                .ifPresent(p -> {
                    throw new RuntimeException("Program already exists.");
                });

        if (program.getActive() == null) {
            program.setActive(true);
        }

        return programRepository.save(program);
    }

    public List<Program> getAll() {
        return programRepository.findAll();
    }

    public Program getById(Long id) {
        return programRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Program not found."));
    }
}