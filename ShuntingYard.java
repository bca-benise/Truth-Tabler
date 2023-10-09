/* This project is a simple CLI tool to create boolean truth tables from (almost) any 
statement.  Expressions can be written in any free mix of classical boolean algebra 
and Java syntax.  However, since multi-character names are allowed, it cannot accept
syntax like ab+c */

/* I began with the Shunting Yard implementation published by Edd Mann 
here (https://eddmann.com/posts/shunting-yard-implementation-in-java/) and then
proceeded from there. */ 

import java.util.*;

public class ShuntingYard {
    public static void main(String[] args){
		TruthTable k = new TruthTable();
		Scanner in = new Scanner(System.in);
		
		// TODO: missing nimply, nand, and nor
		
		System.out.println("--------------------- BOOLEAN EXPRESSION SOLVER ---------------------");
		System.out.println("--------------------------- By Ben Isecke ---------------------------");
		System.out.println("Write Java boolean expressions below (ie. \"a != !(b && c)\").");
		System.out.println("Also accepts \u22c0, \u2227, ·, \u22c1, \u2228, \u2225, +, ->, \u21d2, \u2192, \u2283, \u21d4, \u2194, ==, \u2261, \u2295, \u21ae, \u00ac, ~, and \u02dc");
		System.out.println("(Ctrl-c to exit)");
		String exp;
		String formatted_table = "";
		while(true){
			System.out.print("> ");
			try {
				exp = in.nextLine();
				formatted_table = k.truthTable(exp);
			} catch (Exception e) {
				System.out.println("Invalid command, please try again");
				continue;
			}
			System.out.println(formatted_table);
		}
	}
	
	
    private enum Operator {
    	IMPLIES(1), AND(2), OR(3), XOR(4), EQ(4), NOT(5);
        final int precedence;
        Operator(int p) { precedence = p; }
    }

    private static Map<String, Operator> ops = new HashMap<String, Operator>() {{
    	put("!=", Operator.XOR);
    	put("!", Operator.NOT);
        put("&&", Operator.AND);
        put("||", Operator.OR);
        put("==", Operator.EQ);
        put("->", Operator.IMPLIES);
    }};

    private static boolean isHigerPrec(String op, String sub)
    {
        return (ops.containsKey(sub) && ops.get(sub).precedence >= ops.get(op).precedence);
    }

    public static String postfix(String infix)
    {
        StringBuilder output = new StringBuilder();
        Deque<String> stack  = new LinkedList<>();

        for (String token : infix.split("\\s")) {
            // operator
            if (ops.containsKey(token)) {
                while ( ! stack.isEmpty() && isHigerPrec(token, stack.peek()))
                    output.append(stack.pop()).append(' ');
                stack.push(token);

            // left parenthesis
            } else if (token.equals("(")) {
                stack.push(token);

            // right parenthesis
            } else if (token.equals(")")) {
                while ( ! stack.peek().equals("("))
                    output.append(stack.pop()).append(' ');
                stack.pop();

            // digit
            } else {
                output.append(token).append(' ');
            }
        }

        while ( ! stack.isEmpty())
            output.append(stack.pop()).append(' ');

        return output.toString();
    }
}
