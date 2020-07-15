package com.angel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * @description: JDBC����
 * @create: 2020-07-08 15:00
 **/
public class JDBCMysql8 {

    private final static Logger logger = LoggerFactory.getLogger(JDBCMysql8.class);

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        //������
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/mysql?&useSSL=false&serverTimezone=UTC";
        String user = "root";
        String password = "123456";
        String sql = "select name from gdy_user";
        try {
            //ע��JDBC��������
            Class.forName(driver);
            //��������
            con = DriverManager.getConnection(url, user, password);
            if (!con.isClosed()) {
                System.out.println("���ݿ����ӳɹ�");
            }
            con.setAutoCommit(false);
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                logger.info("�ҵ�������{}", name);
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
