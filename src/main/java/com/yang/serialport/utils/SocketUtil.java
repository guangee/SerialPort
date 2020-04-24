package com.yang.serialport.utils;

import com.google.common.collect.Lists;
import com.guanweiming.common.utils.JsonUtil;
import com.yang.serialport.common.ControlParam;
import com.yang.serialport.common.PushMessage;
import com.yang.serialport.common.ReportParam;
import io.socket.client.IO;
import io.socket.client.Socket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class SocketUtil {

    private Socket socket;
    private static SocketUtil instance;
    private List<Consumer<Character>> consumerList;
    private static String clientId = null;


    private SocketUtil(String serverUrl) throws URISyntaxException {
        consumerList = Lists.newArrayList();
        clientId = 1 + UUID.randomUUID().toString();
        IO.Options options = new IO.Options();
        options.query = "clientId=" + clientId;
        socket = IO.socket(serverUrl, options);
        socket.on(Socket.EVENT_CONNECT, args -> log.debug("connect success"))
                .on("server_event", args -> {
                    if (args.length == 0) {
                        return;
                    }
                    PushMessage pushMessage = JsonUtil.fromJson(String.valueOf(args[0]), PushMessage.class);
                    if (pushMessage == null) {
                        return;
                    }
                    if (pushMessage.getType() != 1) {
                        return;
                    }
                    log.debug(JsonUtil.toJson(pushMessage));
                    ControlParam control = pushMessage.getControl();
                    log.debug("control:{}", getControl(control));
                    pubControl(getControl(control));
                }).on(Socket.EVENT_DISCONNECT, args -> log.info("关闭"));
        socket.connect();
    }

    private void pubControl(char control) {
        for (Consumer<Character> item : consumerList) {
            if (item != null) {
                item.accept(control);
            }
        }
    }

    public static SocketUtil getInstance(String serverUrl) throws URISyntaxException {
        if (instance == null) {
            synchronized (SocketUtil.class) {
                if (instance == null) {
                    instance = new SocketUtil(serverUrl);
                }
            }
        }
        return instance;
    }


    public void addOnControlListener(Consumer<Character> consumer) {
        consumerList.add(consumer);
    }


    private static char getControl(ControlParam control) {
        int n = 0;
        n |= control.getLampOne() << 3;
        n |= control.getLampTwo() << 2;
        n |= control.getLampThree() << 1;
        n |= control.getFunOne();
        return (char) ('A' + n);
    }

    public void report(ReportParam report) {
        if (report == null) {
            log.info("report Param can not be null");
            return;
        }
        if (StringUtils.isBlank(clientId)) {
            log.info("clientId can not be null");
            return;
        }
        log.info("读取到单片机上报的温度湿度数据：{}", report);
        PushMessage pushMessage = new PushMessage();
        pushMessage.setReport(report);
        pushMessage.setClientId(clientId);
        pushMessage.setType(2);
        socket.emit("client_event", JsonUtil.toJson(pushMessage));
    }
}
