package xyz.sadiulhakim.npr.user.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.sadiulhakim.npr.pojo.TableUrlPojo;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.role.model.RoleService;
import xyz.sadiulhakim.npr.user.model.PasswordDTO;
import xyz.sadiulhakim.npr.user.model.UserService;
import xyz.sadiulhakim.npr.user.model.User;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/users")
class UserController {

    private final RoleService roleService;
    private final UserService userService;
    private final AppProperties appProperties;

    private final TableUrlPojo table_url = new TableUrlPojo("/users/search", "/users", "/users/export-csv",
            "/users/create_page", "/users/page");

    UserController(RoleService roleService, UserService userService, AppProperties appProperties) {
        this.roleService = roleService;
        this.userService = userService;
        this.appProperties = appProperties;
    }

    @GetMapping("/page")
    String page(@RequestParam(defaultValue = "0") int page, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("userResult", userService.findAllPaginated(page));
        model.addAttribute("table_url", table_url);

        return "user/user_page";
    }

    @PostMapping("/save")
    String save(@ModelAttribute @Valid User user, BindingResult result, @RequestParam MultipartFile photo,
                @RequestParam(defaultValue = "0") long userId, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("name", AuthenticatedUserUtil.getName());
            model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
            model.addAttribute("roles", roleService.findAll());
            model.addAttribute("userId", userId);
            return "user/create_page";
        }
        userService.save(user, photo);

        return "redirect:/users/page";
    }

    @GetMapping("/search")
    String searchUser(@RequestParam String text, Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("userResult", userService.searchUser(text, 0));
        model.addAttribute("table_url", table_url);

        return "user/user_page";
    }

    @GetMapping("/create_page")
    String createPage(@RequestParam(defaultValue = "0") long userId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("userId", userId);

        Optional<User> user = userService.getById(userId);

        if (user.isEmpty()) {
            model.addAttribute("user", new User());
        } else {
            model.addAttribute("user", user.get());
        }

        return "user/create_page";
    }

    @GetMapping("/change_password_page")
    String changePasswordPage(@RequestParam(defaultValue = "0") long userId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("passwordDto", new PasswordDTO());

        if (Objects.equals(model.getAttribute("userId"), "0")) {
            model.addAttribute("userId", userId);
        }

        return "user/change_password";
    }

    @PostMapping("/change_password")
    String changePassword(@ModelAttribute PasswordDTO passwordDTO,
                          @RequestParam long userId,
                          RedirectAttributes model) {

        String message = userService.changePassword(passwordDTO, userId);
        model.addFlashAttribute("message", message);
        model.addFlashAttribute("hasMessage", true);

        return "redirect:/users/change_password_page?userId=" + userId;
    }

    @GetMapping("/delete/{id}")
    String deleteUser(@PathVariable long id) {

        userService.delete(id);
        return "redirect:/users/page";
    }

    @GetMapping("/export-csv")
    ResponseEntity<byte[]> exportCsv() {
        var data = userService.getCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=users.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
