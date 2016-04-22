import entities.Camera;
import entities.Entity;
import models.TexturedModel;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import renderengine.DisplayManager;
import renderengine.Loader;
import models.RawModel;
import renderengine.Renderer;
import shaders.StaticShader;
import textures.ModelTexture;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class GraphicEngine {

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;

    // The window handle
    private long window;
    private RawModel model;
    private ModelTexture texture;
    private TexturedModel staticModel;
    private Entity entity;
    private Loader loader;
    private StaticShader shader;
    private Renderer renderer;
    private Camera camera;
    float[] vertices;
    int[] indices;
    float[] textureCoordinates;

    public GraphicEngine initialize() {
        DisplayManager.createDisplay();
        loader = new Loader();
        shader = new StaticShader();
        renderer = new Renderer(shader);
        camera = Camera.getInstance();

        initializeCubeModelData();

        model = loader.loadToVAO(vertices, textureCoordinates, indices);
        texture = new ModelTexture(loader.loadTexture("image"));
        staticModel = new TexturedModel(model, texture);
        entity = new Entity(staticModel, new Vector3f(0, 0, -5), 0, 0, 0, 1.0f);

        // initializeIOEvents();
        return this;
    }

    private void initializeQuadModelData() {
        vertices = new float[]{
                -0.5f, 0.5f, -1.05f,
                -0.5f, -0.5f, -1.05f,
                0.5f, -0.5f, -1.05f,
                0.5f, 0.5f, -1.05f,
        };
        indices = new int[]{
                0, 1, 3,
                3, 1, 2
        };
        textureCoordinates = new float[]{
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };
    }

    private void initializeCubeModelData() {
        vertices = new float[]{
                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,

                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                0.5f, 0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,

                -0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,

                -0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, 0.5f,

                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, 0.5f
        };

        textureCoordinates = new float[]{
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                0, 0,
                0, 1,
                1, 1,
                1, 0
        };

        indices = new int[]{
                0, 1, 3,
                3, 1, 2,
                4, 5, 7,
                7, 5, 6,
                8, 9, 11,
                11, 9, 10,
                12, 13, 15,
                15, 13, 14,
                16, 17, 19,
                19, 17, 18,
                20, 21, 23,
                23, 21, 22
        };
    }

    private void initializeIOEvents() {
        glfwSetKeyCallback(DisplayManager.WINDOW, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                    glfwSetWindowShouldClose(DisplayManager.WINDOW, GLFW.GLFW_TRUE);
                }
            }
        });
    }

    public void gameLoop() {
        while (glfwWindowShouldClose(DisplayManager.WINDOW) == GLFW_FALSE) {
            camera.move();
            entity.increasePosition(0.0f, 0.0f, -1.f);
            entity.increaseRotation(0.025f, 0f, 0.025f);
            renderer.prepare();
            shader.start();
            shader.loadViewMatrix(camera);
            renderer.render(entity, shader);
            shader.stop();
            DisplayManager.updateDisplay();
        }
        stop();
    }

    private void stop() {
        shader.cleanup();
        loader.cleanup();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args) {
        new GraphicEngine().initialize().gameLoop();
    }

}
