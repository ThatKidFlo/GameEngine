package shaders;

/**
 * Created by ThatKidFlo on 17.04.2016.
 */
public class StaticShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/main/java/shaders/vertexShader";
    private static final String FRAGMENT_SHADER_FILE = "src/main/java/shaders/fragmentShader";

    public StaticShader() {
        super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
    }

    @Override
    protected void bindAttributes() {
        // connect our variable to the position variable of the vertex shader.
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
    }
}
