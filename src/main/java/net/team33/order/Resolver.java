package net.team33.order;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import java.util.stream.Stream;

class Resolver implements BiFunction<FileTime, String, Path> {

    private static final long NANOS_PER_MS = 1000 * 1000;

    private final Path root;

    Resolver(final Path root) {
        this.root = root;
    }

    @Override
    public Path apply(final FileTime fileTime, final String fileName) {
        final ZonedDateTime zoned = fileTime.toInstant().atZone(ZoneId.systemDefault());
        final String newFileName = String.format(
                "%02d%02d%02d%03d.%s",
                zoned.getHour(), zoned.getMinute(), zoned.getSecond(), zoned.getNano() / NANOS_PER_MS, fileName);
        return Stream.of(zoned.getYear(), zoned.getMonthValue(), zoned.getDayOfMonth())
                .map(Object::toString)
                .map(s -> Paths.get(s))
                .reduce(root, Path::resolve)
                .resolve(newFileName);
    }
}
