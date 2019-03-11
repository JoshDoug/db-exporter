package com.joshuastringfellow.dbexporter;

import com.smattme.MysqlExportService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

@Service
public class DbConnectionService {

    private Properties connectionDetails;
    private Path exportDir;

    public File getDbExportZipFile() {
        MysqlExportService mysqlExportService = new MysqlExportService(getDatabaseConnectionDetails());
        try {
            mysqlExportService.export();
        } catch (IOException | SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return mysqlExportService.getGeneratedZipFile();
    }

    // Return unzipped db file
//    public File getDbExportFile() {
//        return null;
//    }

    public boolean saveExportedDbFile() {
        Path file = getDbExportZipFile().toPath();
        if(Files.isReadable(file) && Files.isDirectory(exportDir)) {
            Path target = Paths.get(this.exportDir.toString(), file.getFileName().toString());
            try {
                Files.move(file, target);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    @PostConstruct
    private void setExportDirectory() {
        // Grab the env set in the env.list for the shared volume
        this.exportDir = Paths.get("/" + System.getenv("EXPORT_DIR") + "/");
    }

    private Properties getDatabaseConnectionDetails() {
        if (connectionDetails != null) {
            return connectionDetails;
        } else {

            connectionDetails = getEnvListProperties();
            connectionDetails.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");
            connectionDetails.setProperty(MysqlExportService.TEMP_DIR, new File("external").getPath());

            return connectionDetails;
        }
    }

    private Properties getEnvListProperties() {
        Properties properties = new Properties();

        // Set Host, Port, Database, User, User Password
        String jdbcUrl = new StringBuilder("jdbc:mysql://")
                .append(System.getenv(EnvConfig.Host.getProperty()))
                .append(":")
                .append(System.getenv(EnvConfig.Port.getProperty()))
                .append("/")
                .append(System.getenv(EnvConfig.DataBase.getProperty()))
                .append("?useUnicode=true")
                .append("&useJDBCCompliantTimezoneShift=true")
                .append("&useLegacyDatetimeCode=false")
                .append("&serverTimezone=UTC")
                .append("&useSSL=false")
                .toString();

        properties.setProperty(MysqlExportService.JDBC_CONNECTION_STRING, jdbcUrl);
        properties.setProperty(MysqlExportService.DB_USERNAME, System.getenv(EnvConfig.User.getProperty()));
        properties.setProperty(MysqlExportService.DB_PASSWORD, System.getenv(EnvConfig.UserPassword.getProperty()));

        return properties;
    }

    enum EnvConfig {
        DataBase("MYSQL_DATABASE"),
        User("MYSQL_USER"),
        UserPassword("MYSQL_PASSWORD"),
        Host("HOST"),
        Port("PORT"),
        RootPassword("MYSQL_ROOT_PASSWORD");

        private final String property;

        EnvConfig(String property) {
            this.property = property;
        }

        public String getProperty() {
            return property;
        }
    }

}
