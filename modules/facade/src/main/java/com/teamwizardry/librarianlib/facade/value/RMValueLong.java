package com.teamwizardry.librarianlib.facade.value;

import com.teamwizardry.librarianlib.math.Easing;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class RMValueLong {
    private long value;
    @Nullable
    private final ChangeListener.Long change;

    public RMValueLong(long initialValue) {
        this.value = initialValue;
        this.change = null;
    }

    public RMValueLong(long initialValue, @NotNull ChangeListener.Long change) {
        this.value = initialValue;
        this.change = change;
    }

    /**
     * Gets the current value
     */
    public long get() {
        return value;
    }

    /**
     * Sets the value. This isn't often called as most classes will provide a delegated property to directly
     * access this value (`someProperty` will call into `somePropery_rm` for its value)
     */
    public void set(long value) {
        this.value = value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public long getValue(Object thisRef, KProperty property) {
        return value;
    }

    /**
     * A kotlin delegate method, used to allow properties to delegate to this IMValue (`var property by property_rm`)
     */
    public void setValue(Object thisRef, KProperty property, long value) {
        set(value);
    }
}