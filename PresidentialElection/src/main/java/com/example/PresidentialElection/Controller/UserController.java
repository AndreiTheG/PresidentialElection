package com.example.PresidentialElection.Controller;

import com.example.PresidentialElection.Models.Candidate;
import com.example.PresidentialElection.Models.User;
import com.example.PresidentialElection.Repository.CandidateRepository;
import com.example.PresidentialElection.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/")
public class UserController {
    private UserRepository userRepository;
    private Boolean choseRegister = false;
    private long idUser;
    private CandidateRepository candidateRepository;
    private long idCandidate;
    private long lastIdCandidate;

    @Autowired
    public UserController(UserRepository userRepository, CandidateRepository candidateRepository) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
    }

    //Open the page with login and register options
    @GetMapping("login-or-register")
    public String getLoginRegister() {
        return "index";
    }

    //Open the login page
    @GetMapping("login")
    public String getLoginPage(Model model) {
        choseRegister = false;
        model.addAttribute("user", new User());
        return "login";
    }

    //Open the login-error page if the user entered the incorrect data
    @GetMapping("login-error")
    public String getErrorLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "loginError";
    }

    //Open the register page
    @GetMapping("register")
    public String getRegisterPage(Model model) {
        choseRegister = true;
        model.addAttribute("user", new User());
        return "register";
    }

    //Find the user by username and password
    public void findTheUser(User user) {
        List<User> userList = userRepository.findAll();
        for (User currentUser : userList) {
            if (user.getUsername().equals(currentUser.getUsername())
                    && user.getPassword().equals(currentUser.getPassword())) {
                user.setId(currentUser.getId());
                user.setName(currentUser.getName());
                user.setSurname(currentUser.getSurname());
                user.setEmail(currentUser.getEmail());
                user.setPhoneNumber(currentUser.getPhoneNumber());
                user.setDescription(currentUser.getDescription());
                user.setVoted(currentUser.getVoted());
                break;
            }
        }
    }

    // If the user signed in, this method verifies there is an user with these data saved in database. If
    // exists, it will open the primary page. Else it will redirect to login-error page. If
    // the user signed up, his/her data will be saved in database and will display the primary Page with
    // its username in the navbar.
    @PostMapping("")
    public String displayPrimaryPageAfterLoginOrPassword(@Validated User user, Model model) {
        findTheUser(user);
        if (choseRegister) {
            userRepository.save(user);
        } else if (user.getId() == 0) {
            return "redirect:/user/login-error";
        }
        userRepository.save(user);
        idUser = user.getId();
        List<Candidate> candidates = candidateRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
        List<Candidate> topCandidates = candidates.stream()
                .sorted(Comparator.comparingLong(Candidate::getNrVotes).reversed()).collect(Collectors.toList());
        model.addAttribute("candidates", candidates);
        model.addAttribute("topCandidates", topCandidates);
        return "primaryPage";
    }

    //Display the primary page with the current data from server if the user didn't log out.
    @GetMapping("")
    public String getPrimaryPage(Model model) {
        model.addAttribute("user", new User());
        if (this.idUser != 0) {
            User user = userRepository.findById(this.idUser).orElseThrow();
            model.addAttribute("user", user);
            List<Candidate> candidates = candidateRepository.findAll().stream()
                    .sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
            List<Candidate> topCandidates = candidates.stream().sorted(Comparator.comparingLong(Candidate::getNrVotes).reversed()).collect(Collectors.toList());
            model.addAttribute("candidates", candidates);
            model.addAttribute("topCandidates", topCandidates);
        } else {
            return "redirect:/user/login-or-register";
        }
        return "primaryPage";
    }

    //Press the navbar-brand and the page will go back to primary page
    @GetMapping(":{idUser}")
    public String getUserIdAndRedirectToPrimaryPage(Model model, @PathVariable("idUser") Long idUser) {
        User user = userRepository.findById(idUser).orElseThrow();
        model.addAttribute("user", user);
        userRepository.save(user);
        this.idUser = idUser;
        this.idCandidate = lastIdCandidate;
        return "redirect:/user/";
    }

    //Display the page profile after the modifications of the user's description
    @PostMapping(value = ":{id}/page-profile")
    public String saveTheModifiedDataOfPageProfile(@PathVariable("id") Long id, Model model, User currentUser) {
        User user = userRepository.findById(id).orElseThrow();
        user.setDescription(currentUser.getDescription());
        model.addAttribute("user", user);
        List<Candidate> listCandidates = candidateRepository.findAll();
        model.addAttribute("candidates", listCandidates);
        userRepository.save(user);
        return "pageProfile";
    }

    //Display the Profile page with the current information of the user
    @GetMapping(":{id}/page-profile")
    public String openPageProfile(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        List<Candidate> listCandidates = candidateRepository.findAll();
        model.addAttribute("candidates", listCandidates);
        return "pageProfile";
    }

    @GetMapping(":{id}/update-description")
    public String getDerivedDescription(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "updateDescription";
    }
}