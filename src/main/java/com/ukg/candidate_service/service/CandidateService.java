package com.ukg.candidate_service.service;

import com.ukg.candidate_service.model.Candidate;
import com.ukg.candidate_service.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {
    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Transactional(readOnly = true)
    public List<Candidate> getAllCandidates() {
        return candidateRepository.findDistinctCandidates();
    }

    @Transactional
    public Candidate applyForJob(Candidate candidate) {
        if (candidateRepository.existsByEmployeeIdAndJobId(candidate.getEmployeeId(), candidate.getJobId())) {
            throw new IllegalStateException("Already applied for this job");
        }
        candidate.setAppliedAt(LocalDateTime.now());
        Candidate savedCandidate = candidateRepository.save(candidate);

        kafkaTemplate.send("job-applications", 
                           String.valueOf(savedCandidate.getId()), 
                           String.format("%s applied for job %s", 
                                         savedCandidate.getEmail(), 
                                         savedCandidate.getJobId()));
        
        return savedCandidate;
    }

    @Transactional(readOnly = true)
    public List<Candidate> getCandidatesForJob(Long jobId) {
        return candidateRepository.findByJobId(jobId);
    }

    @Transactional(readOnly = true)
    public Candidate getCandidateById(Long id) {
        return candidateRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + id));
    }

    @Transactional
    public void deleteCandidate(Long id) {
        Candidate candidate = getCandidateById(id);
        candidateRepository.delete(candidate);
    }

    @Transactional
    public boolean deleteCandidatesByJobId(Long jobId) {
        List<Candidate> candidates = candidateRepository.findByJobId(jobId);
        if (candidates.isEmpty()) {
            return true;
        }
        
        try {
            candidateRepository.deleteAll(candidates);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public Candidate updateCandidate(Long id, Candidate candidateDetails) {
        Candidate candidate = getCandidateById(id);
        candidate.setFirstName(candidateDetails.getFirstName());
        candidate.setLastName(candidateDetails.getLastName());
        candidate.setEmail(candidateDetails.getEmail());
        candidate.setEmployeeId(candidateDetails.getEmployeeId());
        candidate.setJobId(candidateDetails.getJobId());
        candidate.setStatus(candidateDetails.getStatus());
        candidate.setAppliedAt(candidateDetails.getAppliedAt());
        return candidateRepository.save(candidate);
    }

    public List<Long> getJobIdsForCandidate(Long employeeId) {
        List<Candidate> applications = candidateRepository.findByEmployeeId(employeeId);
        return applications.stream()
                           .map(Candidate::getJobId)
                           .distinct()
                           .collect(Collectors.toList());
    }
}
