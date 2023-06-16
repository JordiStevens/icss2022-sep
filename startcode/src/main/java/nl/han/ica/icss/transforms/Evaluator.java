package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.IHANLinkedList;
import nl.han.ica.datastructures.ScopeTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Evaluator implements Transform {

    private ScopeTable<String, Literal> scopes;

    public Evaluator() {
        scopes = new ScopeTable<>();
    }

    @Override
    public void apply(AST ast) {
        Stylesheet stylesheet = ast.root;
        List<ASTNode> removeNodeList = new ArrayList<>();
        scopes.openScope();
        for(ASTNode node : stylesheet.getChildren()){
            if(node instanceof VariableAssignment){
                transformVariableAssignment((VariableAssignment) node);
                removeNodeList.add(node);
            }

            if(node instanceof Stylerule){
                Stylerule stylerule = (Stylerule) node;
                ArrayList<ASTNode> elementsToAdd = new ArrayList<>();
                scopes.openScope();
                for(ASTNode bodyNode : stylerule.body){
                    transformBody(bodyNode, elementsToAdd);
                }
                scopes.closeScope();
                stylerule.body = elementsToAdd;
            }
        }
        scopes.closeScope();
        removeNodeList.forEach(stylesheet::removeChild);
    }

    private void transformBody(ASTNode node, List<ASTNode> parentBody){
        if(node instanceof VariableAssignment){
            transformVariableAssignment((VariableAssignment) node);
            return;
        }
        if(node instanceof Declaration){
            Declaration declaration = (Declaration) node;
            declaration.expression = transformExpression(declaration.expression);
            parentBody.add(node);
            return;
        }

        if(node instanceof IfClause){
            IfClause ifClause = (IfClause) node;
            BoolLiteral bool = (BoolLiteral) transformExpression(ifClause.conditionalExpression);

            removeIfElseStatements(ifClause, bool);
            for(ASTNode ifBody : ifClause.getChildren()){
                transformBody(ifBody, parentBody);
            }
        }
    }

    private static void removeIfElseStatements(IfClause ifClause, BoolLiteral bool) {
        if(bool.value){
            if(ifClause.elseClause != null){
                ifClause.elseClause.body = new ArrayList<>();
            }
        } else {
            if(ifClause.elseClause != null){
                ifClause.body = ifClause.elseClause.body;
                ifClause.elseClause.body = new ArrayList<>();
            } else {
                ifClause.body = new ArrayList<>();
            }
        }
    }

    private void transformVariableAssignment(VariableAssignment variableAssignment){
        Literal literal = transformExpression(variableAssignment.expression);
        scopes.addVariableToScope(variableAssignment.name.name, literal);
    }

    private Literal transformExpression(Expression expression){
        if(expression instanceof VariableReference){
            return scopes.getVariableValue(((VariableReference) expression).name);
        } else if( expression instanceof Operation){
            return transformOperation((Operation) expression);
        } else {
            return (Literal) expression;
        }
    }

    private Literal transformOperation(Operation operation){
        Literal left = transformExpression(operation.lhs);
        Literal right = transformExpression(operation.rhs);

        int leftValue = getOperationLiteralValue(left);
        int rightValue = getOperationLiteralValue(right);

        if(operation instanceof SubtractOperation){
            return createOperationResultLiteral(left, leftValue - rightValue);
        } else if(operation instanceof AddOperation){
            return createOperationResultLiteral(left, leftValue + rightValue);
        } else if(operation instanceof MultiplyOperation){
            //Need to make sure that the Scalar literal is not returned because it is not a valid value for a property
            if(left instanceof ScalarLiteral){
                return createOperationResultLiteral(right, leftValue * rightValue);
            } else {
                return createOperationResultLiteral(left, leftValue * rightValue);
            }
        }
        return null;
    }

    private int getOperationLiteralValue(Literal literal){
        //Since the checker should have detected if a boolean or color is used in the operation we can assume that it is safe.
        if(literal instanceof ScalarLiteral){
           return  ((ScalarLiteral) literal).value;
        } else if(literal instanceof PixelLiteral){
            return ((PixelLiteral) literal).value;
        } else {
            return ((PercentageLiteral) literal).value;
        }
    }


    private Literal createOperationResultLiteral(Literal literal, int value){
        if(literal instanceof PixelLiteral){
            return new PixelLiteral(value);
        } else {
            return new PercentageLiteral(value);
        }
    }
    
}
