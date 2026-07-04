import java.util.*;
import java.util.function.DoubleUnaryOperator;

public class ExpressionEvaluator {

    private static final Map<String, Integer> PRECEDENCE = Map.of(
        "+", 1, "-", 1,
        "*", 2, "/", 2,
        "^", 3
    );

    private static final Set<String> RIGHT_ASSOC = Set.of("^");

    private static final Map<String, DoubleUnaryOperator> BUILTIN_FUNCS = Map.of(
        "sin", Math::sin,
        "cos", Math::cos,
        "tan", Math::tan,
        "sqrt", Math::sqrt,
        "log", Math::log,
        "abs", Math::abs
    );

    public static double evaluate(String expr, Map<String, Double> variables) {
        List<String> rpn = toRPN(expr);
        return evalRPN(rpn, variables);
    }

    /* ------------------ Parsing ------------------ */

    private static List<String> toRPN(String expr) {
        List<String> output = new ArrayList<>();
        Stack<String> ops = new Stack<>();

        List<String> tokens = tokenize(expr);

        for (int i = 0; i < tokens.size(); i++) {
            String t = tokens.get(i);

            if (isNumber(t) || isVariable(t)) {
                output.add(t);
            }

            // FUNCTION: identifier followed by "("
            else if (isFunction(t, tokens, i)) {
                ops.push(t);
            }

            else if (t.equals(",")) {
                while (!ops.peek().equals("("))
                    output.add(ops.pop());
            }

            else if (t.equals("(")) {
                ops.push(t);
            }

            else if (t.equals(")")) {
                while (!ops.peek().equals("("))
                    output.add(ops.pop());
                ops.pop(); // pop "("

                // If top is function, emit it
                if (!ops.isEmpty() && BUILTIN_FUNCS.containsKey(ops.peek()))
                    output.add(ops.pop());
            }

            else if (isOperator(t)) {
                while (!ops.isEmpty() && isOperator(ops.peek())) {
                    String top = ops.peek();
                    if ((RIGHT_ASSOC.contains(t) && PRECEDENCE.get(t) < PRECEDENCE.get(top)) ||
                        (!RIGHT_ASSOC.contains(t) && PRECEDENCE.get(t) <= PRECEDENCE.get(top))) {
                        output.add(ops.pop());
                    } else break;
                }
                ops.push(t);
            }
        }

        while (!ops.isEmpty())
            output.add(ops.pop());

        return output;
    }

    /* ------------------ Evaluation ------------------ */

    private static double evalRPN(List<String> rpn, Map<String, Double> vars) {
        Stack<Double> stack = new Stack<>();

        for (String t : rpn) {

            if (isNumber(t)) {
                stack.push(Double.parseDouble(t));
            }
            else if (vars.containsKey(t)) {
                stack.push(vars.get(t));
            }
            else if (t.equals("pi")) {
                stack.push(Math.PI);
            }
            else if (t.equals("e")) {
                stack.push(Math.E);
            }
            else if (BUILTIN_FUNCS.containsKey(t)) {
                double a = stack.pop();
                stack.push(BUILTIN_FUNCS.get(t).applyAsDouble(a));
            }
            else if (isOperator(t)) {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyOp(t, a, b));
            }
        }

        return stack.pop();
    }

    /* ------------------ Helpers ------------------ */

    private static double applyOp(String op, double a, double b) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalStateException("Unknown op: " + op);
        };
    }

    private static boolean isOperator(String s) {
        return PRECEDENCE.containsKey(s);
    }

    private static boolean isNumber(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isVariable(String s) {
        return s.matches("[a-zA-Z_]\\w*")
               && !BUILTIN_FUNCS.containsKey(s);
    }

    private static boolean isFunction(String t, List<String> tokens, int i) {
        return BUILTIN_FUNCS.containsKey(t)
               && i + 1 < tokens.size()
               && tokens.get(i + 1).equals("(");
    }

    private static List<String> tokenize(String expr) {
        return Arrays.stream(
            expr.replaceAll("([()+\\-*/^,])", " $1 ")
                .trim()
                .split("\\s+")
        ).toList();
    }
}
