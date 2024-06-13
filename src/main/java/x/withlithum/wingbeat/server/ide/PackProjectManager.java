package x.withlithum.wingbeat.server.ide;

import com.google.gson.Gson;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import x.withlithum.wingbeat.engine.exceptions.ProjectLoadException;
import x.withlithum.wingbeat.interactive.InteractiveException;
import x.withlithum.wingbeat.server.ide.projects.IdeProject;
import x.withlithum.wingbeat.server.ide.projects.IdeProjectMetadata;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Provides data pack project management service for worlds.
 */
public class PackProjectManager {
    private final ServerLevel level;
    private static final Logger LOGGER = LoggerFactory.getLogger(PackProjectManager.class);
    private static final Gson gson = new Gson();
    private @Nullable IdeProject currentProject = null;

    /**
     * Initializes a new instance of the {@link PackProjectManager} class.
     * @param level The level to associate.
     */
    public PackProjectManager(ServerLevel level) {
        this.level = level;
    }

    /**
     * Loads the specified project and opens it. This method assists in interactive usage of the
     * {@link #loadProject(String)} method.
     * @param name The name of the project to load.
     * @throws InteractiveException Cannot load project; see exception message.
     */
    public void loadProjectInteractive(String name) throws InteractiveException {
        try {
            loadProject(name);
        } catch (ProjectLoadException x) {
            LOGGER.warn("Interactive error from creating project", x);
            throw new InteractiveException(Component.translatable(x.translateKey,
                    name));
        }
    }

    /**
     * Loads the specified project and opens it.
     * @param name The name of the project to load.
     * @throws ProjectLoadException Cannot load project; see exception message.
     */
    public void loadProject(String name) throws ProjectLoadException {
        var dataDir = level.getServer().getWorldPath(LevelResource.DATAPACK_DIR)
                .toFile();

        var packDir = new File(dataDir, name);
        if (!packDir.isDirectory()) {
            throw ProjectLoadException.notExist();
        }

        if (!IdeProject.doesAppearsToBeProject(packDir)) {
            throw ProjectLoadException.invalidStructure();
        }

        var project = IdeProject.load(packDir);
        openProject(project);
    }

    /**
     * Sets the current project as the specified project, and perform some initialization work.
     * @param project The project to set to.
     */
    public void openProject(IdeProject project) {
        currentProject = project;
        LOGGER.info("Current project set to {}", project.metadata.name);
    }

    public void saveProjectInteractive() throws InteractiveException {
        if (currentProject == null) {
            throw new InteractiveException(Component.translatable("wingbeat.commands.ide.no_open_project"));
        }

        try {
            currentProject.store();
        } catch (IOException e) {
            throw new InteractiveException(Component.translatable("wingbeat.commands.ide.save.failed"));
        }
    }

    /**
     * Removes the current project.
     */
    public void closeProject() {
        currentProject = null;
    }

    /**
     * Interactively create a new project. Any errors are wrapped in {@link InteractiveException}, which contains
     * a component that can be displayed to the end user.
     * @param id The identifier of the project to create.
     * @param displayName The display name of the pack. Optional. If omitted, {@link Component#literal(String)} with the ID is used.
     * @throws InteractiveException Thrown when interactive error have occurred.
     */
    public void newProjectInteractive(String id, @Nullable Component displayName) throws InteractiveException {
        try {
            newProject(id, displayName);
        } catch (IOException x) {
            LOGGER.warn("Interactive error from creating project", x);
            throw new InteractiveException(Component.translatable(x.getMessage(),
                    id));
        }
    }

    /**
     * Create a new project.
     * @param id The identifier of the project to create.
     * @param displayName The display name of the pack. Optional. If omitted, {@link Component#literal(String)} with the ID is used.
     * @throws IOException Thrown when file error have occurred. Check exception message.
     */
    public void newProject(String id, @Nullable Component displayName) throws IOException {
        var dataDir = level.getServer().getWorldPath(LevelResource.DATAPACK_DIR)
                .toFile();

        var child = new File(dataDir, id);
        if (child.exists()) {
            throw new IOException("wingbeat.commands.ide.pack_exists");
        }

        if (!child.mkdir()) {
            throw new IOException("wingbeat.commands.ide.pack_unable_to_mkdir");
        }

        // Create project metadata
        var projectMeta = new IdeProjectMetadata();
        projectMeta.name = id;

        // Configure display name
        var displayComponent = displayName;
        if (displayComponent == null) {
            displayComponent = Component.literal(id);
        }

        // Create pack metadata
        var packMeta = new PackMetadataSection(displayComponent,
                SharedConstants.getCurrentVersion().getPackVersion(PackType.SERVER_DATA),
                Optional.empty()
                );

        // Create project file
        var project = new IdeProject(child, projectMeta, packMeta);
        project.store();
        openProject(project);
    }

    public @Nullable IdeProject getCurrentProject() {
        return currentProject;
    }
}
