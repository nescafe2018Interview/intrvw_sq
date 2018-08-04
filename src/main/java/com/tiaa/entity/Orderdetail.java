package com.tiaa.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({
    "orderid",
    "billamount"
})
public class Orderdetail {

    protected byte orderid;
    protected float billamount;

    /**
     * Gets the value of the orderid property.
     * 
     */
    public byte getOrderid() {
        return orderid;
    }

    /**
     * Sets the value of the orderid property.
     * 
     */
    public void setOrderid(byte value) {
        this.orderid = value;
    }

    /**
     * Gets the value of the billamount property.
     * 
     */
    public float getBillamount() {
        return billamount;
    }

    /**
     * Sets the value of the billamount property.
     * 
     */
    public void setBillamount(float value) {
        this.billamount = value;
    }

}
