package xyz.sadiulhakim.npr.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
    private static final String BASE_PATH = "F:/NPR/picture/";

    private FileUtil() {
    }

    public static String uploadFile(String folderName, String fileName, InputStream is) {
        try {

            fileName = UUID.randomUUID() + "_" + fileName;
            File file = new File(BASE_PATH, (folderName + fileName));
            Files.copy(is, file.toPath());
            return fileName;
        } catch (Exception ex) {
            LOGGER.error("FileUtil.upload :: failed to upload file {}", (folderName + fileName));
            return "";
        }
    }

    public static boolean deleteFile(String folderName, String fileName) {
        try {

            File file = new File(BASE_PATH, (folderName + fileName));
            if (file.exists()) {
                return file.delete();
            }
        } catch (Exception ex) {
            LOGGER.error("FileUtil.upload :: failed to delete file {}", (folderName + fileName));
            return false;
        }

        return false;
    }
}
