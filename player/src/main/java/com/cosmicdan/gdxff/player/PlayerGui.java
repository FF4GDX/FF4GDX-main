package com.cosmicdan.gdxff.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cosmicdan.gdxff.AssetManagerEx;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.file.FileChooser;
import com.kotcrab.vis.ui.widget.file.FileChooser.Mode;
import com.kotcrab.vis.ui.widget.file.FileChooser.SelectionMode;
import com.kotcrab.vis.ui.widget.file.FileChooserAdapter;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;

import java.nio.ByteBuffer;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
public class PlayerGui {
    // resource-related constants
    public static final float BUTTON_BAR_SIZE_BUTTON = 44.0f;
    public static final float FADE_ANIM_TIME = 0.2f;
    private static final float MIN_GUI_FPS = 30.0f;

    // resources
    private final AssetManagerEx assets;
    private final AssetDescriptor<Texture> logoAsset;
    private final AssetDescriptor<Texture> buttonBarBgAsset;
    private final AssetDescriptor<Texture> iconMenuAsset;
    private final AssetDescriptor<Texture> iconInfoAsset;
    private final AssetDescriptor<Texture> iconOpenAsset;
    private final AssetDescriptor<Texture> iconPlayAsset;
    private final AssetDescriptor<Texture> iconVolMuteAsset;

    // player gui related things
    private final Stage stage;
    private final Table rootTable;
    private final Image videoImage;
    private final Cell<Image> videoImageCell;
    private final TextureRegionDrawable logoTexDrawable;
    private final Table buttonRow;
    private final Cell<Table> buttonRowLeftCell;
    private final Cell<Table> buttonRowMidCell;
    private final Cell<Table> buttonRowRightCell;
    private final ButtonBarHandler buttonbar;
    private FileChooser openFileChooser = null;

    public PlayerGui() {
        // setup assets
        assets = new AssetManagerEx();

        // load all player assets
        logoAsset = assets.loadTexture("bg.png");
        buttonBarBgAsset = assets.loadTexture("buttonBarBg.png");
        iconMenuAsset = assets.loadTexture("icon_menu.png");
        iconInfoAsset = assets.loadTexture("icon_info.png");
        iconOpenAsset = assets.loadTexture("icon_open.png");
        iconPlayAsset = assets.loadTexture("icon_play.png");
        iconVolMuteAsset = assets.loadTexture("icon_vol_mute.png");

        // start visui
        VisUI.setSkipGdxVersionCheck(true);
        final Skin skin = new Skin(VisUI.SkinScale.X1.getSkinFile());
        VisUI.load(skin);

        // build base gui
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        // root table
        rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.center();
        rootTable.setPosition(0, 0);
        stage.addActor(rootTable);

        // video image for root
        videoImage = new Image();
        videoImage.setScaling(Scaling.fit);
        videoImageCell = rootTable.add(videoImage);
        // logo texture for video image
        final Texture logoTex = assets.get(logoAsset);
        logoTexDrawable = new TextureRegionDrawable(new TextureRegion(logoTex));
        // assign icon to image as default
        videoImage.setDrawable(logoTexDrawable);
        rootTable.row();

        // button row
        buttonRow = new Table();
        final Drawable buttonBarBg = new TextureRegionDrawable(new TextureRegion(assets.get(buttonBarBgAsset)));
        buttonRow.setBackground(buttonBarBg);
        rootTable.add(buttonRow);
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

        buttonbar = new ButtonBarHandler(assets);
        buttonRowLeft.add(buttonbar.newButton(iconInfoAsset, this::clickedInfo)).size(BUTTON_BAR_SIZE_BUTTON);
        buttonRowLeft.add(buttonbar.newButton(iconVolMuteAsset, this::clickedVolume)).size(BUTTON_BAR_SIZE_BUTTON);
        buttonRowRight.add(buttonbar.newButton(iconOpenAsset, this::clickedOpen)).size(BUTTON_BAR_SIZE_BUTTON);
        buttonRowRight.add(buttonbar.newButton(iconMenuAsset, this::clickedMenu)).size(BUTTON_BAR_SIZE_BUTTON);
        buttonRowMid.add(buttonbar.newButton(iconPlayAsset, this::clickedPlay)).size(BUTTON_BAR_SIZE_BUTTON);

        setupFileChooser();

        // TODO: better "disabled" theme
        buttonbar.setButtonDisabled(iconVolMuteAsset, true);
        buttonbar.setButtonDisabled(iconPlayAsset, true);

        Gdx.graphics.setContinuousRendering(false);
        //root.setDebug(true, true);
        //buttonRow.setDebug(true, true);
    }

    // TODO --------------------------------------
    //      --------------------------------------
    //      Lib stuff
    //      --------------------------------------
    //      --------------------------------------
    private long decoderStartTime;
    private FFmpegFrameGrabber grabber;
    private int videoAvPixFmt;
    long lastTimeStamp = -1L;
    private PlaybackTimer playbackTimer;
    private Pixmap[] decodeBuffer = new Pixmap[10];


    // TODO: for lib
    private void handleDecoding() throws FrameGrabber.Exception {
        if (grabber != null) {
            // update pixel format if changed
            if (grabber.getPixelFormat() != videoAvPixFmt) {
                final int videoAvPixFmtOld = videoAvPixFmt;
                videoAvPixFmt = grabber.getPixelFormat();
                System.out.println("AvPxlFmt update: was " + videoAvPixFmtOld + "; now " + videoAvPixFmt);
            }
            // grab next frame
            final Frame frameRaw = grabber.grabFrame();
            if (frameRaw == null) {
                grabber.close();
                grabber = null;
                return;
                // TODO: better lifecycle management
            }
            if (lastTimeStamp < 0) {
                playbackTimer = new PlaybackTimer();
            }
            lastTimeStamp = frameRaw.timestamp;
            final Pixmap framePixmap = convertFrame(frameRaw);

            System.out.println("Decoded a single frame in " + playbackTimer.elapsedMicrosecs() / 1000.0f + "ms");
            grabber.close();
            grabber = null;

        }
    }

    private Pixmap convertFrame(final Frame frameRaw) {
        Pixmap.Format pixmapFormat = null;

        final ByteBuffer pixelBufferConverted;
        if (frameRaw.image[0] instanceof ByteBuffer) {
            final ByteBuffer pixelBufferIn = (ByteBuffer) frameRaw.image[0];
            final ByteBuffer pixelBufferOut = ByteBuffer.allocateDirect(pixelBufferIn.capacity());
            if (videoAvPixFmt == avutil.AV_PIX_FMT_BGR24) {
                pixmapFormat = Pixmap.Format.RGB888;
                // Convert BGR24 to RGB24 (RGB888 in Pixmap terms)
                int byteOffset = 0;
                for (int pixelChunk = 0; pixelChunk < (pixelBufferIn.capacity() / 3); pixelChunk++ ) {
                    pixelBufferOut.put(byteOffset, pixelBufferIn.get(byteOffset + 2));
                    pixelBufferOut.put(byteOffset + 1, pixelBufferIn.get(byteOffset + 1));
                    pixelBufferOut.put(byteOffset + 2, pixelBufferIn.get(byteOffset));
                    byteOffset += 3;
                }
                pixelBufferConverted = pixelBufferOut;
            } else {
                // unhandled source pixel format
                final String msg = "Unknown AV_PIX_FMT_ value '" + videoAvPixFmt + "' - FIX ME!";
                System.out.println(msg);
                throw new RuntimeException(msg);
            }
        } else {
            final String msg = "Decoded JavaCV frame is not a ByteBuffer...? Unsupported!";
            System.out.println(msg);
            throw new RuntimeException(msg);
        }

        final Pixmap pmap = new Pixmap(frameRaw.imageWidth, frameRaw.imageHeight, pixmapFormat);
        pmap.setPixels(pixelBufferConverted);
        return pmap;
    }

    final void openFile(final String filePath) {
        System.out.println("Open file: " + filePath);
        grabber = new FFmpegFrameGrabber(filePath);
        try {
            grabber.start();
        } catch (FFmpegFrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }


        /*
        try (final FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(filePath)) {
            grabber.start();
            final PlaybackTimer playbackTimer = new PlaybackTimer();


        } catch (final FFmpegFrameGrabber.Exception exception) {
            System.out.println("[!] Exception from FFmpegFrameGrabber");
            throw new RuntimeException(exception);
        } catch (final FrameGrabber.Exception exception) {
            System.out.println("[!] Exception from FrameGrabber (base)");
            throw new RuntimeException(exception);
        }

         */
    }
    // TODO --------------------------------------
    //      --------------------------------------
    //      End lib stuff
    //      --------------------------------------
    //      --------------------------------------

    private void setupFileChooser() {
        FileChooser.setDefaultPrefsName(getClass().getPackage().getName());
        openFileChooser = new FileChooser(Mode.OPEN);
        openFileChooser.setCenterOnAdd(false);
        openFileChooser.setDirectory(Gdx.files.getLocalStoragePath());
        openFileChooser.setSelectionMode(SelectionMode.FILES);

        openFileChooser.setListener(new FileChooserAdapter() {
            @Override
            public void selected(final Array<FileHandle> files) {
                openFile(files.first().file().getAbsolutePath());
            }
        });
    }

    private void clickedPlay(final VisImageButton buttonPlay) {

    }

    private void clickedMenu(final VisImageButton buttonMenu) {

    }

    private void clickedOpen(final VisImageButton actor) {
        stage.addActor(openFileChooser.fadeIn(FADE_ANIM_TIME));
    }

    private void clickedInfo(final VisImageButton actor) {
        //actor.setFocusBorderEnabled(false);
        // TODO - overlay text
    }


    private void clickedVolume(final VisImageButton visImageButton) {

    }

    public final void onResize(final int width, final int height) {
        stage.getViewport().update(width, height, true);
        // center + pad the buttons
        final float buttonBarParts = 3.0f;
        final float buttonBarPartWidth = width / buttonBarParts;
        buttonRow.setWidth(width);
        buttonRowLeftCell.width(buttonBarPartWidth);
        buttonRowMidCell.width(buttonBarPartWidth);
        buttonRowRightCell.width(buttonBarPartWidth);


        final float openDialogWidth = width * 0.5f;
        openFileChooser.setWidth(openDialogWidth);
        openFileChooser.setHeight(height - buttonRowLeftCell.getPrefHeight());
        openFileChooser.setPosition(width - openDialogWidth, height);

    }

    public final void onRender() {
        try {
            handleDecoding();
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        }
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / MIN_GUI_FPS)); // default 30.0f
        stage.draw();
    }

    public final void onPause() {

    }

    public final void onResume() {

    }

    public final void onDispose() {
        VisUI.dispose();
        assets.dispose();
    }

    private class PlaybackTimer {
        private final long timerStart;

        PlaybackTimer() {
            timerStart = System.nanoTime();
        }

        long elapsedMicrosecs() {
            return (System.nanoTime() - timerStart) / 1000;
        }
    }
}
