package me.tangledmaze.gorgeousone.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class NMSProvider {

    private static final String version = Bukkit.getServer().getClass().getName().split("\\.")[3];

    private static final Class<?> MINECRAFT_KEY = getMCClass("net.minecraft.server.%s.MinecraftKey");
    private static final Constructor<?> MINECRAFT_KEY_CONSTRUCTOR = getConstructor(Objects.requireNonNull(MINECRAFT_KEY), String.class);

    private static final Class<?> ITEM_CLASS = getMCClass("net.minecraft.server.%s.Item");
    private static final Object ITEM_REGISTRY = getFieldInstance(Objects.requireNonNull(getDeclaredField(ITEM_CLASS, "REGISTRY")), null);

    private static final Class<?> MINECRAFT_REGISTRY = getMCClass("net.minecraft.server.%s.RegistryMaterials");
    private static final Method MINECRAFT_REGISTRY_GET = getDeclaredMethod(Objects.requireNonNull(MINECRAFT_REGISTRY), "get", Object.class);

    private static final Class<?> CRAFTITEMSTACK = getMCClass("org.bukkit.craftbukkit.%s.inventory.CraftItemStack");
    private static final Method CRAFTITEMSTACK_NEW_CRAFTSTACK = getDeclaredMethod(Objects.requireNonNull(CRAFTITEMSTACK), "asNewCraftStack", ITEM_CLASS);


    public static Material getMaterial(String mcName){
        try {
            Object mcKey = Objects.requireNonNull(MINECRAFT_KEY_CONSTRUCTOR).newInstance(mcName);
            ItemStack itemStack = (ItemStack) Objects.requireNonNull(CRAFTITEMSTACK_NEW_CRAFTSTACK).invoke(null, Objects.requireNonNull(MINECRAFT_REGISTRY_GET).invoke(ITEM_REGISTRY, mcKey));
            return itemStack.getType();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static Class<?> getMCClass(String clazz){
        try {
            return Class.forName(String.format(clazz, version));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Method getDeclaredMethod(Class<?> clazz, String name, Class<?>... parameters){
        try {
            return clazz.getDeclaredMethod(name, parameters);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Field getDeclaredField(Class<?> clazz, String name){
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Object getFieldInstance(Field field, Object handle){
        try {
            return field.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameters){
        try {
            return clazz.getConstructor(parameters);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}