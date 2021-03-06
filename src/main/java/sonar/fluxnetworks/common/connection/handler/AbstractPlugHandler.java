package sonar.fluxnetworks.common.connection.handler;

import net.minecraft.util.Direction;
import sonar.fluxnetworks.api.tiles.IFluxConnector;
import sonar.fluxnetworks.common.connection.FluxNetworkServer;

public abstract class AbstractPlugHandler<C extends IFluxConnector> extends AbstractTransferHandler<C> {

    public AbstractPlugHandler(C fluxConnector) {
        super(fluxConnector);
    }

    public long getBufferLimiter() {
        return ((FluxNetworkServer) getNetwork()).bufferLimiter;
    }

    @Override
    public long getAddLimit() {
        return Math.min(getBufferLimiter() - buffer, fluxConnector.getCurrentLimit());
    }

    public abstract long addEnergy(long amount, Direction dir, boolean simulate);
}
