import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import org.joml.Vector3f;
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

import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class GraphicEngine {

    /******************************************
     * MODELS AND ENTITIES
     ******************************************/
    private RawModel model;
    private ModelTexture texture;
    private TexturedModel staticModel, grass, fern;
    private List<Entity> entities;
    private Terrain terrain, terrain1;
    public Player player;

    /******************************************
     * ENGINE AND LIGHTS
     ******************************************/
    private Loader loader;
    private Camera camera;
    private Light light;
    private MasterRenderer renderer;

    public GraphicEngine initialize() {
        /******************************************ENGINE AND LIGHTS******************************************/
        DisplayManager.createDisplay();
        loader = new Loader();

        // Loading the player must precede the camera, as the camera requires the player object, BUT IT MUST COME AFTER GLFW CONTEXT INIT
        TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("stall", loader), new ModelTexture(loader.loadTexture("orange")));
        player = new Player(playerModel, new Vector3f(0, 0.0f, -50), 0, 0, 0, 1);

        camera = Camera.getInstance(player);
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
