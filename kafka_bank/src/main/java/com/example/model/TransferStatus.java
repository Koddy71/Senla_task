package com.example.model;

public enum TransferStatus {
    READY("готово"),
    FAILED("завершилась с ошибкой");

    private final String dbValue;

    TransferStatus(String dbValue){
        this.dbValue=dbValue;
    }

    public String dbValue() {
        return dbValue;
    }

    public static TransferStatus fromDbValue(String value){
        for (TransferStatus s : values()){
            if (s.dbValue.equals(value)) return s;
        }
        throw new IllegalArgumentException("Неизвестный статус" + value);
    }
}
