package com.bridgetrack.bridgetrack.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bridgetrack.bridgetrack.model.Term;
import com.bridgetrack.bridgetrack.repository.TermRepository;

@Service
public class TermService {

    private final TermRepository termRepository;

    public TermService(TermRepository termRepository) {
        this.termRepository = termRepository;
    }

    public Term create(Term term) {
        termRepository.findByTermNameIgnoreCase(term.getTermName())
                .ifPresent(t -> { throw new RuntimeException("Term already exists (name)."); });

        termRepository.findByTermCode(term.getTermCode())
                .ifPresent(t -> { throw new RuntimeException("Term already exists (termCode)."); });

        if (term.getActive() == null) {
            term.setActive(true);
        }

        return termRepository.save(term);
    }

    public List<Term> getAll() {
        return termRepository.findAll();
    }

    public Term getById(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Term not found."));
    }
}

