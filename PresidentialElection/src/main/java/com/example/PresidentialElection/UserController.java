package com.example.PresidentialElection;
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
    private Long id;
    private long idUser;
    private CandidateRepository candidateRepository;
    private Boolean voted = false;
    private Long idCandidate;
    private Long lastIdCandidate;

    @Autowired
    public UserController(UserRepository userRepository, CandidateRepository candidateRepository) {
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
    }
    @PostMapping("login-or-register")
    public String createLoginRegister() {
        return "index";
    }

    @GetMapping("login-or-register")
    public String getLoginRegister() {
        return "index";
    }

    @PostMapping("login")
    public String createLoginPage() {
        return "login";
    }

    @GetMapping("login")
    public String getLoginPage(Model model) {
        choseRegister = false;
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("login-error")
    public String postErrorLoginPage() {
        return "loginError";
    }

    @GetMapping("login-error")
    public String getErrorLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "loginError";
    }

    @PostMapping("register")
    public String createRegisterPage() {
        return "register";
    }

    @GetMapping("register")
    public String getRegisterPage(Model model) {
        choseRegister = true;
        model.addAttribute("user", new User());
        return "register";
    }

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
                break;
            }
        }
    }

    @PostMapping("")
    public String createPrimaryPage(@Validated User user, Model model) {
        findTheUser(user);
        System.out.println(choseRegister);
        if (choseRegister == true) {
            userRepository.save(user);
        } else if (user.getId() == 0) {
            return "redirect:/user/login-error";
        }
        userRepository.save(user);
        idUser = user.getId();
        List<Candidate> candidates = candidateRepository.findAll().stream()
                .sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
        List<Candidate> topCandidates = candidates.stream().sorted(Comparator.comparingLong(Candidate::getNrVotes).reversed()).collect(Collectors.toList());
        model.addAttribute("candidates", candidates);
        model.addAttribute("topCandidates", topCandidates);
        return "primaryPage";
    }

    @GetMapping("")
    public String getPrimaryPage(Model model) {
        model.addAttribute("user", new User());
        if (this.id != null) {
            User user = userRepository.findById(this.id).orElseThrow();
            model.addAttribute("user", user);
            System.out.println(this.idCandidate);
            List<Candidate> candidates = candidateRepository.findAll().stream().sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
            List<Candidate> topCandidates = candidates.stream().sorted(Comparator.comparingLong(Candidate::getNrVotes).reversed()).collect(Collectors.toList());
            model.addAttribute("candidates", candidates);
            model.addAttribute("topCandidates", topCandidates);
        } else {
            return "redirect:/user/login-or-register";
        }
        return "primaryPage";
    }

    @PostMapping(":{id}")
    public String goBackToPrimaryPage(Model model, @PathVariable("id") Long id) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        userRepository.save(user);
        return "primaryPage";
    }

    @GetMapping(":{id}")
    public String getToPrimaryPage(Model model, @PathVariable("id") Long id) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        userRepository.save(user);
        this.id = id;
        this.idCandidate = lastIdCandidate;
        System.out.println(this.idCandidate);
        return "redirect:/user/";
    }

    @PostMapping(value = "page-profile/:{id}")
    public String createPageProfile(@PathVariable("id") Long id, Model model, User currentUser) {
        User user = userRepository.findById(id).orElseThrow();
        user.setDescription(currentUser.getDescription());
        model.addAttribute("user", user);
        userRepository.save(user);
        return "redirect:/user/:" + id + "/page-profile";
    }

    @PostMapping(value = ":{id}/page-profile")
    public String createPageProfileDerived(@PathVariable("id") Long id, Model model, User currentUser) {
        User user = userRepository.findById(id).orElseThrow();
        user.setDescription(currentUser.getDescription());
        model.addAttribute("user", user);
        userRepository.save(user);
        return "pageProfile";
    }

    @GetMapping("page-profile/:{id}")
    public String getPageProfile(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        System.out.println(id);
        model.addAttribute("user", user);
        return "redirect:/user/:" + id + "/page-profile";
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

    @GetMapping(":{id}/page-profile")
    public String getPageProfileDerived(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        List<Candidate> listCandidates = candidateRepository.findAll();
        model.addAttribute("candidates", listCandidates);
        for (Candidate candidate : listCandidates) {
            System.out.println(candidate);
        }
        return "pageProfile";
    }

    @PostMapping("update-description/:{id}")
    public String updateDescription(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "updateDescription";
    }

    @GetMapping("update-description/:{id}")
    public String getDescription(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return  "redirect:/user/:" + id + "/update-description";
    }

    @GetMapping(":{id}/update-description")
    public String getDerivedDescription(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        model.addAttribute("user", user);
        return "updateDescription";
    }

//    @PostMapping("add-candidates/:{id}")
//    public String addListCandidates(@PathVariable("id") Long id) {
//        return "redirect:/user/" + id + "";
//    }
//
//    @GetMapping("add-candidates/:{id}")
//    public String getListCandidates(@PathVariable("id") Long id) {
//        return "redirect:/user/:" + id + "";
//    }
//
//    @PostMapping("{candidateId}")
//    public String createCandidateProfilePage(@PathVariable("candidateId") Long candidateId) {
//        return "redirect:/user/:" + candidateId + "/candidate-profile";
//    }
//
//    @GetMapping("{idCandidate}")
//    public String candidateProfilePage(@PathVariable("idCandidate") Long candidateId, Model model){
//        System.out.println(idUser);
//        if (idUser == 0) {
//            return "redirect:/user/login-or-register";
//        }
//        User user = userRepository.findById(idUser).orElseThrow();
//        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
//        List<Candidate> listOfCandidates = candidateRepository.findAll();
//        model.addAttribute("user", user);
//        model.addAttribute("candidate", candidate);
//        model.addAttribute("candidates", listOfCandidates);
//        return "redirect:/user/:" + candidateId + "/candidate-profile";
//    }
//
//    @PostMapping(":{candidateId}/candidate-profile")
//    public String createCandidatePageProfile(@PathVariable("candidateId") Long candidateId, Model model) {
//        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
//        model.addAttribute("candidate", candidate);
//        return "candidatePageProfile";
//    }
//
//    @GetMapping(":{idCandidate}/candidate-profile")
//    public String candidatePageProfile(@PathVariable("idCandidate") Long candidateId, Model model){
//        if (idUser == 0) {
//            return "redirect:/user/login-or-register";
//        }
//        idCandidate = candidateId;
//        User user = userRepository.findById(idUser).orElseThrow();
//        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow();
//        List<Candidate> listCandidates = candidateRepository.findAll();
//        updateCandidatesList(listCandidates, user);
//        model.addAttribute("user", user);
//        model.addAttribute("candidate", candidate);
//        model.addAttribute("candidates", listCandidates);
//        return "candidatePageProfile";
//    }
//
//    @GetMapping("/vote/{idCandidate}")
//    public String getTheVote(@PathVariable("idCandidate") Long candidateId) {
//        System.out.println(idUser);
//        voted = true;
//        this.idCandidate = candidateId;
//        List<Candidate> candidates = candidateRepository.findAll().stream().sorted(Comparator.comparingLong(Candidate::getId)).collect(Collectors.toList());
//        for (Candidate candidate : candidates) {
//            candidateRepository.save(candidate);
//            if (candidate.getId() == this.idCandidate) {
//                long nrVotes = candidate.getNrVotes();
//                ++nrVotes;
//                candidate.setNrVotes(nrVotes);
//                candidateRepository.save(candidate);
//            }
//        }
//        lastIdCandidate = candidateId;
//        return "redirect:/user/:" + idUser + "";
//    }
}