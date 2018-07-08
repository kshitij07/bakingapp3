package com.example.android.bakingapp5.TestSuites;

import com.example.android.bakingapp5.DetailsActivityTests.DetailsActivityBasicPhoneTests;
import com.example.android.bakingapp5.MainActivityTests.MainActivityPhoneTests;
import com.example.android.bakingapp5.StepsListActivityTests.StepsListBasicPhoneTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite that contains all tests for phones
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainActivityPhoneTests.class,
        DetailsActivityBasicPhoneTests.class,
        StepsListBasicPhoneTests.class
})

public class PhoneTestSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
