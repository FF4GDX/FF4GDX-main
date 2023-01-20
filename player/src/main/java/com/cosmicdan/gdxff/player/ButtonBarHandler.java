package com.cosmicdan.gdxff.player;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosmicdan.gdxff.AssetManagerEx;
import com.kotcrab.vis.ui.widget.VisImageButton;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
class ButtonBarHandler extends ChangeListener {
    private static final float BUTTON_BAR_SIZE_ICON = 32.0f;
    private final AssetManagerEx assets;
    private final Map<Actor, Consumer<VisImageButton>> buttonCallbacks = new HashMap<>(10);
    private final Map<AssetDescriptor<Texture>, VisImageButton> buttonLookup = new HashMap<>(10);

    ButtonBarHandler(final AssetManagerEx assetsIn) {
        assets = assetsIn;
    }

    @Override
    public final void changed(final ChangeEvent event, final Actor actor) {
        final Consumer<VisImageButton> buttonConsumer = buttonCallbacks.get(actor);
        buttonConsumer.accept((VisImageButton) actor);
    }

    public final VisImageButton newButton(
            final AssetDescriptor<Texture> newButtonAsset,
            final Consumer<VisImageButton> onClick) {
        final VisImageButton newButton = new VisImageButton(
                new TextureRegionDrawable(new TextureRegion(assets.get(newButtonAsset)))
        );
        newButton.getImageCell().size(BUTTON_BAR_SIZE_ICON);
        buttonCallbacks.put(newButton, onClick);
        buttonLookup.put(newButtonAsset, newButton);
        newButton.addListener(this);
        return newButton;
    }

    public void setButtonDisabled(final AssetDescriptor<Texture> buttonToDisable, final boolean disabled) {
        buttonLookup.get(buttonToDisable).setDisabled(disabled);
    }
}
