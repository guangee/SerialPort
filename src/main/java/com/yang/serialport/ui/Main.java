package com.yang.serialport.ui;

import com.yang.serialport.manager.SerialPortManager;
import com.yang.serialport.utils.ByteUtils;
import com.yang.serialport.utils.ShowUtils;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static int n = 0;

    public static void main(String[] args) throws PortInUseException {


        List<String> ports = SerialPortManager.findPorts();
        System.out.println(ports);
        final SerialPort serialPort = SerialPortManager.openPort("COM4", 9600);
        if (serialPort == null) {
            return;
        }
        System.out.println("串口已经打开");
        SerialPortManager.addListener(serialPort, new SerialPortManager.DataAvailableListener() {

            @Override
            public void dataAvailable() {
                byte[] data = SerialPortManager.readFromPort(serialPort);
                System.out.println(new String(data));
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int k = 'A';
                    n = (n + 1) % 16;
                    char c = (char) (k + n);
                    System.out.println(c);
                    System.out.println(String.valueOf(c));
                    SerialPortManager.sendToPort(serialPort, String.valueOf(c).getBytes());
                }
            }
        }).start();
    }


}
