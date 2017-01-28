package at.rseiler.homeauto.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

/**
 * Very basic YAL util class.
 */
public final class YmlUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(new YAMLFactory());

    public static <T> T read(File source, Class<T> valueType) throws IOException {
        return OBJECT_MAPPER.readValue(source, valueType);
    }

    private YmlUtil() {
    }
}
