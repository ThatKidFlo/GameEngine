package renderengine;

import de.matthiasmann.twl.utils.PNGDecoder;
import models.RawModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class Loader {

    public static final String RESOURCES_FOLDER = "deps/";

    /**
     * These two fields are for memory management purposes (i.e. they keep track of
     * the VAOs, and VBOs that have been allocated, in order to be able to properly
     * free the allocated memory, before closing our game.
     */
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public RawModel loadToVAO(float[] positions, float[] textureCoordinates, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoordinates);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    /**
     * Load a texture file, and bind it to the texture ID which is returned from this method.
     *
     * @param fileName - the name of the file containing 2D image data; "res/" will be prepended, and ".png" appended.
     * @return - the ID of the generated texture.
     */
    public int loadTexture(String fileName) {
        ByteBuffer buffer = null;
        int height = 0;
        int width = 0;
        try {
            InputStream in = new FileInputStream(RESOURCES_FOLDER + fileName + ".png");
            PNGDecoder decoder = new PNGDecoder(in);
            height = decoder.getHeight();
            width = decoder.getWidth();
            buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            buffer.order(ByteOrder.nativeOrder());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            in.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);

        GL30.glGenerateMipmap(textureID);
        GL11.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        GL11.glTexParameterf(GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.2f);
        //GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        // Specify the 2D image data that should be bound to the texture
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
        // Bind the texture to its id.
        glBindTexture(GL_TEXTURE_2D, textureID);
        // This will wrap the textures.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // Sharp filtering, for now
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        // add anisotropic filtering to the texture which is currently active
        GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, 16.0f);

        // Keep track of the texture, for cleanup purposes.
        textures.add(textureID);
        return textureID;
    }

    public void cleanup() {
        vaos.forEach(GL30::glDeleteVertexArrays);
        vbos.forEach(GL15::glDeleteBuffers);
        textures.forEach(GL11::glDeleteTextures);
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
    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
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
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);

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
