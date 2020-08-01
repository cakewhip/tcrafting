package com.kqp.tcrafting.api;

import net.minecraft.util.Identifier;

import java.util.HashSet;

public class TRecipeInterface extends HashSet<Identifier> {
    public TRecipeInterface(Identifier... types) {
        super();

        for (Identifier type : types) {
            this.add(type);
        }
    }
}