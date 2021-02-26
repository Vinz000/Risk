package common.validation;

public class ValidatorResponse {
    private final boolean isValid;
    private final String message;

    public boolean isValid() {
        return isValid;
    }

    public String getMessage() {
        String messagePrefix = "✗ Invalid input: ";
        boolean addPrefix = message != null && !message.contains(messagePrefix);

        return addPrefix ?
                messagePrefix + message :
                message;
    }

    public ValidatorResponse(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

    /**
     * Provide only the message and infer
     * isValid.
     *
     * @param message to be logged by ShellComponent.
     * @see shell.component.ShellComponent
     */
    public ValidatorResponse(String message) {
        this.message = message;
        this.isValid = message == null;
    }

    public static ValidatorResponse validNoMessage() {
        return new ValidatorResponse(true, null);
    }

    public static ValidatorResponse valid(String message) {
        return new ValidatorResponse(true, message);
    }

    public static ValidatorResponse invalid(String message) {
        return new ValidatorResponse(false, message);
    }
}
