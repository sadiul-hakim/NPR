package xyz.sadiulhakim.npr.role.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import xyz.sadiulhakim.npr.pojo.TableUrlPojo;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.role.model.RoleService;
import xyz.sadiulhakim.npr.role.model.Role;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.util.Optional;

@Controller
@RequestMapping("/roles")
class RoleController {

    private final TableUrlPojo table_url = new TableUrlPojo("/roles/search", "/roles", "/roles/export-csv",
            "/roles/create_page", "/roles/page");

    private final RoleService roleService;
    private final AppProperties appProperties;

    RoleController(RoleService roleService, AppProperties appProperties) {
        this.roleService = roleService;
        this.appProperties = appProperties;
    }

    @GetMapping("/page")
    String page(@RequestParam(defaultValue = "0") int page, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("roleResult", roleService.findAllPaginated(page));
        model.addAttribute("table_url", table_url);

        return "role/role_page";
    }

    @PostMapping("/save")
    String save(@ModelAttribute Role roleDto, RedirectAttributes attributes) {
        roleService.save(roleDto);
        return "redirect:/roles/page";
    }

    @GetMapping("/search")
    String searchUser(@RequestParam String text, Model model) {
        model.addAttribute("roleResult", roleService.search(text, 0));
        model.addAttribute("table_url", table_url);

        return "role/role_page";
    }

    @GetMapping("/create_page")
    String createPage(@RequestParam(defaultValue = "0") long roleId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
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
    String deleteUser(@PathVariable long id) {

        roleService.delete(id);
        return "redirect:/roles/page";
    }

    @GetMapping("/export-csv")
    ResponseEntity<byte[]> exportCsv() {
        var data = roleService.getCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=roles.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
