package fluxnetworks.common.connection;

import com.google.common.collect.Lists;
import fluxnetworks.FluxNetworks;
import fluxnetworks.api.energy.ITileEnergyHandler;
import fluxnetworks.api.network.IFluxTransfer;
import fluxnetworks.api.tileentity.IFluxConnector;
import fluxnetworks.common.handler.TileEntityHandler;
import fluxnetworks.common.tileentity.TileFluxCore;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flux connector(point or plug) transfer handler
 */
public class ConnectionTransferHandler extends FluxTransferHandler {

    public final TileFluxCore tileEntity;
    public Map<EnumFacing, IFluxTransfer> transfers = new HashMap<>();

    public ConnectionTransferHandler(TileFluxCore tile, IFluxConnector fluxConnector) {
        super(fluxConnector);
        this.tileEntity = tile;
    }

    {
        for(EnumFacing facing : EnumFacing.VALUES) {
            transfers.put(facing, null);
        }
    }

    @Override
    public void onServerStartTick() {
        super.onServerStartTick();
        transfers.entrySet().stream().filter(e -> e.getValue() != null).forEach(e -> e.getValue().onServerStartTick());
    }

    @Override
    public void onWorldEndTick() {
        super.onWorldEndTick();
    }

    @Override
    public void updateTransfers(EnumFacing... faces) {
        for(EnumFacing facing : faces) {
            TileEntity tile = tileEntity.getWorld().getTileEntity(tileEntity.getPos().offset(facing));
            setTransfer(facing, tile);
        }
    }

    public void setTransfer(EnumFacing side, TileEntity tileEntity) {
        IFluxTransfer transfer = transfers.get(side);
        ITileEnergyHandler handler;
        if(tileEntity == null || (handler = TileEntityHandler.getEnergyHandler(tileEntity, side.getOpposite())) == null) {
            transfers.put(side, null);
        } else if (transfer == null || transfer.getTile() != tileEntity) {
            transfers.put(side, new ConnectionTransfer(this, handler, tileEntity, side));
        } else if (transfer.isInvalid()) {
            transfers.put(side, null);
        }
    }

    @Override
    public List<IFluxTransfer> getTransfers() {
        return Lists.newArrayList(transfers.values());
    }

    public long addPhantomEnergyToNetwork(long amount, EnumFacing side, boolean simulate) {
        if(getNetwork().isInvalid()) {
            return 0;
        }
        IFluxTransfer transfer = transfers.get(side);
        if(transfer != null) {
            long a = addToBuffer(amount, simulate);
            if(!simulate) {
                transfer.addedToNetwork(a);
            }
            return a;
        }
        return 0;
    }

}
