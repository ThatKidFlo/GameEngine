package entities;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWScrollCallback;
import renderengine.DisplayManager;

import static utils.Maths.cosf;
import static utils.Maths.sinf;
import static utils.Maths.toRadiansf;

/**
 * Created by ThatKidFlo on 17.04.2016.
 */
public class Camera {

    private Player player;
    private Vector3f position = new Vector3f(0, 0, 0);
    // camera rotation about X axis
    private float pitch;
    // camera rotation about Y axis
    private float yaw;
    // camera rotation about Z axis
    private float roll;
    private float distanceFromPlayer = 50.0f;
    private float angleAroundPlayer = 0.0f;
    private float theta = 0.0f;
    private float horizontalDistance = 0.0f;
    private float verticalDistance = 0.0f;

    private float Y_OFFSET = 5.0f;
    private static final float MAX_PITCH = 90.0f;
    private static final float MIN_PITCH = -1.0f;
    private static final float MAX_DISTANCE_FROM_PLAYER = 90.0f;
    private static final float MIN_DISTANCE_FROM_PLAYER = 3.0f;
    private static final float MOVEMENT_SPEED = 40.0f;
    private static Camera SINGLETON_INSTANCE = new Camera();

    private Camera() {
    }

    public static Camera getInstance(Player player) {
        SINGLETON_INSTANCE.player = player;
        SINGLETON_INSTANCE.setupZoomHandler();
        return SINGLETON_INSTANCE;
    }

    public void move() {
        thirdPersonCameraControls();
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

    private void thirdPersonCameraControls() {
        updateAngles();
        calculateHorizontalDistance();
        calculateVerticalDistance();
        updateCameraPosition();
    }

    private void freeLookCameraControls() {
        if (GLFW.glfwGetKey(DisplayManager.WINDOW, GLFW.GLFW_KEY_C) == GLFW.GLFW_PRESS) {
            position.y -= MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(DisplayManager.WINDOW, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) {
            position.y += MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(DisplayManager.WINDOW, GLFW.GLFW_KEY_UP) == GLFW.GLFW_PRESS) {
            position.z -= MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(DisplayManager.WINDOW, GLFW.GLFW_KEY_DOWN) == GLFW.GLFW_PRESS) {
            position.z += MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(DisplayManager.WINDOW, GLFW.GLFW_KEY_LEFT) == GLFW.GLFW_PRESS) {
            position.x -= MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
        if (GLFW.glfwGetKey(DisplayManager.WINDOW, GLFW.GLFW_KEY_RIGHT) == GLFW.GLFW_PRESS) {
            position.x += MOVEMENT_SPEED * DisplayManager.getTimeDelta();
        }
    }

    private void calculateHorizontalDistance() {
        horizontalDistance = distanceFromPlayer * cosf(toRadiansf(pitch));
    }

    private void calculateVerticalDistance() {
        verticalDistance = distanceFromPlayer * sinf(toRadiansf(pitch));
    }

    private void setupZoomHandler() {
        GLFWScrollCallback.create((w, x, y) -> {
            if ((distanceFromPlayer < MAX_DISTANCE_FROM_PLAYER || y > 0.0f) &&
                    (distanceFromPlayer > MIN_DISTANCE_FROM_PLAYER || y < 0.0f)) {
                distanceFromPlayer -= y;
            }
        }).set(DisplayManager.WINDOW);
    }

    private void updateAngles() {
        if (GLFW.glfwGetMouseButton(DisplayManager.WINDOW, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS) {
            pitch -= (DisplayManager.getDY() * 0.1f);

            if (pitch < MIN_PITCH) {
                pitch = MIN_PITCH;
            } else if (pitch > MAX_PITCH) {
                pitch = MAX_PITCH;
            }

            angleAroundPlayer -= DisplayManager.getDX() * 0.3f;
        }

        theta = angleAroundPlayer + player.rotY;
        yaw = 180.0f - theta;
    }

    private void updateCameraPosition() {
        this.position.x = player.position.x - horizontalDistance * sinf(toRadiansf(theta));
        this.position.y = player.position.y + verticalDistance + Y_OFFSET;
        this.position.z = player.position.z - horizontalDistance * cosf(toRadiansf(theta));
    }
}
