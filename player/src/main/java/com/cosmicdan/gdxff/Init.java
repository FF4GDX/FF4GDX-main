package com.cosmicdan.gdxff;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Init implements ApplicationListener {
	private Stage stage = null;
	private Image videoImage = null;

	/**
	 * Called when the {@link Application} is first created.
	 */
	@Override
	public void create() {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		final Table root = new Table();
		root.setFillParent(true);
		stage.addActor(root);
		videoImage = new Image();
		root.add(videoImage).fill().align(Align.center);
	}

	/**
	 * Called when the {@link Application} is resized. This can happen at any point during a non-paused state but will never
	 * happen before a call to {@link #create()}.
	 *
	 * @param width  the new width in pixels
	 * @param height the new height in pixels
	 */
	@Override
	public void resize(int width, int height) {

	}

	/**
	 * Called when the {@link Application} should render itself.
	 */
	@Override
	public void render() {

	}

	/**
	 * Called when the {@link Application} is paused, usually when it's not active or visible on-screen. An Application is also
	 * paused before it is destroyed.
	 */
	@Override
	public void pause() {

	}

	/**
	 * Called when the {@link Application} is resumed from a paused state, usually when it regains focus.
	 */
	@Override
	public void resume() {

	}

	/**
	 * Called when the {@link Application} is destroyed. Preceded by a call to {@link #pause()}.
	 */
	@Override
	public void dispose() {

	}
}