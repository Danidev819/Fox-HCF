package me.danidev.core.utils;

import javax.annotation.Nullable;
import com.google.common.base.Preconditions;
import com.google.common.base.Optional;

public class GuavaCompat
{
    public static <T extends Enum<T>> Optional<T> getIfPresent(final Class<T> enumClass, final String value) {
        Preconditions.checkNotNull(enumClass);
        Preconditions.checkNotNull(value);
        try {
            return Optional.of(Enum.valueOf(enumClass, value));
        }
        catch (IllegalArgumentException iae) {
            return Optional.absent();
        }
    }
    
    public static <T> T firstNonNull(@Nullable final T first, @Nullable final T second) {
        return (T)((first != null) ? first : Preconditions.checkNotNull(second));
    }
}
