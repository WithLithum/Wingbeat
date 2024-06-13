package x.withlithum.wingbeat.server.ide.projects;

import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x.withlithum.wingbeat.engine.exceptions.ProjectLoadException;
import x.withlithum.wingbeat.util.JsonFiles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class IdeProject {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdeProject.class);

    public IdeProject(File directory, IdeProjectMetadata metadata, PackMetadataSection packMetadata) {
        this.directory = directory;
        this.metadata = metadata;
        this.packMetadata = packMetadata;

        domains = new ArrayList<>(metadata.domains.size());
    }

    public final File directory;
    public final IdeProjectMetadata metadata;
    public final PackMetadataSection packMetadata;
    public final List<String> domains;

    /**
     * Determines whether the specified directory appears to have a correct file structure
     * for a project.
     * @param directory The directory to check.
     * @return {@code true} if the directory appears to have a correct file structure for a project; otherwise {@code false}.
     */
    public static boolean doesAppearsToBeProject(File directory) {
        var packFile = new File(directory, PackResources.PACK_META);
        var projectFile = new File(directory, IdeProjectMetadata.METADATA_FILE);

        return packFile.isFile() || projectFile.isFile();
    }

    public static IdeProject load(File directory) throws ProjectLoadException {
        var packFile = new File(directory, PackResources.PACK_META);
        var projectFile = new File(directory, IdeProjectMetadata.METADATA_FILE);

        if (!packFile.isFile() || !projectFile.isFile()) {
            throw ProjectLoadException.invalidStructure();
        }

        // Load pack file and project file
        try {
            var packMeta = JsonFiles.readCodecObject(PackMcMeta.CODEC, packFile);
            var projectMeta = JsonFiles.readJsonObject(projectFile, IdeProjectMetadata.class);

            var result = new IdeProject(directory, projectMeta, packMeta.pack());
            result.loadDomains();
            return result;
        } catch (IOException e) {
            throw ProjectLoadException.parseProjectFailure(e);
        }
    }

    public void store() throws IOException {
        var packFile = new File(directory, PackResources.PACK_META);
        var projectFile = new File(directory, IdeProjectMetadata.METADATA_FILE);

        updateMetadataDomains();

        // Store pack.mcmeta
        var mcmeta = new PackMcMeta(packMetadata);
        JsonFiles.writeCodecObject(mcmeta, PackMcMeta.CODEC, packFile);

        // Store project information
        JsonFiles.writeJsonObject(metadata, projectFile);
    }

    /**
     * Updates the {@link IdeProjectMetadata#domains} in the metadata.
     */
    public void updateMetadataDomains() {
        metadata.domains.clear();
        metadata.domains.addAll(domains);
    }

    /**
     * Discards all changes, and reload & set-up all domains included in the metadata.
     */
    public void loadDomains() {
        domains.clear();
        for (var domain : metadata.domains) {
            var domainDir = new File(directory, domain);
            if (domainDir.isDirectory()) {
                domains.add(domain);
                continue;
            }

            if (domainDir.isFile()) {
                // Invalid domain
                LOGGER.warn("Domain {} have a file in its place", domain);
                continue;
            }

            if (!domainDir.mkdir()) {
                // Invalid domain
                LOGGER.warn("Unable to create directory for domain {}", domain);
            }
        }
    }
}
