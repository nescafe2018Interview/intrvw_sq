
package com.tiaa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@XmlAccessorType(XmlAccessType.FIELD)
@JsonPropertyOrder({
    "orderdetail"
})
public class Orders {

	protected List<Orderdetail> orderdetail;

	public List<Orderdetail> getOrderdetail() {
		if (orderdetail == null) {
			orderdetail = new ArrayList<Orderdetail>();
		}
		return this.orderdetail;
	}

	public void setOrderdetail(List<Orderdetail> orderdetail) {
		this.orderdetail = orderdetail;
	}

}
