package io.reark.rxgithubapp.utils.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

import io.reark.reark.utils.Preconditions;

/**
 * Created by Pawel Polanski on 8/7/15.
 */
public class SerialTarget<T> implements Target<T> {

    private volatile State<T> state = new State<>(NullTarget.empty());

    private static final AtomicReferenceFieldUpdater<SerialTarget, State> STATE_UPDATER
            = AtomicReferenceFieldUpdater.newUpdater(SerialTarget.class, State.class, "state");


    private static final class State<T> {

        final Target<T> target;

        State(Target<T> t) {
            target = t;
        }

        State<T> set(@NonNull Target<T> t) {
            return new State<>(t);
        }

    }

    public void set(@NonNull Target<T> s) {
        Preconditions.checkNotNull(s, "Target can not be null");

        State oldState;
        State newState;
        do {
            oldState = state;
            newState = oldState.set(s);
        } while (!STATE_UPDATER.compareAndSet(this, oldState, newState));
        oldState.target.onDestroy();
    }

    public Target<T> get() {
        return state.target;
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        state.target.onLoadStarted(placeholder);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        state.target.onLoadFailed(e, errorDrawable);
    }

    @Override
    public void onResourceReady(T resource, GlideAnimation<? super T> glideAnimation) {
        state.target.onResourceReady(resource, glideAnimation);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        state.target.onLoadCleared(placeholder);
    }

    @Override
    public void getSize(SizeReadyCallback cb) {
        state.target.getSize(cb);
    }

    @Override
    public void setRequest(Request request) {
        state.target.setRequest(request);
    }

    @Override
    public Request getRequest() {
        return state.target.getRequest();
    }

    @Override
    public void onStart() {
        state.target.onStart();
    }

    @Override
    public void onStop() {
        state.target.onStop();
    }

    @Override
    public void onDestroy() {
        state.target.onDestroy();
    }

}
