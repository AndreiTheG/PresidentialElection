package com.example.PresidentialElection.Repository;

//import com.example.PresidentialElection.Model.Candidate;
import com.example.PresidentialElection.Models.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByUsername(String username);
}
