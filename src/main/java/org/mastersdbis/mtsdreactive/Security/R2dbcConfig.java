package org.mastersdbis.mtsdreactive.Security;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

import java.time.Duration;

@Configuration
@EnableR2dbcRepositories(basePackages = "org.mastersdbis.mtsdreactive.Repositories")
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    @Value("${DB_HOST:localhost}")
    private String host;

    @Value("${DB_PORT:5432}")
    private int port;

    @Value("${DB_NAME:MTSD}")
    private String database;

    @Value("${DB_USER:postgres}")
    private String username;

    @Value("${DB_PASS:parola}")
    private String password;

    @Value("${spring.r2dbc.pool.max-size:50}")
    private int maxPoolSize;

    @Value("${spring.r2dbc.pool.initial-size:10}")
    private int initialPoolSize;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        PostgresqlConnectionFactory factory = new PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(host)
                .port(port)
                .database(database)
                .username(username)
                .password(password)
                .build()
        );

        ConnectionPoolConfiguration poolConfig = ConnectionPoolConfiguration.builder(factory)
            .initialSize(initialPoolSize)
            .maxSize(maxPoolSize)
            .maxIdleTime(Duration.ofMinutes(30))
            .validationQuery("SELECT 1")
            .build();

        return new ConnectionPool(poolConfig);
    }

    /**
     * R2dbcEntityTemplate — required by TaskRepository for composite key operations.
     * Spring Boot auto-configures one, but declaring it explicitly here ensures
     * it uses our pooled connection factory.
     */
    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory) {
        return new R2dbcEntityTemplate(connectionFactory);
    }
}