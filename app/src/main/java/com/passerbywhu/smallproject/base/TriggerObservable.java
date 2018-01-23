package com.passerbywhu.smallproject.base;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.disposables.Disposable;

public class TriggerObservable<T> extends Observable<T> {
    private Observer<? super T> observer;
    private Disposable disposable;

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        this.observer = observer;
        disposable = new MainThreadDisposable() {
            @Override
            protected void onDispose() {
                //nothing to do
            }
        };
        observer.onSubscribe(disposable);
    }

    public void trigger(T value) {
        if(!disposable.isDisposed()) {
            observer.onNext(value);
            observer.onComplete();
        }
    }
}
