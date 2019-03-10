package com.joshuastringfellow.dbexporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DBExporter {

    private final
    DbConnectionService dbConnectionService;

    private Path exportDir;

    @Autowired
    public DBExporter(DbConnectionService dbConnectionService) {
        this.dbConnectionService = dbConnectionService;
    }

    /**
     * Scheduled export for the top of every hour
     * @documentation https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/scheduling/support/CronSequenceGenerator.html
     */
    @Scheduled(cron = "0 0 * * * *")
    private void saveExportedDbFile() {
        Path file = dbConnectionService.getDbExportZipFile().toPath();
        if(Files.isReadable(file) && Files.isDirectory(exportDir)) {
            Path target = Paths.get(this.exportDir.toString(), file.getFileName().toString());
            try {
                Files.move(file, target);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @PostConstruct
    private void setExportDirectory() {
        // Grab the env set in the env.list for the shared volume
        this.exportDir = Paths.get("/" + System.getenv("EXPORT_DIR") + "/");
    }

}
