package x.withlithum.wingbeat.engine.exceptions;

public class ProjectLoadException extends Exception {
    public ProjectLoadException(String message, String translateKey) {
        super(message);
        this.translateKey = translateKey;
    }

    public ProjectLoadException(String message, String translateKey, Throwable cause) {
        super(message, cause);
        this.translateKey = translateKey;
    }

    public final String translateKey;

    public static ProjectLoadException parseProjectFailure(Throwable cause) {
        return new ProjectLoadException("Unable to parse project files",
                "wingbeat.commands.ide.unable_to_parse_project",
                cause);
    }

    public static ProjectLoadException notExist() {
        return new ProjectLoadException("The specified project does not exist.",
                "wingbeat.commands.ide.pack_not_exist");
    }

    public static ProjectLoadException invalidStructure() {
        return new ProjectLoadException("The specified directory is either invalid or not a project.",
                "wingbeat.commands.ide.invalid_project");
    }
}
