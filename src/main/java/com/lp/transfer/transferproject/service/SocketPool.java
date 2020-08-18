package com.lp.transfer.transferproject.service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/18 15:52
 */
public class SocketPool {

    private static final ConcurrentHashMap<String, ClientSocket> ONLINE_SOCKET_MAP = new ConcurrentHashMap<>();


    public static void add(ClientSocket clientSocket){
        if (clientSocket != null && !clientSocket.getKey().isEmpty())
            ONLINE_SOCKET_MAP.put(clientSocket.getKey(), clientSocket);
    }

    public static void remove(String key){
        if (!key.isEmpty())
            ONLINE_SOCKET_MAP.remove(key);
    }

}