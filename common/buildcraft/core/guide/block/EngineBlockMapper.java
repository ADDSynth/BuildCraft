package buildcraft.core.guide.block;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import buildcraft.api.enums.EnumEngineType;
import buildcraft.api.properties.BuildCraftProperties;

public class EngineBlockMapper implements IBlockGuidePageMapper {
    @Override
    public String getFor(World world, BlockPos pos, IBlockState state) {
        EnumEngineType type = state.getValue(BuildCraftProperties.ENGINE_TYPE);
        return "engine_" + type.unlocalizedTag;
    }

    @Override
    public List<String> getAllPossiblePages() {
        List<String> list = Lists.newArrayList();
        for (EnumEngineType type : EnumEngineType.values()) {
            list.add("engine_" + type.unlocalizedTag);
        }
        return list;
    }
}
