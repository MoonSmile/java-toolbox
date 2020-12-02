package Bak;

public class Server {
    private String ip = "127.0.0.1";
    private String name = null;
    private Environment environment;

    public Server() {
        this.environment = Environment.UNKNOWN;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public static enum Environment {
        DEV("dev", new String[]{"10.1.8"}, "DEV"),
        TEST("test", new String[]{"10.1.1"}, "TEST"),
        BAK("bak", new String[]{"10.80.38", "10.80.41", "10.64.140"}, "BAK"),
        PROD("prod", new String[]{"10.80.36", "10.80.40", "10.80.37"}, "PROD"),
        UNKNOWN("xxx", new String[]{"xxx.xxx.xxx"}, "UNKNOWN");

        private String profilesName;
        private String[] prefixes;
        private String name;

        private Environment(String profilesName, String[] prefixes, String name) {
            this.profilesName = profilesName;
            this.prefixes = prefixes;
            this.name = name;
        }

        public String getProfilesName() {
            return this.profilesName;
        }

        public String[] getPrefixes() {
            return this.prefixes;
        }

        public String getName() {
            return this.name;
        }
    }
}
