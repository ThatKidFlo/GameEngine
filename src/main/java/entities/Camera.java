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

    private static Camera SINGLETON_INSTANCE = new Camera();

    private Camera() {

    }

    public static Camera getInstance() {
        return SINGLETON_INSTANCE;
    }

    public void move() {
        long window = DisplayManager.WINDOW;

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == GLFW.GLFW_PRESS) {
            position.x += 0.2f;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == GLFW.GLFW_PRESS) {
            position.x -= 0.2f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == GLFW.GLFW_PRESS) {
            position.y -= 0.2f;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) {
            position.y += 0.2f;
        }

        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == GLFW.GLFW_PRESS) {
            position.z -= 10f;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == GLFW.GLFW_PRESS) {
            position.z += 10f;
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
