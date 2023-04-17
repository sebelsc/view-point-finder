import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class ViewPointService {

    /**
     * Processes the mesh.json file from the path provided by {@link InputParameter} and tries to find
     * the maximum numberOfViewPoints requested.
     *
     * @param inputParameter {@link InputParameter} that specifies the filePath to evaluate
     *                       and the maximum numberOfViewPoints to find
     *
     * @return List of found {@link ViewPoint}
     */
    public List<ViewPoint> findViewPoints(InputParameter inputParameter) {
        final Map<Long, Element> elementLookupMap;
        final Map<Long, Set<Element>> nodeNeighborLookupMap;

        try (BufferedReader jsonStream = Files.newBufferedReader(inputParameter.filePath());
            JsonReader rdr = Json.createReader(jsonStream)) {

            final JsonObject obj = rdr.readObject();
            final JsonArray elements = obj.getJsonArray("elements");
            final JsonArray heightValues = obj.getJsonArray("values");
            final JsonArray nodes = obj.getJsonArray("nodes");

            elementLookupMap = new LinkedHashMap<>(elements.size());
            nodeNeighborLookupMap = new HashMap<>(nodes.size());

            processValues(heightValues, elementLookupMap);
            processElements(elements, elementLookupMap, nodeNeighborLookupMap);

            return getViewPoints(
                inputParameter.numberOfViewPoints(), elementLookupMap, nodeNeighborLookupMap);

        }
        catch (FileNotFoundException e) {
            System.err.println(
                "The mesh json file could not be found. Provided path "
                    + inputParameter.filePath()
                    + "Error Message:\n"
                    + e.getMessage());
        }
        catch (IOException e) {
            System.err.println(
                "An IO Exception occurred during file processing.\n "
                    + "Error message:\n"
                    + e.getMessage());
        }

        return null;
    }

    /**
     * Populates the elementLookupMap with elementId as key and creates an {@link Element} object as value.
     *
     * @param values           {@link JsonArray} of value JSON objects that include elementId and the
     *                         corresponding height value
     * @param elementLookupMap Map that returns an {@link Element} object for an elementId
     */
    private void processValues(final JsonArray values, final Map<Long, Element> elementLookupMap) {
        values.stream()
              .map(JsonValue::asJsonObject)
              .sorted(
                  Comparator.comparing(
                      v -> v.getJsonNumber("value").doubleValue(), Comparator.reverseOrder()))
              .forEach(
                  e -> {
                      final double height = e.getJsonNumber("value").doubleValue();
                      final long elementId = e.getJsonNumber("element_id").longValue();

                      elementLookupMap.put(elementId, new Element(elementId, height));
                  });
    }

    /**
     * Processes the elements JSON data to populate the nodeNeighborLookupMap and
     * add the nodeIds to the {@link Element} values in the elementLookupMap.
     *
     * @param elements              {@link JsonArray} of elements JSON objects that include elementId
     *                              and the corresponding nodeIds
     * @param elementLookupMap      Map that returns an {@link Element} object for an elementId
     * @param nodeNeighborLookupMap Map that returns all Elements connected to the nodeId
     */
    private void processElements(
        final JsonArray elements,
        final Map<Long, Element> elementLookupMap,
        final Map<Long, Set<Element>> nodeNeighborLookupMap) {

        elements.stream()
                .map(JsonValue::asJsonObject)
                .forEach(
                    e -> {
                        final Long elementId = e.getJsonNumber("id").longValue();
                        final List<Long> nodes = e.getJsonArray("nodes").getValuesAs(JsonNumber::longValue);

                        elementLookupMap.get(elementId).addMultipleNodeIds(nodes);

                        nodes.forEach(
                            node -> {
                                if (nodeNeighborLookupMap.containsKey(node)) {
                                    nodeNeighborLookupMap.get(node).add(elementLookupMap.get(elementId));
                                }
                                else {
                                    Set<Element> neighbors = new HashSet<>(10);
                                    neighbors.add(elementLookupMap.get(elementId));
                                    nodeNeighborLookupMap.put(node, neighbors);
                                }
                            });
                    });
    }

    /**
     * Processes the elementLookupMap and the nodeNeighborLookupMap to retrieve the requested
     * maximum number of {@link ViewPoint}.
     *
     * @param numberOfViewPoints    maximum number of ViewPoints to find
     * @param elementLookupMap      Map that returns an {@link Element} object for an elementId
     * @param nodeNeighborLookupMap Map that returns all Elements connected to the nodeId
     *
     * @return List of ViewPoints with a maximum size of numberOfViewPoints
     */
    private List<ViewPoint> getViewPoints(
        int numberOfViewPoints,
        Map<Long, Element> elementLookupMap,
        Map<Long, Set<Element>> nodeNeighborLookupMap) {

        List<ViewPoint> viewPoints = new ArrayList<>(numberOfViewPoints);
        Set<Long> alreadyProcessed = new HashSet<>(elementLookupMap.size());

        for (Map.Entry<Long, Element> entry : elementLookupMap.entrySet()) {
            if (alreadyProcessed.contains(entry.getKey())) {
                continue;
            }

            if (viewPoints.size() < numberOfViewPoints) {
                Element element = entry.getValue();
                Set<Element> neighbors =
                    element.getNodeIds().stream()
                           .map(nodeNeighborLookupMap::get)
                           .flatMap(Collection::stream)
                           .filter(not(e -> e.equals(element)))
                           .collect(Collectors.toSet());

                double currentElementHeight = element.getHeight();
                long higherNeighbors =
                    neighbors.stream().filter(e -> currentElementHeight < e.getHeight()).count();
                long sameHeightNeighbors =
                    neighbors.stream().filter(e -> currentElementHeight == e.getHeight()).count();

                if (higherNeighbors == 0 || sameHeightNeighbors > 0) {
                    viewPoints.add(new ViewPoint(element));
                    alreadyProcessed.add(element.getId());
                    alreadyProcessed.addAll(
                        neighbors.stream().map(Element::getId).collect(Collectors.toSet()));
                }

            }
            else {
                break;
            }
        }

        return viewPoints;
    }
}
