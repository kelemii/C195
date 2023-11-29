package Help;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
public class TimeConversion {


    public static ZonedDateTime convertTime(LocalDateTime localDateTime) {
        ZonedDateTime zoneDateTime = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcDateTime = zoneDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcDateTime;
    }

    public static LocalDateTime convertUtcToTime(LocalDateTime utcDateTime) {
        ZoneId systemDefaultZone = ZoneId.systemDefault();
        ZonedDateTime utcZonedDateTime = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(systemDefaultZone);
        return localZonedDateTime.toLocalDateTime();
    }

}
