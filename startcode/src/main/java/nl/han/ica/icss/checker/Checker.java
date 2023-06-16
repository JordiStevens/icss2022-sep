package nl.han.ica.icss.checker;


import nl.han.ica.datastructures.ScopeTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;


public class Checker {

    private final ScopeTable<String, ExpressionType> scopes = new ScopeTable<>();

    public void check(AST ast) {
         Stylesheet stylesheet = ast.root;
         scopes.openScope();

         for(ASTNode node : stylesheet.getChildren()){
             if(node instanceof VariableAssignment){
                 checkVariableAssignment(node);
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
                checkVariableAssignment(bodyNode);
            }
            // if clause
            if(bodyNode instanceof IfClause){
                checkIfStatement(bodyNode);
            }
            // declaration

        }
    }

    private void checkVariableAssignment(ASTNode node) {
        VariableAssignment variableAssignment = (VariableAssignment) node;
        VariableReference variableReference = variableAssignment.name;
        ExpressionType expressionType = variableAssignment.getExpressionType();
        if(expressionType == null || expressionType == ExpressionType.UNDEFINED){
            node.setError("The variable assignment is not valid because of a faulty Expression type");
            return;
        }
        ExpressionType previousType = scopes.getVariableValue(variableReference.name);
        if(previousType != null && expressionType != previousType){
            node.setError("The variable " + variableReference.name +" can't change type from " + previousType.toString() + " to " + expressionType.toString());
            return;
        }
        scopes.addVariableToScope(variableReference.name, expressionType);
    }

    private void checkIfStatement(ASTNode node){
        IfClause ifClause = (IfClause) node;
        scopes.openScope();
        // Check if the condition is of type Boolean
        checkIfStatementExpressionType(ifClause);
        // Check the body of the if clause
        checkStyleBody(ifClause.body);

        if(ifClause.elseClause != null){
            checkStyleBody(ifClause.elseClause.body);
        }
        scopes.closeScope();
    }

    private void checkIfStatementExpressionType(IfClause ifClause){
        Expression expression = ifClause.conditionalExpression;
        ExpressionType expressionType = getExpressionTypeOfExpression(expression);
        if(expressionType != ExpressionType.BOOL){
            ifClause.setError("A conditional expression has to be of type boolean");
        }
    }

    private ExpressionType getExpressionTypeOfExpression(Expression expression){
        ExpressionType expressionType;
        if(expression instanceof VariableReference){
            VariableReference variableReference = (VariableReference) expression;
            expressionType = scopes.getVariableValue(variableReference.name);
            if(expressionType == null){
                expression.setError("Variable " + variableReference.name + " is not defined!");
            }
        } else {
            expressionType = expression.getExpressionType();
        }
        return expressionType;
    }

}
