package renderengine;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.StaticShader;
import textures.ModelTexture;
import utils.Maths;

import java.util.List;
import java.util.Map;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class EntityRenderer {

    private StaticShader shader;

    public EntityRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;

        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    public void render(Map<TexturedModel, List<Entity>> entities) {
        entities.keySet().forEach(
                (model) -> {
                    prepareTexturedModel(model);
                    entities.get(model).forEach(
                            (entity) -> {
                                prepareInstance(entity);
                                GL11.glDrawElements(GL11.GL_TRIANGLES, model.getRawModel().getVertexCount(), GL11
                                        .GL_UNSIGNED_INT, 0);
                            });
                    unbindTexturedModel();
                }
        );
    }

    /**
     * Prepares a {@link TexturedModel}  for rendering, by binding the VAO of the model to be rendered, and enabling the
     * vertex attribute arrays at index 0, 1, and 2. They contain, respectively:
     * 0: the positional data, (XYZ, floats).
     * 1: the texture data,  (UV, floats).
     * 2: the normal vectors (XYZ, floats).
     *
     * @param model - the {@link TexturedModel} to be prepared for rendering
     */
    private void prepareTexturedModel(TexturedModel model) {
        RawModel rawModel = model.getRawModel();

        // Bind the VAO of the model to be rendered
        GL30.glBindVertexArray(rawModel.getVaoID());

        // Load the vertex attribute array on position 0 in the VAO list (contains position data)
        GL20.glEnableVertexAttribArray(0);

        // Position 1 contains the texture coordinates data.
        GL20.glEnableVertexAttribArray(1);

        // Position 2 contains the normal coordinates of each vertex.
        GL20.glEnableVertexAttribArray(2);
        ModelTexture texture = model.getTexture();

        if (texture.isHasTransparency()) {
            MasterRenderer.disableCulling();
        }
        shader.loadFakeLightingVariable(texture.isUseFakeLighting());
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        // Must activate texture bank 0, since the sampler2D from the fragment shader uses this bank by default.
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

    }

    private void unbindTexturedModel() {
        MasterRenderer.enableCulling();
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        // Unbind the VAO
        GL30.glBindVertexArray(0);
    }

    /**
     * Prepares the {@link Entity} instance passed as parameter for rendering, by creating a transformation matrix
     * from the current state of the {@link Entity}, and then loading it into the shader program.
     *
     * @param entity - the {@link Entity} to prepare for rendering.
     */
    private void prepareInstance(Entity entity) {
        shader.loadTransformationMatrix(
                Maths.createTransformationMatrix(
                        entity.getPosition(),
                        entity.getRotX(),
                        entity.getRotY(),
                        entity.getRotZ(),
                        entity.getScale()
                )
        );
    }

    /**
     * This method is now {@link Deprecated}. It represents the old rendering technique, where this method, and all the
     * preparations it implies would be called once PER {@link Entity} (implying a lot of overhead). It has been
     * superseded by methods exposed by the {@link MasterRenderer}, namely {@link MasterRenderer#render(Light, Camera)}.
     *
     * @param entity - the {@link Entity} to prepare for rendering.
     * @param shader - the {@link StaticShader} program to use for rendering this entity.
     */
    @Deprecated
    public void render(Entity entity, StaticShader shader) {
        TexturedModel model = entity.getModel();
        RawModel rawModel = model.getRawModel();

        // Bind the VAO of the model to be rendered
        GL30.glBindVertexArray(rawModel.getVaoID());

        // Load the vertex attribute array on position 0 in the VAO list (contains position data)
        GL20.glEnableVertexAttribArray(0);

        // Position 1 contains the texture coordinates data.
        GL20.glEnableVertexAttribArray(1);

        // Position 2 contains the normal coordinates of each vertex.
        GL20.glEnableVertexAttribArray(2);

        Matrix4f transformationMatrix = Maths
                .createTransformationMatrix(
                        entity.getPosition(),
                        entity.getRotX(),
                        entity.getRotY(),
                        entity.getRotZ(),
                        entity.getScale()
                );

        shader.loadTransformationMatrix(transformationMatrix);

        ModelTexture texture = model.getTexture();
        shader.loadShineVariables(texture.getShineDamper(), texture.getReflectivity());

        // Must activate texture bank 0, since the sampler2D from the fragment shader uses this bank by default.
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

        // The call to glDrawArrays was replaced, since now we also have the indices buffer bound
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        // Unbind the VAO
        GL30.glBindVertexArray(0);
    }
}
