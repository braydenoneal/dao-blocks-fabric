package com.braydenoneal.dao.util.json.blockstate;

import java.util.Map;

public class Blockstate {
	public Map<String, Variant> variants;

	public Blockstate(String name) {
		variants = Map.of(
				"facing=north", Variant.of("dao:block/" + name, 180),
				"facing=east", Variant.of("dao:block/" + name, 270),
				"facing=south", Variant.of("dao:block/" + name, 0),
				"facing=west", Variant.of("dao:block/" + name, 90)
		);
	}
}
