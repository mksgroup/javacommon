/**
 * Licensed to Open-Ones Group under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Open-Ones Group licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package mksgroup.java.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author thachle
 */
public class FileUtil {
    private final static Logger LOG = LoggerFactory.getLogger(FileUtil.class);

    /**
     * [Give the description for method].
     * @param filePath
     * @return
     * @throws IOException 
     * @see https://code.google.com/p/juniversalchardet/
     */
    public static String getEncode(String filePath) throws IOException {
        byte[] buf = new byte[4096];
        FileInputStream fis = new FileInputStream(filePath);

        // (1)
        UniversalDetector detector = new UniversalDetector(null);

        // (2)
        int nread;
        while ((nread = fis.read(buf)) > 0 && !detector.isDone()) {
          detector.handleData(buf, 0, nread);
        }

        // (3)
        detector.dataEnd();

        // (4)
        String encoding = detector.getDetectedCharset();

        // (5)
        detector.reset();
        
        return encoding;
    }

    /**
     * Đọc nội dung của file resource với encoding cho trước
     * @param resourcePath đường dẫn file trong CLASSPATH
     * @param encoding
     * @return nội dung file. Nếu có lỗi thì trả lại null
     */
    public static String getContent(String resourcePath, String encoding) throws IOException {
        InputStream fis = null;
        InputStreamReader isReader = null;
        BufferedReader buffReader = null;
        char[] buff = new char[512];
        int len;
        StringBuffer sb = new StringBuffer();
        try {
            fis = CommonUtil.loadResource(resourcePath);
            isReader = new InputStreamReader(fis, encoding);
            buffReader = new BufferedReader(isReader);
            while ((len = buffReader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            if (buffReader != null) {
                buffReader.close();
            }
            if (isReader != null) {
                isReader.close();
            }
            if (fis != null) {
                fis.close();
            }
        }

        return sb.toString();

    }

    /**
     * Đọc nội dung của file resource với encoding cho trước
     * @param resourcePath đường dẫn file trong CLASSPATH
     * @param encoding
     * @return nội dung file. Nếu có lỗi thì trả lại null
     */
    public static String getContent(String resourcePath, boolean isResource, String encoding) {
        InputStream fis = null;
        InputStreamReader isReader = null;
        BufferedReader buffReader = null;
        char[] buff = new char[512];
        int len;
        StringBuffer sb = new StringBuffer();
        try {
            if (isResource) {
                fis = CommonUtil.class.getResourceAsStream(resourcePath);
            } else {
                fis = new FileInputStream(resourcePath);
            }
            if (CommonUtil.isNNandNB(encoding)) {
                isReader = new InputStreamReader(fis, encoding);
            } else {
                isReader = new InputStreamReader(fis);
            }

            buffReader = new BufferedReader(isReader);
            while ((len = buffReader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
        } catch (IOException ioe) {
            LOG.warn("CommonUtil.getContent(" + resourcePath + "," + encoding + ") throws", ioe);
            return null;
        } finally {
            close(buffReader);
            close(isReader);
            close(fis);
        }

        return sb.toString();

    }

    public static String getContent(File file, String encoding) throws IOException {
        InputStream fis = null;
        InputStreamReader isReader = null;
        BufferedReader buffReader = null;
        char[] buff = new char[512];
        int len;
        StringBuffer sb = new StringBuffer();

        try {
            fis = new FileInputStream(file);
            isReader = new InputStreamReader(fis, encoding);
            buffReader = new BufferedReader(isReader);
            while ((len = buffReader.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
        } catch (Exception ex) {
            LOG.error("Reading content of file", ex);
        } finally {
            close(buffReader);
            close(isReader);
            close(fis);
        }

        return sb.toString();
    }

    /**
     * Extract the inputstream of zip file.
     * @param fileIS stream of file.
     * @param outputFolder folder will contain the extracted file. It will be created if it not existed.
     * @return true if no error.
     */
    public static boolean unzip(InputStream fileIS, String outputFolder) {
        byte[] buffer = new byte[1024 * 5];
        ZipInputStream zis = null;
        try {
            zis = new ZipInputStream(fileIS);
            
            CommonUtil.mkdir(outputFolder);

            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();

            }

            return true;
        } catch (IOException ioEx) {
            LOG.error("Could not extract the zip file.", ioEx);
        } finally {
            if (zis != null) {
                try {
                    zis.closeEntry();
                } catch (IOException ex) {
                    LOG.warn("Could not close the Zip Entry.");
                }
                try {
                    zis.close();
                } catch (IOException ex) {
                    LOG.warn("Could not close the Zip InputStream.");
                }
            }
        }
        
        return false;
    }
    
    /**
     * Combine path1 and path2 into a URL path.
     * @param folder1.
     * @param folder2.
     * @return resultUrl.
     */
    public static String buildUrl(String path1, String...paths) {
        String resultPath = path1;
        
        for (String path: paths) {
            if (resultPath.endsWith("/") && path.startsWith("/")) {
                resultPath = resultPath + path.substring(1);
            } else if (!path1.endsWith("/") && !path.startsWith("/")) {
                resultPath = resultPath + "/" + path;
            } else {
                resultPath = resultPath + path;
            }
        }

        return resultPath;
    }
    
    public static String buildPath(String path1, String...paths) {
        String resultPath = path1;
        
        for (String path: paths) {
            if (resultPath.endsWith("/") && path.startsWith("/")) {
                resultPath = resultPath + path.substring(1);
            } else if (!path1.endsWith("/") && !path.startsWith("/")) {
                resultPath = resultPath + File.separator + path;
            } else {
                resultPath = resultPath + path;
            }
        }

        return resultPath;
    }

    public static void saveFile(String pathFile, String Content) {
        Writer output = null;
        File file = new File(pathFile);
        try {
            output = new BufferedWriter(new FileWriter(file));
            output.write(Content);
            output.close();
        } catch (IOException ex) {
            LOG.error("Could not save file '" + pathFile + "'", ex);
        }
    }

    public static void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException ex) {
                LOG.warn("Could not close the InputStream", ex);
            }
        }
    }

    public static void close(BufferedReader buffReader) {
        if (buffReader != null) {
            try {
                buffReader.close();
            } catch (IOException ex) {
                LOG.warn("Could not close the BufferedReader", ex);
            }
        }
    }

    public static void close(InputStreamReader isReader) {
        if (isReader != null) {
            try {
                isReader.close();
            } catch (IOException ex) {
                LOG.warn("Could not close the InputStreamReader", ex);
            }
        }
    }
}
