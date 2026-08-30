package com.phuc.cobbledollarsminigames.mixin;

import fr.harmex.cobbledollars.common.utils.extensions.PlayerExtensionKt;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "com.barto.cobblemonminigames.arcade.economy.CoinEconomy")
public abstract class CoinEconomyHooks {
    private static final BigInteger DOLLARS_PER_MINIGAMES_COIN = BigInteger.valueOf(50L);
    private static final String ARCADE_COIN_ID = "cobblemonminigames:arcade_coin";

    @Shadow
    protected abstract long totalArcadeCoins(class_3222 player);

    @Shadow
    protected abstract Iterable<class_1799> coinCaseStacks(class_3222 player);

    @Shadow
    protected abstract boolean isCoinCase(class_1799 stack);

    @Shadow
    protected abstract int countStoredCoins(class_1799 stack);

    @Shadow
    protected abstract void setStoredCoins(class_1799 stack, int amount);

    private long countLegacyCoins(class_3222 player) {
        return totalArcadeCoins(player);
    }

    private boolean consumeLegacyCoins(class_3222 player, long amount) {
        long remaining = amount;
        for (class_1799 stack : coinCaseStacks(player)) {
            if (remaining <= 0L || !isCoinCase(stack)) {
                continue;
            }

            int stored = countStoredCoins(stack);
            int taken = (int) Math.min((long) stored, remaining);
            setStoredCoins(stack, stored - taken);
            remaining -= taken;
        }

        if (remaining > 0L) {
            for (Object stack : inventoryStacks(player)) {
                if (remaining <= 0L || !isArcadeCoin(stack)) {
                    continue;
                }

                int available = (int) invoke(stack, "method_7947");
                int taken = (int) Math.min((long) available, remaining);
                invoke(stack, "method_7934", taken);
                remaining -= taken;
            }
        }

        return remaining == 0L;
    }

    private List<Object> inventoryStacks(class_3222 player) {
        Object inventory = invoke(player, "method_31548");
        try {
            Field main = inventory.getClass().getField("field_7547");
            Field offhand = inventory.getClass().getField("field_7544");
            List<Object> stacks = new ArrayList<>();

            for (Object stack : (Iterable<?>) main.get(inventory)) {
                stacks.add(stack);
            }
            for (Object stack : (Iterable<?>) offhand.get(inventory)) {
                stacks.add(stack);
            }
            return stacks;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to inspect player inventory", exception);
        }
    }

    private boolean isArcadeCoin(Object stack) {
        if ((boolean) invoke(stack, "method_7960")) {
            return false;
        }

        Object item = invoke(stack, "method_7909");
        try {
            Class<?> registries = Class.forName("net.minecraft.class_2378");
            Object itemRegistry = registries.getField("field_11146").get(null);
            Object identifier = itemRegistry.getClass().getMethod("method_10221", Object.class).invoke(itemRegistry, item);
            return String.valueOf(identifier).equals(ARCADE_COIN_ID);
        } catch (ReflectiveOperationException exception) {
            return false;
        }
    }

    private Object invoke(Object target, String methodName, Object... arguments) {
        for (Method method : target.getClass().getMethods()) {
            if (method.getName().equals(methodName) && method.getParameterCount() == arguments.length) {
                try {
                    return method.invoke(target, arguments);
                } catch (ReflectiveOperationException exception) {
                    throw new IllegalStateException("Unable to call " + methodName, exception);
                }
            }
        }
        throw new IllegalStateException("Missing Minecraft method " + methodName);
    }

    @Overwrite(remap = false)
    public long countArcadeCoins(class_3222 player) {
        long dollarsAsCoins = PlayerExtensionKt.getCobbleDollars(player)
            .max(BigInteger.ZERO)
            .divide(DOLLARS_PER_MINIGAMES_COIN)
            .longValue();
        return dollarsAsCoins + countLegacyCoins(player);
    }

    @Overwrite(remap = false)
    public boolean consumeArcadeCoins(class_3222 player, long amount) {
        if (amount <= 0L) {
            return true;
        }

        long legacyCoins = countLegacyCoins(player);
        BigInteger balance = PlayerExtensionKt.getCobbleDollars(player).max(BigInteger.ZERO);
        long dollarCoins = balance.divide(DOLLARS_PER_MINIGAMES_COIN).longValue();
        if (dollarCoins + legacyCoins < amount) {
            return false;
        }

        long dollarsToSpend = Math.min(amount, dollarCoins);
        BigInteger remainder = balance.subtract(BigInteger.valueOf(dollarsToSpend).multiply(DOLLARS_PER_MINIGAMES_COIN));
        PlayerExtensionKt.setCobbleDollars(player, remainder);
        return dollarsToSpend == amount || consumeLegacyCoins(player, amount - dollarsToSpend);
    }

    @Overwrite(remap = false)
    public void giveArcadeCoins(class_3222 player, long amount) {
        if (amount <= 0L) {
            return;
        }

        BigInteger balance = PlayerExtensionKt.getCobbleDollars(player);
        BigInteger reward = BigInteger.valueOf(amount).multiply(DOLLARS_PER_MINIGAMES_COIN);
        PlayerExtensionKt.setCobbleDollars(player, balance.add(reward));
    }
}