package io.anuke.ucore.graphics.postprocessing.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import io.anuke.ucore.graphics.postprocessing.PostProcessorEffect;
import io.anuke.ucore.graphics.postprocessing.filters.LightFilter;

public final class Light extends PostProcessorEffect {
	private LightFilter light = null;
	private Texture lightmap;

	public Light (int viewportWidth, int viewportHeight) {
		light = new LightFilter(viewportWidth, viewportHeight);
	}

	public void SetSize (int width, int height) {
		// the old filter (and its shader program) may have been disposed along with a previous PostProcessor
		if(light != null){
			light.dispose();
		}
		light = new LightFilter(width, height);
	}

	@Override
	public void dispose () {
		if(light != null){
			light.dispose();
			light = null;
		}
	}

	@Override
	public void rebind () {
		if(light != null){
			light.rebind();
		}
	}

	@Override
	public void render (FrameBuffer src, FrameBuffer dest) {
		restoreViewport(dest);
		if(lightmap != null){
			lightmap.bind(6);
		}

		light.setInput(src).setOutput(dest).render();

		// unbind to prevent issues with subsequent draws
		if(lightmap != null){
			Gdx.gl.glActiveTexture(GL20.GL_TEXTURE6);
			Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
		}
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
	}

	public void setLightmap (Texture texture) {
		this.lightmap = texture;
	}
}
