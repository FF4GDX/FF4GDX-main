package com.cosmicdan.ff4gdx.player;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.cosmicdan.ff4gdx.AssetManagerEx;
import com.kotcrab.vis.ui.widget.VisImageButton;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
class ButtonBarHandler extends ChangeListener {
    private static final float BUTTON_BAR_SIZE_ICON = 32.0f;
    public static final float BUTTON_BAR_SIZE_BUTTON = 44.0f;

    private final AssetManagerEx assets;
    private final AssetDescriptor<Texture> buttonBarBgAsset;
    private final AssetDescriptor<Texture> iconMenuAsset;
    private final AssetDescriptor<Texture> iconInfoAsset;
    private final AssetDescriptor<Texture> iconOpenAsset;
    private final AssetDescriptor<Texture> iconPlayAsset;
    private final AssetDescriptor<Texture> iconVolMuteAsset;

    private final Map<Actor, Consumer<VisImageButton>> buttonCallbacks = new HashMap<>(10);
    private final Map<AssetDescriptor<Texture>, VisImageButton> buttonLookup = new HashMap<>(10);

    private final Table buttonRow;
    private final Cell<Table> buttonRowLeftCell;
    private final Cell<Table> buttonRowMidCell;
    private final Cell<Table> buttonRowRightCell;

    ButtonBarHandler(final AssetManagerEx assetsIn, final Table rootTable, final IPlayerActions player) {
        assets = assetsIn;
        buttonBarBgAsset = assets.loadTextureNow("buttonBarBg.png");
        iconMenuAsset = assets.loadTextureNow("icon_menu.png");
        iconInfoAsset = assets.loadTextureNow("icon_info.png");
        iconOpenAsset = assets.loadTextureNow("icon_open.png");
        iconPlayAsset = assets.loadTextureNow("icon_play.png");
        iconVolMuteAsset = assets.loadTextureNow("icon_vol_mute.png");
        // bottom button row
        buttonRow = new Table();
        final Drawable buttonBarBg = new TextureRegionDrawable(new TextureRegion(assets.get(buttonBarBgAsset)));
        buttonRow.setBackground(buttonBarBg);
        final Table buttonRowLeft = new Table();
        buttonRowLeft.left();
        buttonRowLeft.pack();
        final Table buttonRowMid = new Table();
        buttonRowMid.center();
        final Table buttonRowRight = new Table();
        buttonRowRight.right();
        buttonRowLeftCell = buttonRow.add(buttonRowLeft);
        buttonRowMidCell = buttonRow.add(buttonRowMid);
        buttonRowRightCell = buttonRow.add(buttonRowRight);

        // TODO: move to PlayerGui
        buttonRowLeft.add(newButton(iconInfoAsset, player::actionInfo)).size(BUTTON_BAR_SIZE_BUTTON);
        buttonRowLeft.add(newButton(iconVolMuteAsset, player::actionVolume)).size(BUTTON_BAR_SIZE_BUTTON);
        buttonRowRight.add(newButton(iconOpenAsset, player::actionOpen)).size(BUTTON_BAR_SIZE_BUTTON);
        buttonRowRight.add(newButton(iconMenuAsset, player::actionMenu)).size(BUTTON_BAR_SIZE_BUTTON);
        buttonRowMid.add(newButton(iconPlayAsset, player::actionPlay)).size(BUTTON_BAR_SIZE_BUTTON);

        rootTable.add(buttonRow);
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

    public final void setState(final State newState) {
        if (newState == State.NOTHING_OPEN) {
            // TODO: better "disabled" theme
            buttonLookup.get(iconVolMuteAsset).setDisabled(true);
            buttonLookup.get(iconPlayAsset).setDisabled(true);
        }
    }

    public float getPrefHeight() {
        // TODO: test this, was buttonRowLeft.getPrefHeight()
        return buttonRow.getPrefHeight();
    }

    public void onResize(final int width, final int height) {
        // center + pad the buttons
        final float buttonBarParts = 3.0f;
        final float buttonBarPartWidth = width / buttonBarParts;

        buttonRow.setWidth(width);
        buttonRowLeftCell.width(buttonBarPartWidth);
        buttonRowMidCell.width(buttonBarPartWidth);
        buttonRowRightCell.width(buttonBarPartWidth);


    }

    public enum State {
        NOTHING_OPEN,
    }
}
