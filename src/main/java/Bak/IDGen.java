package Bak;

public class IDGen {
    public static void main(String[] args) {
        SnowflakeIdWorker snowflakeIdWorker = SnowflakeIdWorker.INSTANCE;
        for (int i = 0; i < 10; i++) {
            Long insId = snowflakeIdWorker.nextId();
            System.out.println(insId);
        }
    }
}