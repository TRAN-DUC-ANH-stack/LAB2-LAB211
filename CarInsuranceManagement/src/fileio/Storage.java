package fileio;

import java.io.IOException;

public interface Storage<T> {

    void save(String fileName, T data) throws IOException;

    T load(String fileName) throws IOException;
}
