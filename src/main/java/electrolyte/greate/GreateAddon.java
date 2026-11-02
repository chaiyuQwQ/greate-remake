package electrolyte.greate;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

@GTAddon(Greate.MOD_ID)
public class GreateAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return Greate.GT_REGISTRATE;
    }

    @Override
    public void gtInitComplete() {
        Greate.LOGGER.info("[Greate] GT addon 初始化完成");
    }
}
