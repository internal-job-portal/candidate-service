package com.ukg.candidate_service.controller;

import com.ukg.candidate_service.model.Candidate;
import com.ukg.candidate_service.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping
    public ResponseEntity<List<Candidate>> getAllCandidates() {
        return ResponseEntity.ok(candidateService.getAllCandidates());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable Long id) {
        return ResponseEntity.ok(candidateService.getCandidateById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidate> updateCandidate(@PathVariable Long id,  @Valid @RequestBody Candidate candidateDetails) {
        return ResponseEntity.ok(candidateService.updateCandidate(id, candidateDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        candidateService.deleteCandidate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/apply")
    public ResponseEntity<Candidate> applyForJob(@Valid @RequestBody Candidate candidate) {
        return ResponseEntity.ok(candidateService.applyForJob(candidate));
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Candidate>> getCandidatesForJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(candidateService.getCandidatesForJob(jobId));
    }

    @DeleteMapping("/jobservice/{jobId}")
    public ResponseEntity<Boolean> deleteCandidatesByJobId(@PathVariable Long jobId) {
        boolean result = candidateService.deleteCandidatesByJobId(jobId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/jobservice/{employeeId}")
    public ResponseEntity<List<Long>> getJobIdsForCandidate(@PathVariable Long employeeId) {
        List<Long> jobIds = candidateService.getJobIdsForCandidate(employeeId);
        return ResponseEntity.ok(jobIds);
    }

}
