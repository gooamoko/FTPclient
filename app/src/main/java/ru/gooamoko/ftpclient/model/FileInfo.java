package ru.gooamoko.ftpclient.model;

import static ru.gooamoko.ftpclient.utils.ApplicationUtils.isEmpty;

import android.content.ContentResolver;
import android.net.Uri;

import java.io.InputStream;
import java.util.List;

/**
 * Класс, для инкапсуляции информации о файле.
 */
public class FileInfo {
    private String name;
    private InputStream data;

    public FileInfo(ContentResolver contentResolver, Uri uri) {
        try {
            if (uri != null) {
                List<String> pathSegments = uri.getPathSegments();
                if (!isEmpty(pathSegments)) {
                    String fileName = pathSegments.get(pathSegments.size() - 1);
                    String[] parts = fileName.split("/");
                    setName(parts[parts.length -1]);
                }
                if (contentResolver != null) {
                    setData(contentResolver.openInputStream(uri));
                }
            }
        } catch (Exception e) {
            name = null;
            data = null;
        }
    }

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
