package me.danidev.core.utils.menu.callback;

import java.io.Serializable;

public interface TypeCallback<T> extends Serializable {

    void callback(T data);

}
