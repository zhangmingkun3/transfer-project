package com.lp.transfer.transferproject.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lp.transfer.transferproject.enums.Response;
import com.lp.transfer.transferproject.utils.FileUtils;
import com.lp.transfer.transferproject.utils.HttpClientUtils;
import com.lp.transfer.transferproject.utils.OpenExe;
import com.lp.transfer.transferproject.utils.WriteExcel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
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

    @Value("${transfer.programPath}")
    private String programPath;

    @Value("${transfer.sourcePath}")
    private String sourcePath;

    @Value("${transfer.sourceName}")
    private String sourceName;

    @Value("${transfer.sinkPath}")
    private String sinkPath;

    @Value("${request.httpUrl}")
    private String httpUrl;


    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static Selector selector;

    public static final Integer DATA_NUM = 1000*36;

    /**
     * 发送数据缓冲区
     */
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);

    public static void main(String[] args){
//        new SocketServer().start(8068);
    }

    @PostConstruct
    public void start() {
        try {
            // 打开通信信道
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            // 设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            // 获取套接字
            ServerSocket serverSocket = serverSocketChannel.socket();
            // 绑定端口号
            serverSocket.bind(new InetSocketAddress(port));
            // 打开监听器
            selector = Selector.open();
            // 将通信信道注册到监听器
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            // 监听器会一直监听，如果客户端有请求就会进入相应的事件处理
            while (true) {
                // select方法会一直阻塞直到有相关事件发生或超时
                selector.select();
                // 监听到的事件
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey key : selectionKeys) {
                    handle(key);
                }
                // 清除处理过的事件
                selectionKeys.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle(SelectionKey selectionKey){
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
        int count = 0;
        if (selectionKey.isAcceptable()) {

            try {
                //新连接请求，注册到选择器中   每有客户端连接，即注册通信信道为可读
                serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
                socketChannel.register(selector, SelectionKey.OP_READ);
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendInfo(socketChannel, "连接服务器成功!");
            System.err.println("client IP :" + socketChannel.socket().getRemoteSocketAddress());
        } else if (selectionKey.isReadable()) try {
            //连接可读请求，处理读业务逻辑
            socketChannel = (SocketChannel) selectionKey.channel();

            ByteBuffer buffer = ByteBuffer.allocate(1009);
            ArrayList<Byte> byteList = new ArrayList<>();

            while (socketChannel.read(buffer) > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte[] bytes = buffer.get(new byte[buffer.limit()]).array();
                    for (byte b : bytes) {
                        byteList.add(b);
                    }
                }
                buffer.clear();
            }

            byte[] out = toPrimitives(byteList.toArray(new Byte[0]));

            String asciiId = bytesToHexString(out);
            String id = AsciiStringToString(asciiId).replace("|","");
            String deviceId = id.substring(0,14);
            String num = id.substring(15,16);
            log.info("AsciiId={}, id={}, deviceId={}, num={}",asciiId,id,deviceId,num);

            List<Integer> totalList = new ArrayList<>();
            int sum = 0;
            for(int i = 18 ;i <out.length;i+=2){
                byte low = out[i];
                byte high = out[i+1];
                int x = merge(high,low);
                sum+=1;
                totalList.add(x);
            }

            log.info("deviceId={},总数：" + sum + "   arrayList.Size:" + totalList.size() + "  arrayList:" + JSON.toJSONString(totalList),deviceId);

            if (null != localCache.getIfPresent(deviceId) && localCache.getIfPresent(deviceId).size() > 0) {
                List<Integer> list = localCache.get(deviceId);
                list.addAll(totalList);
                localCache.asMap().forEach((k,v) -> {
                    log.info("缓存中数据 key={},value.size={}",k,v.size());
                });
                if (list.size() >= DATA_NUM) {
                    // 写入excel  调用exe程序
                    WriteExcel.dataWriteExcel(sourcePath,sourceName,null,list);
                    // 然后重新设置 Map 以及缓存
                    localCache.put(deviceId, new ArrayList<>());

                    // 调用exe
                    File file = null;
                    try {
                        log.info("执行exe程序解析。。。");
                        OpenExe.runExe(programPath);
                        log.info("休眠等待中。。。。。。");
                        Thread.sleep(1000);

                        //读取文件，判断文件是否存在
                        file = new File(sinkPath);
                        JSONObject jsonObject = null;
                        String response = null;
                        if (file.exists()){
                            // 文件存在,判断文件是都最近创建
                            response = doHttpPost(FileUtils.readExcel(sinkPath),deviceId);
                        }else{
                            log.info("继续休眠等待中。。。。。。");
                            Thread.sleep(1000);
                            if (file.exists()){
                                response = doHttpPost(FileUtils.readExcel(sinkPath),deviceId);
                            }else{
                                log.info("{} 文件不存在。。。。。。",sinkPath);
                            }
                        }
                        log.info("调用http接口完成，返回消息{}",response);
                    }catch (Exception e){
                        e.getStackTrace();
                        log.error("失败 " + e.getMessage());
                    }finally {
                        if (file != null){
                            DataOutputStream dos = null;
                            try {
                                dos = new DataOutputStream(new FileOutputStream(file));
                                dos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            boolean delete = file.delete();
                            log.info("删除生成的结果文件 {}",delete);
                        }
                    }

                }else {
                    localCache.put(deviceId, list);
                }
            } else {
                COMMON_POOL.submit(() -> {
                    localCache.put(deviceId, new ArrayList<>(totalList));
                });
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert socketChannel != null;
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String doHttpPost(JSONObject jsonObject,String deviceId){
        Map<String,Object> param = new HashMap<>(4);
        param.put("result",jsonObject.toJSONString());
        param.put("deviceId",deviceId);
        try {
            return HttpClientUtils.post(httpUrl,param);
        } catch (IOException e) {
            log.info("调用http接口出现异常 {},{}",httpUrl,e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private void sendInfo(SocketChannel clientChannel, String msg){
        // 向客户端发送连接成功信息
        try {
            clientChannel.write(ByteBuffer.wrap(msg.getBytes()));
        } catch (IOException e) {
            log.info("向请求端发送回执消息失败");
            e.printStackTrace();
        }
    }

        private static String toHex(byte[] buffer) {
        StringBuffer sb = new StringBuffer(buffer.length * 2);
        for (int i = 0; i < buffer.length; i++) {
            sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(buffer[i] & 15, 16));
        }
        return sb.toString();
    }



    private void handleAccept(SelectionKey selectionKey) throws Exception {
        // 返回创建此键的通道，接受客户端建立连接的请求，并返回 SocketChannel 对象
        ServerSocketChannel ServerSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel clientChannel = ServerSocketChannel.accept();
        // 非阻塞式
        clientChannel.configureBlocking(false);
        // 注册到selector
        clientChannel.register(selectionKey.selector(), SelectionKey.OP_READ);

//        sendInfo(clientChannel, "连接服务器成功!");
        System.err.println("client IP :" + clientChannel.socket().getRemoteSocketAddress());
    }




     private static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];

        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }
}