package Bak;

public enum SnowflakeIdWorker {
    INSTANCE;

    private final long twepoch = 1525104000000L;
    private final long workerIdBits = 16L;
    private final long datacenterIdBits = 2L;
    private final long maxWorkerId = 65535L;
    private final long maxDatacenterId = 3L;
    private final long sequenceBits = 4L;
    private final long workerIdShift = 4L;
    private final long datacenterIdShift = 20L;
    private final long timestampLeftShift = 22L;
    private final long sequenceMask = 15L;
    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private final long clockBackOffset = 1000L;

    private SnowflakeIdWorker() {
        if (this.workerId <= 65535L && this.workerId >= 0L) {
            if (this.datacenterId <= 3L && this.datacenterId >= 0L) {
                try {
                    this.initWorkerAndDatacenterId();
                } catch (Exception var4) {
                    this.workerId = 0L;
                    this.datacenterId = 0L;
                }

            } else {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", 3L));
            }
        } else {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", 65535L));
        }
    }

    private void initWorkerAndDatacenterId() {
        Server.Environment env = ServerUtils.getServerEnviroment();
        String ip = ServerUtils.getServerIp();
        String[] ips = ip.split("\\.");
        String workerIdBinary = Integer.toBinaryString(Integer.valueOf(ips[2])) + Integer.toBinaryString(Integer.valueOf(ips[3]));
        this.workerId = Long.valueOf(workerIdBinary, 2);
        switch(env) {
            case PROD:
                this.datacenterId = 0L;
                break;
            case BAK:
                this.datacenterId = 1L;
                break;
            case TEST:
                this.datacenterId = 2L;
                break;
            default:
                this.datacenterId = 3L;
        }

    }

    public synchronized long nextId() {
        long timestamp = this.currentTime();
        if (timestamp < this.lastTimestamp) {
            long offset = this.lastTimestamp - timestamp + 1L;
            if (offset >= 1000L) {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }

            this.waitForOffset(offset);
        }

        if (this.lastTimestamp == timestamp) {
            this.sequence = this.sequence + 1L & 15L;
            if (this.sequence == 0L) {
                timestamp = this.tilNextMillis(this.lastTimestamp);
            }
        } else {
            this.sequence = 0L;
        }

        this.lastTimestamp = timestamp;
        return timestamp - 1525104000000L << 22 | this.datacenterId << 20 | this.workerId << 4 | this.sequence;
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for(timestamp = this.currentTime(); timestamp <= lastTimestamp; timestamp = this.currentTime()) {
        }

        return timestamp;
    }

    protected long currentTime() {
        return System.currentTimeMillis();
    }

    private void waitForOffset(long offset) {
        try {
            Thread.sleep(offset);
        } catch (InterruptedException var4) {
            throw new RuntimeException("snowflake wait error: " + var4.getLocalizedMessage());
        }
    }

    protected long[] decode(long id) {
        long[] ret = new long[4];
        SnowflakeIdWorker snow = INSTANCE;
        String binary = Long.toBinaryString(id);
        int length = binary.length();
        long var10000 = (long)length;
        snow.getClass();
        int sequenceIndex = (int)(var10000 - 4L);
        var10000 = (long)sequenceIndex;
        snow.getClass();
        int workerIndex = (int)(var10000 - 16L);
        var10000 = (long)workerIndex;
        snow.getClass();
        int datacenterIndex = (int)(var10000 - 2L);
        long var10002 = Long.valueOf(binary.substring(0, datacenterIndex), 2);
        snow.getClass();
        ret[0] = var10002 + 1525104000000L;
        ret[1] = Long.valueOf(binary.substring(datacenterIndex, workerIndex), 2);
        ret[2] = Long.valueOf(binary.substring(workerIndex, sequenceIndex), 2);
        ret[3] = Long.valueOf(binary.substring(sequenceIndex, length), 2);
        return ret;
    }
}