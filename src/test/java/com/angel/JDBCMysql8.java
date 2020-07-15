package com.angel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @description: JDBC事务
 * @create: 2020-07-08 15:00
 **/
public class JDBCMysql8 {

    private final static Logger logger = LoggerFactory.getLogger(JDBCMysql8.class);

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        //新驱动
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/mysql?&useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "123456";
        String sql = "select name from gdy_user";
        try {
            //注册JDBC驱动程序
            Class.forName(driver);
            //建立连接
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed()) {
                System.out.println("数据库连接成功");
            }
            con.setAutoCommit(false);
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                logger.info("我的姓名是{}", name);
            }
            con.commit();
        } catch (SQLException | ClassNotFoundException e) {
            con.rollback();
        } finally {
            con.setAutoCommit(true);
            con.close();
        }
    }
}
