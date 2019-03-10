package com.joshuastringfellow.dbexporter;

import com.smattme.MysqlExportService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

@Service
public class DbConnectionService {

    private Properties connectionDetails;

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
