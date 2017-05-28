package net.team33.order;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

class FileName {

    final String name;
    final String prefix;
    final Optional<String> suffix;
    final long millis;
    final ZonedDateTime zoned;

    FileName(final Path path) throws IOException {
        this(path.getFileName().toString(), Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS));
    }

    FileName(final String name, final FileTime time) {
        millis = time.toMillis();
        zoned = ZonedDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
        this.name = name;

        final int index = name.lastIndexOf('.');
        if (0 > index) {
            prefix = name;
            suffix = Optional.empty();
        } else {
            prefix = name.substring(0, index);
            suffix = Optional.of(name.substring(index + 1));
        }
    }
}
