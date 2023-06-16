package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.Expression;
import nl.han.ica.icss.ast.Operation;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.types.ExpressionType;

public class OperationChecker {

    private final ExpressionHandler expressionHandler;

    public OperationChecker(ExpressionHandler expressionHandler){
        this.expressionHandler = expressionHandler;
    }

    // CH02 & CH03
    public ExpressionType checkOperation(Operation operation){
        Expression left = operation.lhs;
        Expression right = operation.rhs;
        ExpressionType leftType = expressionHandler.getExpressionTypeOfExpression(left);
        ExpressionType rightType = expressionHandler.getExpressionTypeOfExpression(right);

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
