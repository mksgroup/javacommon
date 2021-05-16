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

import static mksgroup.java.common.CommonUtil.isNNandNB;

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

import org.apache.log4j.Logger;
import org.mozilla.universalchardet.UniversalDetector;
/**
 * @author thachle
 */
public class FileUtil {
    private final static Logger LOG = Logger.getLogger(FileUtil.class);

    /**
     * [Give the description for method].
     * @param filePath
     * @return
     * @throws IOException 
     * @see https://code.google.com/p/juniversalchardet/
     */
    public static String getEncode(String filePath) throws IOException {
        byte[] buf = new byte[4096];
        InputStream fis = new FileInputStream(filePath);

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
        } catch (Exception ex) {
            LOG.error("Error in reading content of resourse: " + resourcePath, ex);
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

    /**
     * Create directory including parent folders.
     * @param path
     * @return Null if the path is existed.
     * True if created folder successfully.
     */
    public static Boolean mkdir(String path) {
        File filePath = new File(path);
        
        if (filePath.isDirectory() && filePath.exists()) {
            return null;
        } else {
            return filePath.mkdirs();
        }
    }

    /**
     * Get extension of file.
     * @param fileName
     * @return extension part without dot character
     */
    public static String getExtension(String fileName) {
        if (!isNNandNB(fileName)) {
            return fileName;
        }

        int idxOfDot = fileName.lastIndexOf(Constant.CHAR_DOT);
        if (idxOfDot == -1) {
            return null;
        } else {
            return fileName.substring(idxOfDot + 1);
        }
    }

    /**
     * Get file name from path. Ex: Input C:/folder/file.txt => Filename: file.txt Input C:\folder\file.txt => Filename:
     * file.txt Input /folder/file.txt => Filename: file.txt
     * @param filePath
     * @return file name includes extension
     */
    public static String getFilename(String filePath) {
        if ((filePath == null) || (filePath.isEmpty())) {
            return filePath;
        }

        int idx = filePath.lastIndexOf(Constant.STR_RIGHTSLASH);

        if (idx > -1) { // Has separator /
            return filePath.substring(idx + 1);
        } else {
            idx = filePath.lastIndexOf(Constant.STR_BACKSLASH);

            if (idx > -1) { // Has separator \
                return filePath.substring(idx + 1);
            } else {
                return filePath;
            }
        }
    }
    
    /**
     * Get folder path from the full file path.
     * @param filePath full file path. Ex: C:/A/B/C/abc.txt
     * @return Folder path without the separator at the end. Ex: C:/A/B/C
     */
    public static String getFolder(String filePath) {
        if ((filePath == null) || (filePath.isEmpty())) {
            return filePath;
        }

        int idx = filePath.lastIndexOf("/");

        if (idx > -1) { // Has separator /
            return filePath.substring(0, idx);
        } else {
            idx = filePath.lastIndexOf("\\");

            if (idx > -1) { // Has separator \
                return filePath.substring(0, idx);
            } else {
                return filePath;
            }
        }
    }

    /**
     * Delete folder.
     * @param dir to be deleted folder.
     * @param isDeleteOwn true to delete the
     * @return
     */
    public static boolean deleteDir(File dir, boolean isDeleteOwn) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]), true);
                if (!success) {
                    return false;
                }
            }
        }
        // The directory is now empty so delete it
        if (isDeleteOwn) {
            return dir.delete();
        }
        return true;
    }

    public static void saveFile(String pathFile, String Content) throws IOException {
        Writer output = null;
        File file = new File(pathFile);

        output = new BufferedWriter(new FileWriter(file));
        output.write(Content);
        output.close();
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
