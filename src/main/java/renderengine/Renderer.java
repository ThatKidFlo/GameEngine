package renderengine;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class Renderer {

    /**
     * Prepares the viewport for rendering by filling the screen with the background color (here, red), and
     * then clearing the color buffer bit.
     */
    public void prepare() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL11.glClearColor(1,0,0,1);
    }

    public void render(RawModel model) {
        // Bind the VAO of the model to be rendered
        GL30.glBindVertexArray(model.getVaoID());

        // Load the vertex attribute array on position 0 in the VAO list (hardcoded for now)
        GL20.glEnableVertexAttribArray(0);

        // The call to glDrawArrays was replaced, since now we also have the indices buffer bound
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);

        // Unbind the VAO
        GL30.glBindVertexArray(0);
    }
}
