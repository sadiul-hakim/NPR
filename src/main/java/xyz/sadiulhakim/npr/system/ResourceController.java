package xyz.sadiulhakim.npr.system;

import org.springframework.stereotype.Controller;

@Controller
public class ResourceController {


}

/**
 * var bean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
 * DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z");
 * <p>
 * while (true) {
 * updateOrAddRow("Date Time", ZonedDateTime.now().format(formatter));
 * updateOrAddRow("Name", bean.getName());
 * updateOrAddRow("Arch", bean.getArch());
 * updateOrAddRow("Version", bean.getVersion());
 * <p>
 * updateOrAddRow("Available Processors", bean.getAvailableProcessors());
 * updateOrAddRow("System Load Average", formatDouble(bean.getSystemLoadAverage()));
 * updateOrAddRow("Committed Virtual Memory", formatBytes(bean.getCommittedVirtualMemorySize()));
 * updateOrAddRow("CPU Load", formatPercentage(bean.getCpuLoad()));
 * updateOrAddRow("Free Memory Size", formatBytes(bean.getFreeMemorySize()));
 * updateOrAddRow("Free Swap Space Size", formatBytes(bean.getFreeSwapSpaceSize()));
 * updateOrAddRow("Process CPU Load", formatPercentage(bean.getProcessCpuLoad()));
 * updateOrAddRow("Process CPU Time", formatTime(bean.getProcessCpuTime()));
 * updateOrAddRow("Total Memory Size", formatBytes(bean.getTotalMemorySize()));
 * updateOrAddRow("Total Swap Memory Size", formatBytes(bean.getTotalSwapSpaceSize()));
 * <p>
 * try {
 * TimeUnit.SECONDS.sleep(1);
 * } catch (Exception ex) {
 * System.out.println(ex.getMessage());
 * }
 * }
 */
