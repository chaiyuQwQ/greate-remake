package electrolyte.greate.content.kinetics.fan;

import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;

/**
 * Hides processing types the fan's tier cannot run: {@code getTypeAt} returns {@code null},
 * so Create skips the recipe, the particles and the handler registration alike.
 */
public class TieredAirCurrent extends AirCurrent {

    private final int tier;

    public TieredAirCurrent(IAirCurrentSource source, int tier) {
        super(source);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }

    @Override
    public FanProcessingType getTypeAt(float offset) {
        FanProcessingType type = super.getTypeAt(offset);
        if (type == null || !TieredFanProcessing.typeEnabled(type, tier)) {
            return null;
        }
        return type;
    }
}
