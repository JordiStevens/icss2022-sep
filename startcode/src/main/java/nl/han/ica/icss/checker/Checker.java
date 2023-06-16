package nl.han.ica.icss.checker;

import nl.han.ica.datastructures.ScopeTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;




public class Checker {

    private final ScopeTable<String, ExpressionType> scopes = new ScopeTable<>();

    public void check(AST ast) {
         Stylesheet stylesheet = ast.root;
         scopes.openScope();

         for(ASTNode node : stylesheet.getChildren()){
             if(node instanceof VariableAssignment){
                 checkVariableAssignment(node);
             }
         }

         scopes.closeScope();
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

}
