package com.braydenoneal.dao.util.json.blockstate;

public class Variant {
	public String model;
	public int y;

	public Variant(String model, int y) {
		this.model = model;
		this.y = y;
	}

	public static Variant of(String model, int y) {
		return new Variant(model, y);
	}
}
