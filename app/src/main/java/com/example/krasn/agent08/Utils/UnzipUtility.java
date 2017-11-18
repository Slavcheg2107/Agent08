package com.example.krasn.agent08.Utils;

import com.facebook.stetho.common.Utf8Charset;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnzipUtility {
    private static final int BUFFER_SIZE = 4096;

    public void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (entry.isDirectory()) {
                new File(filePath).mkdir();
            } else {
                extractFile(zipIn, filePath);
            }
            zipIn.closeEntry();
        }
        zipIn.close();
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[4096];
        while (true) {
            int read = zipIn.read(bytesIn);
            if (read != -1) {
                bos.write(bytesIn, 0, read);
            } else {
                bos.close();
                return;
            }
        }
    }
}
