package com.github.instacart.ahoy.tests;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.instacart.ahoy.Ahoy;
import com.github.instacart.ahoy.Event;
import com.github.instacart.ahoy.LifecycleCallbackWrapper;
import com.github.instacart.ahoy.Storage;
import com.github.instacart.ahoy.Visit;
import com.github.instacart.ahoy.delegate.AhoyDelegate;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class NewEventTest {

    private Ahoy ahoy;
    private AhoyDelegate delegate;
    private Storage storage;
    private LifecycleCallbackWrapper wrapper;
    private String visitorToken;

    @Before public void setupAhoy() {
        ahoy = new Ahoy();
        delegate = mock(AhoyDelegate.class);
        storage = mock(Storage.class);
        wrapper = new LifecycleCallbackWrapper();
        visitorToken = UUID.randomUUID().toString();
        when(storage.readVisitorToken(any(String.class))).thenReturn(visitorToken);
        when(storage.readVisit(any(Visit.class))).thenReturn(Visit.empty());

        Timber.uprootAll();
    }

    @Test public void trackEventTest() throws Exception {
        final Visit visit = Visit.create(
                UUID.randomUUID().toString(),
                Collections.emptyMap(),
                System.currentTimeMillis() + 3600);
        when(storage.readVisit(nullable(Visit.class))).thenReturn(visit);
        
        // Create Event object
        Map<String, Object> properties = Collections.singletonMap("key", "value");
        String eventName = "Sample Event";
        Event event = Event.create(eventName, properties);

        when(delegate.newVisitorToken()).thenReturn(visitorToken);
        doAnswer(invocation -> {
            ((AhoyDelegate.AhoyCallback) invocation.getArguments()[3]).onSuccess(null);
            return null;
        }).when(delegate).trackEvent(any(String.class), any(String.class), any(Event.class), any());

        ahoy.init(storage, wrapper, delegate, true);
        wrapper.onActivityCreated(null, null);

        // Track event
        ahoy.trackEvent(event);
        // Verify that the event was tracked
        verify(delegate, timeout(1000)).trackEvent(eq(ahoy.visit().visitToken()), eq(ahoy.visitorToken()), eq(event), any());
    }
}