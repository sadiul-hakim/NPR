package xyz.sadiulhakim.npr.brand.web;

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
import xyz.sadiulhakim.npr.brand.model.BrandService;
import xyz.sadiulhakim.npr.brand.model.Brand;
import xyz.sadiulhakim.npr.pojo.TableUrlPojo;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

import java.util.Optional;

@Controller
@RequestMapping("/brands")
class BrandController {

    private final TableUrlPojo table_url = new TableUrlPojo("/brands/search", "/brands", "/brands/export-csv",
            "/brands/create_page", "/brands/page");

    private final AppProperties appProperties;
    private final BrandService brandService;

    BrandController(AppProperties appProperties, BrandService brandService) {
        this.appProperties = appProperties;
        this.brandService = brandService;
    }

    @GetMapping("/page")
    String page(@RequestParam(defaultValue = "0") int page, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("brandResult", brandService.findAllPaginated(page));
        model.addAttribute("table_url", table_url);

        return "brand/brand_page";
    }

    @PostMapping("/save")
    String save(@ModelAttribute @Valid Brand dro, @RequestParam MultipartFile photo, BindingResult result) {

        if(result.hasErrors()){
            return "brand/create_page";
        }

        brandService.save(dro, photo);
        return "redirect:/brands/page";
    }

    @GetMapping("/search")
    String searchUser(@RequestParam String text, Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("brandResult", brandService.search(text, 0));
        model.addAttribute("table_url", table_url);

        return "brand/brand_page";
    }

    @GetMapping("/create_page")
    String createPage(@RequestParam(defaultValue = "0") long brandId, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("brandId", brandId);

        Optional<Brand> brand = brandService.getById(brandId);

        if (brand.isEmpty()) {
            model.addAttribute("dto", new Brand());
        } else {
            model.addAttribute("dto", brand.get());
        }

        return "brand/create_page";
    }

    @GetMapping("/delete/{id}")
    String deleteUser(@PathVariable long id) {

        brandService.delete(id);
        return "redirect:/brands/page";
    }

    @GetMapping("/export-csv")
    ResponseEntity<byte[]> exportCsv() {
        var data = brandService.getCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=brands.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
