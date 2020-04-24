package com.yang.serialport.ui;

import com.yang.serialport.utils.SerialUtil;
import com.yang.serialport.utils.SocketUtil;
import gnu.io.PortInUseException;
import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;

@Slf4j
public class SocketMain {

    private static final String SERVER_URL = "http://47.98.138.194:9099";
    private static final String PORT_NAME = "COM4";
    private static final int BAUD_RATE = 9600;

    public static void main(String[] args) throws URISyntaxException, PortInUseException {
        SocketUtil socketInstance = SocketUtil.getInstance(SERVER_URL);
        SerialUtil serialInstance = SerialUtil.getInstance(PORT_NAME, BAUD_RATE);

        socketInstance.addOnControlListener(control -> {
            //接收到来自服务器的控制命令，在这里将命令下发下去
            log.info("接收到来自服务器的控制命令:{}", control);
            serialInstance.write(control);
        });

        serialInstance.addOnReportListener(report -> {
            log.info("读取到单片机上报的温度湿度数据：{}", report);
            socketInstance.report(report);
        });
    }


}
