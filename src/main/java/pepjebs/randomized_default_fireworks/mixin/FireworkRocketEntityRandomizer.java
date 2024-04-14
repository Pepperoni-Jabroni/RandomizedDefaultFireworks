package pepjebs.randomized_default_fireworks.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pepjebs.randomized_default_fireworks.RandomizedDefaultFireworksMod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityRandomizer extends ProjectileEntity {

    @Shadow @Final
    private static TrackedData<ItemStack> ITEM;

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V",
            at = @At("TAIL")
    )
    public void randomizeItemStack(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
        if (!stack.isEmpty()) {
            this.dataTracker.set(ITEM, randomizerImpl(stack.copy()));
        }
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;)V",
            at = @At("TAIL")
    )
    public void unrandomizeIfFlying(World world, ItemStack stack, LivingEntity li, CallbackInfo ci) {
        // So if we just set the randomized firework in the fn above, but the player is flying,
        // we actually want to un-set the randomized firework
        boolean overrideShouldExplode = RandomizedDefaultFireworksMod.CONFIG != null
                && RandomizedDefaultFireworksMod.CONFIG.generateDuringElytra;
        if (!overrideShouldExplode && needsEnriching(stack) && li.isFallFlying()) {
            if (stack.hasNbt() && stack.getSubNbt("Fireworks") != null) {
                stack.getSubNbt("Fireworks").remove("Explosions");
            }
            this.dataTracker.set(ITEM, stack);
        }
    }

    private static boolean needsEnriching(ItemStack stack) {
        return !stack.hasNbt() || !stack.getNbt().contains("Fireworks")
                || !stack.getNbt().getCompound("Fireworks").contains("Explosions");
    }

    private static ItemStack randomizerImpl(ItemStack stack) {
        ItemStack copy = stack.copy();
        if (needsEnriching(stack)) {
            var topNbt = stack.getNbt();
            if (topNbt == null)
                topNbt = new NbtCompound();
            NbtCompound fireworks;
            if (topNbt.contains("Fireworks")) {
                fireworks = topNbt.getCompound("Fireworks");
            } else {
                fireworks = new NbtCompound();
            }
            fireworks.put("Explosions", generateRandomExplosions());
            topNbt.put("Fireworks", fireworks);
            copy.setNbt(topNbt);
        }
        return copy;
    }

    private static NbtList generateRandomExplosions() {
        Random random = new Random();
        NbtList list = new NbtList();
        list.add(generateRandomExplosion(random));
        if (random.nextBoolean()) {
            for (int i = 0; i < random.nextInt(2) + 1; i++) {
                list.add(generateRandomExplosion(random));
            }
        }
        return list;
    }

    private static NbtCompound generateRandomExplosion(Random random) {
        NbtCompound comp = new NbtCompound();
        // Randomize Colors
        comp.putIntArray("Colors", getRandomColors(random, 1));
        // Randomize FadeColors
        comp.putIntArray("FadeColors", getRandomColors(random, 5));
        // Randomize Type
        comp.putByte("Type", (byte) random.nextInt(5));
        // Randomize Flicker & Trail
        int randomFlickerTrail = random.nextInt(10);
        if (randomFlickerTrail == 7 || randomFlickerTrail == 9) {
            comp.putBoolean("Trail", true);
        }
        if (randomFlickerTrail == 8 || randomFlickerTrail == 9) {
            comp.putBoolean("Flicker", true);
        }
        return comp;
    }

    private static ArrayList<Integer> getRandomColors(Random random, int probChance) {
        ArrayList<Integer> colors = new ArrayList<>();
        if (random.nextInt(probChance) != 0) {
            return colors;
        }
        colors.add(getRandomDyeColor(random));
        if (random.nextInt(2) == 0) {
            return colors;
        }
        for(int i = 0; i < random.nextInt(3); i++) {
            colors.add(getRandomDyeColor(random));
        }
        return colors;
    }

    private static int getRandomDyeColor(Random random) {
        DyeColor c = null;
        String blocklistColors = RandomizedDefaultFireworksMod.DEFAULT_BLOCKLIST_COLORS;
        if (RandomizedDefaultFireworksMod.CONFIG != null) {
            blocklistColors = RandomizedDefaultFireworksMod.CONFIG.blocklistColors;
        }
        var idsToAvoid = Arrays.stream(blocklistColors.split(","))
                .map(col -> DyeColor.byName(col, DyeColor.WHITE).getId())
                .collect(Collectors.toSet());
        do {
            c = DyeColor.byId(random.nextInt(16));
        } while(idsToAvoid.contains(c.getId()));
        return c.getFireworkColor();
    }

    // Dummy
    public FireworkRocketEntityRandomizer(EntityType<? extends ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }
}
