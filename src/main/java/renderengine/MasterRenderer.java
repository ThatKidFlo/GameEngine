package renderengine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This updated version of the renderer will bind multiple entities that use the same object model to it, and this
 * means that the render method will be global (i.e. called once per frame), as opposed to the previous rendering
 * technique, which would issue a call to render for each object rendered.
 * <p>
 * Created by ThatKidFlo on 02.05.2016.
 */
public class MasterRenderer {

    private static final float FOV = 60f;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 600f;

    private static float RED = 0.0f;
    private static float GREEN = 0.5f;
    private static float BLUE = 0.6f;


    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private EntityRenderer renderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer() {
        // Disable rendering of faces pointing away from the camera
        enableCulling();
        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    /**
     * Performs a render of the currently bound entities.
     *
     * @param sun    - light source to be used as the sun.
     * @param camera - camera, used in creating the view matrix.
     */
    public void render(Light sun, Camera camera) {
        prepare();
        shader.start();
        shader.loadSkyColour(RED, GREEN, BLUE);
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        terrainShader.start();
        terrainShader.loadSkyColour(RED, GREEN, BLUE);
        terrainShader.loadLight(sun);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();
        terrains.clear();
        entities.clear();
    }

    /**
     * Prepares the viewport for rendering by filling the screen with the background color (here, red), and
     * clearing the color buffer bit.
     */
    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        // Map the viewport to the size of the whole window.
        GL11.glClearColor(RED, GREEN, BLUE, 1);
    }

    /**
     * Processes the terrain, by enqueueing it for rendering in the next frame. This method must be called before
     * every frame, for each terrain that should be rendered in the respective frame.
     *
     * @param terrain - the terrain to prepare for rendering.
     */
    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    /**
     * Processes the entity, by enqueueing it for rendering in the next frame. This method must be called before every
     * frame, for each entity that is to be rendered in the respective frame.
     *
     * @param entity - the entity to prepare for rendering.
     */
    public void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            batch = new ArrayList<>();
            batch.add(entity);
            entities.put(entityModel, batch);
        }
    }

    /**
     * Performs a cleanup of the used shader programs.
     */
    public void cleanup() {
        shader.cleanup();
        terrainShader.cleanup();
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) DisplayManager.WINDOW_WIDTH / (float) DisplayManager.WINDOW_HEIGHT;
        float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float xScale = yScale / aspectRatio;
        float frustumLength = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
        projectionMatrix.m00(xScale);
        projectionMatrix.m11(yScale);
        projectionMatrix.m22(-((FAR_PLANE + NEAR_PLANE) / frustumLength));
        projectionMatrix.m23(-1);
        projectionMatrix.m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustumLength));
        projectionMatrix.m33(0);
    }
}
