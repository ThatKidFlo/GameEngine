package renderengine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;

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

    private StaticShader shader = new StaticShader();
    private Renderer renderer = new Renderer(shader);

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();

    /**
     * Performs a render of the currently bound entities.
     *
     * @param sun    - light source to be used as the sun.
     * @param camera - camera, used in creating the view matrix.
     */
    public void render(Light sun, Camera camera) {
        renderer.prepare();
        shader.start();
        shader.loadLight(sun);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        entities.clear();
    }

    /**
     * Processes the entity, and enqueues it for rendering in the next frame. This method must be called before every
     * frame, for each entity that is to be rendered in the said frame.
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
    }
}
