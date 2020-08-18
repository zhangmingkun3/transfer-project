package com.lp.transfer.transferproject.service;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/18 15:50
 */

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static com.lp.transfer.transferproject.service.SocketPool.add;
import static com.lp.transfer.transferproject.service.SocketPool.remove;

/**
 * Socket操作处理类
 */
@Slf4j
public class SocketHandler{

//
//    /**
//     * 将连接的Socket注册到Socket池中
//     * @param socket
//     * @return
//     */
//    public static ClientSocket register(Socket socket){
//
//        DataInputStream input = null;
//        try {
//            input = new DataInputStream(socket.getInputStream());
//            byte[] b = new byte[1024];
//            int len = 0;
//            String response = "";
//            while (true) {
//                len = input.read(b);
//                response = new String(b, 0, len);
//                log.info("datadadata", response);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//
//    }

    /**
     * 指定Socket资源回收
     * @param clientSocket
     */
    public static void close(ClientSocket clientSocket){
        log.info("进行资源回收");
        if (clientSocket != null){
            log.info("开始回收socket相关资源，其Key为{}", clientSocket.getKey());
            remove(clientSocket.getKey());
            Socket socket = clientSocket.getSocket();
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
            } catch (IOException e) {
                log.error("关闭输入输出流异常，{}", e);
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("关闭socket异常{}", e);
                }
            }
        }
    }


    /**
     * 发送数据包，判断数据连接状态
     * @param clientSocket
     * @return
     */
    public static boolean isSocketClosed(ClientSocket clientSocket){
        try {
            clientSocket.getSocket().sendUrgentData(1);
            return false;
        } catch (IOException e) {
            return true;
        }
    }

}