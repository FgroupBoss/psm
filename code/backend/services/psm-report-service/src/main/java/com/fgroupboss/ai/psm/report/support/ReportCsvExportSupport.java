package com.fgroupboss.ai.psm.report.support;

import com.fgroupboss.ai.psm.common.BusinessException;
import com.fgroupboss.ai.psm.report.model.vo.AlarmReportDetailVO;
import com.fgroupboss.ai.psm.report.model.vo.WorkPermitReportDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 报表 CSV 文件落盘（一期试点本地存储，可替换对象存储）。
 */
@Slf4j
@Component
public class ReportCsvExportSupport {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Value("${psm.report.export-root:./data/psm-exports}")
    private String exportRoot;

    private Path rootPath;

    @PostConstruct
    public void init() throws IOException {
        rootPath = Paths.get(exportRoot).toAbsolutePath().normalize();
        Files.createDirectories(rootPath);
        log.info("report export root={}", rootPath);
    }

    public String writeWorkPermits(Long tenantId, Long taskId, List<WorkPermitReportDetailVO> rows) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,permitNo,workType,status,title,areaId,contractorCompanyId,siteTraceComplete,contractorEligibilityChecked,planStartAt,createdAt\n");
        for (WorkPermitReportDetailVO row : rows) {
            csv.append(row.getId()).append(',')
                    .append(escape(row.getPermitNo())).append(',')
                    .append(escape(row.getWorkType())).append(',')
                    .append(escape(row.getStatus())).append(',')
                    .append(escape(row.getTitle())).append(',')
                    .append(nullSafe(row.getAreaId())).append(',')
                    .append(nullSafe(row.getContractorCompanyId())).append(',')
                    .append(row.isSiteTraceComplete()).append(',')
                    .append(row.isContractorEligibilityChecked()).append(',')
                    .append(escape(formatDate(row.getPlanStartAt()))).append(',')
                    .append(escape(formatDate(row.getCreatedAt()))).append('\n');
        }
        return writeFile(tenantId, taskId, "WORK_PERMIT", csv.toString());
    }

    public String writeAlarms(Long tenantId, Long taskId, List<AlarmReportDetailVO> rows) {
        StringBuilder csv = new StringBuilder();
        csv.append("id,alarmNo,alarmLevel,status,sourceType,areaId,hazardId,confirmDurationMinutes,disposeDurationMinutes,firstOccurredAt\n");
        for (AlarmReportDetailVO row : rows) {
            csv.append(row.getId()).append(',')
                    .append(escape(row.getAlarmNo())).append(',')
                    .append(escape(row.getAlarmLevel())).append(',')
                    .append(escape(row.getStatus())).append(',')
                    .append(escape(row.getSourceType())).append(',')
                    .append(nullSafe(row.getAreaId())).append(',')
                    .append(nullSafe(row.getHazardId())).append(',')
                    .append(nullSafe(row.getConfirmDurationMinutes())).append(',')
                    .append(nullSafe(row.getDisposeDurationMinutes())).append(',')
                    .append(escape(formatDate(row.getFirstOccurredAt()))).append('\n');
        }
        return writeFile(tenantId, taskId, "ALARM", csv.toString());
    }

    public String writeSummary(Long tenantId, Long taskId, String reportType, String[] headers, List<String[]> rows) {
        StringBuilder csv = new StringBuilder();
        csv.append(String.join(",", headers)).append('\n');
        for (String[] row : rows) {
            for (int i = 0; i < row.length; i++) {
                if (i > 0) {
                    csv.append(',');
                }
                csv.append(escape(row[i]));
            }
            csv.append('\n');
        }
        return writeFile(tenantId, taskId, reportType, csv.toString());
    }

    public Path resolve(String storagePath) {
        Path path = rootPath.resolve(storagePath).normalize();
        if (!path.startsWith(rootPath) || !Files.exists(path)) {
            throw new BusinessException(404, "export file not found");
        }
        return path;
    }

    private String writeFile(Long tenantId, Long taskId, String reportType, String content) {
        String relative = tenantId + "/" + reportType + "-" + taskId + ".csv";
        Path target = rootPath.resolve(relative).normalize();
        if (!target.startsWith(rootPath)) {
            throw new BusinessException(400, "invalid export path");
        }
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, content.getBytes(StandardCharsets.UTF_8));
            return relative;
        } catch (IOException ex) {
            throw new BusinessException(500, "export file write failed");
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String nullSafe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String formatDate(Date date) {
        return date == null ? "" : DATE_FORMAT.format(date);
    }
}
