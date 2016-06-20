package renderengine;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import shaders.TerrainShader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexturePack;
import utils.Maths;

import java.util.List;

/**
 * Created by ThatKidFlo on 03.05.2016.
 */
public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(List<Terrain> terrains) {
        terrains.stream().forEach(
                (terrain) -> {
                    prepareTerrain(terrain);
                    loadModelMatrix(terrain);
                    GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11
                            .GL_UNSIGNED_INT, 0);
                    unbindTexturedModel();
                }
        );
    }

    /**
     * Prepares a {@link Terrain}  for rendering, by binding the VAO of the terrain to be rendered, and enabling the
     * vertex attribute arrays at index 0, 1, and 2. They contain, respectively:
     * 0: the positional data, (XYZ, floats).
     * 1: the texture data,  (UV, floats).
     * 2: the normal vectors (XYZ, floats).
     *
     * @param terrain - the {@link Terrain} to be prepared for rendering
     */
    private void prepareTerrain(Terrain terrain) {
        RawModel rawModel = terrain.getModel();

        // Bind the VAO of the terrain to be rendered
        GL30.glBindVertexArray(rawModel.getVaoID());

        // Load the vertex attribute array on position 0 in the VAO list (contains position data)
        GL20.glEnableVertexAttribArray(0);

        // Position 1 contains the texture coordinates data.
        GL20.glEnableVertexAttribArray(1);

        // Position 2 contains the normal coordinates of each vertex.
        GL20.glEnableVertexAttribArray(2);

        bindTextures(terrain);
        shader.loadShineVariables(1, 0);
    }

    private void bindTexture(int textureID, int where) {
        GL13.glActiveTexture(where);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
    }

    private void bindTextures(Terrain terrain) {
        final TerrainTexturePack texturePack = terrain.getTexturePack();

        bindTexture(texturePack.getBackgroundTexture().getTextureID(), GL13.GL_TEXTURE0);
        bindTexture(texturePack.getRedTexture().getTextureID(), GL13.GL_TEXTURE1);
        bindTexture(texturePack.getGreenTexture().getTextureID(), GL13.GL_TEXTURE2);
        bindTexture(texturePack.getBlueTexture().getTextureID(), GL13.GL_TEXTURE3);
        bindTexture(terrain.getBlendmap().getTextureID(), GL13.GL_TEXTURE4);
    }

    private void unbindTexturedModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        // Unbind the VAO
        GL30.glBindVertexArray(0);
    }

    /**
     * Prepares the {@link Terrain} instance passed as parameter for rendering, by creating a transformation matrix
     * from the current state of the {@link Terrain}, and then loading it into the shader program.
     *
     * @param terrain - the {@link Terrain} to prepare for rendering.
     */
    private void loadModelMatrix(Terrain terrain) {
        shader.loadTransformationMatrix(
                Maths.createTransformationMatrix(
                        terrain.getPosition(),
                        0.0f,
                        0.0f,
                        0.0f,
                        1.0f
                )
        );
    }
}
