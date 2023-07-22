package com.braydenoneal.dao.util.json.model;

import java.util.List;
import java.util.Map;

public class Model {
	public Map<String, String> textures;
	public List<Element> elements;

	public Model(Map<String, String> textures, List<Element> elements) {
		this.textures = textures;
		this.elements = elements;
	}
}
