package com.ppfly.socketio.service;

import com.ppfly.socketio.message.MessageInfo;

public interface SocketIOService {


    /**
     * 启动服务
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 停止服务
     */
    void stop();

    /**
     * 推送给-单一客户端
     *
     * @param eventName
     * @param messageInfo
     */
    void send(String eventName, MessageInfo messageInfo);


    /**
     * 广播
     *
     * @param eventName
     * @param messageStr
     */
    void broadcast(String eventName, MessageInfo messageStr);
}