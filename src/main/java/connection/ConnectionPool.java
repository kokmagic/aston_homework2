package connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost:5432/hometask2";
    private static final HikariConfig CONFIG = new HikariConfig();
    private final HikariDataSource dataSource;

    static {
        CONFIG.setJdbcUrl(URL);
        CONFIG.setUsername(USER);
        CONFIG.setPassword(PASSWORD);
        CONFIG.setDriverClassName(DB_DRIVER);
        CONFIG.addDataSourceProperty("cachePrepStmts", "true");
        CONFIG.addDataSourceProperty("prepStmtCacheSize", "250");
        CONFIG.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        CONFIG.setMaximumPoolSize(10);
        CONFIG.setMinimumIdle(5);
        CONFIG.setConnectionTimeout(30000);
        CONFIG.setIdleTimeout(600000);
    }

    public ConnectionPool() {
        this.dataSource = new HikariDataSource(CONFIG);
    }

    public ConnectionPool(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
