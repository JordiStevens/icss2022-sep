package nl.han.ica.icss.generator;


import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.ColorLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;

public class Generator {

	public String generate(AST ast) {
		Stylesheet stylesheet = ast.root;
		StringBuilder stringBuilder = new StringBuilder();
		for(ASTNode node : stylesheet.getChildren()){
			stringBuilder.append(getSelectorString((Stylerule) node));
			stringBuilder.append(getDeclarationString(node));
			stringBuilder.append("}\n\n");
		}
        return stringBuilder.toString();
	}

	private String getSelectorString(Stylerule stylerule){
		StringBuilder builder = new StringBuilder();
		for(Selector selector : stylerule.selectors){
			builder.append(selector.toString());
		}
		builder.append(" {\n");
		return builder.toString();
	}

	private String getDeclarationString(ASTNode body){
		StringBuilder builder = new StringBuilder();
		for(ASTNode node : body.getChildren()){
			if(node instanceof Declaration){
				Declaration declaration = (Declaration) node;
				builder.append("  ")
						.append(declaration.property.name)
						.append(": ")
						.append(getExpressionValueString(declaration.expression))
						.append(";\n");
			}
		}
		return builder.toString();
	}

	private String getExpressionValueString(Expression expression){
		if(expression instanceof ColorLiteral){
			return ((ColorLiteral) expression).value;
		} else if(expression instanceof PixelLiteral){
			return ((PixelLiteral) expression).value + "px";
		} else if(expression instanceof PercentageLiteral){
			return ((PercentageLiteral) expression).value + "%";
		}
		return "";
	}
	
}
