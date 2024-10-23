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
    private Date createdAt;

}
