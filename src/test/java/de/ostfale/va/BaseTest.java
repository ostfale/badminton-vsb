package de.ostfale.va;

import java.io.InputStream;

public abstract class BaseTest {

    /**
     * Reads a file from test resources and returns it as an InputStream.
     *
     * @param fileName the name of the file in test resources
     * @return InputStream of the requested file
     * @throws IllegalArgumentException if the file cannot be found
     */
    protected InputStream getInputStreamFromResources(String fileName) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found in test resources: " + fileName);
        }
        return inputStream;
    }
}
