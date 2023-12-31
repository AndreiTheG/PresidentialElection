package com.example.PresidentialElection.Controller;

import com.example.PresidentialElection.Models.Candidate;
import com.example.PresidentialElection.Models.User;
import com.example.PresidentialElection.Repository.CandidateRepository;
import com.example.PresidentialElection.Repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    private final UserRepository userRepository;
    private Boolean choseRegister = false;
    private final CandidateRepository candidateRepository;
    private long userId;

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
    @PostMapping("login")
    public String openPrimaryPageAfterLoginOrRegister(@Validated User user, Model model, HttpServletRequest request) {
        findTheUser(user);
        if (choseRegister) {
            userRepository.save(user);
        } else if (user.getId() == 0) {
            return "redirect:/user/login-error";
        }
        userRepository.save(user);
        this.userId = user.getId();
        HttpSession httpSession = request.getSession();
        List<Candidate> candidates = candidateRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
        List<Candidate> topCandidates = candidates.stream()
                .sorted(Comparator.comparingLong(Candidate::getNrVotes).reversed()).collect(Collectors.toList());
        model.addAttribute("candidates", candidates);
        model.addAttribute("topCandidates", topCandidates);
        httpSession.setAttribute("user", user);
        return "redirect:/user/";
//        return "primaryPage";
    }

    //Display the primary page with the current data from server if the user didn't log out.
    @GetMapping("")
    public String getPrimaryPage(Model model) {
        model.addAttribute("user", new User());
        if (this.userId != 0) {
            User user = userRepository.findById(this.userId).orElseThrow();
            model.addAttribute("user", user);
            List<Candidate> candidates = candidateRepository.findAll().stream()
                    .sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
            List<Candidate> topCandidates = candidates.stream().sorted(Comparator.comparingLong(Candidate::getNrVotes)
                    .reversed()).collect(Collectors.toList());
            model.addAttribute("candidates", candidates);
            model.addAttribute("topCandidates", topCandidates);
        } else {
            return "redirect:/user/login-or-register";
        }
        return "primaryPage";
    }

    //Display the page profile after the modifications of the user's description
    @PostMapping(":{userId}/edit")
    public String saveTheModifiedDescription(@PathVariable("userId") Long userId, Model model, User currentUser, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setDescription(currentUser.getDescription());
        model.addAttribute("user", user);
        List<Candidate> listCandidates = candidateRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Candidate::getId)).toList();
        model.addAttribute("candidates", listCandidates);
        HttpSession httpSession = request.getSession();
        userRepository.save(user);
        httpSession.setAttribute("user", user);
        return "redirect:/user/:" + userId + "/page-profile";
    }

    //Display the Profile page with the current information of the user
    @GetMapping(":{userId}/page-profile")
    public String openPageProfile(@PathVariable("userId") Long userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow();
        model.addAttribute("user", user);
        List<Candidate> listCandidates = candidateRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Candidate::getId)).toList();
        model.addAttribute("candidates", listCandidates);
        return "pageProfile";
    }

    @GetMapping(":{userId}/update-description")
    public String updateDescription(@PathVariable("userId") Long userId, Model model) {
        User user = userRepository.findById(userId).orElseThrow();
        model.addAttribute("user", user);
        return "updateDescription";
    }
}