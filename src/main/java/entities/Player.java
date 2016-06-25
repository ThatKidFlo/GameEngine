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

    // this variable is expressed in units/second
    private static final float RUN_SPEED = 20.0f;
    // this variable is expressed in radians/second
    private static final float TURN_SPEED = 160.0f;
    private static final float JUMP_STRENGTH = 30.0f;
    private static final float GRAVITY = -50.0f;
    private static final float TERRAIN_HEIGHT = 0.0f;

    private float currentMovementSpeed = 0.0f;
    private float currentTurnSpeed = 0.0f;
    private float upwardSpeed = 0.0f;
    private boolean isJumped = false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
        initInput();
    }

    public void move() {
        float timeDelta = DisplayManager.getTimeDelta();
        rotY += currentTurnSpeed * timeDelta;
        float distanceMoved = currentMovementSpeed * timeDelta;

        upwardSpeed += GRAVITY * timeDelta;

        position.x += (float) Math.sin(Math.toRadians(getRotY())) * distanceMoved;
        position.y += upwardSpeed * timeDelta;
        position.z += (float) Math.cos(Math.toRadians(getRotY())) * distanceMoved;

        if (position.y < TERRAIN_HEIGHT) {
            upwardSpeed = 0.0f;
            position.y = TERRAIN_HEIGHT;
            isJumped = false;
        }
    }

    private void initInput() {
        glfwSetKeyCallback(DisplayManager.WINDOW, handlerW = new GLFWKeyCallback() {
                    @Override
                    public void invoke(long window, int key, int scancode, int action, int mods) {
                        if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                            glfwSetWindowShouldClose(DisplayManager.WINDOW, true);
                        }

                        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                            if (key == GLFW.GLFW_KEY_W) {
                                currentMovementSpeed = RUN_SPEED;
                            } else if (key == GLFW.GLFW_KEY_S) {
                                currentMovementSpeed = -RUN_SPEED;
                            }
                            if (key == GLFW.GLFW_KEY_D) {
                                currentTurnSpeed = -TURN_SPEED;
                            } else if (key == GLFW.GLFW_KEY_A) {
                                currentTurnSpeed = TURN_SPEED;
                            }
                            if (key == GLFW.GLFW_KEY_SPACE && !isJumped) {
                                upwardSpeed = JUMP_STRENGTH;
                                isJumped = true;
                            }
                        } else if (action == GLFW.GLFW_RELEASE) {
                            if (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_S) {
                                currentMovementSpeed = 0.0f;
                            } else if (key == GLFW.GLFW_KEY_D || key == GLFW.GLFW_KEY_A) {
                                currentTurnSpeed = 0.0f;
                            }
                        }
                    }
                }
        );
    }
}
