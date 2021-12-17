package com.ppfly.socketio.controller;

import com.ppfly.restful.ResultModel;
import com.ppfly.socketio.message.MessageInfo;
import com.ppfly.socketio.service.SocketIOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping(value = "/test")
public class TestController {
    @Autowired
    private SocketIOService socketIOService;


    /**
     * 服务器主动推送-推送给某个客户端
     */
    @GetMapping(value = "/sendToOne/{sessionId}")
    public String testPushMessage2Client(@PathVariable("sessionId") String sessionId) throws Exception {
        try {
            socketIOService.send("testSendEvent", MessageInfo.builder().sessionId(sessionId).content("服务器主动推送-推送给单一客户端").build());
        } catch (Exception e) {
            e.printStackTrace();
            return "errorerrorerrorerror";
        }
        return "okokokokokokokokokokokokokokokokokokokok";
    }

    /**
     * 服务器主动推送-推送给所有客户端
     *
     * @return
     */
    @GetMapping(value = "/sendToAll")
    public String socketIoTest() {
        socketIOService.broadcast("testBoradcastEvent", MessageInfo.builder().content("服务器广播消息...testBoradcastEvent...").build());
        return "okokokokokokokokokokokokokokokokokokokok...";
    }

    /**
     * 获取服务目录接口
     *
     * @return
     */
    @RequestMapping(value = "/slaService")
    public ResponseEntity<ResultModel> getSlaService() {
        List<Map> retList = new ArrayList<Map>();
        for (int i = 0; i < 5; i++) {
            Map<String, String> map = new HashMap<>();
            String id = UUID.randomUUID().toString();
            String pId = UUID.randomUUID().toString();
            String serviceName = ((int) Math.random() * 100) + "";
            map.put("serviceName", serviceName);
            map.put("id", id);
            map.put("pId", pId);
            retList.add(map);
        }
        return new ResponseEntity<>(ResultModel.ok(retList), HttpStatus.OK);
    }
}