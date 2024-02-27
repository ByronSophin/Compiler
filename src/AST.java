import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;


public class AST {
	public String name;
    public String type;
    public String value;
	public String varType;

    private ArrayList<AST> children = new ArrayList<>();
    private AST parent = null;

	// public boolean isRoot() {
	// 	return parent == null;
	// }

	public boolean isLeaf() {
		return children.size() == 0;
	}

	public AST(String name, String type, String value, String varType) {
        this.name = name;
        this.type = type;
        this.value = value;
		this.varType = varType;
    }

    public void addChild(AST node) {
        this.children.add(node);
    }

    // public void addChild(AST child) {
    //     child.setParent(this);
    //     this.children.add(child);
    // }

    // public void addChild(String name, String type, String value) {
    //     AST newChild = new AST(name, type, value);
    //     this.addChild(newChild);
    // }

    // public void addChildren(List<AST> children) {
    //     for(AST n : children) {
    //         n.setParent(this);
    //     }
    //     this.children.addAll(children);
    // }

    public List<AST> getChildren() {
        return children;
    }

    public AST getChild(int index){
        return children.get(index);
    }

    // private void setParent(AST parent) {
    //     this.parent = parent;
    // }

    public int size(){
        return children.size();
    }

    public AST getParent() {
        return parent;
    }

    public String getName() {
		return name != null ? name : "null";
	}

    public String getType() {
		return type != null ? type : "null";
	}

    public String getValue() {
		return value != null ? value : "null";
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getVarType() {
		return varType != null ? varType : "null";
	}

    public void print() {
        System.out.println("name: " + this.getName() + ", type: " + this.getType() + ", value: " + this.getValue() + ", variable type:" + this.getVarType());
    }

    public void printAST() {
        for (AST node : children) {
            node.print();
        }
    }
}