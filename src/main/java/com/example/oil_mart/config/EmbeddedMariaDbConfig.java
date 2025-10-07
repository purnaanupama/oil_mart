//package com.example.oil_mart.config;
//
//import ch.vorburger.exec.ManagedProcessException;
//import ch.vorburger.mariadb4j.DB;
//import ch.vorburger.mariadb4j.DBConfigurationBuilder;
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//import java.io.File;
//import java.sql.SQLException;
//
//@Configuration
//public class EmbeddedMariaDbConfig {
//
//    @Bean(initMethod = "start", destroyMethod = "stop")
//    public DB embeddedMariaDb() throws Exception {
//        // Create cross-platform data directory path
//        String userHome = System.getProperty("user.home");
//        String dataDir = userHome + File.separator + "oilmart-db";
//
//        // Create directory if it doesn't exist
//        File dataDirFile = new File(dataDir);
//        if (!dataDirFile.exists()) {
//            dataDirFile.mkdirs();
//        }
//
//        DBConfigurationBuilder config = DBConfigurationBuilder.newBuilder();
//        config.setPort(3307);
//        config.setDataDir(dataDir);  // Set persistent data directory
//
//        DB db = DB.newEmbeddedDB(config.build());
//        return db;
//    }
//
//    @Bean
//    public DataSource dataSource(DB embeddedMariaDb) throws SQLException, ManagedProcessException {
//        // Ensure the DB is running and create schema
//        embeddedMariaDb.createDB("testdb");
//
//        String url = "jdbc:mariadb://localhost:3307/testdb";
//        HikariDataSource ds = new HikariDataSource();
//        ds.setJdbcUrl(url);
//        ds.setUsername("root");
//        ds.setPassword(""); // default for MariaDB4j
//        return ds;
//    }
//}