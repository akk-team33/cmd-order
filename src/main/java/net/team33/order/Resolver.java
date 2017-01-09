package net.team33.order;

import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

class Resolver {

    private final Path root;
    private final List<PathElement> elements;

    Resolver(final Path root, final List<PathElement> elements) {
        this.root = root;
        this.elements = unmodifiableList(new ArrayList<>(elements));
    }

    public Path resolve(final FileTime fileTime, final String extension) {
        final ZonedDateTime zoned = fileTime.toInstant().atZone(ZoneId.systemDefault());
        Path newParent = root;
        for (final PathElement element : elements)
            newParent = newParent.resolve(element.resolve(zoned, extension));
        return newParent;
    }

    public enum PathElement {
        YEAR("Y", (zdt, ext) -> String.format("%04d", zdt.getYear())),
        MONTH("M", (zdt, ext) -> String.format("%02d", zdt.getMonthValue())),
        DAY("D", (zdt, ext) -> String.format("%02d", zdt.getDayOfMonth())),
        EXTENSION("X", (zdt, ext) -> ext);

        private final String symbol;
        private final Extractor extractor;

        PathElement(final String symbol, final Extractor extractor) {
            this.symbol = symbol;
            this.extractor = extractor;
        }

        public static PathElement from(final String element) {
            return Stream.of(values())
                    .filter(entry -> entry.symbol.equals(element))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Unknown: '" + element + "'"));
        }

        private String resolve(final ZonedDateTime zoned, final String extension) {
            return extractor.extract(zoned, extension);
        }

        @FunctionalInterface
        private interface Extractor {
            String extract(ZonedDateTime zoned, String extension);
        }
    }
}
