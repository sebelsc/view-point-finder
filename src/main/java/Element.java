import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Element {
    private final long id;
    private final double height;
    private final Set<Long> nodeIds;

    public Element(final long id, final double height) {
        this.id = id;
        this.height = height;
        nodeIds = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public double getHeight() {
        return height;
    }

    public void addMultipleNodeIds(final List<Long> nodeIdList) {
        this.nodeIds.addAll(nodeIdList);
    }

    public Set<Long> getNodeIds() {
        return Collections.unmodifiableSet(nodeIds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Element element)) {
            return false;
        }
        return getId() == element.getId() && Double.compare(element.getHeight(), getHeight()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, getHeight());
    }

    @Override
    public String toString() {
        return "Element{" + "id=" + id + ", height=" + height + ", nodeIds=" + nodeIds + '}';
    }
}
