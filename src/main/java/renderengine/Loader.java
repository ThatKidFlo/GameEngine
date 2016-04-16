package renderengine;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class Loader {

    /**
     * These two fields are for memory management purposes (i.e. they keep track of
     * the VAOs, and VBOs that have been allocated, in order to be able to properly
     * free the allocated memory, before closing our game.
     */
    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();

    public RawModel loadToVAO(float[] positions, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, positions);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public void cleanup() {
        vaos.stream().forEach((vao) -> GL30.glDeleteVertexArrays(vao));
        vbos.stream().forEach((vbo) -> GL15.glDeleteBuffers(vbo));
    }

    /**
     * Will create an empty Vertex Array Object(VAO), and bind it to an ID.
     *
     * @return - The ID of the newly created VAO.
     */
    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        // add generated VAO to the garbage collection list, for collection once the window should close.
        vaos.add(vaoID);

        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    /**
     * This method stores the vertex data given as the latter parameter in the
     * attribute list of the VAO, at the index specified by the former parameter.
     *
     * @param attributeNumber - VAO attribute list index.
     * @param data            - The vertex array to store in the VAO.
     */
    private void storeDataInAttributeList(int attributeNumber, float[] data) {
        int vboID = GL15.glGenBuffers();
        // add generated VBO to the garbage collection list.
        vbos.add(vboID);

        // Bind the generated Vertex Buffer Object(VBO).
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);

        // Obtain the data as a FloatBuffer.
        FloatBuffer buffer = storeDataInFloatBuffer(data);

        // Buffer the data, and inform OpenGL it is static (i.e. read-only)
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

        // Set the attribute pointer to the one we've allocated, specifying that there are
        // 3 elements per vertex (i.e. 3D coordinates), of type float, not normalized, without
        // stride (no elements in between data), which start at index 0 of the array buffer.
        GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0);

        // Unbind the current VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Unbinds the most recently bound VAO.
     */
    private void unbindVAO() {
        //This unbinds the currently bound VAO.
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    /**
     * Util method, which will convert a raw int array into an IntBuffer.
     *
     * @param data - the float data that should be converted to a IntBuffer.
     * @return - the converted data.
     */
    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Util method, which will convert a raw float array into a FloatBuffer.
     *
     * @param data - the float data that should be converted to a FloatBuffer.
     * @return - the converted data.
     */
    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        // LWJGL specific buffer allocation
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        // Flipping means we are done writing to the buffer, and it will be read from.
        buffer.flip();
        return buffer;
    }
}
