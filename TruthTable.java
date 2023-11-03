import java.util.*;

public class TruthTable {
    public ArrayList<String> varcount(String eq){
		ArrayList<String> cmds = new ArrayList<String>(Arrays.asList(eq.split("\\s")));
		for(int i = cmds.size()-1; i>=0; i--){
			if (cmds.get(i).equals("(")
					|| cmds.get(i).equals(")")
					|| cmds.get(i).equals("&&")
					|| cmds.get(i).equals("!&")
					|| cmds.get(i).equals("||")
					|| cmds.get(i).equals("!|")
					|| cmds.get(i).equals("!")
					|| cmds.get(i).equals("==")
					|| cmds.get(i).equals("!=")
					|| cmds.get(i).equals("->"))
				cmds.remove(i);
		}
		Collections.sort(cmds);
		for(int i = cmds.size()-1; i>0; i--){
			if (cmds.get(i).equals(cmds.get(i-1)))
				cmds.remove(i);
		}
		return cmds;
	}

	public String evaluate(ArrayList<String> vars, ArrayList<String> vals, String expression){
		// Get the expression into postfix (evaluatable) order
		ShuntingYard creator = new ShuntingYard();
		String postfix = creator.postfix(expression);

		ArrayList<String> cmds = new ArrayList<String>(Arrays.asList(postfix.split("\\s")));

		// replace each variable with its corresponding 0/1 (F/T) value
		for(int i = 0; i<cmds.size(); i++){
			for(int v = 0; v < vars.size(); v++){
				if (cmds.get(i).equalsIgnoreCase(vars.get(v))){
					cmds.set(i, vals.get(v));
				}
			}
		}

		// process the commands!
		Stack<String> stack = new Stack<String>();
		for(String item : cmds){
			if (item.equals("!")){
				String val = stack.pop();
				if (val.equals("0")){
					stack.push("1");
				} else if (val.equals("1")){
					stack.push("0");
				} else
					throw new IllegalArgumentException("Not (!) did not receive proper argument, received "+val);
			} else if (item.equals("&&")){
				String val1 = stack.pop();
				String val2 = stack.pop();
				if(val1.equals("0") && val2.equals("0")){
					stack.push("0");
				} else if(val1.equals("0") && val2.equals("1")){
					stack.push("0");
				} else if(val1.equals("1") && val2.equals("0")){
					stack.push("0");
				} else if(val1.equals("1") && val2.equals("1")){
					stack.push("1");
				} else 
					throw new IllegalArgumentException("And (&&) did not receive proper arguments, received "+val1+", "+val2);
			} else if (item.equals("!&")){
				String val1 = stack.pop();
				String val2 = stack.pop();
				if(val1.equals("0") && val2.equals("0")){
					stack.push("1");
				} else if(val1.equals("0") && val2.equals("1")){
					stack.push("1");
				} else if(val1.equals("1") && val2.equals("0")){
					stack.push("1");
				} else if(val1.equals("1") && val2.equals("1")){
					stack.push("0");
				} else 
					throw new IllegalArgumentException("Nand (!&) did not receive proper arguments, received "+val1+", "+val2);
			} else if (item.equals("||")){
				String val1 = stack.pop();
				String val2 = stack.pop();
				if(val1.equals("0") && val2.equals("0")){
					stack.push("0");
				} else if(val1.equals("0") && val2.equals("1")){
					stack.push("1");
				} else if(val1.equals("1") && val2.equals("0")){
					stack.push("1");
				} else if(val1.equals("1") && val2.equals("1")){
					stack.push("1");
				} else 
					throw new IllegalArgumentException("Or (||) did not receive proper arguments, received "+val1+", "+val2);

			} else if (item.equals("!|")){
				String val1 = stack.pop();
				String val2 = stack.pop();
				if(val1.equals("0") && val2.equals("0")){
					stack.push("1");
				} else if(val1.equals("0") && val2.equals("1")){
					stack.push("0");
				} else if(val1.equals("1") && val2.equals("0")){
					stack.push("0");
				} else if(val1.equals("1") && val2.equals("1")){
					stack.push("0");
				} else 
					throw new IllegalArgumentException("Or (||) did not receive proper arguments, received "+val1+", "+val2);

			} else if (item.equals("==")){
				String val1 = stack.pop();
				String val2 = stack.pop();
				if(val1.equals("0") && val2.equals("0")){
					stack.push("1");
				} else if(val1.equals("0") && val2.equals("1")){
					stack.push("0");
				} else if(val1.equals("1") && val2.equals("0")){
					stack.push("0");
				} else if(val1.equals("1") && val2.equals("1")){
					stack.push("1");
				} else 
					throw new IllegalArgumentException("Equals (==) did not receive proper arguments, received "+val1+", "+val2);

			} else if (item.equals("!=")){
				String val1 = stack.pop();
				String val2 = stack.pop();
				if(val1.equals("0") && val2.equals("0")){
					stack.push("0");
				} else if(val1.equals("0") && val2.equals("1")){
					stack.push("1");
				} else if(val1.equals("1") && val2.equals("0")){
					stack.push("1");
				} else if(val1.equals("1") && val2.equals("1")){
					stack.push("0");
				} else 
					throw new IllegalArgumentException("Xor (!=) did not receive proper arguments, received "+val1+", "+val2);
			} else if (item.equals("->")){
				String val1 = stack.pop();
				String val2 = stack.pop();
				if(val1.equals("0") && val2.equals("0")){
					stack.push("1");
				} else if(val1.equals("0") && val2.equals("1")){
					stack.push("0");
				} else if(val1.equals("1") && val2.equals("0")){
					stack.push("1");
				} else if(val1.equals("1") && val2.equals("1")){
					stack.push("1");
				} else 
					throw new IllegalArgumentException("Implies (->) did not receive proper arguments, received "+val1+", "+val2);

			} else {
				if(item.equals("1") || item.equals("0")){
					stack.push(item);
				} else {
					throw new IllegalArgumentException("attempting to push "+item+" to stack, which is currently "+stack+".\nCommand list is "+cmds);
				}
			}
		}

		if (stack.size() != 1){
			throw new ArithmeticException("Too many arguments on stack, evaluation incomplete. Stack is "+stack+".\nCommands list is "+cmds);
		}
		String answer = stack.pop();
		if (!answer.equals("1") && !answer.equals("0")){
			throw new IllegalArgumentException("Illegal final answer on stack: "+answer);
		}
		return answer;
	}


	/*
	 * Returns num as a binary number with at least digits number of digits.
	 * Used to create a truth table row (so "001" would represent false false true)
	 */
	public String getBinary(int num, int digits){
		String binary = Integer.toBinaryString(num);
		while(binary.length() < digits)
			binary = "0"+binary;
		return binary;
	}
	
	/*
	 * Deals with funny spacing in the input expression
	 */
	public String cleanInput(String expression){
		// Deal with unicode boolean tokens:
		expression = expression.replaceAll("\u22c0","&&");
		expression = expression.replaceAll("\u2227","&&");
		expression = expression.replace("·","&&");
		expression = expression.replaceAll("\u22bc","!&");
		expression = expression.replaceAll("\u22c1","||");
		expression = expression.replaceAll("\u2228","||");
		expression = expression.replaceAll("\u2225","||");
		expression = expression.replace("+","||");
		expression = expression.replaceAll("\u22bd","!|");
		expression = expression.replaceAll("\u21d2","->");
		expression = expression.replaceAll("\u2192","->");
		expression = expression.replaceAll("\u2283","->");
		expression = expression.replaceAll("\u21d4","==");
		expression = expression.replaceAll("\u2261","==");
		expression = expression.replaceAll("\u2194","==");
		expression = expression.replaceAll("\u2295","!=");
		expression = expression.replaceAll("\u21ae","!=");
		expression = expression.replace("~","!");
		expression = expression.replaceAll("\u00ac","!");
		expression = expression.replaceAll("\u02dc","!");
		
		// Deal with spacing by adding, and then removing, spaces around all tokens:
		expression = expression.replace("("," ( ");
		expression = expression.replace(")"," ) ");
		expression = expression.replace("=="," == ");
		expression = expression.replace("!=","notEqual temporary name to get past NOT processing");expression = expression.replace("->"," -> ");
		expression = expression.replace("&&"," && ");
		expression = expression.replace("!&","nand temporary name to get past NOT processing");
		expression = expression.replace("||"," || ");
		expression = expression.replace("!|","nor temporary name to get past NOT processing");		expression = expression.replace("!=","notEqual temporary name to get past NOT processing");
		expression = expression.replace("!"," ! ");
		expression = expression.replace("notEqual temporary name to get past NOT processing"," != ");
		expression = expression.replace("nand temporary name to get past NOT processing"," !& ");
		expression = expression.replace("nor temporary name to get past NOT processing"," !| ");
		expression = expression.replaceAll("\\s+", " ");
		return expression.trim();
	}

	public String cleanOutput(String exp){
		exp = exp.replace("[", "");
		exp = exp.replace("]", "");
		exp = exp.replace(",", "");
		return exp;
	}
	
	// TODO: accommodate longer variables names more gracefully.
	public String truthTable(String expression){
		expression = cleanInput(expression);
		ArrayList<String> vars = varcount(expression);
		
		// initial expression
		String table = expression+"\n";
		// separator bar of correct length
		table += expression.replaceAll(".", "-") + "\n";
		// variables above table
		table += cleanOutput(vars.toString())+"  RES" + "\n";
		
		int combinations = (int) (Math.pow(2, vars.size()) + 0.1); // +0.1 in case of rounding error
		ArrayList<String> vals = new ArrayList<String>();
		for(int i = 0; i < vars.size(); i++){
			vals.add(i, "");
		}

		for(int i = 0; i < combinations; i++){
			String bin = getBinary(i,vals.size());
			for(int k = 0; k < vals.size(); k++){
				vals.set(k, bin.substring(k,k+1));
			}
			String answer = evaluate(vars, vals, expression);
			table += cleanOutput(vals.toString())+"   "+answer + "\n";
		}
		
		// remove last return
		table = table.substring(0, table.length()-1);
		return table;
	}
}
