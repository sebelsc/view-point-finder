import jakarta.json.bind.annotation.JsonbProperty;

public class ViewPoint {
    @JsonbProperty("element_id")
    private final long elementId;

    @JsonbProperty("value")
    private final double height;

    public ViewPoint(final Element element) {
        this.elementId = element.getId();
        this.height = element.getHeight();
    }

    public long getElementId() {
        return elementId;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "ViewPoint{" + "elementId=" + elementId + ", height=" + height + '}';
    }
}
