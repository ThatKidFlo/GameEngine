package entities;

import models.TexturedModel;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;
import renderengine.DisplayManager;

import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

/**
 * Created by ThatKidFlo on 22/06/16.
 */
public class Player extends Entity {

    private GLFWKeyCallback handlerW;
    private GLFWKeyCallback handlerA;
    private GLFWKeyCallback handlerS;
    private GLFWKeyCallback handlerD;


    // this variable is expressed in units/second
    private static final float RUN_SPEED = 20.0f;
    // this variable is expressed in radians/second
    private static final float TURN_SPEED = 160.0f;

    private volatile float currentMovementSpeed = 0.0f;
    private volatile float currentTurnSpeed = 0.0f;

    private long window;

    public Player(long window, TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
        this.window = window;
        initInput();
    }

    public synchronized void move() {
        rotY += currentTurnSpeed * DisplayManager.getTimeDelta();
        float distanceMoved = currentMovementSpeed * DisplayManager.getTimeDelta();

        position.x += (float) Math.sin(Math.toRadians(getRotY())) * distanceMoved;
        position.z += (float) Math.cos(Math.toRadians(getRotY())) * distanceMoved;
    }

    private void initInput() {
        glfwSetKeyCallback(DisplayManager.WINDOW, handlerW = new GLFWKeyCallback() {
                    @Override
                    public void invoke(long window, int key, int scancode, int action, int mods) {
                        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                            glfwSetWindowShouldClose(DisplayManager.WINDOW, true);
                        }

                        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
                            currentMovementSpeed = RUN_SPEED;
                        } else if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
                            currentMovementSpeed = -RUN_SPEED;
                        } else {
                            currentMovementSpeed = 0.0f;
                        }

                        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                            currentTurnSpeed = TURN_SPEED;
                        } else if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
                            currentTurnSpeed = -TURN_SPEED;
                        } else {
                            currentTurnSpeed = 0.0f;
                        }
                    }
                }

        );
    }
}
