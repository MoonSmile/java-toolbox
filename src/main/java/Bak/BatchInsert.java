package Bak;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BatchInsert {
    public static void main(String[] args) throws ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        batch(500);
//        Stream.iterate(1L, n -> n + 1).limit(200).forEach(e-> {
//            System.out.println(e);
//        });
    }
    private static <T>  void test1(Class<T> clazz) throws Exception{

        T o1 = clazz.newInstance();
        ArrayList<T> obj = new ArrayList<T>() {{
            add( o1);

        }};
    }
    private static void batch (Integer size){
        List<Long> elList = new ArrayList( Arrays.asList(76358998972958832L, 76358998981347440L, 76358998993930352L,
                76358999006513264L, 76358999014901872L));
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo?characterEncoding=utf8&useSSL=true", "demo","demo")){
            connection.setAutoCommit(false);
            PreparedStatement p1 = connection.prepareStatement(
                    "INSERT INTO `t_form_instance` (`id_`, `form_version_id_`, `title_`, `status_`, `create_user_`, `gmt_create_`, `update_user_`, `gmt_update_`, `org_id_`, `delete_`) VALUES (?, '76358999220422768', '李钰彬李钰彬李钰彬', '0', 'liyubin@chamc.com.cn@27645', '2018-12-12 19:25:26', 'liyubin@chamc.com.cn@27645', '2018-12-12 19:25:26', 'ORG100002274', 0)");
            PreparedStatement p2 = connection.prepareStatement(
                    "INSERT INTO `t_data_76352912052193392` (`id_`, `form_element_id_`, `form_instance_id_`, `value_`) VALUES (?, ?, ?, ?)");
            SnowflakeIdWorker snowflakeIdWorker = SnowflakeIdWorker.INSTANCE;
            for (int i = 0; i < size; i++) {
                Long insId = snowflakeIdWorker.nextId();
                p1.setLong(1, insId);
                p1.addBatch();
                for (int j = 0; j < elList.size(); j++) {
                    p2.setLong(1, snowflakeIdWorker.nextId());
                    p2.setLong(2, elList.get(j));
                    p2.setLong(3, insId);
                    String value = "";
                    if (j != 3 && j != 7 && j != 9 && j != 10) {
                        value = "n" + j;
                    }
                    if (j == 10) {
                        value = "[]";
                    }
                    p2.setString(4, value);
                    p2.addBatch();
                }
            }
            p1.executeBatch();
            p2.executeBatch();
            connection.commit();
            p1.close();
            p2.close();
            connection.close();
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }

    }
}
