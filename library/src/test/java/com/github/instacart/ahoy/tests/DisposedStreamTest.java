package com.github.instacart.ahoy.tests;

import com.github.instacart.ahoy.RxAhoyDelegate;
import com.github.instacart.ahoy.Visit;
import com.github.instacart.ahoy.delegate.AhoyDelegate;
import com.github.instacart.ahoy.delegate.VisitParams;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import io.reactivex.rxjava3.subscribers.TestSubscriber;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DisposedStreamTest {

    @Test public void testErrorAfterDisposal() throws Throwable {
        AhoyDelegate delegate = mock(AhoyDelegate.class);
        VisitParams params = mock(VisitParams.class);

        TestScheduler scheduler = new TestScheduler();

        doAnswer(
                invocation -> {
                    Observable.timer(1, TimeUnit.SECONDS, scheduler)
                            .subscribe((ignored) -> {
                                ((AhoyDelegate.AhoyCallback) invocation.getArguments()[1])
                                        .onFailure(new IllegalStateException());
                            });
                    return null;
                })
                .when(delegate)
                .saveVisit(any(VisitParams.class), any(AhoyDelegate.AhoyCallback.class));

        Consumer<Throwable> errorHandler = mock(Consumer.class);
        RxJavaPlugins.setErrorHandler(errorHandler);

        Flowable<Visit> stream = RxAhoyDelegate.createNewVisitStream(delegate, params);
        TestSubscriber<Visit> subscriber = new TestSubscriber<>();

        stream.subscribe(subscriber);
        subscriber.cancel();
        scheduler.advanceTimeBy(2, TimeUnit.SECONDS);

        subscriber.assertNoErrors();
        verify(errorHandler, times(0)).accept(any());
    }
}
