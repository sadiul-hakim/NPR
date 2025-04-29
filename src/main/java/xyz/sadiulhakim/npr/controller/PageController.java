package xyz.sadiulhakim.npr.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import xyz.sadiulhakim.npr.category.model.CategoryService;
import xyz.sadiulhakim.npr.product.model.ProductService;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

@Controller
class PageController {

    private final AppProperties appProperties;
    private final CategoryService categoryService;
    private final ProductService productService;

    PageController(AppProperties appProperties, CategoryService categoryService, ProductService productService) {
        this.appProperties = appProperties;
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping("/")
    String home(Model model, Authentication authentication) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        String username = authentication == null ? "guest" : authentication.getName();
        model.addAttribute("username", username);
        model.addAttribute("categories", categoryService.findAll(0, 25));
        model.addAttribute("topRatedProduct", productService.findTopRatedProduct(0, 25));
        return "index";
    }

    @GetMapping("/admin_login")
    String loginPage() {
        return "auth/admin_login";
    }
}