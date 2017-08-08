package com.unixtrong.anbo.tools;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by danyun on 2017/8/5
 */

public class CodeUtils {
    public static String readText(InputStream is) throws IOException {
        InputStreamReader inputReader = null;
        BufferedReader bufferedReader = null;
        String builder = null;
        try {
            inputReader = new InputStreamReader(is);
            bufferedReader = new BufferedReader(inputReader);
            builder = "";
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(bufferedReader);
            closeStream(inputReader);
        }
        return builder;
    }

    public static void closeStream(Closeable closeable) {
        if (closeable != null) try {
            closeable.close();
        } catch (Exception e) {
            Lg.warn(e);
        }
    }
}
