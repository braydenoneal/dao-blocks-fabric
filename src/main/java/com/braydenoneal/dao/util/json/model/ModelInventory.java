package com.braydenoneal.dao.util.json.model;

import java.util.List;
import java.util.Map;

public class ModelInventory extends Model {
    public String parent = "block/block";
    public Display display = new Display();

    public ModelInventory(Map<String, String> textures, List<Element> elements) {
        super(textures, elements);
    }

    public ModelInventory(Model model) {
        super(model.textures, model.elements);
    }
}
