package xyz.sadiulhakim.npr.product.web;

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
import xyz.sadiulhakim.npr.brand.model.Brand;
import xyz.sadiulhakim.npr.brand.model.BrandService;
import xyz.sadiulhakim.npr.category.model.Category;
import xyz.sadiulhakim.npr.category.model.CategoryService;
import xyz.sadiulhakim.npr.pojo.TableUrlPojo;
import xyz.sadiulhakim.npr.product.model.Product;
import xyz.sadiulhakim.npr.product.model.ProductService;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
class ProductController {

    private final AppProperties appProperties;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    private final TableUrlPojo table_url = new TableUrlPojo("/products/search", "/products", "/products/export-csv",
            "/products/create_page", "/products/page");

    ProductController(AppProperties appProperties, ProductService productService, CategoryService categoryService, BrandService brandService) {
        this.appProperties = appProperties;
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }

    @GetMapping("/page")
    String page(@RequestParam(defaultValue = "0") int page, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("productResult", productService.findAllPaginated(page));
        model.addAttribute("table_url", table_url);

        return "product/product_page";
    }

    @PostMapping("/save")
    String save(@ModelAttribute @Valid Product dto, @RequestParam MultipartFile photo, BindingResult result) {

        if (result.hasErrors()) {
            return "product/create_page";
        }
        productService.save(dto, photo);
        return "redirect:/products/page";
    }

    @GetMapping("/search")
    String searchUser(@RequestParam String text, Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("productResult", productService.search(text, 0));
        model.addAttribute("table_url", table_url);

        return "product/product_page";
    }

    @GetMapping("/create_page")
    String createPage(@RequestParam(defaultValue = "0") long productId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("productId", productId);

        Optional<Product> product = productService.getById(productId);

        if (product.isEmpty()) {
            model.addAttribute("dto", new Product());
        } else {
            model.addAttribute("dto", product.get());
        }

        List<Category> categories = categoryService.findAll();
        List<Brand> brands = brandService.findAll();
        model.addAttribute("categories", categories);
        model.addAttribute("brands", brands);

        return "product/create_page";
    }

    @GetMapping("/details_page")
    String detailsPage(@RequestParam(defaultValue = "0") long productId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("productId", productId);

        Optional<Product> product = productService.getById(productId);
        model.addAttribute("details", product.isPresent() ?
                product.get().getDetails() : new HashMap<>());

        return "product/details_page";
    }

    @PostMapping("/add_detail")
    String addProductDetail(@RequestParam String key, @RequestParam String value,
                            @RequestParam long productId) {

        productService.addDetail(key, value, productId);

        return "redirect:/products/details_page?productId=" + productId;
    }

    @GetMapping("/delete_detail/{key}")
    String deleteProductDetail(@PathVariable String key, @RequestParam long productId) {

        productService.deleteDetail(key, productId);
        return "redirect:/products/details_page?productId=" + productId;
    }

    @GetMapping("/delete/{id}")
    String deleteUser(@PathVariable long id) {

        productService.delete(id);
        return "redirect:/products/page";
    }

    @GetMapping("/export-csv")
    ResponseEntity<byte[]> exportCsv() {
        var data = productService.getCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=products.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
