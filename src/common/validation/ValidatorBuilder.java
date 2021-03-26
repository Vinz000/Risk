package common.validation;

@FunctionalInterface
public interface ValidatorBuilder<T> {
    Validator build(T input);
}
