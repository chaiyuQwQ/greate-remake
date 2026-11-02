package electrolyte.greate.registry;

import com.gregtechceu.gtceu.api.tag.TagPrefix;
import electrolyte.greate.Greate;

import static com.gregtechceu.gtceu.api.GTValues.M;

public final class GreateTagPrefixes {

    public static final TagPrefix shaft = new TagPrefix("shaft")
            .defaultTagPath("shafts/%s")
            .unformattedTagPath("shafts")
            .materialAmount(M / 6)
            .unificationEnabled(true)
            .enableRecycling()
            .langValue("%s Shaft")
            .itemTable(() -> Shafts.TABLE);

    public static final TagPrefix cogwheel = new TagPrefix("cogwheel")
            .defaultTagPath("cogwheels/%s")
            .unformattedTagPath("cogwheels")
            .materialAmount(M / 4)
            .unificationEnabled(true)
            .enableRecycling()
            .langValue("%s Cogwheel")
            .itemTable(() -> Cogwheels.SMALL_TABLE);

    public static final TagPrefix beltConnector = new TagPrefix("belt_connector")
            .defaultTagPath("belt_connectors/%s")
            .unformattedTagPath("belt_connectors")
            .materialAmount(M * 6)
            .unificationEnabled(true)
            .enableRecycling()
            .langValue("%s Belt Connector")
            .itemTable(() -> Belts.BELT_CONNECTORS);

    public static final TagPrefix largeCogwheel = new TagPrefix("large_cogwheel")
            .defaultTagPath("large_cogwheels/%s")
            .unformattedTagPath("large_cogwheels")
            .materialAmount(M / 4)
            .unificationEnabled(true)
            .enableRecycling()
            .langValue("Large %s Cogwheel")
            .itemTable(() -> Cogwheels.LARGE_TABLE);

    public static void init() {
        Greate.LOGGER.info("[Greate] TagPrefix 初始化: shaft / cogwheel / large_cogwheel / belt_connector"
                + "（由 GT 构造器自动注册到 gtceu: 命名空间）");
    }

    private GreateTagPrefixes() {
    }
}
