package com.example.PresidentialElection.Controller;

import com.example.PresidentialElection.Models.Candidate;
import com.example.PresidentialElection.Models.User;
import com.example.PresidentialElection.Repository.CandidateRepository;
import com.example.PresidentialElection.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/candidate/")
public class CandidateController {
      private final UserRepository userRepository;
      private long userId;
      private User newUser;
      private final CandidateRepository candidateRepository;
      private long candidateId;

    @Autowired
    public CandidateController(UserRepository userRepository, CandidateRepository candidateRepository) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        //super(userRepository, candidateRepository);
    }

    // Verify if an applicant appears in the list and in case he/she modified the description,
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

    // The user registers as an applicant and will be displayed in the list of applicants
    @GetMapping(":{userId}/add-candidates")
    public String addAndDisplayCandidates(@PathVariable("userId") Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        System.out.println(userId);
        Candidate candidate = candidateRepository.findById(user.getId())
                .orElse(new Candidate(user.getName(), user.getSurname(), user.getEmail()
                        , user.getPhoneNumber(), user.getUsername(), user.getDescription(), 0));
        candidate.setId(user.getId());
        candidateRepository.save(candidate);
        return "redirect:/user/:" + userId + "";
    }

    // Save the values of the id of user and the id of the current applicant so we can
    // display the username of the user and the details of the applicant when the method will
    // redirect to "applicant/:{idCandidate}/candidate-page-profile"
    @GetMapping(/*":{userId}*/"visits-candidate-profile/:{candidateId}")
    public String getAccessToCandidateProfilePage(/*@PathVariable("userId") long userId,*/ @PathVariable("candidateId") long candidateId) {
        /*this.userId = userId;*/
        if (this.userId == 0) {
            return "redirect:/user/login-or-register";
        }
        return "redirect:/candidate/:" + candidateId + "";
    }


    // Display the profile page of the candidate with the id equal to the value of idCandidate
    @GetMapping(":{candidateId}")
    public String openCandidatePageProfile(@PathVariable("candidateId") long candidateId, Model model, HttpSession session) {
        UserController userController = new UserController(userRepository, candidateRepository);
        String idUser = (String) session.getAttribute("user");
        System.out.println(userId);
        if (this.userId == 0) {
            return "redirect:/user/login-or-register";
        }
        this.candidateId = candidateId;
        User user = userRepository.findById(this.userId).orElseThrow();
        Candidate candidate = candidateRepository.findById(this.candidateId).orElseThrow();
        updateCandidatesListOrAddCandidate(user);
        List<Candidate> listCandidates = candidateRepository.findAll().stream().
                sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
        model.addAttribute("user", user);
        model.addAttribute("candidate", candidate);
        model.addAttribute("candidates", listCandidates);
        return "candidatePageProfile";
    }

    public User getUser() {
        return userRepository.findById(this.userId).orElseThrow();
    }

    // A user with idUser has the right to vote once one of the applicants with idCandidate.
    // His/her vote will be added to total number of votes of the chosen applicant this method
    // will be redirected to home page with changes.
    @GetMapping(":{userId}/votes/:{candidateId}")
    public String sendTheVoteToCandidate(@PathVariable("userId") long userId, @PathVariable("candidateId") long candidateId) {
        User user = userRepository.findById(userId).orElseThrow();
        this.candidateId = candidateId;
        List<Candidate> candidates = candidateRepository.findAll().stream().
                sorted(Comparator.comparingLong(Candidate::getId)).toList();
        for (Candidate candidate : candidates) {
            candidateRepository.save(candidate);
            if (candidate.getId() == this.candidateId && !user.getVoted()) {
                long nrVotes = candidate.getNrVotes();
                ++nrVotes;
                candidate.setNrVotes(nrVotes);
                user.setVoted(true);
                userRepository.save(user);
                candidateRepository.save(candidate);
            }
        }
        return "redirect:/user/:" + userId + "";
    }
}