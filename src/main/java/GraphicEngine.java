import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
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
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    /******************************************
     * MODELS AND ENTITIES
     ******************************************/
    private RawModel model;
    private ModelTexture texture;
    private TexturedModel staticModel, grass, fern;
    private List<Entity> entities;
    private Terrain terrain, terrain1;
    public Player player;
    public static GraphicEngine engine;

    /******************************************
     * ENGINE AND LIGHTS
     ******************************************/
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
        engine = this;
        /******************************************ENGINE AND LIGHTS******************************************/
        DisplayManager.createDisplay();
        loader = new Loader();
        initializeIOEvents();


        // Loading the player must precede the camera, as the camera requires the player object, BUT IT MUST COME AFTER GLFW CONTEXT INIT
        TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("stall", loader), new ModelTexture(loader.loadTexture("orange")));
        player = new Player(playerModel, new Vector3f(0, 0.0f, -50), 0, 0, 0, 1);

        camera = Camera.getInstance(DisplayManager.WINDOW, player);
        light = new Light(new Vector3f(20000, 20000, 20000), new Vector3f(1, 1, 1));
        renderer = new MasterRenderer();


        /******************************************MODELS AND ENTITIES******************************************/
        model = OBJLoader.loadObjModel("tree", loader);
        texture = new ModelTexture(loader.loadTexture("tree"));
        staticModel = new TexturedModel(model, texture);
        grass = new TexturedModel(OBJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUseFakeLighting(true);
        fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader), new ModelTexture(loader.loadTexture("fern")));
        fern.getTexture().setHasTransparency(true);
        fern.getTexture().setUseFakeLighting(true);

        TerrainTexture grass_texture = new TerrainTexture(loader.loadTexture("grass_1"));
        TerrainTexture rocks_texture = new TerrainTexture(loader.loadTexture("stones1"));
        TerrainTexture dirt_texture = new TerrainTexture(loader.loadTexture("dirt1"));
        TerrainTexture grass_texture1 = new TerrainTexture(loader.loadTexture("grass1"));
        TerrainTexture blendmap = new TerrainTexture(loader.loadTexture("blendmap"));

        TerrainTexturePack texturePack = new TerrainTexturePack(grass_texture, rocks_texture, dirt_texture, grass_texture1);

        terrain = new Terrain(0, -1, loader, texturePack, blendmap);
        terrain1 = new Terrain(-1, -1, loader, texturePack, blendmap);

        entities = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            entities.add(new Entity(staticModel, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 3));
            entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 1));
            entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0, 0.6f));
        }

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
                    glfwSetWindowShouldClose(DisplayManager.WINDOW, true);
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
                if (mouseX < xpos) {
                    camera.increaseYaw(mouseDX * 0.001f);
                } else {
                    camera.increaseYaw(mouseDX * -0.001f);
                }
                // Set new positions of x and y
                mouseX = (int) xpos;
                mouseY = (int) ypos;

            }
        });
    }

    public void gameLoop() {
        while (!glfwWindowShouldClose(DisplayManager.WINDOW)) {
            camera.move();
            player.move();
            renderer.processEntity(player);

            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain1);
            entities.stream().forEach((entity) -> renderer.processEntity(entity));
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
