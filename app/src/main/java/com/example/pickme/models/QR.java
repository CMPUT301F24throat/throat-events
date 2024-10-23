package com.example.pickme.models;

import com.example.pickme.models.Enums.QrType;

import java.util.Date;

/**
 * Represents uploaded QRs (event posters, waiting list etc..)
 * Responsibilities:
 * Stores metadata about uploaded qrs in the QR collection,
 * including the hash data
 **/

public class QR {
    private String qrId;
    private String qrData;
    private QrType type;
    private String qrAssociation;
    private String uploaderId;
    private final Date createdAt;
    private Date updatedAt;

    public QR() {
        this.createdAt = new Date();
    }

    public QR(String qrData, QrType type, String qrAssociation, String uploaderId) {
        this.qrData = qrData;
        this.type = type;
        this.qrAssociation = qrAssociation;
        this.uploaderId = uploaderId;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
        this.updatedAt = new Date();
    }

    public void setType(QrType type) {
        this.type = type;
        this.updatedAt = new Date();
    }

    public void setQrAssociation(String qrAssociation) {
        this.qrAssociation = qrAssociation;
        this.updatedAt = new Date();
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
        this.updatedAt = new Date();
    }
}
