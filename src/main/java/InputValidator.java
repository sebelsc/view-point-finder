import org.apache.commons.lang3.StringUtils;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class InputValidator {
    /**
     * Validates the arguments array and creates an {@link InputParameter} Optional.
     *
     * @param args Arguments the program was called with
     *
     * @return InputParameter Optional that is not empty if the arguments were valid
     */
    public Optional<InputParameter> getValidatedParameters(String[] args) {
        boolean isCountCorrect = isArgumentCountCorrect(args);

        if (!isCountCorrect) {
            return Optional.empty();
        }

        return parseArguemtnsToParameters(args);
    }

    /**
     * Validates the argument count.
     *
     * @param args Arguments the program was called with
     *
     * @return true if the count is two, false otherwise
     */
    private boolean isArgumentCountCorrect(String[] args) {
        if (args.length == 0 || args.length == 1) {
            System.err.println(
                "Too few arguments. Two are required: "
                    + "<path to mesh json file> <number of view points>");
            return false;
        }
        else if (args.length > 2) {
            System.err.println(
                "Too many arguments. Two are expected: "
                    + "<path to mesh json file> <number of view points>");
            return false;
        }

        return true;
    }

    /**
     * Parses the arguments to {@link InputParameter} if possible
     *
     * @param args Arguments the program was called with
     *
     * @return Optional of InputParameters, not empty if parsing is successful
     */
    private Optional<InputParameter> parseArguemtnsToParameters(String[] args) {
        final String filePathString = args[0];
        final String viewPointString = args[1];

        if (StringUtils.isNoneBlank(filePathString, viewPointString)) {
            try {
                Path filePath = Paths.get(filePathString).toAbsolutePath();
                int numberOfViewPoints = Integer.parseInt(viewPointString);

                if (Files.notExists(filePath)
                    || !Files.isRegularFile(filePath)
                    || Files.isDirectory(filePath)) {
                    System.err.println(
                        "The provided path does not lead to a file or does not point to a regular file. "
                            + "Resolved path: "
                            + filePath);

                    return Optional.empty();
                }

                if (numberOfViewPoints < 0) {
                    System.err.println("The numberOfViewPoints must be 0 or a positive integer");

                    return Optional.empty();
                }

                return Optional.of(new InputParameter(filePath, numberOfViewPoints));
            }
            catch (NumberFormatException e) {
                System.err.println("Provided numberOfViewPoints could not be parsed into an integer.");
                return Optional.empty();
            }
            catch (InvalidPathException e) {
                System.err.println("Provided path could not be processed and seems to be invalid.");
                return Optional.empty();
            }
        }
        else {
            System.err.println(
                "One or both parameters were null or blank. "
                    + "File Path: "
                    + filePathString
                    + "\n"
                    + "Number of View Points: "
                    + viewPointString);
            return Optional.empty();
        }
    }
}
