package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.ScopeTable;
import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.types.ExpressionType;

public class ExpressionHandler {

    private final ScopeTable<String, ExpressionType> scopes;
    private final OperationChecker operationChecker;

    public ExpressionHandler(ScopeTable<String, ExpressionType> scopes){
        this.scopes = scopes;
        this.operationChecker = new OperationChecker(this);
    }

    //CH01 & CH06
    public ExpressionType getExpressionTypeOfExpression(Expression expression){
        ExpressionType expressionType;
        if(expression instanceof VariableReference){
            VariableReference variableReference = (VariableReference) expression;
            expressionType = scopes.getVariableValue(variableReference.name);
            if(expressionType == null || expressionType == ExpressionType.UNDEFINED){
                expression.setError("Variable " + variableReference.name + " is not defined");
            }
        } else if(expression instanceof Operation){
            Operation operation = (Operation) expression;
            expressionType = operationChecker.checkOperation(operation);
        } else {
            expressionType = expression.getExpressionType();
        }
        return expressionType;
    }
}
