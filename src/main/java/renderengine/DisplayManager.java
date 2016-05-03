package renderengine;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
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
    private static GLFWWindowSizeCallback windowsSizeCallback;

    public static long WINDOW = 0;

    public static int WINDOW_WIDTH = 1280;
    public static int WINDOW_HEIGHT = 720;

    public static void createDisplay() {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        if (glfwInit() != GLFW_TRUE) {
            throw new RuntimeException("Failed to init GLFW.");
        }
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        // Add antialiasing.
        //glfwWindowHint(GLFW_STENCIL_BITS, 4);
        glfwWindowHint(GLFW_SAMPLES, 4);

        WINDOW = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "My Game Engine! - v0.0.1", NULL, NULL);

        if (WINDOW == NULL) {
            throw new RuntimeException("Failed to create WINDOW.");
        }

        glfwSetWindowSizeCallback(WINDOW, windowsSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                DisplayManager.WINDOW_WIDTH = width;
                DisplayManager.WINDOW_HEIGHT = height;
                GL11.glViewport(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
            }
        });

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

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
    }

    public static void updateDisplay() {
        glfwPollEvents();
        glfwSwapBuffers(WINDOW);
    }

    public static void closeDisplay() {
        glfwDestroyWindow(WINDOW);
        glfwTerminate();
    }
}
