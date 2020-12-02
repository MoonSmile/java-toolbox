package Bak;

import Bak.Server.Environment;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ServerUtils {
    private static final Server server = new Server();

    private ServerUtils() {
    }

    private static Environment getEnvByIp(String ip) {
        return (Environment) Stream.of(Environment.values()).filter((e) -> {
            return Stream.of(e.getPrefixes()).anyMatch((p) -> {
                return ip.startsWith(p);
            });
        }).findFirst().orElse(Environment.UNKNOWN);
    }

    public static String getServerIp() {
        return server.getIp();
    }

    public static String getServerName() {
        return server.getName();
    }

    public static Environment getServerEnviroment() {
        return server.getEnvironment();
    }

    public static InetAddress getLocalHostLANAddress () throws SocketException,UnknownHostException{
        try {
            List<NetworkInterface> ifaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            List<InetAddress> inetAddrList = (List)ifaceList.stream().flatMap((iface) -> {
                return Collections.list(iface.getInetAddresses()).stream();
            }).filter((ia) -> {
                return !ia.isLoopbackAddress();
            }).collect(Collectors.toList());
            if (isEmpty(inetAddrList)) {
                return InetAddress.getLocalHost();
            } else {
                Optional<InetAddress> ret = inetAddrList.stream().filter(InetAddress::isSiteLocalAddress).findFirst();
                return (InetAddress)ret.orElse(inetAddrList.get(0));
            }
        } catch (SocketException se) {
            throw se;
        }
        catch (UnknownHostException ue)
        {
            throw ue;
        }
    }

    public static boolean isEmpty(Collection coll) {
        return (coll == null || coll.isEmpty());
    }
    public static Server getServer() {
        return server;
    }

    static {
        InetAddress localHostLANAddress = null;

        try {
            localHostLANAddress = getLocalHostLANAddress();
        } catch (Exception var2) {
            System.out.println("获取IP发生异常：" + var2.getLocalizedMessage());
        }

        if (localHostLANAddress != null) {
            server.setIp(localHostLANAddress.getHostAddress());
            server.setName(localHostLANAddress.getHostName());
            server.setEnvironment(getEnvByIp(server.getIp()));
        }

    }
}
