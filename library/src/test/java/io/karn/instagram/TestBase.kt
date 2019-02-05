package io.karn.instagram

import android.app.Application
import org.robolectric.RuntimeEnvironment

open class TestBase {

    val applicationContext: Application = RuntimeEnvironment.application
}
