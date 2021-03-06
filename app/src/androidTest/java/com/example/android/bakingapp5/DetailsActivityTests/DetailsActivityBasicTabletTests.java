package com.example.android.bakingapp5.DetailsActivityTests;

import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp5.CustomAssertions.RecyclerViewNumberOfItemsAssertion;
import com.example.android.bakingapp5.CustomMatchers.RecyclerViewMatcher;
import com.example.android.bakingapp5.DetailsActivity;
import com.example.android.bakingapp5.R;
import com.example.android.bakingapp5.RecipesData.Ingredient;
import com.example.android.bakingapp5.RecipesData.Recipe;
import com.example.android.bakingapp5.RecipesData.Step;
import com.example.android.bakingapp5.TestsHelperMethods.RecipeDataHelperMethods;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test to verify Details Activity's basic functionality
 */

@RunWith(AndroidJUnit4.class)
public class DetailsActivityBasicTabletTests {

     /*
     * Fields
     */

    private ArrayList<Ingredient> mIngredientsArrayList = new ArrayList<Ingredient>();
    private ArrayList<Step> mStepsArrayList = new ArrayList<Step>();

    private int mNumIngredients = 6;
    private int mNumSteps = 6;

    private Recipe mRecipe;

    /*
     * Test set up
     */

    @Rule
    public IntentsTestRule<DetailsActivity> intentsTestRule =
            new IntentsTestRule<DetailsActivity>(DetailsActivity.class, true, true) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, DetailsActivity.class);

                    RecipeDataHelperMethods.populateIngredientsArray(mIngredientsArrayList, mNumIngredients);
                    RecipeDataHelperMethods.populateStepsArray(mStepsArrayList, mNumSteps);

                    mRecipe = new Recipe("RecipeName", mIngredientsArrayList,
                            mStepsArrayList, 8, "recipe1");

                    // Sending necessary recipe data as extras
                    result.putExtra("recipeObject", mRecipe);
                    result.putExtra("isTabletLayout", true);

                    return result;
                }
            };

    /*
     * Tests
     */

    @Test
    public void detailsActivityTitle_DisplaysCorrectly() {
        onView(withText("BakingApp - " + "RecipeName")).check(matches(isDisplayed()));
    }

    @Test
    public void stepsList_IsDisplayedCorrectly() {
        onView(withId(R.id.steps_list_fragment_view)).check(matches(isDisplayed()));
    }

    @Test
    public void stepDetails_ShowsIngredientsByDefault() {
        onView(withText("RecipeName")).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnGenericStep_UpdatesDetailsScreen() {
        onView(withId(R.id.step_details_frame_layout)).check(matches(hasDescendant(withText("RecipeName"))));
        onView(withRecyclerView(R.id.steps_list_recycler_view).atPosition(3)).perform(click());
        onView(withText(mRecipe.getRecipeSteps().get(2).getStepDescription())).check(matches(isDisplayed()));
    }

    @Test
    public void stepsShortDescription_IsDisplayed() {
        onView(withRecyclerView(R.id.steps_list_recycler_view).atPosition(3)).perform(click());
        onView(withText(mRecipe.getRecipeSteps().get(2).getStepShortDescription())).check(matches(isDisplayed()));
    }

    @Test
    public void stepsList_IsPopulatedCorrectly() {
        onView(withId(R.id.steps_list_recycler_view)).check(new RecyclerViewNumberOfItemsAssertion(mRecipe.getRecipeSteps().size() + 1));
    }

    @Test
    public void videoLogos_AreDisplayedCorrectly() {
        onView(withRecyclerView(R.id.steps_list_recycler_view).atPosition(2))
                .check(matches(hasDescendant(withContentDescription("step_video_logo"))));
    }

    @Test
    public void clickOnStepWithVideo_DisplaysVideo() {
        onView(withRecyclerView(R.id.steps_list_recycler_view).atPosition(1)).perform(click());
        onView(withId(R.id.step_exoplayer_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        onView(withId(R.id.default_step_image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void clickOnStepWithNoVideo_DisplaysPlaceholderImage() {
        onView(withRecyclerView(R.id.steps_list_recycler_view).atPosition(2)).perform(click());
        onView(withId(R.id.step_exoplayer_view)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        onView(withId(R.id.default_step_image)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    /*
     * Helper methods
     */

    private static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
        return new RecyclerViewMatcher(recyclerViewId);
    }
}
