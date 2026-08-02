package io.anuke.ucore.graphics.postprocessing.filters;

import io.anuke.ucore.graphics.Shader;

public final class LightFilter extends Filter<LightFilter> {

	private int lightmapUnit = 6;

	public enum Param implements Parameter {
		Texture("u_texture0", 0), Lightmap("u_lightmap", 0);

		private String mnemonic;
		private int elementSize;

		private Param (String mnemonic, int arrayElementSize) {
			this.mnemonic = mnemonic;
			this.elementSize = arrayElementSize;
		}

		@Override
		public String mnemonic () {
			return this.mnemonic;
		}

		@Override
		public int arrayElementSize () {
			return this.elementSize;
		}
	}

	public LightFilter (int width, int height) {
		super(Shader.load("screenspace", "light"));
		rebind();
	}

	@Override
	public void rebind () {
		// reimplement super to batch every parameter
		setParams(Param.Texture, u_texture0);
		setParams(Param.Lightmap, lightmapUnit);
	}

	public void setLightmapUnit (int unit) {
		this.lightmapUnit = unit;
	}

	@Override
	protected void onBeforeRender () {
		inputTexture.bind(u_texture0);

		// the program must be active before setting uniforms, otherwise they go to whatever program is bound
		program.begin();
		program.setUniformi(Param.Lightmap.mnemonic(), lightmapUnit);
		program.end();
	}
}

