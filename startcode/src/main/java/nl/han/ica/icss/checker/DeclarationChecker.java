package nl.han.ica.icss.checker;

import nl.han.ica.icss.ast.Declaration;
import nl.han.ica.icss.ast.types.ExpressionType;

public class DeclarationChecker {

    private final ExpressionHandler expressionHandler;

    public DeclarationChecker(ExpressionHandler expressionHandler){
        this.expressionHandler = expressionHandler;
    }

    // CH04
    public void checkDeclaration(Declaration declaration){
        ExpressionType expressionType = expressionHandler.getExpressionTypeOfExpression(declaration.expression);
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
}
