package io.anuke.ucore.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;
import io.anuke.ucore.core.Core;
import io.anuke.ucore.core.Graphics;

/** A framebuffer wrapper. */
public class Surface implements Disposable{
    protected FrameBuffer buffer;
    private int scale;
    private boolean linear = false;
    private int bind;
    private boolean fixedSize = false;
    private int fixedWidth, fixedHeight;

    public Surface(int scale, int bind){
        this.scale = scale;
        this.bind = bind;
        onResize();
    }

    public Surface(FrameBuffer buffer){
        this.buffer = buffer;
        this.fixedSize = true;
        this.fixedWidth = buffer.getWidth();
        this.fixedHeight = buffer.getHeight();
    }

    public FrameBuffer getBuffer(){
        return buffer;
    }

    public Surface setSize(int width, int height){
        return setSize(width, height, true);
    }

    public Surface setSize(int width, int height, boolean fixed){
        fixedSize = fixed;
        this.fixedWidth = width;
        this.fixedHeight = height;
        onResize();
        return this;
    }

    public int width(){
        return buffer.getWidth();
    }

    public int height(){
        return buffer.getHeight();
    }

    public void onResize(){

        if(!fixedSize){
            int scale = this.scale == -1 ? Core.cameraScale : this.scale;
            int width = Gdx.graphics.getBackBufferWidth() / scale;
            int height = Gdx.graphics.getBackBufferHeight() / scale;

            //skip invalid sizes, eg. when the window is minimized to 0x0
            if(width <= 0 || height <= 0){
                return;
            }

            if(buffer != null){
                buffer.dispose();
            }

            buffer = new FrameBuffer(Format.RGBA8888, width, height, false);
        }else if(buffer == null || buffer.getWidth() != fixedWidth || buffer.getHeight() != fixedHeight){
            if(fixedWidth <= 0 || fixedHeight <= 0){
                return;
            }

            if(buffer != null){
                buffer.dispose();
            }

            buffer = new FrameBuffer(Format.RGBA8888, fixedWidth, fixedHeight, false);
        }

        if(!linear)
            buffer.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    public Texture texture(){
        return buffer.getColorBufferTexture();
    }

    public void setLinear(boolean linear){
        this.linear = linear;
    }

    public void setScale(int scale){
        this.scale = scale;
        onResize();
    }

    public void begin(){
        begin(true);
    }

    public void begin(boolean clear){
        begin(clear, true);
    }

    public void begin(boolean clear, boolean viewport){
        if(viewport){
            buffer.begin();
        }else{
            buffer.bind();
        }
        if(bind != 0) buffer.getColorBufferTexture().bind(bind);

        if(clear)
            Graphics.clear(1f, 1f, 1f, 0f);
    }

    //bind textures to 0 if necessary here
    public void end(boolean render){
        buffer.end();
        if(bind != 0) buffer.getColorBufferTexture().bind(0);
    }

    @Override
    public void dispose(){
        buffer.dispose();
    }
}
