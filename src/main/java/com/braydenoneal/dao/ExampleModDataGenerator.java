package com.braydenoneal.dao;

import com.braydenoneal.dao.blockstate.Blockstate;
import com.braydenoneal.dao.blockstate.Variant;
import com.braydenoneal.dao.model.Display;
import com.braydenoneal.dao.model.Element;
import com.braydenoneal.dao.model.GUI;
import com.braydenoneal.dao.model.Model;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ExampleModDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(DaoProvider::new);
	}

	public static class DaoProvider implements DataProvider {
		private final DataOutput dataOutput;

		public DaoProvider(FabricDataOutput fabricDataOutput) {
			this.dataOutput = fabricDataOutput;
		}

		@Override
		public CompletableFuture<?> run(DataWriter writer) {
			Reader reader = new InputStreamReader(Objects.requireNonNull(DaoProvider.class.getResourceAsStream("/dao_blocks_all.json")));
			Model model = new Gson().fromJson(reader, Model.class);

			// Get list of unique element names
			List<String> elementNames = new ArrayList<>();

			for (Element element : model.elements) {
				if (!elementNames.contains(element.name)) {
					elementNames.add(element.name);
				}
			}

			// Create json file for each unique element name
			CompletableFuture<?>[] writes = elementNames.stream().map((elementName) -> {
				Model block = new Model();
				block.textures = model.textures;
				block.elements = new ArrayList<>();

				for (Element element : model.elements) {
					if (element.name.equals(elementName)) {
						block.elements.add(element);
					}
				}

				Blockstate blockstate = new Blockstate();
				blockstate.variants = new HashMap<>();
				blockstate.variants.put("facing=north", new Variant("dao:block/" + elementName, 180));
				blockstate.variants.put("facing=east", new Variant("dao:block/" + elementName, 270));
				blockstate.variants.put("facing=south", new Variant("dao:block/" + elementName, 0));
				blockstate.variants.put("facing=west", new Variant("dao:block/" + elementName, 90));

				Model blockInventory = new Model();
				blockInventory.parent = "block/block";
				blockInventory.display = new Display();
				blockInventory.display.gui = new GUI();
				blockInventory.display.gui.rotation = List.of(30, 135, 0);
				blockInventory.display.gui.translation = List.of(0, 0, 0);
				blockInventory.display.gui.scale = List.of(0.625F, 0.625F, 0.625F);
				blockInventory.display.fixed = new GUI();
				blockInventory.display.fixed.rotation = List.of(0, 90, 0);
				blockInventory.display.fixed.translation = List.of(0, 0, 0);
				blockInventory.display.fixed.scale = List.of(0.5F, 0.5F, 0.5F);
				blockInventory.textures = block.textures;
				blockInventory.elements = block.elements;

				return CompletableFuture.allOf(
						DataProvider.writeToPath(writer, new Gson().toJsonTree(block), dataOutput.getPath().resolve("assets/dao/models/block/" + elementName + ".json")),
						DataProvider.writeToPath(writer, new Gson().toJsonTree(blockInventory), dataOutput.getPath().resolve("assets/dao/models/block/" + elementName + "_inventory.json")),
						DataProvider.writeToPath(writer, JsonParser.parseString("{\"parent\": \"dao:block/" + elementName + "_inventory\"}"), dataOutput.getPath().resolve("assets/dao/models/item/" + elementName + ".json")),
						DataProvider.writeToPath(writer, new Gson().toJsonTree(blockstate), dataOutput.getPath().resolve("assets/dao/blockstates/" + elementName + ".json"))
				);
			}).toArray(CompletableFuture<?>[]::new);

			return CompletableFuture.allOf(writes);
		}

		@Override
		public String getName() {
			return "Dao Provider";
		}
	}
}
