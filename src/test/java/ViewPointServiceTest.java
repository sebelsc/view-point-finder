import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ViewPointServiceTest {
  final ViewPointService viewPointService = new ViewPointService();

  @Test
  void testSameHeightNeighbors() {
    List<ViewPoint> viewPoints =
        viewPointService.findViewPoints(
            new InputParameter(
                Path.of("src/test/resources/test.mesh.same.height.neighbors.json"), 5));
    assertEquals(1, viewPoints.size());
  }
  
  @ParameterizedTest
  @ValueSource(strings = {
      "src/test/resources/mesh_x_sin_cos_20000[1][1][1][1][1][1].json",
      "src/test/resources/mesh[1][1][1][1][1][1].json",
      "src/test/resources/mesh_x_sin_cos_10000[82][1][1][1][1][1][1].json"
  })
  void testFindViewPoints20000(String filePathString){
    Instant start = Instant.now();
    List<ViewPoint> viewPoints =
        viewPointService.findViewPoints(
            new InputParameter(
                Path.of(filePathString), 10));

    System.out.println("Processing took " + Duration.between(start, Instant.now()).toMillis() + "ms");
    assertEquals(9, viewPoints.size());
  }
}
