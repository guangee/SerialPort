package com.yang.serialport.utils;

import com.google.common.collect.Lists;
import com.yang.serialport.common.ReportParam;
import com.yang.serialport.manager.SerialPortManager;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.function.Consumer;

@Slf4j
public class SerialUtil {
    private static SerialUtil instance;

    private List<Consumer<ReportParam>> consumerList;

    private final SerialPort serialPort;

    private SerialUtil(String portName, int baudrate) throws PortInUseException {
        serialPort = SerialPortManager.openPort(portName, baudrate);
        if (serialPort == null) {
            return;
        }
        consumerList = Lists.newArrayList();

        log.info("串口已经打开,等待接收数据");
        SerialPortManager.addListener(serialPort, () -> {
            String content = new String(SerialPortManager.readFromPort(serialPort)).trim();
            if (StringUtils.isBlank(content)) {
                log.info("read unused content");
                return;
            }
            int data = NumberUtils.toInt(content);
            double temperature = data / 1000000.00;
            double humidity = (data % 10000) / 100.00;
            if (temperature == 0 || humidity == 0) {
                log.info("read unused content:{}", content);
                return;
            }
            ReportParam reportParam = new ReportParam();
            reportParam.setHumidity(humidity);
            reportParam.setTemperature(temperature);
            sendReportData(reportParam);
            log.info("data:{}", data);
        });

    }

    private void sendReportData(ReportParam reportParam) {
        for (Consumer<ReportParam> item : consumerList) {
            item.accept(reportParam);
        }
    }

    public static SerialUtil getInstance(String portName, int baudrate) throws PortInUseException {
        if (instance == null) {
            synchronized (SerialUtil.class) {
                if (instance == null) {
                    instance = new SerialUtil(portName, baudrate);
                }
            }
        }
        return instance;
    }

    public void addOnReportListener(Consumer<ReportParam> consumer) {
        consumerList.add(consumer);

    }

    public void write(Character control) {
        log.info("接收到来自服务器的控制命令:{}", control);
        SerialPortManager.sendToPort(serialPort, String.valueOf(control).getBytes());
    }
}
