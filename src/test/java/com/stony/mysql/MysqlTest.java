package com.stony.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * @author stony
 * @version 下午6:11
 * @since 2018/10/22
 */
public class MysqlTest {

    static String url = "jdbc:mysql://10.0.11.172:3311/test?user=slave&password=slave&useUnicode=true&characterEncoding=UTF8";
    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    public static Connection getCurrentConnecton() throws SQLException {
        Connection conn = threadLocal.get();
        if(conn == null || conn.isClosed()){
            conn = getConnection();
            threadLocal.set(conn);
        }
        return conn;
    }
    static synchronized Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }
    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        ExecutorService executorService = java.util.concurrent.Executors.newFixedThreadPool(100);

        int all = 0;
        List<Future<Integer>> list = new ArrayList<>(128);
        for (int i = 0; i < 100; i++) {

            Future<Integer> f = executorService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int update = 0;
                    int v = 0;
                    Connection conn = getCurrentConnecton();
                    String sql = "INSERT INTO `t1` (`name`, `add_time`, `update_time`) VALUES ('v4', now(), now()),('v2', now(), now()),('v3', now(), now())";

                    conn.setAutoCommit(false);

                    Statement statement = null;
                    for (int i = 0; i < 100; i++) {
                        statement = conn.createStatement();
                        update = statement.executeUpdate(sql);
                        v += update;
                        System.out.println(System.currentTimeMillis() + " | INSERT: " + update);
                        statement.close();
                        conn.commit();
                    }
                    System.out.println(System.currentTimeMillis() + " | Thread INSERT ALL: " + v);
                    conn.close();
                    return v;
                }
            });
            list.add(f);
        }
        for (Future<Integer> f : list){
            all += f.get();
        }
        System.out.println("INSERT ALL: " + all);


        executorService.shutdown();
    }
}