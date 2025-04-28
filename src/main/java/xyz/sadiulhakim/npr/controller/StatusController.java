package xyz.sadiulhakim.npr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.auth.AuthenticatedUserUtil;

@Controller
@RequestMapping("/status")
public class StatusController {

    private final HealthEndpoint healthEndpoint;
    private final InfoEndpoint infoEndpoint;
    private final MetricsEndpoint metricsEndpoint;
    private final AppProperties appProperties;

    @Autowired
    public StatusController(HealthEndpoint healthEndpoint,
                            InfoEndpoint infoEndpoint,
                            MetricsEndpoint metricsEndpoint, AppProperties appProperties) {
        this.healthEndpoint = healthEndpoint;
        this.infoEndpoint = infoEndpoint;
        this.metricsEndpoint = metricsEndpoint;
        this.appProperties = appProperties;
    }

    @GetMapping
    public String statusPage(Model model) {
        model.addAttribute("name", AuthenticatedUserUtil.getName());
        model.addAttribute("picture", AuthenticatedUserUtil.getPicture(appProperties.getUserImageFolder()));

        model.addAttribute("health", healthEndpoint.health());
        model.addAttribute("info", infoEndpoint.info());

        // Memory stats
        model.addAttribute("memoryUsage", metricsEndpoint.metric("jvm.memory.used", null));
        model.addAttribute("heapMemory", metricsEndpoint.metric("jvm.memory.heap.used", null));
        model.addAttribute("nonHeapMemory", metricsEndpoint.metric("jvm.memory.non-heap.used", null));

        // CPU Usage (optional)
        model.addAttribute("cpuUsage", metricsEndpoint.metric("system.cpu.usage", null));

        // Uptime
        model.addAttribute("uptime", metricsEndpoint.metric("process.uptime", null));

        // Garbage Collection stats
        model.addAttribute("gcCount", metricsEndpoint.metric("jvm.gc.pause.count", null));
        model.addAttribute("gcTime", metricsEndpoint.metric("jvm.gc.pause.sum", null));

        return "status";
    }
}

