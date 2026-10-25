package fileio;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BinaryStorage<T> implements Storage<T> {

    @Override
    public void save(String fileName, T data) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(fileName)))) {
            output.writeObject(data);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T load(String fileName) throws IOException {
        if (!new File(fileName).exists()) {
            return null;
        }
        try (ObjectInputStream input = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(fileName)))) {
            return (T) input.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("Unexpected data format in " + fileName, e);
        }
    }
}
