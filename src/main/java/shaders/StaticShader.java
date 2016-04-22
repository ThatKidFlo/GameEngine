package shaders;

import entities.Camera;
import org.joml.Matrix4f;
import utils.Maths;

/**
 * Created by ThatKidFlo on 17.04.2016.
 */
public class StaticShader extends ShaderProgram {

    private static final String VERTEX_SHADER_FILE = "src/main/java/shaders/vertexShader";
    private static final String FRAGMENT_SHADER_FILE = "src/main/java/shaders/fragmentShader";

    /**
     * Variable representing the transformation matrix location (i.e. the uniform variable).
     */
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;

    public StaticShader() {
        super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
    }

    @Override
    protected void bindAttributes() {
        // connect our variable to the position variable of the vertex shader.
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
    }

    public void loadTransformationMatrix(Matrix4f transformation) {
        super.loadMatrix(location_transformationMatrix, transformation);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }
}
