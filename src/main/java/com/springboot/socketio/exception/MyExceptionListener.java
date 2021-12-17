package com.springboot.socketio.exception;

import com.corundumstudio.socketio.listener.ExceptionListenerAdapter;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyExceptionListener extends ExceptionListenerAdapter {
    @Override
    public boolean exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.debug(e.getMessage());
        ctx.close();
        //返回true才是不抛出异常
        return true;
    }
}