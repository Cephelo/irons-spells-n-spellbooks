package io.redspace.ironsspellbooks.effect;

import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.network.SyncManaPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class InstantManaEffect extends CustomDescriptionMobEffect {
    public static final int manaPerAmplifier = 25;
    public static final float manaPerAmplifierPercent = .05f;

    public InstantManaEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public Component getDescriptionLine(MobEffectInstance instance) {
        int amp = instance.getAmplifier() + 1;
        int addition = amp * InstantManaEffect.manaPerAmplifier;
        int percent = (int) (amp * InstantManaEffect.manaPerAmplifierPercent * 100);
        return Component.translatable("tooltip.irons_spellbooks.instant_mana_description", addition, percent).withStyle(ChatFormatting.BLUE);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }

    @Override
    public void applyInstantenousEffect(@Nullable Entity pSource, @Nullable Entity pIndirectSource, LivingEntity livingEntity, int pAmplifier, double pHealth) {
        //IronsSpellbooks.LOGGER.debug("Instant mana applying effect");
        int i = pAmplifier + 1;
        int maxMana = (int) livingEntity.getAttributeValue(AttributeRegistry.MAX_MANA);
        int manaAdd = (int) (i * manaPerAmplifier + (maxMana * (i * manaPerAmplifierPercent)));
        MagicData pmg = MagicData.getPlayerMagicData(livingEntity);
        //IronsSpellbooks.LOGGER.debug("old mana: {}", pmg.getMana());
        pmg.setMana(pmg.getMana() + manaAdd);
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new SyncManaPacket(pmg));
        }
        //IronsSpellbooks.LOGGER.debug("new mana: {}", pmg.getMana());

    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity livingEntity, int pAmplifier) {
        applyInstantenousEffect(null, null, livingEntity, pAmplifier, livingEntity.getHealth());
        return true;
    }
}
