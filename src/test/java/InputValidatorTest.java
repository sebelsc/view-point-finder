import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class InputValidatorTest {
  private final InputValidator validator = new InputValidator();
  private static final ByteArrayOutputStream systemErrorOutput = new ByteArrayOutputStream();

  @BeforeAll
  static void configureSystemErr() {
    System.setErr(new PrintStream(systemErrorOutput));
  }

  @AfterEach
  void resetOutputStream() {
    systemErrorOutput.reset();
  }

  @Test
  void testValidParameters() {
    String testPathString = "src/test/resources/test.mesh.same.height.neighbors.json";
    Path testPath = Paths.get(testPathString).toAbsolutePath();

    Optional<InputParameter> validatedParameters =
        validator.getValidatedParameters(new String[] {testPathString, "2"});

    assertTrue(validatedParameters.isPresent());
    assertEquals(2, validatedParameters.get().numberOfViewPoints());
    assertEquals(testPath, validatedParameters.get().filePath());
  }

  @ParameterizedTest(name = "{index} testPathString: {0}, numberOfViewPoints {1}")
  @MethodSource("invalidParameters")
  void testInvalidParameters(
      String testPathString, String numberOfViewPoints, String expectedSErrMessage) {

    Optional<InputParameter> validatedParameters =
        validator.getValidatedParameters(new String[] {testPathString, numberOfViewPoints});

    assertFalse(validatedParameters.isPresent());
    assertTrue(systemErrorOutput.toString().contains(expectedSErrMessage));
  }

  private static Stream<Arguments> invalidParameters() {
    return Stream.of(
        Arguments.of(
            "src/test/resources/test.mesh.same.height.neighbors.json",
            "asdf",
            "Provided numberOfViewPoints could not be parsed into an integer."),
        Arguments.of(
            "src/test/resources/test.mesh.same.height.neighbors.json",
            "-12",
            "The numberOfViewPoints must be 0 or a positive integer"),
        Arguments.of(
            "src/test/resrces/some.file.json",
            "2",
            "The provided path does not lead to a file or does not point to a regular file."),
        Arguments.of(
            "src/test/resources/",
            "2",
            "The provided path does not lead to a file or does not point to a regular file."),
        Arguments.of(
            "src/test/resources/test.mesh.same.height.neighbors.json",
            "asdf",
            "Provided numberOfViewPoints could not be parsed into an integer."),
        Arguments.of("", "", "One or both parameters were null or blank. "),
        Arguments.of(null, null, "One or both parameters were null or blank. "));
  }
}
