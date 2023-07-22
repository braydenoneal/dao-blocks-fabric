package com.braydenoneal.dao.util.json.model;

public class ModelInventory extends Model {
    public String parent = "block/block";
    public Display display = new Display();

    public ModelInventory(Model model) {
        super(model.textures, model.elements);
    }
}
