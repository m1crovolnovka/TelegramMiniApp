package com.casino.storage.internal.local;

import com.casino.storage.api.StoragePort;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LocalFileStorageService implements StoragePort {

    private final Path baseDir;

    public LocalFileStorageService(@Value("${casino.storage.local-dir:data/storage}") String localDir)
            throws IOException {
        this.baseDir = Path.of(localDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.baseDir);
        } catch (IOException e) {
            throw new IOException("Cannot create storage directory: " + this.baseDir, e);
        }
    }

    @Override
    public Optional<Path> resolvePublicPath(String storageKey) {
        if (storageKey == null || storageKey.isBlank()) {
            return Optional.empty();
        }
        Path p = baseDir.resolve(storageKey).normalize();
        if (!p.startsWith(baseDir)) {
            return Optional.empty();
        }
        return Optional.of(p);
    }

    @Override
    public String store(byte[] bytes, String suggestedName) {
        String name = UUID.randomUUID() + "-" + suggestedName.replaceAll("[^a-zA-Z0-9._-]", "_");
        Path target = baseDir.resolve(name);
        try {
            Files.write(target, bytes);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot store file", e);
        }
        return name;
    }
}
