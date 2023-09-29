package com.example.PresidentialElection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/")
public class CandidateController {
    private UserRepository userRepository;
    private Boolean choseRegister = false;
    private Long id;
    private long idUser;
    private CandidateRepository candidateRepository;
    private Boolean voted = false;
    private Long idCandidate;
    private Long lastIdCandidate;

    @Autowired
    public CandidateController(UserRepository userRepository, CandidateRepository candidateRepository) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
    }

    public void updateCandidatesList(List<Candidate> listCandidates, User user) {
        boolean isCandidate = false;
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
            candidate.setId(id);
            candidate.setName(user.getName());
            candidate.setSurname(user.getSurname());
            candidate.setEmail(user.getEmail());
            candidate.setPhoneNumber(user.getPhoneNumber());
            candidate.setUsername(user.getUsername());
            candidate.setDescription(user.getDescription());
            candidateRepository.save(candidate);
        }
    }

    @PostMapping("add-candidates/:{id}")
    public String addListCandidates(@PathVariable("id") Long id) {
        return "redirect:/user/" + id + "";
    }

    @GetMapping("add-candidates/:{id}")
    public String getListCandidates(@PathVariable("id") Long id) {
        return "redirect:/user/:" + id + "";
    }

    @PostMapping("{candidateId}")
    public String createCandidateProfilePage(@PathVariable("candidateId") Long candidateId) {
        return "redirect:/user/:" + candidateId + "/candidate-profile";
    }

    @GetMapping("{idCandidate}")
    public String candidateProfilePage(@PathVariable("idCandidate") Long candidateId, Model model){
        System.out.println(idUser);
        if (idUser == 0) {
            return "redirect:/user/login-or-register";
        }
        User user = userRepository.findById(idUser).orElseThrow();
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
        List<Candidate> listOfCandidates = candidateRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("candidate", candidate);
        model.addAttribute("candidates", listOfCandidates);
        return "redirect:/user/:" + candidateId + "/candidate-profile";
    }

    @PostMapping(":{candidateId}/candidate-profile")
    public String createCandidatePageProfile(@PathVariable("candidateId") Long candidateId, Model model) {
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
        model.addAttribute("candidate", candidate);
        return "candidatePageProfile";
    }

    @GetMapping(":{idCandidate}/candidate-profile")
    public String candidatePageProfile(@PathVariable("idCandidate") Long candidateId, Model model){
        if (idUser == 0) {
            return "redirect:/user/login-or-register";
        }
        idCandidate = candidateId;
        User user = userRepository.findById(idUser).orElseThrow();
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
        List<Candidate> listCandidates = candidateRepository.findAll();
        updateCandidatesList(listCandidates, user);
        model.addAttribute("user", user);
        model.addAttribute("candidate", candidate);
        model.addAttribute("candidates", listCandidates);
        return "candidatePageProfile";
    }

    @GetMapping("/vote/{idCandidate}")
    public String getTheVote(@PathVariable("idCandidate") Long candidateId) {
        System.out.println(idUser);
        voted = true;
        this.idCandidate = candidateId;
        List<Candidate> candidates = candidateRepository.findAll().stream().sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
        for (Candidate candidate : candidates) {
            candidateRepository.save(candidate);
            if (candidate.getId() == this.idCandidate) {
                long nrVotes = candidate.getNrVotes();
                ++nrVotes;
                candidate.setNrVotes(nrVotes);
                candidateRepository.save(candidate);
            }
        }
        lastIdCandidate = candidateId;
        return "redirect:/user/:" + idUser + "";
    }
}
