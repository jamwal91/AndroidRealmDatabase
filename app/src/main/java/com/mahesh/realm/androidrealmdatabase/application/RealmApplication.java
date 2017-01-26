package com.mahesh.realm.androidrealmdatabase.application;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.mahesh.realm.androidrealmdatabase.database.SharedPreferencesManager;
import com.mahesh.realm.androidrealmdatabase.definition.SharedPrefConstants;

import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmApplication extends Application {

    private static RealmApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        mInstance = this;

        Realm.setDefaultConfiguration(getRealmConfiguration());
    }

    public static RealmApplication getInstance() {
        return mInstance;
    }

    private RealmConfiguration getRealmConfiguration() {
        return new RealmConfiguration.Builder()
                .name(Realm.DEFAULT_REALM_NAME)
                .encryptionKey(getKey())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    private static byte[] getKey() {
        if (isEqual(SharedPreferencesManager.with(mInstance).getString(SharedPrefConstants.PREF_STORE_DATABASE_SECRET_KEY, ""), "")) {
            byte[] key = new byte[64];
            new SecureRandom().nextBytes(key);

            SharedPreferencesManager.with(mInstance).edit().putString(SharedPrefConstants.PREF_STORE_DATABASE_SECRET_KEY, Base64.encodeToString(key, 64)).
                    apply();
        }

        return Base64.decode(SharedPreferencesManager.with(mInstance).getString(SharedPrefConstants.PREF_STORE_DATABASE_SECRET_KEY, ""), 64);
    }

    private static boolean isEqual(@Nullable Object o1, Object o2) {
        return o1 == o2 || (o1 != null && o1.equals(o2));
    }
}
