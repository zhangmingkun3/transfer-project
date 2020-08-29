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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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

    ByteBuffer totalBuffer = ByteBuffer.allocate(2018);

    /**
     * 发送数据缓冲区
     */
    private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);

    public static void main(String[] args) throws IOException {
//        new SocketServer().start(8068);

        String url = "http://123.56.96.214:8000/checkResult/getResult";
        doHttpPostStatic(FileUtils.readExcel("C:\\Users\\User\\Desktop\\Result.xlsx"),"20200000000001",url);

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

    private void handle(SelectionKey selectionKey) {
        ServerSocketChannel serverSocketChannel = null;
        SocketChannel socketChannel = null;
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
            assert socketChannel != null;
            sendInfo(socketChannel, "连接服务器成功!");
            System.out.println("client IP :" + socketChannel.socket().getRemoteSocketAddress());
        } else if (selectionKey.isReadable()){
            try {
                //连接可读请求，处理读业务逻辑
                socketChannel = (SocketChannel) selectionKey.channel();

//                Socket socket = socketChannel.socket();
//                // 装饰流BufferedReader封装输入流（接收客户端的流）
//                BufferedInputStream bis = new BufferedInputStream(
//                        socket.getInputStream());
//                ArrayList<Byte> byteLists = new ArrayList<>();
//
//                DataInputStream dis = new DataInputStream(bis);
//
//                byte[] tempchars = new byte[1009];
//                int charsReadCount = 0;
//
//                while ((charsReadCount = dis.read(tempchars)) != -1) {
//                    for(int i = 0 ; i < charsReadCount ; i++){
//                        byteLists.add (tempchars[i]);
//                    }
//                }
//                System.out.println("byteLists =" + byteLists.size());


                ByteBuffer buffer = ByteBuffer.allocate(2018);

                ByteBuffer buffer1 = ByteBuffer.allocate(2018);

                ArrayList<Byte> byteList = new ArrayList<>();

                int length = socketChannel.read(buffer);
                System.out.println("length=" + length ) ;

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                socketChannel = (SocketChannel) selectionKey.channel();
                int length1 = socketChannel.read(buffer1);
                System.out.println("length=" + length + ",length1=" + length1) ;
                while (length > 0) {
                    buffer.flip();
                    buffer1.flip();
                    while (buffer.hasRemaining()) {
                        byte[] bytes = buffer.get(new byte[buffer.limit()]).array();
                        byte[] bytes1 = buffer1.get(new byte[buffer1.limit()]).array();
                        System.out.println("字节列表1:"+Arrays.toString(bytes) );
                        System.out.println("字节列表2:"+Arrays.toString(bytes1));
                        for (byte b : bytes) {
                            if (byteList.size() < length){
                                byteList.add(b);
                            }
                        }
                        for (byte b : bytes1) {
                            if (byteList.size() < length + length1){
                                byteList.add(b);
                            }
                        }
                    }
                    length = 0;
                    length1 = 0;
                    buffer.clear();
                    buffer1.clear();
                }

                log.info("byteList.sixe={} {}", byteList.size(),JSON.toJSON(byteList));
                byte[] out = toPrimitives(byteList.toArray(new Byte[0]));

                String asciiId = bytesToHexString(out);
                log.info("asciiId={}", asciiId);
                String deviceId = null;
                List<Integer> totalList = new ArrayList<>();
                if (StringUtils.isNotEmpty(asciiId)) {
                    try {
                        String id = AsciiStringToString(asciiId).replace("|", "");
                        deviceId = id.substring(0, 14);
                        String num = id.substring(14, 16);
                        log.info("AsciiId={}, id={}, deviceId={}, num={}", asciiId, id, deviceId, num);

                        int sum = 0;
                        for (int i = 18; i < out.length; i += 2) {
                            int x = 0;
                            try {
                                byte low = out[i];
                                byte high = out[i + 1];
                                x = merge(high, low);
                                sum += 1;
                            } catch (Exception e) {
                                log.info("解析数据出现异常 {}", e.getMessage());
                                e.getStackTrace();
                            } finally {
                                totalList.add(x);
                            }
                        }
                        log.info("deviceId={},总数：" + sum + "   arrayList.Size:" + totalList.size() + "  arrayList:" + JSON.toJSONString(totalList), deviceId);
                    } catch (Exception ex) {
                        log.info("解析数据出现问题 {}", ex.getMessage());
                        ex.getStackTrace();
                    }


                    if (null != localCache.getIfPresent(deviceId) && localCache.getIfPresent(deviceId).size() > 0) {
                        List<Integer> list = localCache.get(deviceId);
                        list.addAll(totalList);
                        localCache.asMap().forEach((k, v) -> {
                            log.info("缓存中数据 key={},value.size={}", k, v.size());
                        });
                        if (list.size() >= DATA_NUM) {
                            // 写入excel  调用exe程序
                            WriteExcel.dataWriteExcel(sourcePath, sourceName, null, list);
                            // 然后重新设置 Map 以及缓存
                            localCache.put(deviceId, new ArrayList<>());

                            // 调用exe
                            File file = null;
                            try {
                                log.info("执行exe程序解析。。。");
                                OpenExe.runExe(programPath);
                                log.info("休眠等待中。。。。。。");
                                Thread.sleep(10000);

                                //读取文件，判断文件是否存在
                                file = new File(sinkPath);
                                String response = null;
                                if (file.exists() ) {
                                    log.info("文件存在。。。。。。");
                                    // 文件存在,判断文件是都最近创建
                                    response = doHttpPost(FileUtils.readExcel(sinkPath), deviceId);
                                } else {
                                    log.info("继续休眠等待中。。。。。。");
                                    Thread.sleep(10000);
                                    if (file.exists()) {
                                        response = doHttpPost(FileUtils.readExcel(sinkPath), deviceId);
                                    } else {
                                        log.info("{} 文件不存在或大小为0。。。。。。", sinkPath);
                                    }
                                }
                                log.info("调用http接口完成，返回消息{}", response);
                            } catch (Exception e) {
                                e.getStackTrace();
                                log.error("失败 {}" , e.getMessage());
                            } finally {
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

                        } else {

                            localCache.put(deviceId, list);

                        }
                    } else {
                        String finalDeviceId1 = deviceId;
                        Long id = Long.getLong(finalDeviceId1);
                        if (!CollectionUtils.isEmpty(totalList) && totalList.size() >= 1000) {
                            COMMON_POOL.submit(() -> {
                                localCache.put(finalDeviceId1, new ArrayList<>(totalList));
                            });
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socketChannel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
//                        }finally {
//                            try {
//                                socketChannel.close();
//                            } catch (IOException ex) {
//                                ex.printStackTrace();
//                            }
            }
    }
    }

    private String doHttpPost(JSONObject jsonObject,String deviceId){
        if (null != jsonObject){
            Map<String,Object> param = new HashMap<>(4);
            param.put("result",jsonObject);
            param.put("deviceId",deviceId);
            try {
                return HttpClientUtils.post(httpUrl,param);
            } catch (IOException e) {
                log.info("调用http接口出现异常 {},{}",httpUrl,e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String doHttpPostStatic(JSONObject jsonObject,String deviceId,String url){
        if (null != jsonObject){
            Map<String,Object> param = new HashMap<>(4);
            param.put("result",jsonObject);
            param.put("deviceId",deviceId);
            try {
                return HttpClientUtils.post(url,param);
            } catch (IOException e) {
                log.info("调用http接口出现异常 {},{}",url,e.getMessage());
                e.printStackTrace();
            }
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