package com.test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class MybatisTest {


    public static void main(String[] args) {
        try {
            ClassPool pool = ClassPool.getDefault();
            // 此处改为你实际的的 MyBatisCodeHelper-Pro-obfuss.jar 的路径
            pool.insertClassPath("C:\\Users\\HP\\AppData\\Roaming\\JetBrains\\IntelliJIdea2024.3\\plugins\\MyBatisCodeHelper-Pro\\lib\\MyBatisCodeHelper-Pro-obfuss.jar");
            // 通过绝对定位，加载指定的类
            CtClass cc = pool.get("com.ccnode.codegenerator.Y.e.e");

            // 获取validTo的get方法
            // public Long e()
            CtMethod getValidToMethod = cc.getDeclaredMethod("a");

            // 获取validTo的set方法的参数：Long
            CtClass[] params = new CtClass[]{pool.get("java.lang.Long")};

            // 获取validTo的set方法
            // public void a(Long l2)
            CtMethod setValidToMethod = cc.getDeclaredMethod("a", params);

            // 获取valid的set方法的参数：Boolean
            CtClass[] params1 = new CtClass[]{pool.get("java.lang.Boolean")};
            // 获取Valid的set方法
            // public void a(Boolean bl)
            CtMethod setValidMethod = cc.getDeclaredMethod("a", params1);

            // 获取valid的get方法
            // public Boolean b()
            CtMethod getValidMethod = cc.getDeclaredMethod("e");

            // 修改validTo的get方法
            // 直接返回4797976044000
            StringBuilder builder = new StringBuilder();
            builder.append("{")
                    .append("       return new Long(4797976044000L);")
                    .append("}");
            getValidToMethod.setBody(builder.toString());

            // 修改validTo的set方法
            // 直接设为4797976044000
            StringBuilder builder1 = new StringBuilder();
            builder1.append("{")
                    .append("        this.a = new Long(4797976044000L);")
                    .append("}");
            setValidToMethod.setBody(builder1.toString());

            // 修改valid的set方法
            // 设为True
            String getValidMethodBuilder = "{" +
                    "       return Boolean.TRUE;" +
                    "}";
            getValidMethod.setBody(getValidMethodBuilder);

            // 修改valid的get方法
            // 直接返回True
            String setValidMethodBuilder = "{" +
                    "this.c = Boolean.TRUE;" +
                    "}";
            setValidMethod.setBody(setValidMethodBuilder);

            // 将修改后的Class b写入指定文件夹
            cc.writeFile("C:\\baidunetdiskdownload");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
