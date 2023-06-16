package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.ScopeTable;
import nl.han.ica.icss.ast.VariableAssignment;
import nl.han.ica.icss.ast.VariableReference;
import nl.han.ica.icss.ast.types.ExpressionType;

public class VariableChecker {

    ScopeTable<String, ExpressionType> scopes;
    ExpressionHandler expressionHandler;

    public VariableChecker(ScopeTable<String, ExpressionType> scopes, ExpressionHandler expressionHandler){
        this.scopes = scopes;
        this.expressionHandler = expressionHandler;
    }

    public void checkVariableAssignment(VariableAssignment variableAssignment) {
        VariableReference variableReference = variableAssignment.name;
        ExpressionType expressionType = expressionHandler.getExpressionTypeOfExpression(variableAssignment.expression);
        if(expressionType == null){
            variableAssignment.setError("The variable assignment of " + variableReference.name + " is not valid because of a faulty Expression type");
            return;
        }
        ExpressionType previousType = scopes.getVariableValue(variableReference.name);
        if(previousType != null && expressionType != previousType){
            variableAssignment.setError("The variable " + variableReference.name +" can't change type from " + previousType.toString() + " to " + expressionType.toString());
            return;
        }
        scopes.addVariableToScope(variableReference.name, expressionType);
    }
}
