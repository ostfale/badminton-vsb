package de.ostfale.va;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public abstract class BaseTest {

    protected InputStream getInputStreamFromResources(String fileName) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in test resources: " + fileName);
        }
        return inputStream;
    }

    protected Path readFile(String fileName) throws URISyntaxException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = Objects.requireNonNull(classLoader.getResource(fileName), "file not found! " + fileName);
        return new File(resource.toURI()).toPath();
    }
}
