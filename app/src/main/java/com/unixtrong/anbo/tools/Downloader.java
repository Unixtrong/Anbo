package com.unixtrong.anbo.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by danyun on 2017/8/8
 */

public class Downloader {
    public static File start(String url, String path) {
        InputStream in = null;
        BufferedInputStream buffIn = null;
        FileOutputStream out = null;
        try {
            in = HttpUtils.doGetRequest(url, null);
            if (in != null) {
                File file = new File(path);
                buffIn = new BufferedInputStream(in);
                out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = buffIn.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                return file;
            }
        } catch (IOException e) {
            Lg.warn(e);
        } finally {
            CodeUtils.closeStream(out);
            CodeUtils.closeStream(buffIn);
            CodeUtils.closeStream(in);
        }
        return null;
    }
}
