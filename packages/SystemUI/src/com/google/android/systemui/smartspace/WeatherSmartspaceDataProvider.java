package com.google.android.systemui.smartspace;

import android.content.Context;
import android.app.smartspace.SmartspaceTarget;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.systemui.plugins.BcSmartspaceDataPlugin;
import com.android.systemui.res.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class WeatherSmartspaceDataProvider
        implements BcSmartspaceDataPlugin {

    private static final boolean DEBUG =
            Log.isLoggable("WeatherSSDataProvider", Log.DEBUG);

    private final Set<SmartspaceTargetListener> mListeners =
            new HashSet<>();
    private final List<SmartspaceTarget> mTargets =
            new ArrayList<>();
    private final EventNotifierProxy mEventNotifier =
            new EventNotifierProxy();

    @Override
    public SmartspaceEventNotifier getEventNotifier() {
        return mEventNotifier;
    }

    @Override
    public SmartspaceView getView(Context context) {
     return (SmartspaceView) LayoutInflater.from(context)
               .inflate(R.layout.weather, null);
    }

    @Override
    public SmartspaceView getLargeClockView(Context context) {
    View view = LayoutInflater.from(context)
            .inflate(R.layout.weather_large, null);
        view.setId(R.id.weather_smartspace_view_large);
        return (SmartspaceView) view;
    }

    @Override
    public void onTargetsAvailable(List<SmartspaceTarget> targets) {
        if (DEBUG) {
            Log.d("WeatherSSDataProvider",
                    this + " onTargetsAvailable. Callers="
                            + Debug.getCallers(3));
        }

        mTargets.clear();
        for (SmartspaceTarget target : targets) {
            if (target.getFeatureType() == 1) {
                mTargets.add(target);
            }
        }

        mListeners.forEach(
                l -> l.onSmartspaceTargetsUpdated(mTargets));
    }

    @Override
    public void registerListener(SmartspaceTargetListener listener) {
        mListeners.add(listener);
        listener.onSmartspaceTargetsUpdated(mTargets);
    }

    @Override
    public void unregisterListener(SmartspaceTargetListener listener) {
        mListeners.remove(listener);
    }

    @Override
    public void setEventDispatcher(SmartspaceEventDispatcher dispatcher) {
        mEventNotifier.eventDispatcher = dispatcher;
    }

    @Override
    public void setIntentStarter(IntentStarter intentStarter) {
        mEventNotifier.intentStarterRef = intentStarter;
    }
}
