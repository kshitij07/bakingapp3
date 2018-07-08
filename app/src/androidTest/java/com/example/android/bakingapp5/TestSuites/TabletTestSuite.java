package com.example.android.bakingapp5.TestSuites;

import com.example.android.bakingapp5.DetailsActivityTests.DetailsActivityBasicTabletTests;
import com.example.android.bakingapp5.MainActivityTests.MainActivityTabletTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Suite that contains all tests for tablets
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainActivityTabletTests.class,
        DetailsActivityBasicTabletTests.class,
})

public class TabletTestSuite {
    // the class remains empty,
    // used only as a holder for the above annotations
}
