package io.anuke.ucore.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shader{
    /** Whether to fallback on the default shader when things fail. */
    public static boolean useFallback = true;
    /** Whether to enable pedantic shader compilation. */
    public static boolean pedantic = false;

    public final String frag, vert;
    public boolean isFallback;
    public ShaderProgram shader;
    public TextureRegion region;

    public Shader(String frag, String vert){
        this(frag, vert, "");
    }

    public Shader(String frag){
        this(frag, "default");
    }

    public Shader(String frag, String vert, String defines){
        ShaderProgram.pedantic = pedantic;
        this.frag = frag;
        this.vert = vert;
        reload(defines);
    }

    public void reload(){
        reload("");
    }

    public void reload(String defines){
        if(this.shader != null) this.shader.dispose();
        this.shader = new ShaderProgram(defines + "\n" + Gdx.files.internal("shaders/" + vert + ".vertex").readString(),
                defines + "\n" + Gdx.files.internal("shaders/" + frag + ".fragment").readString());

        if(!shader.isCompiled()){
            if(useFallback){
                Gdx.app.error("Shaders", "Failed to load shaders\"" + frag + "\" and \"" + vert + "\", using fallback: " + shader.getLog());
                isFallback = true;
                shader = SpriteBatch.createDefaultShader();
            }else{
                throw new RuntimeException("Error compiling shaders \"" + frag + "\" and \"" + vert + "\": " + shader.getLog());
            }
        }else{
            isFallback = false;
        }

        if(shader.getLog().length() > 0)
            Gdx.app.error("Shaders", "Shader Log (" + frag + "/" + vert + "): " + shader.getLog());
    }

    /** Loads a shader program from the shaders folder, optionally prefixing both sources with #defines. */
    public static ShaderProgram load(String vert, String frag, String defines){
        String vertSrc = Gdx.files.internal("shaders/" + vert + ".vertex").readString();
        String fragSrc = Gdx.files.internal("shaders/" + frag + ".fragment").readString();
        return loadString(vertSrc, fragSrc, defines);
    }

    public static ShaderProgram load(String vert, String frag){
        return load(vert, frag, "");
    }

    /** Compiles a shader program from source strings, throwing if compilation fails. */
    public static ShaderProgram loadString(String vertSrc, String fragSrc, String defines){
        ShaderProgram.pedantic = pedantic;
        ShaderProgram program = new ShaderProgram(defines + "\n" + vertSrc, defines + "\n" + fragSrc);

        if(!program.isCompiled()){
            Gdx.app.error("Shaders", program.getLog());
            throw new RuntimeException("Error compiling shader: " + program.getLog());
        }

        return program;
    }

    protected void apply(){
    }

    public void applyParams(){
        if(!isFallback){
            apply();
        }
    }

    public ShaderProgram program(){
        return shader;
    }
}
