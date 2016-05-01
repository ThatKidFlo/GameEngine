package renderengine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.StaticShader;
import utils.Maths;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class Renderer {

    private static final float FOV = (float) Math.toRadians(90.0f);
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000f;

    private Matrix4f projectionMatrix;

    public Renderer(StaticShader shader) {
        createProjectionMatrix();
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }

    /**
     * Prepares the viewport for rendering by filling the screen with the background color (here, red), and
     * clearing the color buffer bit.
     */
    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        // Map the viewport to the size of the whole window.
        GL11.glViewport(0, 0, DisplayManager.WINDOW_WIDTH, DisplayManager.WINDOW_HEIGHT);
        GL11.glClearColor(0, 0, 0, 1);
    }

    public void render(Entity entity, StaticShader shader) {
        TexturedModel model = entity.getModel();
        RawModel rawModel = model.getRawModel();

        // Bind the VAO of the model to be rendered
        GL30.glBindVertexArray(rawModel.getVaoID());

        // Load the vertex attribute array on position 0 in the VAO list (contains position data)
        GL20.glEnableVertexAttribArray(0);

        // Position 1 contains the texture coordinates data.
        GL20.glEnableVertexAttribArray(1);

        Matrix4f transformationMatrix = Maths
                .createTransformationMatrix(
                        entity.getPosition(),
                        entity.getRotX(),
                        entity.getRotY(),
                        entity.getRotZ(),
                        entity.getScale()
                );

        shader.loadTransformationMatrix(transformationMatrix);

        // Must activate texture bank 0, since the sampler2D from the fragment shader uses this bank by default.
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());

        // The call to glDrawArrays was replaced, since now we also have the indices buffer bound
        GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);

        // Unbind the VAO
        GL30.glBindVertexArray(0);
    }

    private void createProjectionMatrix() {
        float aspectRatio = (float) DisplayManager.WINDOW_WIDTH / (float) DisplayManager.WINDOW_HEIGHT;
        float yScale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float xScale = yScale / aspectRatio;
        float frustumLength = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f().perspective(FOV, aspectRatio, NEAR_PLANE, FAR_PLANE);
        projectionMatrix.m00 = xScale;
        projectionMatrix.m11 = yScale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustumLength);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustumLength);
        projectionMatrix.m33 = 0;
    }
}
