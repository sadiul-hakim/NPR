package xyz.sadiulhakim.npr.role;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.sadiulhakim.npr.pojo.TableUrlPojo;
import xyz.sadiulhakim.npr.user.UserService;
import xyz.sadiulhakim.npr.util.AuthenticatedUserUtil;

import java.util.Optional;

@Controller
@RequestMapping("/roles")
public class RoleController {

    private final TableUrlPojo table_url = new TableUrlPojo("/roles/search", "/roles", "/roles/export-csv",
            "/roles/create_page", "/roles/page");

    private final RoleService roleService;
    private final UserService userService;

    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/page")
    public String page(@RequestParam(defaultValue = "0") int page, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(userService.folder));
        model.addAttribute("roleResult", roleService.findAllPaginated(page));
        model.addAttribute("table_url", table_url);

        return "role/role_page";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Role roleDto, RedirectAttributes attributes) {

        roleService.save(roleDto);

        return "redirect:/roles/page";
    }

    @GetMapping("/search")
    public String searchUser(@RequestParam String text, Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(userService.folder));
        model.addAttribute("roleResult", roleService.search(text, 0));
        model.addAttribute("table_url", table_url);

        return "role/role_page";
    }

    @GetMapping("/create_page")
    public String createPage(@RequestParam(defaultValue = "0") long roleId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(userService.folder));
        model.addAttribute("roleId", roleId);

        Optional<Role> role = roleService.getById(roleId);

        if (role.isEmpty()) {
            model.addAttribute("dto", new Role());
        } else {
            model.addAttribute("dto", role.get());
        }

        return "role/create_page";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable long id) {

        roleService.delete(id);
        return "redirect:/roles/page";
    }

    @GetMapping("/export-csv")
    public ResponseEntity<byte[]> exportCsv() {
        var data = roleService.getCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=roles.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
