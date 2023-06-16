package nl.han.ica.icss.checker;


import nl.han.ica.datastructures.ScopeTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;


public class Checker {

    private final ScopeTable<String, ExpressionType> scopes = new ScopeTable<>();

    public void check(AST ast) {
         Stylesheet stylesheet = ast.root;
         scopes.openScope();

         for(ASTNode node : stylesheet.getChildren()){
             if(node instanceof VariableAssignment){
                 checkVariableAssignment((VariableAssignment) node);
             }

             if(node instanceof Stylerule){
                 scopes.openScope();
                 Stylerule stylerule = (Stylerule) node;
                 checkStyleBody(stylerule.body);
                 scopes.closeScope();
             }
         }

         scopes.closeScope();
    }

    private void checkStyleBody(ArrayList<ASTNode> body){
        for(ASTNode bodyNode : body){
            // Variable assignment
            if(bodyNode instanceof VariableAssignment){
                checkVariableAssignment((VariableAssignment) bodyNode);
            }
            // if clause
            if(bodyNode instanceof IfClause){
                checkIfStatement((IfClause) bodyNode);
            }
            // declaration
            if(bodyNode instanceof Declaration){
                checkDeclaration((Declaration) bodyNode);
            }
        }
    }

    private void checkVariableAssignment(VariableAssignment variableAssignment) {
        VariableReference variableReference = variableAssignment.name;
        ExpressionType expressionType = variableAssignment.getExpressionType();
        if(expressionType == null || expressionType == ExpressionType.UNDEFINED){
            variableAssignment.setError("The variable assignment is not valid because of a faulty Expression type");
            return;
        }
        ExpressionType previousType = scopes.getVariableValue(variableReference.name);
        if(previousType != null && expressionType != previousType){
            variableAssignment.setError("The variable " + variableReference.name +" can't change type from " + previousType.toString() + " to " + expressionType.toString());
            return;
        }
        scopes.addVariableToScope(variableReference.name, expressionType);
    }

    private void checkIfStatement(IfClause ifClause){
        scopes.openScope();
        checkIfStatementExpressionType(ifClause);
        checkStyleBody(ifClause.body);

        if(ifClause.elseClause != null){
            checkStyleBody(ifClause.elseClause.body);
        }
        scopes.closeScope();
    }

    //CH05
    private void checkIfStatementExpressionType(IfClause ifClause){
        Expression expression = ifClause.conditionalExpression;
        ExpressionType expressionType = getExpressionTypeOfExpression(expression);
        if(expressionType != ExpressionType.BOOL){
            ifClause.setError("A conditional expression has to be of type boolean.");
        }
    }

    //CH01 & CH06
    private ExpressionType getExpressionTypeOfExpression(Expression expression){
        ExpressionType expressionType;
        if(expression instanceof VariableReference){
            VariableReference variableReference = (VariableReference) expression;
            expressionType = scopes.getVariableValue(variableReference.name);
            if(expressionType == null || expressionType == ExpressionType.UNDEFINED){
                expression.setError("Variable " + variableReference.name + " is not defined");
            }
        } else if(expression instanceof Operation){
            Operation operation = (Operation) expression;
            expressionType = checkOperation(operation);
        } else {
            expressionType = expression.getExpressionType();
        }
        return expressionType;
    }

    // CH04
    private void checkDeclaration(Declaration declaration){
        ExpressionType expressionType = getExpressionTypeOfExpression(declaration.expression);
        String name = declaration.property.name;
        String errorMessage = null;
        switch (name) {
            case "background-color":
                if (expressionType != ExpressionType.COLOR) {
                    errorMessage = "The background-color property can only contain a color literal";
                }
                break;
            case "width":
                if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE) {
                    errorMessage = "The width property can only contain a pixel or percentage literal";
                }
                break;
            case "color":
                if (expressionType != ExpressionType.COLOR) {
                    errorMessage = "The color property can only contain a color literal";
                }
                break;
            case "height":
                if (expressionType != ExpressionType.PIXEL && expressionType != ExpressionType.PERCENTAGE) {
                    errorMessage = "The height property can only contain a pixel or percentage literal";
                }
                break;
            default:
                errorMessage = "The property " + name + " is not a valid property";
        }

        if (errorMessage != null) {
            declaration.setError(errorMessage);
        }
    }

    // CH02 & CH03
    private ExpressionType checkOperation(Operation operation){
        Expression left = operation.lhs;
        Expression right = operation.rhs;
        ExpressionType leftType = getExpressionTypeOfExpression(left);
        ExpressionType rightType = getExpressionTypeOfExpression(right);

        if(leftType == ExpressionType.COLOR || rightType == ExpressionType.COLOR){
            operation.setError("A color literal can not be used in an operation");
            return ExpressionType.UNDEFINED;
        }

        if(leftType == ExpressionType.BOOL || rightType == ExpressionType.BOOL){
            operation.setError("A boolean literal can not be used in an operation");
            return ExpressionType.UNDEFINED;
        }

        // Check if one value is Scalar when multiplying
        if(operation instanceof MultiplyOperation){
            if(leftType != ExpressionType.SCALAR && rightType != ExpressionType.SCALAR){
                operation.setError("When multiplying, one value has to be a scalar literal");
                return ExpressionType.UNDEFINED;
            }
            if(leftType == ExpressionType.SCALAR){
                return rightType;
            } else {
                return leftType;
            }
        }

        // Check if left and right is the same type
        if((operation instanceof AddOperation || operation instanceof SubtractOperation) && leftType != rightType){
            operation.setError("You can only add or subtract when both values have the same literal");
            return ExpressionType.UNDEFINED;
        }
        return leftType;
    }

}
