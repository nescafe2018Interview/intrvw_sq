package com.tiaa.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
    "cmfoodchain"
})
public class Wrapper {
	
	@JsonProperty("cmfoodchain")
	private List<Cmfoodchain> cmfoodchain;

	public List<Cmfoodchain> getCmfoodchain() {
		return cmfoodchain;
	}

	public void setCmfoodchain(List<Cmfoodchain> cmfoodchain) {
		this.cmfoodchain = cmfoodchain;
	}

}
