package main;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Component
public class Scheduler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String DDL_STRING =
            "CREATE TABLE $tableName (TIME_STAMP TIMESTAMP(3), RANDOM_STRING VARCHAR2(10))";

    private static final String DML_STRING =
            "INSERT INTO $tableName VALUES (?, ?)";

    void reportCurrentTime() {
        do {
            LocalDateTime currentSecond = LocalDateTime.now().withNano(0);
            LocalDateTime now = LocalDateTime.now();
            String tableName = "tablename_" + currentSecond.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
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

}