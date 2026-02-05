package com.test;

import java.io.File;

public class DeleteDirectory {

    public static void main(String[] args) {
        String directoryPath = "C:\\A-Work\\imageTest";

        System.out.println("尝试删除目录: " + directoryPath);

        boolean deleted = deleteDirectoryWithFileAPI(directoryPath);

        if (deleted) {
            System.out.println("目录及其所有内容已成功删除");
        } else {
            System.out.println("目录删除失败或目录不存在");
        }
    }

    /**
     * 使用传统File API删除目录
     *
     * @param directoryPath 目录路径
     * @return 是否删除成功
     */
    public static boolean deleteDirectoryWithFileAPI(String directoryPath) {
        File directory = new File(directoryPath);

        // 检查目录是否存在
        if (!directory.exists()) {
            System.out.println("目录不存在: " + directoryPath);
            return false;
        }

        // 递归删除目录及其所有内容
        return deleteDirectory(directory);
    }

    private static boolean deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    if (file.delete()) {
                        System.out.println("已删除文件: " + file.getAbsolutePath());
                    } else {
                        System.out.println("删除文件失败: " + file.getAbsolutePath());
                    }
                }
            }
        }

        boolean deleted = directory.delete();
        if (deleted) {
            System.out.println("已删除目录: " + directory.getAbsolutePath());
        } else {
            System.out.println("删除目录失败: " + directory.getAbsolutePath());
        }

        return deleted;
    }
}