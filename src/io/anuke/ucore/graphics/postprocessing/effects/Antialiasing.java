package io.anuke.ucore.graphics.postprocessing.effects;

import io.anuke.ucore.graphics.postprocessing.PostProcessorEffect;

public abstract class Antialiasing extends PostProcessorEffect {

	public abstract void setViewportSize (int width, int height);
}
