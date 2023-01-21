package com.cosmicdan.ff4gdx.player;

import com.kotcrab.vis.ui.widget.VisImageButton;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
public interface IPlayerActions {

    void actionPlay(VisImageButton buttonPlay);

    void actionMenu(VisImageButton buttonMenu);

    void actionOpen(VisImageButton actor);

    void actionInfo(VisImageButton actor);

    void actionVolume(VisImageButton actor);
}
