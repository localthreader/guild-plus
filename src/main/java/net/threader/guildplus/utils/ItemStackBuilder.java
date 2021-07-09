package net.threader.guildplus.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemStackBuilder {
    private String title;
    private Material material = Material.APPLE;
    private List<String> lore = new ArrayList<>();
    private Map<Enchantment, Integer> enchants = new HashMap<>();
    private int amount = 1;

    public static ItemStackBuilder factory() {
        return new ItemStackBuilder();
    }

    public ItemStackBuilder title(String title) {
        this.title = title;
        return this;
    }

    public ItemStackBuilder lore(String... lore) {
        this.lore = Arrays.asList(lore);
        return this;
    }

    public ItemStackBuilder lore(String lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemStackBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemStackBuilder type(Material material) {
        this.material = material;
        return this;
    }

    public ItemStackBuilder enchant(Enchantment enchant, int level) {
        this.enchants.put(enchant, level);
        return this;
    }

    public ItemStack build() {
        ItemStack stack = new ItemStack(material, amount);
        stack.addUnsafeEnchantments(enchants);
        ItemMeta meta = stack.getItemMeta();
        if(title != null) {
            meta.setDisplayName(title.replace("&", "§"));
        }
        meta.setLore(lore.stream().map(lore -> lore.replace("&", "§")).collect(Collectors.toList()));
        stack.setItemMeta(meta);
        return stack;
    }
}
