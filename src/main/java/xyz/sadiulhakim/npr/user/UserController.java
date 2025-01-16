package xyz.sadiulhakim.npr.user;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.sadiulhakim.npr.pojo.TableUrlPojo;
import xyz.sadiulhakim.npr.role.RoleService;
import xyz.sadiulhakim.npr.util.AuthenticatedUserUtil;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final RoleService roleService;
    private final UserService userService;

    private final TableUrlPojo table_url = new TableUrlPojo("/users/search", "/users", "/users/export-csv",
            "/users/create_page", "/users/page");

    public UserController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/page")
    public String page(@RequestParam(defaultValue = "0") int page, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(userService.folder));
        model.addAttribute("userResult", userService.findAllPaginated(page));
        model.addAttribute("table_url", table_url);

        return "user/user_page";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute User userDto, @RequestParam MultipartFile photo, RedirectAttributes attributes) {

        userService.save(userDto, photo);

        return "redirect:/users/page";
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam String text, Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(userService.folder));
        model.addAttribute("userResult", userService.searchUser(text, 0));
        model.addAttribute("table_url", table_url);

        return "user/user_page";
    }

    @GetMapping("/create_page")
    public String createPage(@RequestParam(defaultValue = "0") long userId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(userService.folder));
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("userId", userId);

        Optional<User> user = userService.getById(userId);

        if (user.isEmpty()) {
            model.addAttribute("dto", new User());
        } else {
            model.addAttribute("dto", user.get());
        }

        return "user/create_page";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable long id) {

        userService.delete(id);
        return "redirect:/users/page";
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = userService.getCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
