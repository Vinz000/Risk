package common;

import javafx.application.Platform;

import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.function.Supplier;

public class BaseComponent implements Observer {

    private final Optional<ObserverCallback> nullableOnUpdate;

    public BaseComponent(Runnable buildComponent, Runnable setCssId, Supplier<Observable[]> modelsToObserve, ObserverCallback onUpdate) {
        setCssId.run();
        observeModels(modelsToObserve);
        buildComponent.run();

        this.nullableOnUpdate = Optional.ofNullable(onUpdate);
    }

    private void observeModels(Supplier<Observable[]> models) {
        for (Observable model : models.get()) {
            model.addObserver(this);
        }
    }

    public static BaseComponent build(Runnable buildComponent, Runnable setCssId, Supplier<Observable[]> modelsToObserve, ObserverCallback onUpdate) {
        return new BaseComponent(buildComponent, setCssId, modelsToObserve, onUpdate);
    }

    public static BaseComponent build(Runnable buildComponent, Supplier<Observable[]> modelsToObserve, ObserverCallback onUpdate) {
        return new BaseComponent(buildComponent, () -> {
        }, modelsToObserve, onUpdate);
    }

    public static BaseComponent build(Runnable buildComponent, Runnable setCssId) {
        return new BaseComponent(buildComponent, setCssId, () -> new Observable[0], null);
    }

    public static BaseComponent build(Runnable buildComponent) {
        return new BaseComponent(buildComponent, () -> {
        }, () -> new Observable[0], null);
    }

    @Override
    public void update(Observable o, Object arg) {
        nullableOnUpdate.ifPresent(onUpdate -> Platform.runLater(() -> onUpdate.run(o, arg)));
    }
}
