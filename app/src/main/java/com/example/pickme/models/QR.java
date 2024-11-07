package com.example.pickme.models;

/**
 * Represents a QR code requested/created by the user
 * @author Cole Mckay
 * @version 1.1
 */
public class QR {

    /* QR Attributes */

    // the unique qr id; auto-generated by Firebase [non-nullable]
    private String qrId;
    // references the associated entity id that the QR is for (event, waiting list) [non-nullable]
    private String qrAssociation;

    public QR() {
        // Default constructor
    }

    public QR(String qrAssociation) {
        this.qrAssociation = qrAssociation;
    }

    // Setter and getter for qrId
    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    public String getQrId() {
        return qrId;
    }

    // Getter for qrAssociation
    public String getQrAssociation() {
        return qrAssociation;
    }
}
