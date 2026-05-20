package com.casino.storage.api;

import java.nio.file.Path;
import java.util.Optional;

public interface StoragePort {

    Optional<Path> resolvePublicPath(String storageKey);

    String store(byte[] bytes, String suggestedName);
}
