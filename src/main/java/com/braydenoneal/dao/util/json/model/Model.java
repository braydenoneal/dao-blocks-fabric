package com.braydenoneal.dao.util.json.model;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Model {
	public Map<String, String> textures;
	public List<Element> elements;

	public Model(Map<String, String> textures, List<Element> elements) {
		this.textures = textures;
		this.elements = elements;
	}

	public static List<List<Integer>> getShapesOf(Stream<Element> elementStream) {
		return elementStream.map(element -> Stream.concat(element.from.stream(), element.to.stream()).toList()).toList();
	}
}
