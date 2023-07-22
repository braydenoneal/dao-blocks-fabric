package com.braydenoneal.dao;

import com.braydenoneal.dao.util.json.blockstate.Blockstate;
import com.braydenoneal.dao.util.json.blockstate.Variant;
import com.braydenoneal.dao.util.json.model.Display;
import com.braydenoneal.dao.util.json.model.GUI;
import com.braydenoneal.dao.util.json.model.Model;
import com.braydenoneal.dao.util.json.model.ModelInventory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
			Reader reader = new InputStreamReader(Objects.requireNonNull(DaoProvider.class.getResourceAsStream("/assets/dao/models/block/dao_blocks_all.json")));
			Model model = new Gson().fromJson(reader, Model.class);

			// Get list of unique element names
			Stream<String> elementNames = model.elements
					.stream()
					.map(element -> element.name)
					.distinct();

			// Create json file for each unique element name
			CompletableFuture<?>[] writes = elementNames.map((elementName) -> {
				Model blockModel = new Model(
						model.textures,
						model.elements
								.stream()
								.filter(element -> element.name.equals(elementName))
								.toList()
				);

				Model blockModelInventory = new ModelInventory(blockModel);
				Blockstate blockstate = new Blockstate(elementName);

				return CompletableFuture.allOf(
						DataProvider.writeToPath(writer, new Gson().toJsonTree(blockModel), dataOutput.getPath().resolve("assets/dao/models/block/" + elementName + ".json")),
						DataProvider.writeToPath(writer, new Gson().toJsonTree(blockModelInventory), dataOutput.getPath().resolve("assets/dao/models/block/" + elementName + "_inventory.json")),
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
