package games.kidsand.mimikids.app.android

import android.app.Application
import timber.log.Timber

class GameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}