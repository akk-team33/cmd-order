package net.team33.order;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static net.team33.order.Resolver.Element.D;
import static net.team33.order.Resolver.Element.LX;
import static net.team33.order.Resolver.Element.M;
import static net.team33.order.Resolver.Element.Y;

public class ResolverTest {

    private Resolver resolver;
    private ZonedDateTime dateTime;
    private Path root;

    @Before
    public void setUp() throws Exception {
        root = Paths.get("target/test/ResolverTest");
        resolver = new Resolver(asList(Y, M, D, LX));
        dateTime = ZonedDateTime.of(2016, 11, 19, 11, 37, 17, 1000000, ZoneId.systemDefault());
    }

    @Test
    public void apply() throws Exception {
        Assert.assertEquals(
                Paths.get("target/test/ResolverTest/2016/11/19/1587c296349"),
                root.resolve(resolver.apply("test", FileTime.from(dateTime.toInstant())))
        );
        Assert.assertEquals(
                Paths.get("target/test/ResolverTest/2016/11/19/1587c296349.mpg"),
                root.resolve(resolver.apply("test.mpg", FileTime.from(dateTime.toInstant())))
        );
    }
}