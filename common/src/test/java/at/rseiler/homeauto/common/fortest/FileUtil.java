package at.rseiler.homeauto.common.fortest;

import java.io.File;

/**
 * Very basic file util class.
 */
public final class FileUtil {

    /**
     * Checks if the file specified by the {@code path} exists. If not then the {@code module} will prepended.
     *
     * @param module the module name
     * @param path   the file path
     * @return the file
     */
    public static File get(String module, String path) {
        File file = new File(path);

        if (!file.exists()) {
            file = new File(module + File.separator + path);
        }

        return file;
    }

    private FileUtil() {
    }
}
