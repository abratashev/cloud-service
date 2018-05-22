package main;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
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
public class Scheduler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private List<String> tableNames = new ArrayList<>();

    private static final String DDL_STRING =
            "CREATE TABLE $tableName (TIME_STAMP TIMESTAMP(3), RANDOM_STRING VARCHAR2(10))";

    private static final String DML_STRING =
            "INSERT INTO $tableName VALUES (?, ?)";

    void reportCurrentTime() {
        do {
            LocalDateTime currentSecond = LocalDateTime.now().withNano(0);
            LocalDateTime now = LocalDateTime.now();
            String tableName = "tablename_" + currentSecond.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
            tableNames.add(tableName);
            jdbcTemplate.execute(DDL_STRING.replace("$tableName", tableName));
            System.out.println("table " + tableName + " created");
            do {
                Timestamp dateParameter = Timestamp.valueOf(now);
                String stringParameter = RandomStringUtils.random(10, true, false);
                jdbcTemplate.update(DML_STRING.replace("$tableName", tableName), dateParameter, stringParameter);
                //sleep();
                now = LocalDateTime.now();
            } while (Duration.between(currentSecond, now).getSeconds() < 1);
        } while (true);
    }

    private void sleep() {
        try {
            Thread.sleep(300);
        } catch (Exception e) {
            System.out.println("something goes wrong: " + e.getLocalizedMessage());
        }
    }

    @Scheduled(fixedRate = 1000)
    void createView() {
        if(!tableNames.isEmpty()) {
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

}
