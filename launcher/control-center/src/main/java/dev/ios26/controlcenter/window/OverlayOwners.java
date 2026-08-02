package dev.ios26.controlcenter.window;

import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;
import androidx.savedstate.ViewTreeSavedStateRegistryOwner;

/**
 * Attaches the owners ComposeView.setContent requires (lifecycle + saved state) to the
 * overlay's view tree. Written in Java: this toolchain's Kotlin compiler cannot resolve
 * the KMP-published ViewTree helper classes and SavedStateRegistryOwner declarations, but
 * their JVM forms are plain public API — Java consumes them without issue.
 */
public final class OverlayOwners {

    private OverlayOwners() {
    }

    public static void attach(View view) {
        LifecycleRegistry[] lifecycleHolder = new LifecycleRegistry[1];
        lifecycleHolder[0] = new LifecycleRegistry(new LifecycleOwner() {
            @Override
            public Lifecycle getLifecycle() {
                return lifecycleHolder[0];
            }
        });
        // The saved-state controller requires the owner's lifecycle to be at INITIALIZED
        // when created ("Restarter must be created during owner's initialization stage");
        // it is moved to RESUMED after attach+restore.
        lifecycleHolder[0].setCurrentState(Lifecycle.State.INITIALIZED);

        final SavedStateRegistryController[] controllerHolder = new SavedStateRegistryController[1];
        SavedStateRegistryOwner owner = new SavedStateRegistryOwner() {
            @Override
            public SavedStateRegistry getSavedStateRegistry() {
                return controllerHolder[0].getSavedStateRegistry();
            }

            @Override
            public Lifecycle getLifecycle() {
                return lifecycleHolder[0];
            }
        };
        controllerHolder[0] = SavedStateRegistryController.create(owner);
        try {
            controllerHolder[0].performAttach();
            controllerHolder[0].performRestore((Bundle) null);
        } catch (Throwable t) {
            android.util.Log.e("IOS26_CC", "registry lifecycle: " + t);
        }
        lifecycleHolder[0].setCurrentState(Lifecycle.State.RESUMED);

        ViewTreeLifecycleOwner.set(view, owner);
        ViewTreeSavedStateRegistryOwner.set(view, owner);
    }
}
