package com.skd.majestic;

import com.skd.majestic.command.MajesticCommand;
import com.skd.majestic.content.MajesticCreativeTabs;
import com.skd.majestic.content.block.MajesticBlocks;
import com.skd.majestic.content.component.MajesticDataComponents;
import com.skd.majestic.content.MajesticEffects;
import com.skd.majestic.content.entity.MajesticEntities;
import com.skd.majestic.content.event.JourneyDropEvents;
import com.skd.majestic.content.event.LanternBearerTrigger;
import com.skd.majestic.content.event.MajesticEvents;
import com.skd.majestic.content.event.VaultChestEvents;
import com.skd.majestic.content.worldgen.StructureKeys;
import com.skd.majestic.content.event.ObservatoryArenas;
import com.skd.majestic.content.event.ShrineUnlockEvents;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majestic.magic.ritual.MajesticRituals;
import com.skd.majestic.magic.spell.MajesticSpells;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(Majestic.MOD_ID)
public final class Majestic {

    public static final String MOD_ID = "majestic";

    public static final Logger LOGGER = LoggerFactory.getLogger("Majestic");

    public Majestic(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Majestic v{} loading", modContainer.getModInfo().getVersion());

        // NOTE: AstralRegistries.register(modEventBus) is NOT called here — astral_core's own
        // AstralCore constructor already registers it on astral_core's own mod bus, which is what
        // actually creates the SPELL_TYPES/RITUAL_TYPES/etc. registries via NewRegistryEvent.
        // Calling it again here would add a second NewRegistryEvent listener that tries to create
        // the same registry keys a second time. We only need our OWN DeferredRegisters tied to the
        // already-created AstralRegistries.*_KEY, which MajesticSpells/MajesticRituals.register() do.
        MajesticSpells.register(modEventBus);
        MajesticItems.register(modEventBus);
        MajesticBlocks.register(modEventBus);
        MajesticDataComponents.register(modEventBus);
        MajesticEffects.register(modEventBus);
        MajesticRituals.register(modEventBus);
        MajesticEntities.register(modEventBus);
        MajesticCreativeTabs.register(modEventBus);

        // Generated Fallen Shrines and Observatories start locked (no break/place/fluids) until
        // their seal is lifted: opening a shrine chest or defeating the Observatory's Warden.
        // Declared per structure in StructureKeys.ALL, so adding one is an entry, not code here.
        StructureKeys.registerProtection();

        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> MajesticCommand.register(event.getDispatcher()));
        NeoForge.EVENT_BUS.addListener(com.skd.astralcore.event.NodeUnlockedEvent.class, MajesticEvents::onNodeUnlocked);
        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickBlock.class, ShrineUnlockEvents::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(PlayerInteractEvent.RightClickBlock.class, VaultChestEvents::onRightClickBlock);
        NeoForge.EVENT_BUS.addListener(PlayerEvent.PlayerLoggedInEvent.class, MajesticEvents::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post.class,
                LanternBearerTrigger::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(net.neoforged.neoforge.event.entity.living.LivingDropsEvent.class,
                JourneyDropEvents::onLivingDrops);
    }
}
