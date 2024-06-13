package x.withlithum.wingbeat.server.ide.projects;

import java.util.ArrayList;
import java.util.List;

public class IdeProjectMetadata {
    public String name;
    public List<String> domains = new ArrayList<>();

    public static final String METADATA_FILE = "project.json";
}
