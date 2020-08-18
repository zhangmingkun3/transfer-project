package com.lp.transfer.transferproject.service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lp.transfer.transferproject.utils.CacheUtils.localCache;
import static com.lp.transfer.transferproject.utils.ThreadPoolUtils.COMMON_POOL;
import static com.lp.transfer.transferproject.utils.ThreadPoolUtils.shareMap;

/**
 * @Author: zhangmingkun3
 * @Description:
 * @Date: 2020/8/18 15:39
 */
@Slf4j
@Data
@Component
public class SocketServer {

    @Value("${socket.port}")
    private Integer port;

    private boolean started;
    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newCachedThreadPool();


    public static void main(String[] args){
//        new SocketServer().start(8068);
    }


    public void start(Integer port) {
        log.info("port: {}, {}", this.port, port);
        try {
            serverSocket = new ServerSocket(port == null ? this.port : port);
            started = true;
            log.info("Socket服务已启动，占用端口： {}", serverSocket.getLocalPort());
        } catch (IOException e) {
            log.error("端口冲突,异常信息：{}", e);
            System.exit(0);
        }

        while (started) {
            try {
                Socket socket = serverSocket.accept();
                socket.setKeepAlive(true);

                DataInputStream input = new DataInputStream(socket.getInputStream());
                byte[] b = new byte[1024];
                int len = 0;
                String response = "";
                while (true) {
                    len = input.read(b);
                    response = new String(b, 0, len);
                    log.info("datadadata", response);



                    //获取 key
                    String key = response;
                    if (shareMap.containsKey(key)){
                        Thread thread = shareMap.get(key);

                        List<String> list = localCache.get(key);

                        if (list.size() > 2999){
                            // 触发计算  调用exe程序

                            // 然后重新设置 Map 以及缓存
                            shareMap.put(key,null);
                            localCache.put(key,new ArrayList<>());
                        }

                        list.add("数据");
                        localCache.put(key,list);

                    }else{

                        COMMON_POOL.submit(() ->{
                            List<String> list = new ArrayList<>();
                            list.add("数据");
                            localCache.put(key,list);
                        });

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}