package com.yang.serialport.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PushMessage {
    private String clientId;

    /**
     * 类型 1-上位机 2-安卓
     */
    private int type;

    private ControlParam control;

    private ReportParam report;


}
