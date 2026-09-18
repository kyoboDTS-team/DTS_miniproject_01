package com.team.orderapp.export;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 시리얼 현황 CSV 출력용 데이터
 */
@Getter
@Setter
public class SerialCsvRow {

    private String productCode;
    private String productName;
    private String serialNumber;
    private String unitStatus;
    private LocalDateTime createdAt;
}