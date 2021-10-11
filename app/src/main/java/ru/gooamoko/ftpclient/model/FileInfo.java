package ru.gooamoko.ftpclient.model;

import java.io.InputStream;

/**
 * Класс, для инкапсуляции информации о файле.
 */
public class FileInfo {
    private String name;
    private InputStream data;


    public boolean exists() {
        return data != null && name != null && !name.trim().isEmpty();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }
}
