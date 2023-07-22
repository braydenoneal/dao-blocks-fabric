package com.braydenoneal.dao;

import com.braydenoneal.dao.blocks.DaoBlock;
import com.braydenoneal.dao.util.json.model.Model;
import com.google.gson.Gson;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ExampleMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("dao");

	private static final ItemGroup ITEM_GROUP = FabricItemGroup.builder()
			.icon(() -> new ItemStack(Items.DARK_OAK_LOG))
			.displayName(Text.translatable("itemGroup.dao.dao"))
			.build();

	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM_GROUP, new Identifier("dao", "dao"), ITEM_GROUP);

		Reader reader = new InputStreamReader(Objects.requireNonNull(ModInitializer.class.getResourceAsStream("/assets/dao/models/block/dao_blocks_all.json")));
		Model model = new Gson().fromJson(reader, Model.class);

		// Get list of unique element names
		Stream<String> elementNames = model.elements
				.stream()
				.map(element -> element.name)
				.distinct();

		// Create block for each unique element name
		elementNames.forEach(elementName -> {
			List<List<Integer>> elementShapes = model.elements
					.stream()
					.filter(element -> element.name.equals(elementName))
					.map(element -> Stream.concat(element.from.stream(), element.to.stream()).toList())
					.toList();

			Block block = new DaoBlock(FabricBlockSettings.create().strength(4.0f), elementShapes);
			Registry.register(Registries.BLOCK, new Identifier("dao", elementName), block);
			Registry.register(Registries.ITEM, new Identifier("dao", elementName), new BlockItem(block, new FabricItemSettings()));
			ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("dao", "dao"))).register(entries -> entries.add(block));
		});
	}
}
