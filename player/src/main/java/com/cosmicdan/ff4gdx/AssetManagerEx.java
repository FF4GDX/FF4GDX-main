package com.cosmicdan.ff4gdx;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;

/**
 * @author Daniel 'CosmicDan' Connolly
 */
public class AssetManagerEx extends AssetManager {
    private final TextureParameter assetTextureParam;

    public AssetManagerEx() {
        assetTextureParam = new TextureLoader.TextureParameter();
        // best quality. see https://www.gamedevelopment.blog/texture-filter/
        assetTextureParam.minFilter = TextureFilter.MipMapLinearLinear;
        assetTextureParam.magFilter = TextureFilter.MipMapLinearLinear;
        assetTextureParam.genMipMaps = true;
        // good enough
        //assetTextureParam.minFilter = TextureFilter.Linear;
        //assetTextureParam.magFilter = TextureFilter.Linear;
    }

    public AssetDescriptor<Texture> loadTextureNow(final String assetPath) {
        final AssetDescriptor<Texture> assetDesc = new AssetDescriptor<>(assetPath, Texture.class, assetTextureParam);
        load(assetDesc);
        finishLoading();
        return assetDesc;
    }
}
