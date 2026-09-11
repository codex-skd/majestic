package com.skd.majestic;

import com.skd.majestic.command.MajesticCommand;
import com.skd.majestic.content.item.MajesticItems;
import com.skd.majestic.magic.spell.MajesticSpells;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
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
        // actually creates the SPELL_TYPES/ESSENCE_MODIFIERS registries via NewRegistryEvent.
        // Calling it again here would add a second NewRegistryEvent listener that tries to create
        // the same registry keys a second time. We only need our OWN DeferredRegister tied to the
        // already-created AstralRegistries.SPELL_TYPE_KEY, which MajesticSpells.register() does.
        MajesticSpells.register(modEventBus);
        MajesticItems.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> MajesticCommand.register(event.getDispatcher()));
    }
}
