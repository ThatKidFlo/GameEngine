package renderengine;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import utils.Constants;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public final class DisplayManager {

    private volatile static int mouseX, mouseY, mouseDX, mouseDY;

    public static long WINDOW = 0;

    public static int WINDOW_WIDTH = 1280;
    public static int WINDOW_HEIGHT = 720;
    public static final int FPS_CAP = 120;

    private static long lastFrameTime;
    private static float timeDelta;

    public static void createDisplay() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) {
            throw new RuntimeException("Failed to init GLFW.");
        }

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Add antialiasing.
        glfwWindowHint(GLFW_STENCIL_BITS, 4);
        glfwWindowHint(GLFW_SAMPLES, 4);

        WINDOW = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "My Game Engine! - v0.0.1", NULL, NULL);

        if (WINDOW == NULL) {
            throw new RuntimeException("Failed to create WINDOW.");
        }

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
        initializeIO();
        lastFrameTime = getCurrentMillis();
    }

    private static void initializeIO() {
        GLFWWindowSizeCallback.create((window, width, height) -> {
            DisplayManager.WINDOW_WIDTH = width;
            DisplayManager.WINDOW_HEIGHT = height;
            GL11.glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        }).set(WINDOW);

        final GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                WINDOW,
                // center WINDOW on the X-axis
                (vidMode.width() - WINDOW_WIDTH) / 2,
                // center WINDOW on the Y-axis
                (vidMode.height() - WINDOW_HEIGHT) / 2
        );

        GLFWKeyCallback.create((window, key, scancode, action, mods) -> {
            if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                glfwSetWindowShouldClose(DisplayManager.WINDOW, true);
            }
        }).set(WINDOW);

        mouseX = mouseY = mouseDX = mouseDY = 0;

        GLFWCursorPosCallback.create((long window, double xpos, double ypos) -> {
            // Add delta of x and y mouse coordinates
            mouseDX = (int) xpos - mouseX;
            mouseDY = (int) ypos - mouseY;

            // Set new positions of x and y
            mouseX = (int) xpos;
            mouseY = (int) ypos;
        }).set(WINDOW);
    }

    public static void updateDisplay() {
        glfwPollEvents();
        glfwSwapBuffers(WINDOW);
        updateFrameTimes();
    }

    private static void updateFrameTimes() {
        final long currentFrameTime = getCurrentMillis();
        timeDelta = (currentFrameTime - lastFrameTime) / Constants.Time.MILLIS_TO_SECONDS;
        lastFrameTime = currentFrameTime;
    }

    public static void closeDisplay() {
        glfwDestroyWindow(WINDOW);
        glfwTerminate();
    }

    public static float getTimeDelta() {
        return timeDelta;
    }

    public static int getDX() {
        return mouseDX;
    }

    public static int getDY() {
        return mouseDY;
    }

    private static long getCurrentMillis() {
        return System.currentTimeMillis();
    }
}
