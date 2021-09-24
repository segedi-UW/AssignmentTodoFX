package com.todo.assignmenttodofx;

import java.util.prefs.Preferences;

public enum Preference {
    TONE("negative.wav"), MUTE(false), MILITARY(false),
    TRAY(true), VERSION("NO_VERSION"),
    LOAD_DEFAULT(true),
    SHOW_STATE(true),
    AUTO_REMIND(true);

    private enum Type {
        STRING, BOOLEAN
    }

    private static final Preferences preferences = Preferences.userRoot();
    private final Object init;
    private final Type type;

    Preference(Object init) {
        this.init = init;
        if (init instanceof String)
            type = Type.STRING;
        else
            type = Type.BOOLEAN;
    }

    public Object getInitialValue() {
        return init;
    }

    public void reset() {
        switch (type) {
            case BOOLEAN:
                preferences.putBoolean(this.name(), (Boolean) init);
                break;
            case STRING:
                preferences.put(this.name(), (String) init);
                break;
            default:
                break;
        }
    }

    public void put(Object value) {
        switch(type) {
            case BOOLEAN:
                preferences.putBoolean(this.name(), (Boolean) value);
                break;
            case STRING:
                preferences.put(this.name(), (String) value);
                break;
            default:
                throw new IllegalArgumentException("Unknown Type");
        }
    }

    public Boolean getBoolean(boolean ifNull) {
        checkType(Type.BOOLEAN);
        return preferences.getBoolean(this.name(), ifNull);
    }

    public Boolean getBoolean() {
        return getBoolean((Boolean) init);
    }

    public String get(String ifNull) {
        checkType(Type.STRING);
        return preferences.get(this.name(), ifNull);
    }

    public String get() {
        return get((String) init);
    }

    private void checkType(Type toCheck) {
        if (toCheck != type)
            throwTypeError(toCheck);
    }

    private void throwTypeError(Type incorrect) {
        throw new IllegalArgumentException("Cannot retrieve type " + incorrect + " from non " + type + " Preference");
    }
}
