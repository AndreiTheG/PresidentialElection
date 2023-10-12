package com.example.PresidentialElection.Controller;

import com.example.PresidentialElection.Models.Candidate;
import com.example.PresidentialElection.Models.User;
import com.example.PresidentialElection.Repository.CandidateRepository;
import com.example.PresidentialElection.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
//import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/applicant/")
public class CandidateController {
      private final JdbcTemplate jdbcTemplate;
      private final UserRepository userRepository;
      private long idUser;
      private final CandidateRepository candidateRepository;
      private boolean voted = false;
      private long idCandidate;
      private long lastIdCandidate;

    @Autowired
    public CandidateController(UserRepository userRepository, CandidateRepository candidateRepository, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    //Verifies if an applicant appears in the list and in case he/she modified the description,
    // then it will be updated on table. In case the user has just applied, he/she will be added
    // in the list
    public void updateCandidatesListOrAddCandidate(User user) throws SQLException {
        Candidate candidate = candidateRepository.findById(user.getId()).orElse(new Candidate());
        candidate.setId(user.getId());
        candidate.setName(user.getName());
        candidate.setSurname(user.getSurname());
        candidate.setEmail(user.getEmail());
        candidate.setPhoneNumber(user.getPhoneNumber());
        candidate.setUsername(user.getUsername());
        candidate.setDescription(user.getDescription());
        candidateRepository.save(candidate);
    }

    @GetMapping("add-candidates/:{idUser}")
    public String addAndDisplayCandidates(@PathVariable("idUser") Long idUser) throws SQLException {
        User user = userRepository.findById(idUser).orElseThrow();
        updateCandidatesListOrAddCandidate(user);
        return "redirect:/user/:" + idUser + "";
    }

    @GetMapping(":{idUser}/visits-candidate-profile/:{idCandidate}")
    public String candidateProfilePage(@PathVariable("idCandidate") Long idCandidate, @PathVariable("idUser") Long idUser, Model model) {
        this.idUser = idUser;
        if (idUser == 0) {
            return "redirect:/user/login-or-register";
        }
        User user = userRepository.findById(idUser).orElseThrow();
        List<Candidate> listOfCandidates = candidateRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("candidates", listOfCandidates);
        return "redirect:/applicant/:" + idCandidate + "/candidate-profile";
    }

    @GetMapping(":{idCandidate}/candidate-profile")
    public String candidatePageProfile(@PathVariable("idCandidate") Long candidateId, Model model) throws SQLException {
        if (idUser == 0) {
            return "redirect:/user/login-or-register";
        }
        idCandidate = candidateId;
        User user = userRepository.findById(idUser).orElseThrow();
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
        updateCandidatesListOrAddCandidate(user);
        List<Candidate> listCandidates = candidateRepository.findAll().stream().
                sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
        model.addAttribute("user", user);
        model.addAttribute("candidate", candidate);
        model.addAttribute("candidates", listCandidates);
        return "candidatePageProfile";
    }

    @GetMapping(":{idUser}/votes/:{idCandidate}")
    public String sendTheVoteToAnyCandidate(@PathVariable("idCandidate") Long candidateId, @PathVariable("idUser") Long idUser) {
        User user = userRepository.findById(idUser).orElseThrow();
        this.idCandidate = candidateId;
        List<Candidate> candidates = candidateRepository.findAll().stream().
                sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
        for (Candidate candidate : candidates) {
            candidateRepository.save(candidate);
            if (candidate.getId() == this.idCandidate && user.getVoted() == false) {
                long nrVotes = candidate.getNrVotes();
                ++nrVotes;
                candidate.setNrVotes(nrVotes);
                user.setVoted(true);
                userRepository.save(user);
                candidateRepository.save(candidate);
            }
        }
        lastIdCandidate = candidateId;
        return "redirect:/user/:" + idUser + "";
    }
}