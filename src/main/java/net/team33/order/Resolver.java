package net.team33.order;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;

class Resolver implements Function<FileTime, Path> {

    private final Path root;

    Resolver(final Path root) {
        this.root = root;
    }

    @Override
    public Path apply(final FileTime fileTime) {
        final ZonedDateTime zoned = fileTime.toInstant().atZone(ZoneId.systemDefault());
        final Path newParent = root
                .resolve(String.format("%04d", zoned.getYear()))
                .resolve(String.format("%02d", zoned.getMonthValue()))
                .resolve(String.format("%02d", zoned.getDayOfMonth()));
        return newParent;
    }
}
