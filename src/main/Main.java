package main;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@SpringBootApplication
@EnableScheduling
@RestController
public class Main {

    @Autowired
    Generator scheduler;

    @Autowired
    Profiler profiler;

    //TODO Переписать как RequestParams
    private Timestamp timestamp1 = Timestamp.valueOf("2018-05-23 02:53:29.00");
    private Timestamp timestamp2 = Timestamp.valueOf("2018-05-23 02:54:29.00");

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }

    @RequestMapping("/generate")
    public String generate() {
        return scheduler.reportCurrentTime();
    }

    @RequestMapping("/profile-view")
    public String profileView() {
        return profiler.profileViewQuery(timestamp1, timestamp2);
    }

    @RequestMapping("/profile-function")
    public String profileFunction() {
        return profiler.profileFunctionQuery(timestamp1, timestamp2);
    }

    @RequestMapping("/profile-table")
    public String profileTable() {
        return profiler.profileTableQuery(timestamp1, timestamp2);
    }

}