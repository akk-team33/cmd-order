package net.team33.order;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;

public class Resolver implements BiFunction<String, FileTime, Path> {

    private List<Element> elements;

    Resolver(final List<Element> elements) {
        this.elements = elements;
    }

    @Override
    public Path apply(final String fileName, final FileTime fileTime) {
        final long millis = fileTime.toMillis();
        final ZonedDateTime dateTime = ZonedDateTime.ofInstant(fileTime.toInstant(), ZoneId.systemDefault());
        final int dotPos = fileName.lastIndexOf('.');
        final Optional<String> extension = (0 > dotPos)
                ? Optional.empty()
                : Optional.of(fileName.substring(dotPos + 1));

        Path result = Paths.get("");
        for (final Element element : elements) {
            result = result.resolve(element.resolve(
                    fileName,
                    millis,
                    dateTime,
                    extension
            ));
        }
        return result;
    }

    enum Element {
        Y("Takes the year value from the file time, eg. \"2017\"") {
            @Override String resolve(final String n, final long m, final ZonedDateTime t, final Optional<String> x) {
                return String.format("%04d", t.getYear());
            }
        },
        M("Takes the month value from the file time, eg. \"05\" (for May)") {
            @Override String resolve(final String n, final long m, final ZonedDateTime t, final Optional<String> x) {
                return String.format("%02d", t.getMonthValue());
            }
        },
        D("Takes the day of month value from the file time, eg. \"27\" (for the 27th day in May)") {
            @Override String resolve(final String n, final long m, final ZonedDateTime t, final Optional<String> x) {
                return String.format("%02d", t.getDayOfMonth());
            }
        },
        X("Takes the original file name extension, eg. \"jpg\"") {
            @Override String resolve(final String n, final long m, final ZonedDateTime t, final Optional<String> x) {
                return x.orElse("").toLowerCase();
            }
        },
        LX("Takes the file time as a long value in hex representation and the original file name extension") {
            @Override String resolve(final String n, final long m, final ZonedDateTime t, final Optional<String> x) {
                return Long.toHexString(m) + x.map(e -> "." + e).orElse("");
            }
        },
        OX("Takes the original file name (including extension)") {
            @Override String resolve(final String n, final long m, final ZonedDateTime t, final Optional<String> x) {
                return n;
            }
        };

        final String description;

        Element(final String description) {
            this.description = description;
        }

        abstract String resolve(
                final String n,
                final long m,
                final ZonedDateTime t,
                final Optional<String> x);
    }
}
