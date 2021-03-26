package common.validation;

@FunctionalInterface
public interface Validator {
    ValidatorResponse validate(String userInput);
}
