package com.fgroupboss.ai.psm.alarm.service;

public interface AlarmEscalationService {

    /** 扫描超时报警并写入升级记录，返回本次升级条数。 */
    int processEscalations();
}
