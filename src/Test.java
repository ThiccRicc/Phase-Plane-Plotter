import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        
    
    Map<String, Double> variables = Map.of(
            "x", 4.0,
            "y", 5.0
        );

        double resultX = ExpressionEvaluator.evaluate("sin(x)", variables);
        //double resultY = ExpressionEvaluator.evaluate("cos(y)", variables);

        System.out.println(resultX);
    }
}
