

import java.util.*;

public class Calculator {
    private final ExpressionEvaluator evaluator;
    private final List<String> history;

    public Calculator() {
        this.evaluator = new ExpressionEvaluator();
        this.history = new ArrayList<>();
    }

    public void start() {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Smart Java Calculator ===\n");
        System.out.println("Examples: 2+3*4, (5+3)^2, sin(30), 5!, sqrt(16)");

        while (true) {
            System.out.print("\nEnter expression (or 'exit'): ");
            String input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye! ☕");
                break;
            }

            if (input.equalsIgnoreCase("history")) {
                showHistory();
                continue;
            }

            try {
                double result = evaluator.evaluate(input);
                System.out.printf("= %.6f%n", result);
                
                String entry = input + " = " + result;
                history.add(entry);
                if (history.size() > 20) history.remove(0);
                
            } catch (CalculatorException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }
        sc.close();
    }

    private void showHistory() {
        if (history.isEmpty()) {
            System.out.println("No history yet.");
            return;
        }
        System.out.println("\n=== History ===");
        for (int i = 0; i < history.size(); i++) {
            System.out.println((i+1) + ". " + history.get(i));
        }
    }
}