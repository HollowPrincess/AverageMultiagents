package ru.spbu.mas;

import java.io.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;

public class Utils {

    public static String serializeToString(Serializable o) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {

            oos.writeObject(o);
            return Base64.getEncoder().encodeToString(baos.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object deserializeFromString(String s) {
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(Base64.getDecoder().decode(s)))) {

            return ois.readObject();

        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String mapToString(HashMap<String, Integer> map) {
        return Arrays.toString(map.entrySet().toArray());
    }
}