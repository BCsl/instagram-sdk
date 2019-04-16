package io.karn.instagram

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
        AccountTest::class,
        DirectMessagesTest::class,
        MediaTest::class,
        SearchTest::class,
        StoriesTest::class
)
class SignedInTestSuite
