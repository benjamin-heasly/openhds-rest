package org.openhds.repository.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Created by Ben on 6/16/15.
 *
 * Convert ZonedDateTime to a database type that we can index.
 *
 * Going into the database: convert to UTC and store as milliseconds since Epoch.
 *
 * Coming from the database: treat as milliseconds since Epoch, UTC.
 *
 */
@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Long> {

    public static final ZoneId ZONE_UTC = ZoneId.of("UTC");

    @Override
    public Long convertToDatabaseColumn(ZonedDateTime attribute) {
        if (null == attribute) {
            return null;
        }
        return attribute.withZoneSameInstant(ZONE_UTC).toInstant().toEpochMilli();
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Long dbData) {
        if (null == dbData) {
            return null;
        }
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(dbData), ZONE_UTC);
    }
}
