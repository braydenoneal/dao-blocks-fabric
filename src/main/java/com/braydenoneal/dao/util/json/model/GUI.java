package com.braydenoneal.dao.util.json.model;

import java.util.List;

public class GUI {
	public List<Integer> rotation;
	public List<Integer> translation;
	public List<Float> scale;

	public GUI(List<Integer> rotation, List<Integer> translation, List<Float> scale) {
		this.rotation = rotation;
		this.translation = translation;
		this.scale = scale;
	}
}
