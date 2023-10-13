package com.example.PresidentialElection.Controller;

import com.example.PresidentialElection.Models.Candidate;
import com.example.PresidentialElection.Models.User;
import com.example.PresidentialElection.Repository.CandidateRepository;
import com.example.PresidentialElection.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
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
    public void updateCandidatesListOrAddCandidate(User user) {
        Candidate candidate = candidateRepository.findById(user.getId())
                .orElse(new Candidate(user.getName(), user.getSurname(), user.getEmail()
                        , user.getPhoneNumber(), user.getUsername(), user.getDescription(), 0));
        candidate.setId(user.getId());
        candidate.setDescription(user.getDescription());
        candidateRepository.save(candidate);
    }

    @GetMapping("add-candidates/:{idUser}")
    public String addAndDisplayCandidates(@PathVariable("idUser") Long idUser) {
        User user = userRepository.findById(idUser).orElseThrow();
        updateCandidatesListOrAddCandidate(user);
        return "redirect:/user/:" + idUser + "";
    }

    // Save the values of the id of user and the id of the current applicant so we can
    // display the username of the user and the details of the applicant when the method will
    // redirect to "applicant/:{idCandidate}/candidate-page-profile"
    @GetMapping(":{idUser}/visits-candidate-profile/:{idCandidate}")
    public String getAccessToCandidateProfilePage(@PathVariable("idCandidate") long idCandidate, @PathVariable("idUser") long idUser) {
        this.idUser = idUser;
        if (idUser == 0) {
            return "redirect:/user/login-or-register";
        }
        return "redirect:/applicant/:" + idCandidate + "/candidate-page-profile";
    }

    // Display the profile page of the candidate with the id equal to
    @GetMapping(":{idCandidate}/candidate-page-profile")
    public String openCandidatePageProfile(@PathVariable("idCandidate") long candidateId, Model model) {
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

    //A user with idUser has the right to vote once one of the applicants with idCandidate. His/her vote will be added
    //to total number of votes of the chosen applicant this method will be redirected to
    //home page with changes.
    @GetMapping(":{idUser}/votes/:{idCandidate}")
    public String sendTheVoteToCandidate(@PathVariable("idCandidate") long candidateId, @PathVariable("idUser") long idUser) {
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