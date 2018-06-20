package net.team33.order;

import java.io.PrintStream;
import java.util.function.Function;
import java.util.stream.Stream;

class Output {

    private PrintStream out = System.out;

    Output println(final Object... args) {
        print(args);
        out.println();
        return this;
    }

    Output print(final Object... args) {
        Stream.of(args).forEach(out::print);
        return this;
    }

    <T> Output printEach(final Stream<T> values, final Function<T, Object[]> function) {
        values.forEach(value -> println(function.apply(value)));
        return this;
    }

    Output printStackTrace(final Throwable value) {
        value.printStackTrace(out);
        return this;
    }
}
