package xyz.sadiulhakim.npr.category.web;

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
import xyz.sadiulhakim.npr.category.model.Category;
import xyz.sadiulhakim.npr.category.model.CategoryService;
import xyz.sadiulhakim.npr.pojo.TableUrlPojo;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.util.Optional;

@Controller
@RequestMapping("/categories")
class CategoryController {

    private final TableUrlPojo table_url = new TableUrlPojo("/categories/search", "/categories", "/categories/export-csv",
            "/categories/create_page", "/categories/page");

    private final AppProperties appProperties;
    private final CategoryService categoryService;

    CategoryController(AppProperties appProperties, CategoryService categoryService) {
        this.appProperties = appProperties;
        this.categoryService = categoryService;
    }

    @GetMapping("/page")
    String page(@RequestParam(defaultValue = "0") int page, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("categoryResult", categoryService.findAllPaginated(page));
        model.addAttribute("table_url", table_url);

        return "category/category_page";
    }

    @PostMapping("/save")
    String save(@ModelAttribute @Valid Category dto, BindingResult result, @RequestParam MultipartFile photo) {

        if (result.hasErrors()) {
            return "category/create_page";
        }

        categoryService.save(dto, photo);
        return "redirect:/categories/page";
    }

    @GetMapping("/search")
    String searchUser(@RequestParam String text, Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("categoryResult", categoryService.search(text, 0));
        model.addAttribute("table_url", table_url);

        return "category/category_page";
    }

    @GetMapping("/create_page")
    String createPage(@RequestParam(defaultValue = "0") long categoryId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("categoryId", categoryId);

        Optional<Category> category = categoryService.getById(categoryId);

        if (category.isEmpty()) {
            model.addAttribute("dto", new Category());
        } else {
            model.addAttribute("dto", category.get());
        }

        return "category/create_page";
    }

    @GetMapping("/delete/{id}")
    String deleteUser(@PathVariable long id) {

        categoryService.delete(id);
        return "redirect:/categories/page";
    }

    @GetMapping("/export-csv")
    ResponseEntity<byte[]> exportCsv() {
        var data = categoryService.getCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=categories.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
