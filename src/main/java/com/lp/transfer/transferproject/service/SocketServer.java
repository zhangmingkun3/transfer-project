package com.lp.transfer.transferproject.service;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

    private ServerSocket serverSocket;
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private static Selector selector;

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

//            StringBuilder msg = new StringBuilder();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            ArrayList<Byte> byteList = new ArrayList<>();

            while (socketChannel.read(buffer) > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    byte[] bytes = buffer.get(new byte[buffer.limit()]).array();
//                    msg.append(new String(buffer.get(new byte[buffer.limit()]).array()));
                    for (byte b : bytes) {
                        byteList.add(b);
                    }
                }
                buffer.clear();
            }
//            System.err.println("收到客户端消息:" + msg);

            byte[] out = toPrimitives(byteList.toArray(new Byte[0]));

            // 解析
            String asciiId = bytesToHexString(out);
            String id = AsciiStringToString(asciiId).replace("|", "");
            System.out.println("AsciiId : " + asciiId + " id ：" + id);

            List<Integer> totalList = new ArrayList<>();
            int sum = 0;
            for (int i = 16; i < out.length; i += 2) {
                byte low = out[i];
                byte high = out[i + 1];
                int x = merge(high, low);
                short low_short = unsignedByteToShort(low);
                short high_short = (short) (((high & 0x00FF) << 8));
                System.out.println("low:" + low_short + "   high : " + high_short + "   sum :" + x);
                sum += 1;
                totalList.add(x);
            }

            System.out.println("总数：sum = " + sum + "   arrayList Size:" + totalList.size() + "  arrayList:" + JSON
                    .toJSONString(totalList));

            String response = new String(out, 0, out.length);
            log.info("datadadata {}", response);

            if (null != localCache.getIfPresent(asciiId) && localCache.getIfPresent(asciiId).size() > 0) {
                List<String> list = localCache.get(asciiId);
                if (list.size() > 2999) {
                    // 触发计算  调用exe程序

                    // 然后重新设置 Map 以及缓存
                    localCache.put(asciiId, new ArrayList<>());
                }

                list.add("数据");
                localCache.put(asciiId, list);

            } else {

                COMMON_POOL.submit(() -> {
                    List<String> list = new ArrayList<>();
                    list.add("数据");
                    localCache.put(asciiId, list);
                });

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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




        static byte[] toPrimitives(Byte[] oBytes) {
        byte[] bytes = new byte[oBytes.length];

        for (int i = 0; i < oBytes.length; i++) {
            bytes[i] = oBytes[i];
        }

        return bytes;
    }
}