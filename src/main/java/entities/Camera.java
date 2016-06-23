package entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import renderengine.DisplayManager;

/**
 * Created by ThatKidFlo on 17.04.2016.
 */
public class Camera {

    private Vector3f position = new Vector3f(0, 0, 0);
    // camera rotation about X axis
    private float pitch;
    // camera rotation about Y axis
    private float yaw;
    // camera rotation about Z axis
    private float roll;

    private Player player;

    private static long window;
    private static Camera SINGLETON_INSTANCE = new Camera();
    private static final float MOVEMENT_SPEED = 20.0f;

    private Camera() {

    }

    public static Camera getInstance(long WINDOW, Player player) {
        window = WINDOW;
        SINGLETON_INSTANCE.player = player;
        return SINGLETON_INSTANCE;
    }

    public void move() {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS) {
            position.y -= MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) {
            position.y += MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            position.z -= MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            position.z += MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            position.x -= MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            position.x += MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
    }

    public void increasePitch(float pitch) {
        this.pitch += pitch;
    }

    public void increaseYaw(float yaw) {
        this.yaw += yaw;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
