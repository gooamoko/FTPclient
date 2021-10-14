package ru.gooamoko.ftpclient.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.ContentResolver;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;

public class FileInfoTest {
    private ContentResolver fakeContentResolver;

    @Before
    public void prepare() throws Exception{
        fakeContentResolver = mock(ContentResolver.class);
        when(fakeContentResolver.openInputStream(any(Uri.class))).thenReturn(new ByteArrayInputStream(new byte[] {}));
    }

    @Test
    public void testFileInfoForNullUriAndResolver() {
        FileInfo fileInfo = new FileInfo(null, null);
        assertFalse(fileInfo.exists());
    }

    @Test
    public void testFileInfoForNullUri() {
        FileInfo fileInfo = new FileInfo(fakeContentResolver, null);
        assertFalse(fileInfo.exists());
    }

    @Test
    public void testFileInfoForContentUri(){
        Uri testUri = mock(Uri.class);
        String protocol = "content://";
        String filePath = "test/file/path/file.mov";
        when(testUri.getPath()).thenReturn(protocol + filePath);
        when(testUri.getPathSegments()).thenReturn(List.of(protocol, filePath));
        FileInfo fileInfo = new FileInfo(fakeContentResolver, testUri);
        assertTrue(fileInfo.exists());
        assertEquals("file.mov", fileInfo.getName());
    }
}