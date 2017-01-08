package net.team33.order;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ResolverTest {

    private Resolver resolver;
    private ZonedDateTime dateTime;

    @Before
    public void setUp() throws Exception {
        resolver = new Resolver(Paths.get("target/test/ResolverTest"));
        dateTime = ZonedDateTime.of(2016, 11, 19, 11, 37, 17, 1000000, ZoneId.systemDefault());
    }

    @Test
    public void apply() throws Exception {
        Assert.assertEquals(
                Paths.get("target/test/ResolverTest/2016/11/19/"),
                resolver.apply(FileTime.from(dateTime.toInstant()))
        );
    }
}