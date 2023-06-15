package nl.han.ica.icss.parser;

import java.util.Stack;


import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {
	
	//Accumulator attributes:
	private AST ast;

	//Use this to keep track of the parent nodes when recursively traversing the ast
	private IHANStack<ASTNode> currentContainer;

	public ASTListener() {
		ast = new AST();
		currentContainer = new HANStack<>();
	}

    public AST getAST() {
        return ast;
    }

	@Override
	public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
		ASTNode stylesheet = new Stylesheet();
		currentContainer.push(stylesheet);
	}

	@Override
	public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
		ast.setRoot((Stylesheet) currentContainer.pop());
	}

	@Override
	public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
		ASTNode styleRule = new Stylerule();
		currentContainer.push(styleRule);
	}

	@Override
	public void exitStyleRule(ICSSParser.StyleRuleContext ctx){
		ASTNode styleRule = currentContainer.pop();
		currentContainer.peek().addChild(styleRule);
	}

	@Override
	public void enterSelector(ICSSParser.SelectorContext ctx){
		ASTNode selector = null;
		if(ctx.LOWER_IDENT() != null) {
			selector = new TagSelector(ctx.getText());
		} else if(ctx.CLASS_IDENT() != null){
			selector = new ClassSelector(ctx.getText());
		} else if(ctx.ID_IDENT() != null){
			selector = new IdSelector(ctx.getText());
		}
		currentContainer.push(selector);
	}

	@Override
	public void exitSelector(ICSSParser.SelectorContext ctx) {
		ASTNode tagSelector = currentContainer.pop();
		currentContainer.peek().addChild(tagSelector);
	}

	@Override
	public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode declaration = new Declaration();
		currentContainer.push(declaration);
	}

	@Override
	public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
		ASTNode declaration = currentContainer.pop();
		currentContainer.peek().addChild(declaration);
	}

	@Override
	public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
		ASTNode property = new PropertyName(ctx.getText());
		currentContainer.push(property);
	}

	@Override
	public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
		ASTNode propertyName = currentContainer.pop();
		currentContainer.peek().addChild(propertyName);
	}

	@Override
	public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ASTNode literal = new ColorLiteral(ctx.getText());
		currentContainer.push(literal);
	}

	@Override
	public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
		ASTNode literal = currentContainer.pop();
		currentContainer.peek().addChild(literal);
	}

	@Override
	public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		ASTNode literal = new BoolLiteral(ctx.getText());
		currentContainer.push(literal);
	}

	@Override
	public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
		ASTNode literal = currentContainer.pop();
		currentContainer.peek().addChild(literal);
	}

	@Override
	public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		ASTNode literal = new PixelLiteral(ctx.getText());
		currentContainer.push(literal);
	}

	@Override
	public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
		ASTNode literal = currentContainer.pop();
		currentContainer.peek().addChild(literal);
	}

	@Override
	public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		ASTNode literal = new PercentageLiteral(ctx.getText());
		currentContainer.push(literal);
	}

	@Override
	public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
		ASTNode literal = currentContainer.pop();
		currentContainer.peek().addChild(literal);
	}

	@Override
	public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ASTNode literal = new ScalarLiteral(ctx.getText());
		currentContainer.push(literal);
	}

	@Override
	public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
		ASTNode literal = currentContainer.pop();
		currentContainer.peek().addChild(literal);
	}

	@Override
	public void enterVarDeclaration(ICSSParser.VarDeclarationContext ctx) {
		ASTNode variableDeclaration = new VariableAssignment();
		currentContainer.push(variableDeclaration);
	}

	@Override
	public void exitVarDeclaration(ICSSParser.VarDeclarationContext ctx) {
		ASTNode variableDeclaration = currentContainer.pop();
		currentContainer.peek().addChild(variableDeclaration);
	}

	@Override
	public void enterVarReference(ICSSParser.VarReferenceContext ctx) {
		ASTNode varReference = new VariableReference(ctx.getText());
		currentContainer.peek().addChild(varReference);
	}

	@Override
	public void enterOperation(ICSSParser.OperationContext ctx) {
		if(ctx.getChildCount() == 3) {
			ASTNode operation = null;
			if (ctx.MIN() != null) {
				operation = new SubtractOperation();
			} else if (ctx.PLUS() != null) {
				operation = new AddOperation();
			} else if (ctx.MUL() != null) {
				operation = new MultiplyOperation();
			}
			currentContainer.push(operation);
		}
	}

	@Override
	public void exitOperation(ICSSParser.OperationContext ctx) {
		if(ctx.PLUS() != null || ctx.MIN() != null || ctx.MUL() != null) {
			ASTNode operation = currentContainer.pop();
			currentContainer.peek().addChild(operation);
		}
	}
}
