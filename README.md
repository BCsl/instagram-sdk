![kHttp](./docs/assets/logo.svg)

## Instagram-SDK
An unofficial Instagram SDK for Android written in Kotlin.

[![Kotlin](https://img.shields.io/badge/Kotlin-1.3.11-blue.svg?style=flat-square)](http://kotlinlang.org)
[![Build Status](https://img.shields.io/travis/Karn/instagram-sdk.svg?style=flat-square)](https://travis-ci.org/Karn/instagram-sdk)
[![Codecov](https://img.shields.io/codecov/c/github/karn/instagram-sdk.svg?style=flat-square)](https://codecov.io/gh/Karn/instagram-sdk)
[![GitHub (pre-)release](https://img.shields.io/github/release/karn/instagram-sdk/all.svg?style=flat-square)
](./../../releases)


#### GETTING STARTED
Instagram-SDK (pre-)releases are available via JitPack. It is recommended that a specific release version is selected when using the library in production as there may be breaking changes at anytime.

> **Tip:** Test out the canary channel to try out features by using the latest develop snapshot; `develop-SNAPSHOT`.

```Groovy
// Project level build.gradle
// ...
repositories {
    maven { url 'https://jitpack.io' }
}
// ...

// Module level build.gradle
dependencies {
    // Replace version with release version, e.g. 1.0.0-alpha, -SNAPSHOT
    implementation "io.karn:instagram-sdk:[VERSION]"
}
```

#### USAGE
The most basic case is as follows:

```Kotlin
// Initialize the SDK -- do this before attempting to use the rest of the SDK functions.
Instagram.init()

// Attempt to sign-in to an account.
Instagram.getInstance().authentication.authenticate("username", "password")
// Print the response.
Log.v("SDK", "Auth Response: $res")
```

#### CONTRIBUTING
There are many ways to [contribute](./.github/CONTRIBUTING.md), you can
- submit bugs,
- help track issues,
- review code changes.