package com.joshuastringfellow.dbexporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DBExporter {

    private final
    DbConnectionService dbConnectionService;


    @Autowired
    public DBExporter(DbConnectionService dbConnectionService) {
        this.dbConnectionService = dbConnectionService;
    }

    /**
     * Scheduled export every half hour
     * https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
     */
    @Scheduled(cron = "*/30 * * * *")
    private void saveExportedDbFile() {
        dbConnectionService.saveExportedDbFile();
    }

}
