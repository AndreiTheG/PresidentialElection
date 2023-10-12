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

    //Verifies if
    public void updateCandidatesListOrAddCandidate(List<Candidate> listCandidates, User user) throws SQLException {
        boolean isCandidate = false;
//        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Users",
//                "postgres", "postgres");
//        connection.createStatement();
//        String str = "SELECT * FROM candidates WHERE id = " + user.getId() + ";";
//        connection.prepareStatement(str);
//        Statement statement = connection.createStatement();
//        statement.executeQuery(str);
//        System.out.println(statement);
//        ResultSet rs = statement.executeQuery(str);
//        while (rs.next()) {
//            System.out.println(rs.getString("Name") + " | " + rs.getString("Surname"));
//        }
        Optional<Candidate> applicant = candidateRepository.findById(user.getId());
        System.out.println(applicant);
        for (Candidate currentCandidate : listCandidates) {
            if (user.getId() == currentCandidate.getId()) {
                isCandidate = true;
                currentCandidate.setName(user.getName());
                currentCandidate.setSurname(user.getSurname());
                currentCandidate.setEmail(user.getEmail());
                currentCandidate.setPhoneNumber(user.getPhoneNumber());
                currentCandidate.setUsername(user.getUsername());
                currentCandidate.setDescription(user.getDescription());
                break;
            }
        }
        if (!isCandidate) {
            Candidate candidate = new Candidate();
            candidate.setId(user.getId());
            candidate.setName(user.getName());
            candidate.setSurname(user.getSurname());
            candidate.setEmail(user.getEmail());
            candidate.setPhoneNumber(user.getPhoneNumber());
            candidate.setUsername(user.getUsername());
            candidate.setDescription(user.getDescription());
            candidateRepository.save(candidate);
        }
    }

    @GetMapping("add-candidates/:{idUser}")
    public String addAndDisplayCandidates(@PathVariable("idUser") Long idUser) throws SQLException {
        User user = userRepository.findById(idUser).orElseThrow();
        List<Candidate> candidates = candidateRepository.findAll();
        updateCandidatesListOrAddCandidate(candidates, user);
        return "redirect:/user/:" + idUser + "";
    }

    @PostMapping("{candidateId}")
    public String createCandidateProfilePage(@PathVariable("candidateId") Long candidateId) {
        return "redirect:/user/:" + candidateId + "/candidate-profile";
    }

    @GetMapping("{idCandidate}/{idUser}")
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

    @PostMapping(":{candidateId}/candidate-profile")
    public String createCandidatePageProfile(@PathVariable("candidateId") Long candidateId, Model model) {
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
        model.addAttribute("candidate", candidate);
        return "candidatePageProfile";
    }

    @GetMapping(":{idCandidate}/candidate-profile")
    public String candidatePageProfile(@PathVariable("idCandidate") Long candidateId, Model model) throws SQLException {
        if (idUser == 0) {
            return "redirect:/user/login-or-register";
        }
        idCandidate = candidateId;
        User user = userRepository.findById(idUser).orElseThrow();
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
        List<Candidate> listCandidates = candidateRepository.findAll().stream().
                sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
        updateCandidatesListOrAddCandidate(listCandidates, user);
        model.addAttribute("user", user);
        model.addAttribute("candidate", candidate);
        model.addAttribute("candidates", listCandidates);
        return "candidatePageProfile";
    }

    @GetMapping("/vote/{idCandidate}/{idUser}")
    public String getTheVote(@PathVariable("idCandidate") Long candidateId, @PathVariable("idUser") Long idUser) {
        voted = true;
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