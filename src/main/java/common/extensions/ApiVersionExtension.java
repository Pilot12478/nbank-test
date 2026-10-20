package common.extensions;

import api.configs.Config;
import common.annotations.ApiVersion;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

public class ApiVersionExtension implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        String apiVersionConf = Config.versionApi();
        String apiVersionTest = findAnnotation(context).versionApi();
        if( apiVersionTest ==null){
            return ConditionEvaluationResult.enabled("No @ApiVersion annotation found");
        }
        if(apiVersionTest.equals(apiVersionConf)){
            return ConditionEvaluationResult.enabled("version equals: " + apiVersionConf);
        }
        return ConditionEvaluationResult.disabled(
              "Test requires '" + apiVersionTest + "' but configured '" + apiVersionConf + "'");

    }

    public ApiVersion findAnnotation(ExtensionContext context) {
        return AnnotationSupport.findAnnotation(context.getTestMethod(), ApiVersion.class)
                .or(() -> AnnotationSupport.findAnnotation(context.getRequiredTestClass(), ApiVersion.class)).
                orElse(null);


    }

}
