package com.cosmicdan.gdxff;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.cosmicdan.gdxff.player.PlayerGui;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Init implements ApplicationListener {
	PlayerGui player;
	private boolean videoPlaying = false;

	/**
	 * Called when the {@link Application} is first created.
	 */
	@Override
	public void create() {
		player = new PlayerGui();
	}

	/**
	 * Called when the {@link Application} is resized. This can happen at any point during a non-paused state but will never
	 * happen before a call to {@link #create()}.
	 *
	 * @param width  the new width in pixels
	 * @param height the new height in pixels
	 */
	@Override
	public void resize(final int width, final int height) {
		player.onResize(width, height);
	}

	/**
	 * Called when the {@link Application} should render itself.
	 */
	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		player.onRender();
	}

	/**
	 * Called when the {@link Application} is paused, usually when it's not active or visible on-screen. An Application is also
	 * paused before it is destroyed.
	 */
	@Override
	public void pause() {
		player.onPause();
	}

	/**
	 * Called when the {@link Application} is resumed from a paused state, usually when it regains focus.
	 */
	@Override
	public void resume() {
		player.onResume();
	}

	/**
	 * Called when the {@link Application} is destroyed. Preceded by a call to {@link #pause()}.
	 */
	@Override
	public void dispose() {
		player.onDispose();
	}
}