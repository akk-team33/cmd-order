package net.team33.order;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

public class Resolver implements Function<FileName, Function<Integer, String>> {

    private List<Element> elements;

    Resolver(final List<Element> elements) {
        this.elements = elements;
    }

    @Override
    public Function<Integer, String> apply(final FileName fileName) {
        final String result = elements.stream()
                .map(e -> e.resolve(fileName))
                .reduce(Paths.get(""), Path::resolve, Path::resolve)
                .toString();
        return index -> result
                + ((0 < index) ? "[" + index + "]" : "")
                + fileName.suffix.map(x -> "." + x).orElse("");
    }

    enum Element {
        Y("Takes the year value from the file time, eg. \"2017\"") {
            @Override String resolve(final FileName fileName) {
                return String.format("%04d", fileName.zoned.getYear());
            }
        },
        M("Takes the month value from the file time, eg. \"05\" (for May)") {
            @Override String resolve(final FileName fileName) {
                return String.format("%02d", fileName.zoned.getMonthValue());
            }
        },
        D("Takes the day of month value from the file time, eg. \"27\" (for the 27th day in May)") {
            @Override String resolve(final FileName fileName) {
                return String.format("%02d", fileName.zoned.getDayOfMonth());
            }
        },
        X("Takes the original file name extension, eg. \"jpg\"") {
            @Override String resolve(final FileName fileName) {
                return fileName.suffix.orElse("").toLowerCase();
            }
        },
        L("Takes the file time as a long value in hex representation") {
            @Override String resolve(final FileName fileName) {
                return Long.toHexString(fileName.millis);
            }
        },
        N("Takes the original file name") {
            @Override String resolve(final FileName fileName) {
                return fileName.prefix;
            }
        };

        final String description;

        Element(final String description) {
            this.description = description;
        }

        abstract String resolve(final FileName fileName);
    }
}
