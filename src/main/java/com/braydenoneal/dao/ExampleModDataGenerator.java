package com.braydenoneal.dao;

import com.braydenoneal.dao.util.CompletableFutureStream;
import com.braydenoneal.dao.util.json.JsonWriter;
import com.braydenoneal.dao.util.json.blockstate.Blockstate;
import com.braydenoneal.dao.util.json.model.Model;
import com.braydenoneal.dao.util.json.model.ModelInventory;
import com.braydenoneal.dao.util.json.model.ModelItem;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import java.io.InputStreamReader;
import java.io.Reader;
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
			return CompletableFutureStream.of(elementNames.map(elementName -> {
				Model blockModel = new Model(
						model.textures,
						model.elements
								.stream()
								.filter(element -> element.name.equals(elementName))
								.toList()
				);

				Model blockModelInventory = new ModelInventory(blockModel);
				Blockstate blockstate = new Blockstate(elementName);
				ModelItem blockModelItem = new ModelItem("dao:block/" + elementName + "_inventory");

				return JsonWriter.create(writer, dataOutput).write(
						JsonWriter.entry(blockModel, "assets/dao/models/block/", elementName),
						JsonWriter.entry(blockModelInventory, "assets/dao/models/block/", elementName + "_inventory"),
						JsonWriter.entry(blockModelItem, "assets/dao/models/item/", elementName),
						JsonWriter.entry(blockstate, "assets/dao/blockstates/", elementName)
				);
			}));
		}

		@Override
		public String getName() {
			return "Dao Provider";
		}
	}
}
