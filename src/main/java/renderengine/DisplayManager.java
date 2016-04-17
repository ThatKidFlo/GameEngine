package renderengine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public class DisplayManager {

    // We need to strongly reference callback instances.
    private static GLFWErrorCallback errorCallback;
    private static GLFWKeyCallback keyCallback;

    public static long WINDOW = 0;

    public static final int WINDOW_WIDTH = 1280;
    public static final int WINDOW_HEIGHT = 720;

    public static void createDisplay() {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        if (glfwInit() != GLFW_TRUE) {
            throw new RuntimeException("Failed to init GLFW.");
        }
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        WINDOW = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "My Game Engine! - v0.0.1", NULL, NULL);

        if (WINDOW == NULL) {
            throw new RuntimeException("Failed to create WINDOW.");
        }

        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                WINDOW,
                // center WINDOW on the X-axis
                (vidMode.width() - WINDOW_WIDTH) / 2,
                // center WINDOW on the Y-axis
                (vidMode.height() - WINDOW_HEIGHT) / 2
        );

        glfwMakeContextCurrent(WINDOW);
        // Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(WINDOW);
        GL.createCapabilities();

        // Map the viewport to the size of the whole window.
        GL11.glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    public static void updateDisplay() {
        glfwPollEvents();
        glfwSwapBuffers(WINDOW);
    }

    public static void closeDisplay() {
        glfwDestroyWindow(WINDOW);
    }
}
