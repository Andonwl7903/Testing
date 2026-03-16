package com.bridgetrack.bridgetrack.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.bridgetrack.bridgetrack.model.Section;
import com.bridgetrack.bridgetrack.repository.SectionRepository;

@RestController
@RequestMapping("/sections")
public class SectionController {

    private final SectionRepository sectionRepo;

    public SectionController(SectionRepository sectionRepo) {
        this.sectionRepo = sectionRepo;
    }

    @GetMapping
    public List<Section> getAll() {
        return sectionRepo.findAll();
    }

    @PostMapping
    public Section create(@RequestBody Section section) {
        return sectionRepo.save(section);
    }
}