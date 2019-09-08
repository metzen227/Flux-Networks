package fluxnetworks.client.gui.basic;

import fluxnetworks.client.gui.GuiFluxHome;
import fluxnetworks.common.registry.RegistrySounds;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.io.IOException;

public abstract class GuiTabCore extends GuiFluxCore {

    public GuiTabCore(EntityPlayer player, TileFluxCore tileEntity) {
        super(player, tileEntity);
    }

    @Override
    protected void keyTypedMain(char c, int k) throws IOException {
        super.keyTypedMain(c, k);
        if (k == 1 || this.mc.gameSettings.keyBindInventory.isActiveAndMatches(k)) {
            if(!textBoxes.stream().anyMatch(GuiTextField::isFocused)) {
                FMLCommonHandler.instance().showGuiScreen(new GuiFluxHome(player, tileEntity));
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(RegistrySounds.BUTTON_CLICK, 1.0F));
            }
        }
    }
}
