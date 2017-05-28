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
import static net.team33.order.Resolver.Element.L;
import static net.team33.order.Resolver.Element.M;
import static net.team33.order.Resolver.Element.Y;

public class ResolverTest {

    private Resolver resolver;
    private ZonedDateTime dateTime;
    private Path root;

    @Before
    public void setUp() throws Exception {
        root = Paths.get("target/test/ResolverTest");
        resolver = new Resolver(asList(Y, M, D, L));
        dateTime = ZonedDateTime.of(2016, 11, 19, 11, 37, 17, 1000000, ZoneId.systemDefault());
    }

    @Test
    public void applyWithExtension0() throws Exception {
        Assert.assertEquals(
                Paths.get("target/test/ResolverTest/2016/11/19/1587c296349.mpg"),
                root.resolve(resolver.apply(new FileName("test.mpg", FileTime.from(dateTime.toInstant()))).apply(0))
        );
    }

    @Test
    public void applyWithExtension278() throws Exception {
        Assert.assertEquals(
                Paths.get("target/test/ResolverTest/2016/11/19/1587c296349[278].mpg"),
                root.resolve(resolver.apply(new FileName("test.mpg", FileTime.from(dateTime.toInstant()))).apply(278))
        );
    }

    @Test
    public void applyWithoutExtension0() throws Exception {
        Assert.assertEquals(
                Paths.get("target/test/ResolverTest/2016/11/19/1587c296349"),
                root.resolve(resolver.apply(new FileName("test", FileTime.from(dateTime.toInstant()))).apply(0))
        );
    }

    @Test
    public void applyWithoutExtension278() throws Exception {
        Assert.assertEquals(
                Paths.get("target/test/ResolverTest/2016/11/19/1587c296349[278]"),
                root.resolve(resolver.apply(new FileName("test", FileTime.from(dateTime.toInstant()))).apply(278))
        );
    }
}