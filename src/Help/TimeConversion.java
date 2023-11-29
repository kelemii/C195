package Help;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * this class helps me convert time to and from UTC
 */
public class TimeConversion {

    /**
     * converts time to UTC
     * @param localDateTime local system time
     * @return utc time converted
     */
    public static ZonedDateTime convertTime(LocalDateTime localDateTime) {
        ZonedDateTime zoneDateTime = localDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime utcDateTime = zoneDateTime.withZoneSameInstant(ZoneId.of("UTC"));
        return utcDateTime;
    }

    /**
     *  converts utc times from db to localtime
     * @param utcDateTime utc time from db
     * @return local time
     */
    public static LocalDateTime convertUtcToTime(LocalDateTime utcDateTime) {
        ZoneId systemDefaultZone = ZoneId.systemDefault();
        ZonedDateTime utcZonedDateTime = utcDateTime.atZone(ZoneId.of("UTC"));
        ZonedDateTime localZonedDateTime = utcZonedDateTime.withZoneSameInstant(systemDefaultZone);
        return localZonedDateTime.toLocalDateTime();
    }

}
