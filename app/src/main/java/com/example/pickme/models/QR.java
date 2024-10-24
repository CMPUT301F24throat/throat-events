package com.example.pickme.models;

import com.example.pickme.models.Enums.QrType;
import com.google.firebase.Timestamp;

/**
 * Represents uploaded QRs (event posters, waiting list etc..)
 * Responsibilities:
 * Models a QR in the QR collection
 * Stores metadata about uploaded qrs in the QR collection, including the hash data
 **/

public class QR {
    private String qrId;
    private String qrData;
    private QrType type;
    private String qrAssociation;
    private String uploaderId;
    private final Timestamp createdAt;
    private Timestamp updatedAt;

    public QR() {
        this.createdAt = Timestamp.now();
    }

    public QR(String qrData, QrType type, String qrAssociation, String uploaderId) {
        this.qrData = qrData;
        this.type = type;
        this.qrAssociation = qrAssociation;
        this.uploaderId = uploaderId;
        this.createdAt = Timestamp.now();
        this.updatedAt = Timestamp.now();
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
        this.updatedAt = Timestamp.now();
    }

    public String getQrData() {
        return qrData;
    }

    public void setType(QrType type) {
        this.type = type;
        this.updatedAt = Timestamp.now();
    }

    public QrType getType() {
        return type;
    }

    public void setQrAssociation(String qrAssociation) {
        this.qrAssociation = qrAssociation;
        this.updatedAt = Timestamp.now();
    }

    public String getQrAssociation() {
        return qrAssociation;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
        this.updatedAt = Timestamp.now();
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}
