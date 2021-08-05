package com.chenfan.finance.utils;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;


import java.io.*;

/**
 * @Author Wen.Xiao
 * @Description // 压缩文件处理
 * @Date 2021/5/20  14:47
 * @Version 1.0
 */
public class ZipUtils {


    public static File[]  unzip(File zipFile, String descDir) {
        try (ZipArchiveInputStream inputStream = getZipFile(zipFile)) {
            File pathFile = new File(descDir);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }
            ZipArchiveEntry entry = null;
            while ((entry = inputStream.getNextZipEntry()) != null) {
                String name = entry.getName();
                if (entry.isDirectory()) {
                    File directory = new File(descDir, name);
                    directory.mkdirs();
                } else {
                    OutputStream os = null;
                    try {
                        os = new BufferedOutputStream(new FileOutputStream(new File(descDir, name)));
                        IOUtils.copy(inputStream, os);
                    } finally {
                        IOUtils.closeQuietly(os);
                    }
                }
            }
            final File[] files = pathFile.listFiles();
            if (files != null && files.length == 1 && files[0].isDirectory()) {
                // 说明只有一个文件夹
                FileUtils.copyDirectory(files[0], pathFile);
                //免得删除错误， 删除的文件必须在/data/demand/目录下。
                boolean isValid = files[0].getPath().contains("/data/www/");
                if (isValid) {
                    FileUtils.forceDelete(files[0]);
                }
            }
            return pathFile.listFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ZipArchiveInputStream getZipFile(File zipFile) throws Exception {
        return new ZipArchiveInputStream(new BufferedInputStream(new FileInputStream(zipFile)),"GBK");
    }
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        try {
            if (dir.isDirectory()) {
                String[] children = dir.list();
                //递归删除目录中的子目录下
                for (int i=0; i<children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
            // 目录此时为空，可以删除
            return dir.delete();
        }catch (Exception e){
            e.printStackTrace();
        }
        return false ;

    }
}
