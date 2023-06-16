package nl.han.ica.icss.checker;


import nl.han.ica.datastructures.ScopeTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.types.ExpressionType;

import java.util.ArrayList;


public class Checker {

    private final ScopeTable<String, ExpressionType> scopes;
    private final ExpressionHandler expressionHandler;

    private final DeclarationChecker declarationChecker;
    private final VariableChecker variableChecker;

    public Checker(){
        this.scopes = new ScopeTable<>();
        this.expressionHandler = new ExpressionHandler(scopes);
        this.declarationChecker = new DeclarationChecker(expressionHandler);
        this.variableChecker = new VariableChecker(scopes);
    }

    public void check(AST ast) {
         Stylesheet stylesheet = ast.root;
         scopes.openScope();

         for(ASTNode node : stylesheet.getChildren()){
             if(node instanceof VariableAssignment){
                 variableChecker.checkVariableAssignment((VariableAssignment) node);
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

    public void checkStyleBody(ArrayList<ASTNode> body){
        for(ASTNode bodyNode : body){
            // Variable assignment
            if(bodyNode instanceof VariableAssignment){
                variableChecker.checkVariableAssignment((VariableAssignment) bodyNode);
            }
            // if clause
            if(bodyNode instanceof IfClause){
                checkIfStatement((IfClause) bodyNode);
            }
            // declaration
            if(bodyNode instanceof Declaration){
                declarationChecker.checkDeclaration((Declaration) bodyNode);
            }
        }
    }

    public void checkIfStatement(IfClause ifClause){
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
        ExpressionType expressionType = expressionHandler.getExpressionTypeOfExpression(expression);
        if(expressionType != ExpressionType.BOOL){
            ifClause.setError("A conditional expression has to be of type boolean.");
        }
    }

}
