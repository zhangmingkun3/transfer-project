package com.lp.transfer.transferproject.service;

import com.alibaba.fastjson.JSON;
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
import static com.lp.transfer.transferproject.utils.MessageParse.*;
import static com.lp.transfer.transferproject.utils.ThreadPoolUtils.COMMON_POOL;

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

        final int readArraySizePerRead = 4096;
        while (started) {
            try {
                Socket socket = serverSocket.accept();
                socket.setKeepAlive(true);


                byte[] b = new byte[1024];
                int len = 0;
                String response = "";
                while (true) {

                    DataInputStream isr = new DataInputStream(socket.getInputStream());

                    ArrayList<Byte> bytes = new ArrayList<>();

                    byte[] tempchars = new byte[readArraySizePerRead];
                    int charsReadCount = 0;
                    while ((charsReadCount = isr.read(tempchars)) != -1) {
                        for(int i = 0 ; i < charsReadCount ; i++){
                            bytes.add (tempchars[i]);
                        }
                    }
                    isr.close();

                    byte[] out = toPrimitives(bytes.toArray(new Byte[0]));

                    // 解析
                    String asciiId = bytesToHexString(out);
                    String id = AsciiStringToString(asciiId).replace("|","");
                    System.out.println("AsciiId : " + asciiId  + " id ："+ id);

                    List<Integer> totalList = new ArrayList<>();
                    int sum = 0;
                    for(int i = 16 ;i <out.length;i+=2){
                        byte low = out[i];
                        byte high = out[i+1];
                        int x = merge(high,low);
                        short low_short = unsignedByteToShort(low);
                        short high_short = (short)(((high & 0x00FF) << 8));
                        System.out.println( "low:" + low_short + "   high : " +  high_short + "   sum :" + x);
                        sum+=1;
                        totalList.add(x);
                    }

                    System.out.println("总数：sum = " + sum+"   arrayList Size:" + totalList.size() + "  arrayList:" + JSON.toJSONString(totalList) );

                    len = isr.read(b);
                    response = new String(b, 0, len);
                    log.info("datadadata", response);



                    if (null != localCache.getIfPresent(asciiId) && localCache.getIfPresent(asciiId).size() > 0){
//                        Thread thread = shareMap.get(asciiId);

                        List<String> list = localCache.get(asciiId);

                        if (list.size() > 2999){
                            // 触发计算  调用exe程序

                            // 然后重新设置 Map 以及缓存
                            localCache.put(asciiId,new ArrayList<>());
                        }

                        list.add("数据");
                        localCache.put(asciiId,list);

                    }else{

                        COMMON_POOL.submit(() ->{
                            List<String> list = new ArrayList<>();
                            list.add("数据");
                            localCache.put(asciiId,list);
                        });

                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];

        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }
}