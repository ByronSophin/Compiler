import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleTableBuilder extends LittleBaseListener {
		Stack<LinkedHashMap<String, String>> scopeStack = new Stack<>();
		Stack<LinkedHashMap<String, String>> printStack = new Stack<>();
		int counter = 1;

		AST tree = new AST(null, null, null, null);
		LinkedList<AST> list = new LinkedList<AST>();
		LinkedList<String> ir = new LinkedList<String>();
		
		
		@Override public void enterProgram(LittleParser.ProgramContext ctx) {
			//1. make a new symbole table for "global"
			//2. add it to the list of symbol tables
			//3. push it to the scope stack
			//Hashtable global = new Hashtable();
			LinkedHashMap<String, String> global = new LinkedHashMap<>();
			// System.out.println("Entering global scope: ");
			global.put("Symbol table ", "GLOBAL");
			scopeStack.push(global);
			printStack.push(global);
		

			// Step4
			//tree.addChild(new AST("GLOBAL", null, null));
		}
		@Override public void exitProgram(LittleParser.ProgramContext ctx) { 
			// System.out.println("Exiting global scope: ");
			scopeStack.pop();
			//if(scopeStack.isEmpty())
			//{
				//System.out.println("Stack has been cleared");
				//tree.printAST();
				//for(AST line : list)
				//{
				//	line.print();
				//}
			//}
			tiny();
			for(String line : ir)
			{
				System.out.println(line);
			}
		}


		@Override public void enterFunc_decl(LittleParser.Func_declContext ctx) { 
			LinkedHashMap<String, String> function = new LinkedHashMap<>();
			String name = ctx.id().getText();
			// System.out.println("Entering function scope: ");
			function.put("Symbol table ", name);
			scopeStack.push(function);
			printStack.push(function);

			//step4
			//tree.addChild(new AST(ctx.id().getText(), ctx.getChild(2).getText(), ctx.getChild(3).getText()));
		}	

		@Override public void exitFunc_decl(LittleParser.Func_declContext ctx) { 
			// System.out.println("Exiting function scope: ");
			scopeStack.pop();
		}
		
		@Override public void enterString_decl(LittleParser.String_declContext ctx) {
			//1. extract name, type, value
			String name = ctx.id().getText();
			String type = "STRING";
			String value = ctx.str().getText();
			//System.out.println(name + "," + type + "," + value);
			
			//2. Create a new symbol table entry using the above  info and insert to the table at thte to of the stack.
			LinkedHashMap<String, String> stringDecl = scopeStack.peek();
			
			//3. stores the information into the peeked hashtable
			if (stringDecl.containsKey("name " + name)) {
				System.out.println("DECLARATION ERROR " + name);
				System.exit(0);
				
			} else {
				stringDecl.put("name " + name, " type " + type + " value " + value);
				printStack.peek().put("name " + name, " type " + type + " value " + value);
				tree.addChild(new AST(name, type, value, "STRING"));
				list.add(new AST(name, type, value, "STRING"));
			}
			// System.out.println("enterString_decl: " + stringDecl.get(name));
			// name str type STRING value "test"
			
		}
		
		@Override public void enterVar_decl(LittleParser.Var_declContext ctx) { 
			String type = ctx.var_type().getText();
			String name = ctx.id_list().getText();
			String[] split = name.split(",");
			// create a new symbol table entry (uses an existing hash table via scopeStack.peek()
			LinkedHashMap<String, String> variables = scopeStack.peek();
			
			// stores information into the hashtable being viewed
			for(int i = 0; i < split.length; ++i)
			{
				
			     if (variables.containsKey("name " + split[i])) {
			            System.out.println("DECLARATION ERROR " + split[i]);
			            System.exit(0);
			        } else {
			            variables.put("name " + split[i], " type " + type);
			            printStack.peek().put("name " + split[i], " type " + type);
						//tree.addChild(new AST("var", split[i], type));
						list.add(new AST(split[i], "var", null, type));
			        }
			    }
			
		}
		
		
		@Override public void enterParam_decl(LittleParser.Param_declContext ctx) { 
			String type = ctx.var_type().getText();
			String name = ctx.id().getText();
			scopeStack.peek().put("name " + name, " type " + type);
			printStack.peek().put("name " + name, " type " + type);
		}
		
		
		@Override public void enterIf_stmt(LittleParser.If_stmtContext ctx) { 
			LinkedHashMap<String, String> block = new LinkedHashMap<>();
			// System.out.println("Entering block scope: ");
			block.put("Symbol table ", "BLOCK " + counter++);
			scopeStack.push(block);
			printStack.push(block);
		}
		
		@Override public void exitIf_stmt(LittleParser.If_stmtContext ctx) { 
			// System.out.println("Exiting block scope: ");
			scopeStack.pop();
		}
		
		@Override public void enterElse_part(LittleParser.Else_partContext ctx) { 
			if (ctx.stmt_list() != null) {
				LinkedHashMap<String, String> block = new LinkedHashMap<>();
				// System.out.println("Entering block scope: ");
				block.put("Symbol table ", "BLOCK " + counter++);
				scopeStack.push(block);
				printStack.push(block);
			}
		}
		
		@Override public void exitElse_part(LittleParser.Else_partContext ctx) { 
			// System.out.println("Exiting block scope: ");
			if (ctx.stmt_list() != null) {
				scopeStack.pop();
			}
		}
		
		@Override public void enterWhile_stmt(LittleParser.While_stmtContext ctx) { 
			LinkedHashMap<String, String> block = new LinkedHashMap<>();
			// System.out.println("Entering block scope: ");
			block.put("Symbol table ", "BLOCK " + counter++);
			scopeStack.push(block);
			printStack.push(block);
		}
		
		@Override public void exitWhile_stmt(LittleParser.While_stmtContext ctx) { 
			// System.out.println("Exiting block scope: ");
			scopeStack.pop();
		}

		//@Override public void enterAssign_stmt(LittleParser.Assign_stmtContext ctx) { 
		//	tree.addChild(new AST(ctx.getChild(0).getText(), null, null));
		//	list.add(new AST(ctx.getChild(0).getText(), null, null));
		//	
		//}

		@Override public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
			//tree.addChild(new AST(ctx.getChild(0).getText(), ctx.getChild(1).getText(), ctx.getChild(2).getText()));
			String varType = null;
			for(AST line : list)
			{
				if(line.getName().equals(ctx.getChild(0).getText()))
				{
					varType = line.getVarType();
				}
			}
			list.add(new AST(ctx.getChild(0).getText(), ctx.getChild(1).getText(), ctx.getChild(2).getText(), varType));
		}

		 @Override public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {
			//tree.addChild(new AST(ctx.getChild(0).getText(), null, ctx.getChild(2).getText()));
			String varType = null;
			String[] idList = ctx.getChild(2).getText().split(",");
			
			for(int i = 0; i < idList.length; ++i)
			{
				for(AST line : list)
				{
					if(line.getName().equals(idList[i]))
					{
						varType = line.getVarType();
					}
					
				}
			     list.add(new AST(ctx.getChild(0).getText(), null, idList[i], varType));
			}
			
		  }

		// @Override public void enterId_list(LittleParser.Id_listContext ctx) {
		// 	tree.addChild(new AST(ctx.getChild(0).getText(), ctx.getChild(1).getText(), null));
		// }

		  @Override public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) { 
			//tree.addChild(new AST(ctx.getChild(0).getText(), null, ctx.getChild(2).getText()));
			String varType = null;
			String[] idList = ctx.getChild(2).getText().split(",");
			
			for(int i = 0; i < idList.length; ++i)
			{
				for(AST line : list)
				{
					if(line.getName().equals(idList[i]))
					{
						varType = line.getVarType();
					}
					
				}
			     list.add(new AST(ctx.getChild(0).getText(), null, idList[i], varType));
			}
			
		  }
		
		// Create a new symbol table entry using the baove info and insert to the table at thte to of the stack.
		
		public void prettyPrint() {
			if (scopeStack.isEmpty()) {
				// System.out.println("Stack is empty");
			} else {
				// System.out.println("not empty");
			}
		
		
			for (LinkedHashMap<String, String> scope : printStack) {
				LinkedHashMap<String, String> hashMap = scope;
				for (String key : hashMap.keySet()) {
					String value = hashMap.get(key);
					System.out.println(key + value);
				}
				
				System.out.println();
				
				// for (String key : hashMap.keySet()) {
					// System.out.println(key);
				// }
				
				
				// for (String dummy : hashMap.keySet()) {
					// String value = hashMap.get(dummy);
					// System.out.println(value);
				// }
				// System.out.println(setKeys);
			}

			
			
			// for each Hashtable we call "scope" in the scopeStack
			// for (Hashtable<String, String> scope : printStack) {
				// we store the scope as a hashtable
				// Hashtable<String, String> hashtable = scope;
				
				// iterate through the hashtable as a keyset to print everything
				// for (String key : hashtable.keySet()) {
					// String value = hashtable.get(key);
					// System.out.println("something something: " + value);
				// }
				
			// }	
		}
		public void tiny(){
			LinkedList<String> registers = new LinkedList<String>();
			int current = 0;
			for(AST line : list)
			{
				String code = "";
				if(line.getType().equals(":="))
				{
					line.setValue(line.getValue().replace("(", ""));
					line.setValue(line.getValue().replace(")", ""));
					if(line.getValue().contains("*"))
					{
						String[] variables = line.getValue().split("\\*");
						String register = "";
						if(line.getVarType().equals("INT"))
						{
							if(inList(variables[0], registers) && inList(variables[1], registers))
							{
								register = getRegister(variables[0], registers);
								code = code.concat("\nmuli " + register + " " );
								register = getRegister(variables[1], registers);
								code = code.concat(register);
								if(inList(line.getName(), registers))
								{
									registers.remove(getRegister(line.getName(), registers) + ":" + line.getName());
								}
								if(inListReg(register, registers))
								{
									registers.remove(register + ":" + getVariable(register, registers));
								}
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
								
							}
							else
							{
								register = "r".concat(Integer.toString(current));
								code = code.concat("move " + variables[0] + " " + register);
								code = code.concat("\nmuli " + variables[1] + " " + register);
								++current;
								
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
							}
								
						}
						else if(line.getVarType().equals("FLOAT"))
						{
							if(inList(variables[0], registers) && inList(variables[1], registers))
							{
								register = getRegister(variables[0], registers);
								code = code.concat("\nmulr " + register + " " );
								register = getRegister(variables[1], registers);
								code = code.concat(register);
								if(inList(line.getName(), registers))
								{
									registers.remove(getRegister(line.getName(), registers) + ":" + line.getName());
								}
								if(inListReg(register, registers))
								{
									registers.remove(register + ":" + getVariable(register, registers));
								}
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
								
							}
							else
							{
								register = "r".concat(Integer.toString(current));
								code = code.concat("move " + variables[0] + " " + register);
								code = code.concat("\nmulr " + variables[1] + " " + register);
								++current;
								
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
							}
						}
						
					}
					else if(line.getValue().contains("/"))
					{
						String[] variables = line.getValue().split("\\/");
						String register = "";
						if(line.getVarType().equals("INT"))
						{
							if(inList(variables[0], registers) && inList(variables[1], registers))
							{
								register = getRegister(variables[0], registers);
								code = code.concat("\ndivi " + register + " " );
								register = getRegister(variables[1], registers);
								code = code.concat(register);
								if(inList(line.getName(), registers))
								{
									registers.remove(getRegister(line.getName(), registers) + ":" + line.getName());
								}
								if(inListReg(register, registers))
								{
									registers.remove(register + ":" + getVariable(register, registers));
								}
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
								
							}
							else
							{
								register = "r".concat(Integer.toString(current));
								code = code.concat("move " + variables[0] + " " + register);
								code = code.concat("\ndivi " + variables[1] + " " + register);
								++current;
								
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
							}
						}
						else if(line.getVarType().equals("FLOAT"))
						{
							if(inList(variables[0], registers) && inList(variables[1], registers))
							{
								register = getRegister(variables[0], registers);
								code = code.concat("\ndivr " + register + " " );
								register = getRegister(variables[1], registers);
								code = code.concat(register);
								if(inList(line.getName(), registers))
								{
									registers.remove(getRegister(line.getName(), registers) + ":" + line.getName());
								}
								if(inListReg(register, registers))
								{
									registers.remove(register + ":" + getVariable(register, registers));
								}
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
								
							}
							else
							{
								register = "r".concat(Integer.toString(current));
								code = code.concat("move " + variables[0] + " " + register);
								code = code.concat("\ndivr " + variables[1] + " " + register);
								++current;
								
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
							}
						}
						
					}
					else if(line.getValue().contains("+"))
					{
						String[] variables = line.getValue().split("\\+");
						String register = "";
						if(line.getVarType().equals("INT"))
						{
							if(inList(variables[0], registers) && inList(variables[1], registers))
							{
								register = getRegister(variables[0], registers);
								code = code.concat("\naddi " + register + " " );
								register = getRegister(variables[1], registers);
								code = code.concat(register);
								if(inList(line.getName(), registers))
								{
									registers.remove(getRegister(line.getName(), registers) + ":" + line.getName());
								}
								if(inListReg(register, registers))
								{
									registers.remove(register + ":" + getVariable(register, registers));
								}
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
								
							}
							else
							{
								register = "r".concat(Integer.toString(current));
								code = code.concat("move " + variables[0] + " " + register);
								code = code.concat("\naddi " + variables[1] + " " + register);
								++current;
								
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
							}
								
						}
						else if(line.getVarType().equals("FLOAT"))
						{
							if(inList(variables[0], registers) && inList(variables[1], registers))
							{
								register = getRegister(variables[0], registers);
								code = code.concat("\naddr " + register + " " );
								register = getRegister(variables[1], registers);
								code = code.concat(register);
								if(inList(line.getName(), registers))
								{
									registers.remove(getRegister(line.getName(), registers) + ":" + line.getName());
								}
								if(inListReg(register, registers))
								{
									registers.remove(register + ":" + getVariable(register, registers));
								}
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
								
							}
							else
							{
								register = "r".concat(Integer.toString(current));
								code = code.concat("move " + variables[0] + " " + register);
								code = code.concat("\naddr " + variables[1] + " " + register);
								++current;
								
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
							}
						}
					}
					else if(line.getValue().contains("-"))
					{
						String[] variables = line.getValue().split("-");
						String register = "";
						if(line.getVarType().equals("INT"))
						{
							if(inList(variables[0], registers) && inList(variables[1], registers))
							{
								register = getRegister(variables[0], registers);
								code = code.concat("\nsubi " + register + " " );
								register = getRegister(variables[1], registers);
								code = code.concat(register);
								if(inList(line.getName(), registers))
								{
									registers.remove(getRegister(line.getName(), registers) + ":" + line.getName());
								}
								if(inListReg(register, registers))
								{
									registers.remove(register + ":" + getVariable(register, registers));
								}
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
								
							}
							else
							{
								register = "r".concat(Integer.toString(current));
								code = code.concat("move " + variables[0] + " " + register);
								code = code.concat("\nsubi " + variables[1] + " " + register);
								++current;
								
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
							}
						}
						else if(line.getVarType().equals("FLOAT"))
						{
							if(inList(variables[0], registers) && inList(variables[1], registers))
							{
								register = getRegister(variables[0], registers);
								code = code.concat("\nsubr " + register + " " );
								register = getRegister(variables[1], registers);
								code = code.concat(register);
								if(inList(line.getName(), registers))
								{
									registers.remove(getRegister(line.getName(), registers) + ":" + line.getName());
								}
								if(inListReg(register, registers))
								{
									registers.remove(register + ":" + getVariable(register, registers));
								}
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
								
							}
							else
							{
								register = "r".concat(Integer.toString(current));
								code = code.concat("move " + variables[0] + " " + register);
								code = code.concat("\nsubr " + variables[1] + " " + register);
								++current;
								
								code = code.concat("\nmove " + register + " " + line.getName());
								register = register.concat(":" + line.getName());
								registers.add(register);
							}
						}
						
					}
					else
					{
						if(checkNumber(line.getValue()))
						{
							code = code.concat("move " + line.getValue() + " " + line.getName());
						}
						else 
						{
							String register = "r".concat(Integer.toString(current));
							code = code.concat("\nmove " + line.getValue() + " " + register);
							code = code.concat("\nmove " + register + " " + line.getName());
						}
					}
					
				}
				else if(line.getName().equals("READ"))
				{
					if(line.getVarType().equals("INT"))
					{
						code = code.concat("sys readi ");
						code = code.concat(line.getValue());
					}
					else if(line.getVarType().equals("FLOAT"))
					{
						code = code.concat("sys readr ");
						code = code.concat(line.getValue());
					}
					
				}
				else if(line.getName().equals("WRITE"))
				{
					if(line.getVarType().equals("STRING"))
					{
						code = code.concat("sys writes ");
						code = code.concat(line.getValue());
					}
					else if(line.getVarType().equals("INT"))
					{
						code = code.concat("sys writei ");
						code = code.concat(line.getValue());
					}
					else if(line.getVarType().equals("FLOAT"))
					{
						code = code.concat("sys writer ");
						code = code.concat(line.getValue());
					}
				}
				else if(line.getType().equals("var"))
				{
					code = code.concat(line.getType() + " " + line.getName());
				}
				else if(line.getType().equals("STRING"))
				{
					code = code.concat("str " + line.getName() + " " + line.getValue());
				}
				ir.add(code);
			}
			ir.add("sys halt");
			
		}
		public static boolean checkNumber(String literal) 
		{
			return literal.matches("-?\\d+(\\.\\d+)?");  
		}
		public static boolean inList(String variable, LinkedList<String> list)
		{
			for(String register: list)
			{
				String[] spl = register.split(":");
				if(variable.equals(spl[1]))
				{
					return true;
				}
			}
			return false;
		}
		public static boolean inListReg(String register, LinkedList<String> list)
		{
			for(String variable: list)
			{
				String[] spl = variable.split(":");
				if(register.equals(spl[0]))
				{
					return true;
				}
			}
			return false;
		}
		public static String getRegister(String variable, LinkedList<String> list)
		{
			for(String register: list)
			{
				String[] spl = register.split(":");
				if(variable.equals(spl[1]))
				{
					return spl[0];
				}
			}
			return null;
		}
		public static String getVariable(String register, LinkedList<String> list)
		{
			for(String variable: list)
			{
				String[] spl = variable.split(":");
				if(register.equals(spl[0]))
				{
					return spl[1];
				}
			}
			return null;
		}
}
