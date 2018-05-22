package main;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class Generator {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private boolean lock = false;

    private List<String> tableNames = new ArrayList<>();

    private static final String DDL_STRING =
            "CREATE TABLE $tableName (TIME_STAMP TIMESTAMP(3), RANDOM_STRING VARCHAR2(10))";

    private static final String DML_STRING =
            "INSERT INTO $tableName VALUES (?, ?)";

    String reportCurrentTime() {
        if (!lock) {
            lock = true;
            dropTables();
            tableNames = new ArrayList<>();
            for (int i = 1; i <= 500; i++) {
                LocalDateTime currentSecond = LocalDateTime.now().withNano(0);
                LocalDateTime now = LocalDateTime.now();
                String tableName = "tablename_" + currentSecond.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
                tableNames.add(tableName);
                jdbcTemplate.execute(DDL_STRING.replace("$tableName", tableName));
                log.info("table " + tableName + " (#" + i + ") created");
                do {
                    Timestamp dateParameter = Timestamp.valueOf(now);
                    String stringParameter = RandomStringUtils.random(10, true, false);
                    jdbcTemplate.update(DML_STRING.replace("$tableName", tableName), dateParameter, stringParameter);
                    //sleep();
                    now = LocalDateTime.now();
                } while (Duration.between(currentSecond, now).getSeconds() < 1);
            }
            lock = false;
            return "success!";
        } else {
            return "procedure in progress";
        }
    }

    private void sleep() {
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            log.info("something goes wrong: " + e.getLocalizedMessage());
        }
    }

    @Scheduled(fixedRate = 1000)
    void createView() {
        if (!tableNames.isEmpty()) {
            List<String> queries = tableNames.stream()
                    .map("SELECT * FROM "::concat)
                    .collect(Collectors.toList());
            String query = StringUtils.join(queries, "\n UNION ALL \n ");
            String view = "CREATE OR REPLACE VIEW ALL_TIMESTAMPS AS ( \n "
                    + query
                    + ")";
            jdbcTemplate.execute(view);
        }
    }

    void dropTables() {
        String drop = "BEGIN " +
                "FOR c IN (SELECT table_name FROM user_tables) LOOP  " +
                "EXECUTE IMMEDIATE ('drop table ' || c.table_name || ' cascade constraints'); \n " +
                "END LOOP; " +
                "END;";
        jdbcTemplate.execute(drop);
    }

}
