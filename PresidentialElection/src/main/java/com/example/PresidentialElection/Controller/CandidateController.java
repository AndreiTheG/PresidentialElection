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
      private final UserRepository userRepository;
      private long idUser;
      private final CandidateRepository candidateRepository;
      private long idCandidate;

    @Autowired
    public CandidateController(UserRepository userRepository, CandidateRepository candidateRepository) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
    }

    //Verifies if an applicant appears in the list and in case he/she modified the description,
    // then it will be updated on table. In case the user has just applied, he/she will be added
    // in the list
    public void updateCandidatesListOrAddCandidate(User user) throws SQLException {
        Candidate candidate = candidateRepository.findById(user.getId())
                .orElse(new Candidate(user.getName(), user.getSurname(), user.getEmail()
                        , user.getPhoneNumber(), user.getUsername(), user.getDescription(), 0));
        candidate.setId(user.getId());
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
    public String candidateProfilePage(@PathVariable("idCandidate") long idCandidate, @PathVariable("idUser") long idUser) {
        //this.idUser = idUser;
        System.out.println(idUser);
        if (idUser == 0) {
            return "redirect:/user/login-or-register";
        }
        return "redirect:/applicant/:" + idCandidate + "/candidate-profile";
    }

    @GetMapping(":{idCandidate}/candidate-profile")
    public String candidatePageProfile(@PathVariable("idCandidate") long candidateId, Model model) throws SQLException {
        System.out.println(idUser);
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

    //A user has the right to vote once one of the applicants. His/her vote will be added
    //to total number of votes of the chosen applicant this method will be redirected to
    //home page with changes.
    @GetMapping(":{idUser}/votes/:{idCandidate}")
    public String sendTheVoteToAnyCandidate(@PathVariable("idCandidate") long candidateId, @PathVariable("idUser") long idUser) {
        User user = userRepository.findById(idUser).orElseThrow();
        this.idCandidate = candidateId;
        List<Candidate> candidates = candidateRepository.findAll().stream().
                sorted(Comparator.comparingLong(Candidate::getId)).toList();
        for (Candidate candidate : candidates) {
            candidateRepository.save(candidate);
            if (candidate.getId() == this.idCandidate && !user.getVoted()) {
                long nrVotes = candidate.getNrVotes();
                ++nrVotes;
                candidate.setNrVotes(nrVotes);
                user.setVoted(true);
                userRepository.save(user);
                candidateRepository.save(candidate);
            }
        }
        return "redirect:/user/:" + idUser + "";
    }
}