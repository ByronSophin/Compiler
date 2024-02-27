import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
public class Driver {
	public static void main(String[] args) throws Exception {
		ANTLRInputStream input = new ANTLRInputStream(System.in);
		// initializes a lexer
		LittleLexer lexer = new LittleLexer(input);
		// initializes a CommonTokenStream 
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// initializes a parser with CommonTokenStream
		LittleParser parser = new LittleParser(tokens);
		
		//begin parsing at prog rule
		ParseTree tree = parser.program();
		
		//System.out.println(tree.toStringTree(parser)); print LISP-style tree
		//System.out.println(tree.getText());
		
		//create a generic parse tree walker that can trigger callbacks
		ParseTreeWalker walker = new ParseTreeWalker();
		
		SimpleTableBuilder stb = new SimpleTableBuilder();
		//SymbolTableBuilder stb = new SimbolTableBuilder();
		
		//Walk the tree created during the parse, trigger callbacks
		walker.walk(stb, tree);
		//stb.prettyPrint();
	}
}
