package com.example.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TransferStatusConverter implements AttributeConverter<TransferStatus, String>{

    @Override
    public String convertToDatabaseColumn(TransferStatus attribute) {
        if (attribute == null){
            return null;
        }
        return attribute.dbValue();
    }

    @Override
    public TransferStatus convertToEntityAttribute(String dbData) {
        if (dbData == null){
            return null;
        }
        return TransferStatus.fromDbValue(dbData);
    }
}
