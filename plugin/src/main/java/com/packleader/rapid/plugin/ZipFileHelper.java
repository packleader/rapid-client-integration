package com.packleader.rapid.plugin;

import org.codehaus.plexus.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component(role = ZipFileHelper.class)
public class ZipFileHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipFileHelper.class);

    public void unpack(File source, File destinationFile, Predicate<? super ZipEntry> filter) throws IOException {
        ZipFile zipFile = new ZipFile(source);
        zipFile.stream()
                .filter(filter)
                .sorted(Comparator.comparingInt(e -> e.getName().length()))
                .forEachOrdered(e -> unpackEntry(zipFile, e, destinationFile));
        zipFile.close();
    }

    private void unpackEntry(ZipFile zipFile, ZipEntry zipEntry, File destinationFile) {
        Optional<File> destination = newFile(destinationFile, zipEntry);
        destination.ifPresent(f -> unpackFile(zipFile, zipEntry, f));
    }

    private Optional<File> newFile(File destinationDir, ZipEntry zipEntry) {
        String zipFile = zipEntry.getName();
        File destFile = new File(destinationDir, zipFile);

        try {
            String destDirPath = destinationDir.getCanonicalPath();
            String destFilePath = destFile.getCanonicalPath();

            if (!destFilePath.startsWith(destDirPath + File.separator)) {
                throw new IOException("Entry is outside of the target dir");
            }
        } catch (IOException ex) {
            LOGGER.warn("Skipping {}. Reason: {}", zipEntry, ex.getMessage());
            destFile = null;
        }

        return Optional.ofNullable(destFile);
    }

    private void unpackFile(ZipFile zipFile, ZipEntry zipEntry, File destinationFile) {
        if (zipEntry.isDirectory()) {
            destinationFile.mkdirs();
        }
        else {
            try(InputStream is = zipFile.getInputStream(zipEntry)) {
                Files.copy(is, destinationFile.toPath());
            } catch (IOException ex) {
                LOGGER.warn("Failed to extract {}. Reason: {}", zipEntry, ex.getMessage());
            }
        }
    }

}
