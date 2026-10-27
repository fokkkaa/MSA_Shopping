package io.hexlet.spring.shopping;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.ActiveProfiles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:flywaycheck;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.flyway.enabled=true",
        "spring.jpa.hibernate.ddl-auto=validate"
})
@ActiveProfiles("test")
class FlywayMigrationTest {
    @Autowired private Flyway flyway;
    @Autowired private JdbcTemplate jdbc;

    @Test
    void createsSchemaAndSampleShoppingList() {
        assertThat(flyway.info().current().getVersion().toString()).isEqualTo("1.1");
        assertThat(flyway.info().applied()).hasSize(2);
        assertThat(jdbc.queryForList("SELECT name FROM products ORDER BY id", String.class))
                .containsExactly("Milk", "Bread", "Apples");
        assertThat(jdbc.queryForObject("SELECT quantity FROM products WHERE name='Milk'", Integer.class)).isEqualTo(2);
        assertThat(jdbc.queryForObject("SELECT purchased FROM products WHERE name='Apples'", Boolean.class)).isTrue();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM products p JOIN shopping_lists s ON p.shopping_list_id=s.id JOIN users u ON p.author_id=u.id", Integer.class)).isEqualTo(3);
    }

    @Test
    void repeatedMigrationDoesNotDuplicateData() {
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM products", Integer.class)).isEqualTo(3);
    }

    @Test
    void adoptsExistingSchemaWithoutLosingRecords() {
        var source = new DriverManagerDataSource("jdbc:h2:mem:legacy-" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        new ResourceDatabasePopulator(new ClassPathResource("db/migration/structure/V1__create_shopping_schema.sql")).execute(source);
        var legacy = new JdbcTemplate(source);
        legacy.update("INSERT INTO users (first_name,last_name,email,password) VALUES ('Before','Flyway','legacy@example.com','password')");
        Flyway migration = Flyway.configure().dataSource(source)
                .locations("classpath:db/migration/structure", "classpath:db/migration/data")
                .baselineOnMigrate(true).baselineVersion("1").load();
        assertThat(migration.migrate().migrationsExecuted).isEqualTo(1);
        assertThat(legacy.queryForObject("SELECT first_name FROM users WHERE email='legacy@example.com'", String.class)).isEqualTo("Before");
        assertThat(legacy.queryForObject("SELECT COUNT(*) FROM products", Integer.class)).isEqualTo(3);
    }

    @Test
    void detectsChangesToAppliedMigration(@TempDir Path directory) throws Exception {
        Path script = directory.resolve("V1__example.sql");
        Files.writeString(script, "CREATE TABLE example (id INT PRIMARY KEY);");
        Flyway migration = Flyway.configure()
                .dataSource("jdbc:h2:mem:checksum-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", "")
                .locations("filesystem:" + directory.toAbsolutePath()).load();
        migration.migrate();
        Files.writeString(script, "CREATE TABLE example (id BIGINT PRIMARY KEY);");
        assertThatThrownBy(migration::validate).isInstanceOf(FlywayException.class)
                .hasMessageContaining("checksum mismatch");
    }
}
