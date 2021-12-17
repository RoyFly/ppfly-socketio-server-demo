package com.ppfly.socketio.message;

import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageInfo {

    /**
     * 发送事件名称
     */
    //登录事件
    public static final String EVENT_LOGIN = "login";
    //单聊
    public static final String EVENT_CHAT = "chatEvent";
    //群聊
    public static final String EVENT_GROUP_CHAT = "groupChatEvent";

    /**
     * 客户端sessionId
     */
    private String sessionId;

    /**
     * 用户登录名
     */
    private String loginId;

    /**
     * 客户端IP
     */
    private String clientIp;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 接受者登录名
     */
    private String receiver;

}