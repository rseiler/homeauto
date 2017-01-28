package at.rseiler.homeauto.common;

import at.rseiler.homeauto.common.fortest.FileUtil;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class YmlUtilTest {
    @Test
    public void read() throws Exception {
        File yml = FileUtil.get("common", "src/test/resources/test.yml");
        TestYml testYml = YmlUtil.read(yml, TestYml.class);

        assertThat(testYml.getHello(), is("world"));
    }

    @Value
    @Builder
    @JsonDeserialize(builder = TestYml.TestYmlBuilder.class)
    static class TestYml {
        private final String hello;

        @JsonPOJOBuilder(withPrefix = "")
        static final class TestYmlBuilder {
        }
    }
}