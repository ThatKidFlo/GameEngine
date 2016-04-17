package shaders;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by ThatKidFlo on 17.04.2016.
 */
public abstract class ShaderProgram {
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;

    public ShaderProgram(String vertexFile, String fragmentFile) {
        vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
        fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexShaderID);
        GL20.glAttachShader(programID, fragmentShaderID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
    }

    public void start() {
        GL20.glUseProgram(programID);
    }

    public void stop() {
        GL20.glUseProgram(0);
    }

    public void cleanup() {
        stop();
        GL20.glDetachShader(programID, vertexShaderID);
        GL20.glDetachShader(programID, fragmentShaderID);
        GL20.glDeleteShader(vertexShaderID);
        GL20.glDeleteShader(fragmentShaderID);
        GL20.glDeleteProgram(programID);
    }

    protected abstract void bindAttributes();

    /**
     * This program will bind the specified attribute in the attribute list of the VAO to a specific
     * shader variable.
     *
     * @param attribute    - the ID of the attribute to bind.
     * @param variableName - the name of the variable in the shader program, which the attribute
     *                     will be bound to.
     */
    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    /**
     * Will load the specified shader program, and will return an id, representing the shader ID.
     *
     * @param file - the name of the shader program file.
     * @param type - the type of shader that this program should be (e.g. GL20.GL_FRAGMENT_SHADER)
     * @return
     */
    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append(System.lineSeparator());
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Could not read file!");
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader.");
            System.exit(-1);
        }
        return shaderID;
    }

}
