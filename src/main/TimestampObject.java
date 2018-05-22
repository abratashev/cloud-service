package main;

import java.time.LocalDateTime;

public class TimestampObject {
    LocalDateTime timestamp;
    String string;

    public TimestampObject(LocalDateTime timestamp, String string) {
        this.timestamp = timestamp;
        this.string = string;
    }

    @Override
    public String toString() {
        return "timestamp: " + timestamp + " string: " + string;
    }
}
