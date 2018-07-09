package com.example.android.bakingapp5;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.bakingapp5.RecipesData.Step;
import com.example.android.bakingapp5.Utils.MediaPlayerUtils;
import com.example.android.bakingapp5.Utils.RecipeDataUtils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.example.android.bakingapp5.Utils.MediaPlayerUtils.mStateBuilder;

/**
 * Fragment to display a recipe step
 */

public class StepFragment extends Fragment implements Player.EventListener {

    /*
     * View
     */

    @BindView(R.id.step_exoplayer_view)
    SimpleExoPlayerView mSimpleExoPlayerView;
    @BindView(R.id.step_text_view)
    TextView mStepTextView;
    @BindView(R.id.step_main_layout)
    LinearLayout mStepMainLayout;
    @BindView(R.id.default_step_image)
    ImageView mStepDefaultImageView;


    /*
     * Constants
     */


    private static final String RECIPE_STEP_KEY = "recipe_step";
    private static final String PLAYER_POSITION_KEY = "playerPosition";
    private boolean PLAY_WHEN_READY = false;
    private static int CURRENT_WINDOW;

    /*
     * Fields
     */


    private Step mStep;
    public static SimpleExoPlayer mExoPlayer;

    // Butter Knife
    private Unbinder unbinder;
    private long position;
    private int currentWindow;


    /*
     * Methods
     */


    /**
     * Method that returns an instance of the fragment, adding extras
     * passed as parameters
     *
     * @param recipeStep The corresponding recipe Step
     * @return A StepFragment instance with arguments
     */
    public static StepFragment newInstance(Step recipeStep) {

        StepFragment stepFragment = new StepFragment();

        // Adds arguments
        Bundle args = new Bundle();
        args.putParcelable(RECIPE_STEP_KEY, recipeStep);
        stepFragment.setArguments(args);

        return stepFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step_fragment, container, false);
        PLAY_WHEN_READY = true;
        // Bind views
        unbinder = ButterKnife.bind(this, rootView);

        if (getArguments() != null && getArguments().containsKey(RECIPE_STEP_KEY)) {
            mStep = getArguments().getParcelable(RECIPE_STEP_KEY);
        }

        // Add tah to the root view
        rootView.setTag(R.drawable.ic_fullscreen_expand);
        // Make description scrollable
        makeStepDescriptionScrollable();
        // Set up video player
        setUpExoPlayer();
        // Populate step description
        setStepDescriptionText();

        if(savedInstanceState != null && savedInstanceState.containsKey(PLAYER_POSITION_KEY)) {
            if(mExoPlayer != null) {
                mExoPlayer.seekTo(savedInstanceState.getLong(PLAYER_POSITION_KEY));
            }
        }

        return rootView;
    }

    /*
     * Sets up video player if the step has a video to display.
     * Else, displays a default image instead
     */
    private void setUpExoPlayer() {
        // If the video URL is not empty, set up the video player
        // Else, display the step thumbnail.
        // If the step doesn't have a video or thumbnail, display a default image
        if (!mStep.getStepVideoUrl().equals("")) {
            mSimpleExoPlayerView.setVisibility(View.VISIBLE);
            initializeExoPlayer(mStep.getStepVideoUrl(), mSimpleExoPlayerView);
        } else if (!mStep.getThumbnailUrl().equals("")) {

            Picasso.with(getActivity())
                    .load(mStep.getThumbnailUrl())
                    .placeholder(R.drawable.chef)
                    .error(R.drawable.chef)
                    .into(mStepDefaultImageView);

            mStepDefaultImageView.setVisibility(View.VISIBLE);
        } else {
            mStepDefaultImageView.setVisibility(View.VISIBLE);
        }

        // Initialize media player
        MediaPlayerUtils.initFullscreenDialog(getActivity(), mSimpleExoPlayerView, mStepMainLayout, mExoPlayer);
        MediaPlayerUtils.initFullscreenButton(getActivity(), mSimpleExoPlayerView, mStepMainLayout, mExoPlayer);
    }

    /**
     * Initializes ExoPlayer on a SimpleExoPlayerView
     *
     * @param videoUrlString The URL to fetch the video that will be played by ExoPlayer
     * @param playerView     The view where the video will be displayed
     */
    public void initializeExoPlayer(String videoUrlString,
                                           SimpleExoPlayerView playerView) {

        if (!videoUrlString.equals("")) {

            TrackSelector trackSelector = new DefaultTrackSelector();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(),
                    trackSelector);

            playerView.setPlayer(mExoPlayer);
            mExoPlayer.setPlayWhenReady(PLAY_WHEN_READY);
            mExoPlayer.seekTo(currentWindow, position);
            MediaSource mediaSource = setupMediaSource(getActivity(), videoUrlString);

            mExoPlayer.prepare(mediaSource);
        }
    }

    /**
     * Sets up the media source that will be displayed by ExoPlayer
     *
     * @param context        The Context
     * @param videoUrlString The URL of the video that will be displayed
     *
     * @return A MediaSource from an HTTP resource
     */
    private static MediaSource setupMediaSource(Context context, String videoUrlString) {
        // Setup Media Source
        String userAgent = Util.getUserAgent(context, "BakingApp");
        return new ExtractorMediaSource(Uri.parse(videoUrlString),
                new DefaultDataSourceFactory(context, userAgent),
                new DefaultExtractorsFactory(),
                null,
                null);
    }

    /*
     * Sets the step description on the corresponding TextView
     */
    private void setStepDescriptionText() {
        mStepTextView.setText(RecipeDataUtils.formatStepDescription(mStep.getStepDescription()));
    }

    /*
     * Makes the step description view scrollable
     */
    private void makeStepDescriptionScrollable() {
        mStepTextView.setMovementMethod(new ScrollingMovementMethod());
    }


    /*
     * Setters
     */


    /*
     * Sets the data of the currently selected step
     */
    public void setStepSelected(Step step) {
        mStep = step;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
//        if(mExoPlayer != null) {
//            position = mExoPlayer.getCurrentPosition();
//            outState.putLong(PLAYER_POSITION_KEY, position);
//
////            PLAY_WHEN_READY = mExoPlayer.getPlayWhenReady();
//            outState.putBoolean("state", PLAY_WHEN_READY);
//        }
        outState.putInt("current_window", currentWindow);
        outState.putLong("play_back_pos", position);
    }

    /*
     * Lifecycle methods
     */


    @Override
    public void onStop() {
        super.onStop();
        MediaPlayerUtils.releaseExoPlayer(mExoPlayer);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null || Util.SDK_INT <= 23) {
            position = mExoPlayer.getCurrentPosition();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
            PLAY_WHEN_READY = mExoPlayer.getPlayWhenReady();
            mExoPlayer.stop();
            mExoPlayer.release();
//            mExoPlayer.stop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /*
     * ExoPlayer Listeners
     */

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || mExoPlayer == null)) {
            initializeExoPlayer(mStep.getStepVideoUrl(), mSimpleExoPlayerView);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            position = savedInstanceState.getLong("play_back_pos");
            currentWindow = savedInstanceState.getInt("current_window");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initializeExoPlayer(mStep.getStepVideoUrl(), mSimpleExoPlayerView);
    }
}
