package org.java.megalib.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Module {

    private String name;

    private List<Block> blocks;

    public Module(String name){
        this.name = name;
        blocks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addBlock(Block b) {
        blocks.add(b);
    }

    public List<Block> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }
}
