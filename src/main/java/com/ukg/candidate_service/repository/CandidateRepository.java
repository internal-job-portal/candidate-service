package com.ukg.candidate_service.repository;

import com.ukg.candidate_service.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByJobId(Long jobId);
    boolean existsByEmployeeIdAndJobId(Long employeeId, Long jobId);
    List<Candidate> findByEmployeeId(Long employeeId);
    
    @Query(value = "SELECT DISTINCT ON (employee_id) * FROM candidate ORDER BY employee_id, applied_at DESC", nativeQuery = true)
    List<Candidate> findDistinctCandidates();
}
