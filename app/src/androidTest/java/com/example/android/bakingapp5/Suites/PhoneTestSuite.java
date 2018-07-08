package com.example.android.bakingapp5.Suites;

import com.example.android.bakingapp5.MainActivityTests.MainActivityBasicGeneralTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite that contains all tests for phones
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainActivityBasicGeneralTests.class
})

public class PhoneTestSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
