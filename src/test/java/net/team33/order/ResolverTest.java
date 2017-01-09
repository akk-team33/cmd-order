package net.team33.order;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static java.util.Arrays.asList;
import static net.team33.order.Resolver.PathElement.DAY;
import static net.team33.order.Resolver.PathElement.EXTENSION;
import static net.team33.order.Resolver.PathElement.MONTH;
import static net.team33.order.Resolver.PathElement.YEAR;

public class ResolverTest {

    private Resolver resolver;
    private ZonedDateTime dateTime;

    @Before
    public void setUp() throws Exception {
        resolver = new Resolver(Paths.get("target/test/ResolverTest"), asList(YEAR, MONTH, DAY, EXTENSION));
        dateTime = ZonedDateTime.of(2016, 11, 19, 11, 37, 17, 1000000, ZoneId.systemDefault());
    }

    @Test
    public void apply() throws Exception {
        Assert.assertEquals(
                Paths.get("target/test/ResolverTest/2016/11/19/test/"),
                resolver.resolve(FileTime.from(dateTime.toInstant()), "test")
        );
    }
}