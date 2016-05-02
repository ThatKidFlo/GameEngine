import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import renderengine.DisplayManager;
import renderengine.Loader;
import renderengine.MasterRenderer;
import renderengine.OBJLoader;
import textures.ModelTexture;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class GraphicEngine {

    // We need to strongly reference callback instances.
    private GLFWErrorCallback errorCallback;
    private GLFWKeyCallback keyCallback;
    private GLFWCursorPosCallback cursorPosCallback;
    private int mouseX, mouseY, mouseDX, mouseDY;

    private RawModel model;
    private ModelTexture texture;
    private TexturedModel staticModel;
    private Entity entity;
    private Loader loader;

    private Camera camera;
    private Light light;
    private MasterRenderer renderer;

    @Deprecated
    private float[] vertices;
    @Deprecated
    private int[] indices;
    @Deprecated
    private float[] textureCoordinates;

    public GraphicEngine initialize() {
        DisplayManager.createDisplay();
        loader = new Loader();
        renderer = new MasterRenderer();
        camera = Camera.getInstance();
        model = OBJLoader.loadObjModel("dragon", loader);
        texture = new ModelTexture(loader.loadTexture("orange"));
        texture.setShineDamper(10);
        texture.setReflectivity(1);
        staticModel = new TexturedModel(model, texture);
        entity = new Entity(staticModel, new Vector3f(0, 0, -50), 0, 0, 0, 1.0f);
        light = new Light(new Vector3f(0, 0, -20), new Vector3f(1, 1, 1));
        initializeIOEvents();
        return this;
    }

    @Deprecated
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

    @Deprecated
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

        //TODO:: complete handling mouse input, by rotating camera.
        mouseX = mouseY = mouseDX = mouseDY = 0;
        glfwSetCursorPosCallback(DisplayManager.WINDOW, cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                // Add delta of x and y mouse coordinates
                mouseDX += (int) xpos - mouseX;
                mouseDY += (int) xpos - mouseY;
                // Set new positions of x and y
                mouseX = (int) xpos;
                mouseY = (int) ypos;
            }
        });
    }

    public void gameLoop() {
        while (glfwWindowShouldClose(DisplayManager.WINDOW) == GLFW_FALSE) {
            camera.move();
            //entity.increaseRotation(0.01f, 0.01f, 0f);
            renderer.processEntity(entity);
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }
        stop();
    }

    private void stop() {
        renderer.cleanup();
        loader.cleanup();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args) {
        new GraphicEngine().initialize().gameLoop();
    }

}
