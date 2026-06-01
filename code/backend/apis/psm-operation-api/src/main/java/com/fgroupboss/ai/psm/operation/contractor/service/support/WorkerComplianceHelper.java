package com.fgroupboss.ai.psm.operation.contractor.service.support;

import com.fgroupboss.ai.psm.operation.contractor.model.entity.WorkerCertificateEntity;
import com.fgroupboss.ai.psm.operation.contractor.model.entity.WorkerTrainingRecordEntity;

import java.time.LocalDate;
import java.util.List;

/**
 * 人员证书/培训合规状态计算，供列表展示与 eligibility-check 复用。
 */
public final class WorkerComplianceHelper {

    private static final String RESULT_PASSED = "PASSED";

    private WorkerComplianceHelper() {
    }

    public static String computeCertificateStatus(List<WorkerCertificateEntity> certificates) {
        if (certificates == null || certificates.isEmpty()) {
            return "MISSING";
        }
        LocalDate today = LocalDate.now();
        boolean expiringSoon = false;
        for (WorkerCertificateEntity certificate : certificates) {
            if (isExpired(certificate.getValidTo(), today)) {
                return "EXPIRED";
            }
            if (certificate.getValidTo() != null && !certificate.getValidTo().isAfter(today.plusDays(30))) {
                expiringSoon = true;
            }
        }
        return expiringSoon ? "EXPIRING" : "VALID";
    }

    public static String computeTrainingStatus(List<WorkerTrainingRecordEntity> trainings) {
        if (trainings == null || trainings.isEmpty()) {
            return "MISSING";
        }
        LocalDate today = LocalDate.now();
        for (WorkerTrainingRecordEntity training : trainings) {
            if (RESULT_PASSED.equals(training.getTrainingResult())
                    && !isExpired(training.getValidTo(), today)) {
                return "VALID";
            }
        }
        return "INVALID";
    }

    public static boolean hasExpiredCertificate(List<WorkerCertificateEntity> certificates) {
        if (certificates == null || certificates.isEmpty()) {
            return false;
        }
        LocalDate today = LocalDate.now();
        for (WorkerCertificateEntity certificate : certificates) {
            if (isExpired(certificate.getValidTo(), today)) {
                return true;
            }
        }
        return false;
    }

    public static WorkerCertificateEntity firstExpiredCertificate(List<WorkerCertificateEntity> certificates) {
        if (certificates == null) {
            return null;
        }
        LocalDate today = LocalDate.now();
        for (WorkerCertificateEntity certificate : certificates) {
            if (isExpired(certificate.getValidTo(), today)) {
                return certificate;
            }
        }
        return null;
    }

    public static boolean hasInvalidTraining(List<WorkerTrainingRecordEntity> trainings) {
        return !"VALID".equals(computeTrainingStatus(trainings));
    }

    private static boolean isExpired(LocalDate validTo, LocalDate today) {
        return validTo != null && validTo.isBefore(today);
    }
}
