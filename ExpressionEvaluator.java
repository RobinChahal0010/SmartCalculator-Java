

import java.util.*;

public class ExpressionEvaluator {

    public double evaluate(String expression) throws CalculatorException {
        try {
            List<Token> tokens = tokenize(expression);
            List<Token> postfix = infixToPostfix(tokens);
            return evaluatePostfix(postfix);
        } catch (Exception e) {
            throw new CalculatorException("Invalid expression: " + e.getMessage());
        }
    }

    private List<Token> tokenize(String expr) {
        List<Token> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        
        String cleaned = expr.replaceAll("\\s+", ""); 
        
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            
            if (Character.isDigit(c) || c == '.') {
                number.append(c);
            } 
            else if (c == '(') {
                if (number.length() > 0) {
                    tokens.add(new Token(number.toString(), Token.TokenType.NUMBER));
                    number.setLength(0);
                }
                tokens.add(new Token("(", Token.TokenType.LEFT_PAREN));
            } 
            else if (c == ')') {
                if (number.length() > 0) {
                    tokens.add(new Token(number.toString(), Token.TokenType.NUMBER));
                    number.setLength(0);
                }
                tokens.add(new Token(")", Token.TokenType.RIGHT_PAREN));
            } 
            else if (isOperator(c)) {
                if (number.length() > 0) {
                    tokens.add(new Token(number.toString(), Token.TokenType.NUMBER));
                    number.setLength(0);
                }
                tokens.add(new Token(String.valueOf(c), Token.TokenType.OPERATOR));
            } 
            else if (Character.isLetter(c)) {
                // Handle functions like sin, cos, sqrt, fact
                StringBuilder func = new StringBuilder();
                while (i < cleaned.length() && Character.isLetter(cleaned.charAt(i))) {
                    func.append(cleaned.charAt(i));
                    i++;
                }
                i--; // adjust for loop
                tokens.add(new Token(func.toString().toLowerCase(), Token.TokenType.FUNCTION));
            }
        }
        
        if (number.length() > 0) {
            tokens.add(new Token(number.toString(), Token.TokenType.NUMBER));
        }
        
        return tokens;
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^' || c == '%';
    }

    private int precedence(String op) {
        return switch (op) {
            case "+", "-" -> 1;
            case "*", "/", "%" -> 2;
            case "^" -> 3;
            default -> -1;
        };
    }

    private List<Token> infixToPostfix(List<Token> tokens) {
        List<Token> postfix = new ArrayList<>();
        Stack<Token> stack = new Stack<>();

        for (Token token : tokens) {
            switch (token.getType()) {
                case NUMBER -> postfix.add(token);
                case FUNCTION -> stack.push(token);
                case LEFT_PAREN -> stack.push(token);
                case RIGHT_PAREN -> {
                    while (!stack.isEmpty() && stack.peek().getType() != Token.TokenType.LEFT_PAREN) {
                        postfix.add(stack.pop());
                    }
                    stack.pop(); // remove left parenthesis
                    if (!stack.isEmpty() && stack.peek().getType() == Token.TokenType.FUNCTION) {
                        postfix.add(stack.pop());
                    }
                }
                case OPERATOR -> {
                    while (!stack.isEmpty() && 
                           (stack.peek().getType() == Token.TokenType.OPERATOR) &&
                           precedence(stack.peek().getValue()) >= precedence(token.getValue())) {
                        postfix.add(stack.pop());
                    }
                    stack.push(token);
                }
            }
        }

        while (!stack.isEmpty()) {
            postfix.add(stack.pop());
        }

        return postfix;
    }

    private double evaluatePostfix(List<Token> postfix) throws CalculatorException {
        Stack<Double> stack = new Stack<>();

        for (Token token : postfix) {
            switch (token.getType()) {
                case NUMBER -> stack.push(Double.parseDouble(token.getValue()));
                
                case OPERATOR -> {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(applyOperator(a, b, token.getValue()));
                }
                
                case FUNCTION -> stack.push(applyFunction(stack.pop(), token.getValue()));
            }
        }

        return stack.pop();
    }

    private double applyOperator(double a, double b, String op) throws CalculatorException {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0) throw new CalculatorException("Division by zero");
                yield a / b;
            }
            case "^" -> Math.pow(a, b);
            case "%" -> a % b;
            default -> throw new CalculatorException("Unknown operator: " + op);
        };
    }

    private double applyFunction(double x, String func) throws CalculatorException {
        return switch (func) {
            case "sin" -> Math.sin(Math.toRadians(x));
            case "cos" -> Math.cos(Math.toRadians(x));
            case "tan" -> Math.tan(Math.toRadians(x));
            case "sqrt" -> {
                if (x < 0) throw new CalculatorException("Square root of negative number");
                yield Math.sqrt(x);
            }
            case "fact" -> factorial(x);
            default -> throw new CalculatorException("Unknown function: " + func);
        };
    }

    private double factorial(double n) throws CalculatorException {
        if (n < 0 || n != Math.floor(n)) 
            throw new CalculatorException("Factorial only for non-negative integers");
        
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
