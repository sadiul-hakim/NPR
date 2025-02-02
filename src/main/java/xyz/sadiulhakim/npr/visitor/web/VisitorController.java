package xyz.sadiulhakim.npr.visitor.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.sadiulhakim.npr.pojo.TableUrlPojo;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;
import xyz.sadiulhakim.npr.visitor.model.VisitorService;

@Controller
@RequestMapping("/visitors")
class VisitorController {

    private final VisitorService visitorService;
    private final AppProperties appProperties;

    private final TableUrlPojo table_url = new TableUrlPojo("/visitors/search", "/visitors", "/visitors/export-csv",
            "", "/visitors/page");

    VisitorController(VisitorService visitorService, AppProperties appProperties) {
        this.visitorService = visitorService;
        this.appProperties = appProperties;
    }

    @GetMapping("/page")
    String page(@RequestParam(defaultValue = "0") int page, Model model) {

        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("visitorResult", visitorService.findAllPaginated(page));
        model.addAttribute("table_url", table_url);

        return "visitor/visitor_page";
    }

    @GetMapping("/search")
    String searchUser(@RequestParam String text, Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));
        model.addAttribute("visitorResult", visitorService.search(text, 0));
        model.addAttribute("table_url", table_url);

        return "visitor/visitor_page";
    }

    @GetMapping("/delete/{id}")
    String deleteUser(@PathVariable long id) {

        visitorService.delete(id);
        return "redirect:/visitors/page";
    }

    @GetMapping("/export-csv")
    ResponseEntity<byte[]> exportCsv() {
        var data = visitorService.getCsvData();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=visitors.csv");
        headers.add(HttpHeaders.CONTENT_TYPE, "text/csv");
        return new ResponseEntity<>(data, headers, HttpStatus.OK);
    }
}
