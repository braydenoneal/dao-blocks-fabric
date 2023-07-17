package com.braydenoneal.dao;

import com.braydenoneal.dao.blocks.DaoBlock;
import com.braydenoneal.dao.model.Element;
import com.braydenoneal.dao.model.Model;
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
import java.util.ArrayList;
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

		Reader reader = new InputStreamReader(Objects.requireNonNull(ModInitializer.class.getResourceAsStream("/dao_blocks_all.json")));
		Model model = new Gson().fromJson(reader, Model.class);

		// Get list of unique element names
		List<String> elementNames = new ArrayList<>();

		for (Element element : model.elements) {
			if (!elementNames.contains(element.name)) {
				elementNames.add(element.name);
			}
		}

		// Create block for each unique element name
		for (String elementName : elementNames) {
			List<List<Integer>> element_shapes = new ArrayList<>();

			for (Element element : model.elements) {
				if (element.name.equals(elementName)) {
					element_shapes.add(Stream.concat(element.from.stream(), element.to.stream()).toList());
				}
			}

			Block block = new DaoBlock(FabricBlockSettings.create().strength(4.0f), element_shapes);
			Registry.register(Registries.BLOCK, new Identifier("dao", elementName), block);
			Registry.register(Registries.ITEM, new Identifier("dao", elementName), new BlockItem(block, new FabricItemSettings()));
			ItemGroupEvents.modifyEntriesEvent(RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("dao", "dao"))).register(entries -> entries.add(block));
		}
	}
}
