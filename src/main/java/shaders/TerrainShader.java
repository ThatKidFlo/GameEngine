package shaders;

import entities.Camera;
import entities.Light;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import utils.Maths;

/**
 * Created by ThatKidFlo on 03.05.2016.
 */
public class TerrainShader extends ShaderProgram {
    private static final String VERTEX_SHADER_FILE = "src/main/java/shaders/terrain.vert";
    private static final String FRAGMENT_SHADER_FILE = "src/main/java/shaders/terrain.frag";

    /**
     * Variable representing the transformation matrix location (i.e. the uniform variable).
     */
    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_lightPosition;
    private int location_lightColour;
    private int location_shineDamper;
    private int location_reflectivity;
    private int location_skyColour;
    private int location_backgroundTexture;
    private int location_redTexture;
    private int location_greenTexture;
    private int location_blueTexture;
    private int location_blendmap;

    public TerrainShader() {
        super(VERTEX_SHADER_FILE, FRAGMENT_SHADER_FILE);
    }

    @Override
    protected void bindAttributes() {
        // connect our variable to the position variable of the vertex shader.
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoordinates");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_lightPosition = super.getUniformLocation("lightPosition");
        location_lightColour = super.getUniformLocation("lightColour");
        location_shineDamper = super.getUniformLocation("shineDamper");
        location_reflectivity = super.getUniformLocation("reflectivity");
        location_skyColour = super.getUniformLocation("skyColour");
        location_backgroundTexture = super.getUniformLocation("backgroundTexture");
        location_redTexture = super.getUniformLocation("redTexture");
        location_greenTexture = super.getUniformLocation("greenTexture");
        location_blueTexture = super.getUniformLocation("blueTexture");
        location_blendmap = super.getUniformLocation("blendmap");
    }

    public void connectTextureUnits() {
        super.loadInt(location_backgroundTexture, 0);
        super.loadInt(location_redTexture, 1);
        super.loadInt(location_greenTexture, 2);
        super.loadInt(location_blueTexture, 3);
        super.loadInt(location_blendmap, 4);
    }

    public void loadSkyColour(float r, float g, float b) {
        super.loadVector(location_skyColour, new Vector3f(r, g, b));
    }

    public void loadShineVariables(float damper, float reflectivity) {
        super.loadFloat(location_shineDamper, damper);
        super.loadFloat(location_reflectivity, reflectivity);
    }

    public void loadLight(Light light) {
        super.loadVector(location_lightPosition, light.getPosition());
        super.loadVector(location_lightColour, light.getColour());
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
