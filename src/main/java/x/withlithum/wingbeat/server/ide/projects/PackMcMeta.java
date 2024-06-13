package x.withlithum.wingbeat.server.ide.projects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public record PackMcMeta(PackMetadataSection pack) {
    public static final Codec<PackMcMeta> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    PackMetadataSection.CODEC.fieldOf("pack").forGetter(PackMcMeta::pack)
            ).apply(instance, PackMcMeta::new)
    );
}
