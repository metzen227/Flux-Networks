package sonar.fluxnetworks.client.gui;

import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.network.EnumAccessType;
import sonar.fluxnetworks.api.gui.EnumNavigationTabs;
import sonar.fluxnetworks.api.network.INetworkConnector;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;
import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.connection.FluxNetworkCache;
import sonar.fluxnetworks.api.network.NetworkSettings;
import sonar.fluxnetworks.api.utils.NBTType;
import sonar.fluxnetworks.common.handler.PacketHandler;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.common.network.SuperAdminRequestPacket;
import sonar.fluxnetworks.common.network.NetworkUpdateRequestPacket;
import sonar.fluxnetworks.common.network.GUIPermissionRequestPacket;

public class GuiFluxAdminHome extends GuiTabCore {

    public InvisibleButton redirectButton;

    private int timer;
    public SlidedSwitchButton detailed_network_view, super_admin;

    public GuiFluxAdminHome(PlayerEntity player, INetworkConnector connector) {
        super(player, connector);
    }

    public EnumNavigationTabs getNavigationTab(){
        return EnumNavigationTabs.TAB_HOME;
    }

    @Override
    protected void drawForegroundLayer(int mouseX, int mouseY) {
        super.drawForegroundLayer(mouseX, mouseY);
        screenUtils.renderNetwork(network.getSetting(NetworkSettings.NETWORK_NAME), network.getSetting(NetworkSettings.NETWORK_COLOR), 20, 8);
        drawCenteredString(font, TextFormatting.RED + FluxNetworks.PROXY.getFeedback(false).getInfo(), 89, 150, 0xffffff);

        font.drawString(EnumAccessType.SUPER_ADMIN.getName(), 20, 30, network.getSetting(NetworkSettings.NETWORK_COLOR));
        font.drawString("Detailed Network View", 20, 42, network.getSetting(NetworkSettings.NETWORK_COLOR));
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTabs.TAB_HOME, navigationTabs);

        redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 8, 135, 12, EnumNavigationTabs.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTabs.TAB_SELECTION, player, connector));
        addButton(redirectButton);

        super_admin = new SlidedSwitchButton(140, 30, 0, guiLeft, guiTop, FluxNetworkCache.INSTANCE.superAdminClient);
        switches.add(super_admin);

        detailed_network_view = new SlidedSwitchButton(140, 42, 1, guiLeft, guiTop, FluxNetworks.PROXY.getDetailedNetworkView());
        switches.add(detailed_network_view);
    }


    public void onSuperAdminChanged(){
        super.onSuperAdminChanged();
        super_admin.slideControl = FluxNetworkCache.INSTANCE.superAdminClient;
    }

    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton){
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if(mouseButton == 0 && button instanceof SlidedSwitchButton) {
            SlidedSwitchButton switchButton = (SlidedSwitchButton) button;
            switchButton.switchButton();
            switch (switchButton.id) {
                case 0:
                    PacketHandler.INSTANCE.sendToServer(new SuperAdminRequestPacket());
                    break;
                case 1:
                    FluxNetworks.PROXY.setDetailedNetworkView(switchButton.slideControl);
                    break;
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if(timer == 0) {
            PacketHandler.INSTANCE.sendToServer(new NetworkUpdateRequestPacket(network.getNetworkID(), NBTType.NETWORK_GENERAL));
            PacketHandler.INSTANCE.sendToServer(new GUIPermissionRequestPacket(network.getNetworkID(), player.getUniqueID()));
        }
        timer++;
        timer %= 100;
    }

}
