package com.braydenoneal.dao.util.json.model;

import java.util.List;

public class Display {
	public GUI gui;
	public GUI fixed;

	public Display() {
		this.gui = new GUI(
				List.of(30, 135, 0),
				List.of(0, 0, 0),
				List.of(0.625F, 0.625F, 0.625F)
		);
		this.fixed = new GUI(
				List.of(0, 90, 0),
				List.of(0, 0, 0),
				List.of(0.5F, 0.5F, 0.5F)
		);
	}
}
