package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class Profiler {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String viewQuery = "SELECT * FROM ALL_TIMESTAMPS WHERE TIME_STAMP > ? AND TIME_STAMP < ?";

    String profileViewQuery(Timestamp timestamp1, Timestamp timestamp2) {
        long start = System.nanoTime();
        List<Map<String, Object>> timestampObjectList =
                jdbcTemplate.queryForList(viewQuery, timestamp1, timestamp2);
        long elapsed = System.nanoTime() - start;
        timestampObjectList.forEach(timestampObject ->
                log.info(
                        "timestamp: " + timestampObject.get("TIME_STAMP") +
                                " string: " + timestampObject.get("RANDOM_STRING")
                )
        );
        String result = "Found " + timestampObjectList.size() + " records. Elapsed Time:" + elapsed + "ms";
        log.info(result);
        return result;
    }


    String profileFunctionQuery(Timestamp timestamp1, Timestamp timestamp2) {
        long start = System.nanoTime();
        LocalDateTime dt1 = timestamp1.toLocalDateTime();
        LocalDateTime dt2 = timestamp2.toLocalDateTime();
        LocalDateTime current = dt1;
        List<Map<String, Object>> timestampObjectList = new ArrayList<>();
        while (current.isBefore(dt2)) {
            String tableName = "tablename_" + current.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
            timestampObjectList.addAll(jdbcTemplate.queryForList("SELECT * FROM " + tableName + " WHERE TIME_STAMP > ? AND TIME_STAMP < ?", timestamp1, timestamp2));
            current = current.plusSeconds(1);
        }
        long elapsed = System.nanoTime() - start;
        timestampObjectList.forEach(timestampObject ->
                log.info(
                        "timestamp: " + timestampObject.get("TIME_STAMP") +
                                " string: " + timestampObject.get("RANDOM_STRING")
                )
        );
        String result = "Found " + timestampObjectList.size() + " records. Elapsed Time:" + elapsed + "ms";
        log.info(result);
        return result;
    }

}