import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        final InputValidator validator = new InputValidator();
        final ViewPointService service = new ViewPointService();

        Optional<InputParameter> validatedParameters = validator.getValidatedParameters(args);

        validatedParameters.ifPresentOrElse(
            p -> {
                List<ViewPoint> viewPoints = service.findViewPoints(p);
                System.out.println(JsonUtils.serialize(viewPoints));
            },
            () -> System.err.println("An error occurred processing the input parameters."));
    }
}
