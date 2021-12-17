package com.ppfly.socketio.service;

import cn.hutool.core.util.StrUtil;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.ppfly.socketio.message.MessageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service(value = "socketIOService")
@Slf4j
@Order(value = 1)
public class SocketIOServiceImpl implements SocketIOService, CommandLineRunner/*, DisposableBean */ {
    /**
     * 用来存储已连接的客户端
     * k: sessionId
     * v: SocketIOClient
     */
    private static Map<String, SocketIOClient> clientMap = new ConcurrentHashMap<>();

    /**
     * 用来存储已登录用户
     * k: loginId
     * v: SocketIOClient
     */
    private static Map<String, SocketIOClient> loginUserMap = new ConcurrentHashMap<>();

    @Autowired
    private SocketIOServer socketIOServer;


    /**
     * 实现CommandLineRunner接口 的Component 会在所有的Spring Beans 都初始化之后，SpringApplication.run() 之前执行，
     * 适合应用程序启动之初的数据初始化工作
     * <p>
     * PostConstruct Annotation是JSR250定义的java规范，更针对性于当前类文件(在初始化SocketIOServiceImpl Bean之后执行);
     * CommandLineRunner Annotation更服务于整个项目
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        this.start();
    }


    /**
     * Spring IoC容器在销毁SocketIOServiceImpl Bean之前关闭,避免重启项目服务端口占用问题
     *
     * @throws Exception
     */
    @PreDestroy
    private void autoStop() throws Exception {
        stop();
    }

    @Override
    public void start() {
//        addSocketIOListener();
        socketIOServer.start();
    }

    /**
     * 添加SocketIO监听
     * 使用注解代替了
     */
    @Deprecated
    private void addSocketIOListener() {
        // 监听客户端连接
        socketIOServer.addConnectListener(client -> {
            // TODO do something
        });

        // 监听客户端断开连接
        socketIOServer.addDisconnectListener(client -> {
            // TODO do something
        });

        //处理自定义的事件，与连接监听类似
        socketIOServer.addEventListener(MessageInfo.EVENT_LOGIN, MessageInfo.class, (client, data, ackSender) -> {
            // TODO do something
        });
    }

    @Override
    public void stop() {
        if (socketIOServer != null) {
            socketIOServer.stop();
            socketIOServer = null;
        }
    }

    @Override
    public void send(String eventName, MessageInfo messageInfo) {
        String sessionId = messageInfo.getSessionId();
        if (StrUtil.isNotBlank(sessionId)) {
            SocketIOClient client = clientMap.get(sessionId);
            if (client != null) {
                client.sendEvent(eventName, messageInfo);
            }
        }
    }

    @Override
    public void broadcast(String eventName, MessageInfo messageInfo) {
        socketIOServer.getBroadcastOperations().sendEvent(eventName, messageInfo);
    }

    /**
     * 内部类
     * 消息监听&处理类
     */
    @Component
    private class EventHander {

        /**
         * 客户端连接的时候触发
         * 前端js触发：socket = io.connect("http://192.168.31.201:9092");
         *
         * @param client
         */
        @OnConnect
        public void onConnect(SocketIOClient client) {
            final UUID sessionId = client.getSessionId();
            clientMap.put(sessionId.toString(), client);
            log.info("客户端:" + sessionId + "已连接...");
//            client.joinRoom("room01");
//            client.sendEvent(Socket.EVENT_CONNECT, client);
        }

        /**
         * 客户端关闭连接时触发
         * 前端js触发：socket.disconnect();
         *
         * @param client
         */
        @OnDisconnect
        public void onDisconnect(SocketIOClient client) {
            final UUID sessionId = client.getSessionId();
            if (sessionId != null) {
                clientMap.remove(sessionId.toString());
//                client.leaveRoom("room01");
                client.disconnect();
            }
            log.info("客户端:" + client.getSessionId() + "已断开连接...");
        }

        /**
         * 监听用户登录事件
         * <p>
         * 客户端js触发：socket.emit('login', {msgContent: msg});
         * 前端js的 socket.emit("事件名","参数数据")方法，是触发后端自定义消息事件的时候使用的;
         * 前端js的 socket.on("事件名",匿名函数(服务器向客户端发送的数据))为监听服务器端的事件
         *
         * @param client      客户端信息
         * @param request     请求信息
         * @param messageInfo 客户端发送数据{sessionId: "";loginId:"";content:""}
         */
        @OnEvent(value = MessageInfo.EVENT_LOGIN)
        public void onLoginEvent(SocketIOClient client, AckRequest request, MessageInfo messageInfo) {
            log.info("客户端登录成功：" + messageInfo);
            if (StrUtil.isBlank(messageInfo.getLoginId())) {
                throw new RuntimeException("loginId必传...");
            }
            loginUserMap.put(messageInfo.getLoginId(), client);
        }

        /**
         * 监听单聊事件
         *
         * @param client
         * @param request
         * @param messageInfo
         */
        @OnEvent(value = MessageInfo.EVENT_CHAT)
        public void onChatEvent(SocketIOClient client, AckRequest request, MessageInfo messageInfo) {
            log.info(messageInfo.getLoginId() + messageInfo.getReceiver() + "发起单聊...");
            final SocketIOClient receiveClient = loginUserMap.get(messageInfo.getReceiver());
            if (receiveClient != null) {
                receiveClient.sendEvent(MessageInfo.EVENT_CHAT, messageInfo);
            }
        }

        /**
         * 监听群聊事件
         *
         * @param client
         * @param request
         * @param messageInfo
         */
        @OnEvent(value = MessageInfo.EVENT_GROUP_CHAT)
        public void onGroupChatEvent(SocketIOClient client, AckRequest request, MessageInfo messageInfo) {
            log.info(messageInfo.getLoginId() + "发起群聊...");
            loginUserMap.forEach((key, receiveClient) -> {
                receiveClient.sendEvent(MessageInfo.EVENT_GROUP_CHAT, messageInfo);
            });
        }
    }
}