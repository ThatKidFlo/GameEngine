package renderengine;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by ThatKidFlo on 15.04.2016.
 */
public final class DisplayManager {

    // Need to strongly reference callback instances.
    private static GLFWErrorCallback errorCallback;
    private static GLFWWindowSizeCallback windowsSizeCallback;
    private static GLFWKeyCallback keyCallback;
    private static GLFWCursorPosCallback cursorPosCallback;

    private static int mouseX, mouseY, mouseDX, mouseDY;

    public static long WINDOW = 0;

    public static int WINDOW_WIDTH = 1280;
    public static int WINDOW_HEIGHT = 720;
    public static final int FPS_CAP = 120;

    private static long lastFrameTime;
    private static long currentFrameTime;
    private static float timeDelta;

    public static void createDisplay() {
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        if (!glfwInit()) {
            throw new RuntimeException("Failed to init GLFW.");
        }
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

        // Add antialiasing.
        //glfwWindowHint(GLFW_STENCIL_BITS, 4);
        //glfwWindowHint(GLFW_SAMPLES, 4);

        WINDOW = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, "My Game Engine! - v0.1.0", NULL, NULL);

        if (WINDOW == NULL) {
            throw new RuntimeException("Failed to create WINDOW.");
        }

        DisplayManager.initializeIO();

        glfwMakeContextCurrent(WINDOW);
        // Enable v-sync
        //glfwSwapInterval(1);

        glfwShowWindow(WINDOW);
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        initializeIO();
        lastFrameTime = getCurrentMilis();
    }

    private static void initializeIO() {
        glfwSetWindowSizeCallback(WINDOW, windowsSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                DisplayManager.WINDOW_WIDTH = width;
                DisplayManager.WINDOW_HEIGHT = height;
                GL11.glViewport(0,0,WINDOW_WIDTH,WINDOW_HEIGHT);
            }
        });

        final GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(
                WINDOW,
                // center WINDOW on the X-axis
                (vidMode.width() - WINDOW_WIDTH) / 2,
                // center WINDOW on the Y-axis
                (vidMode.height() - WINDOW_HEIGHT) / 2
        );

        glfwSetKeyCallback(DisplayManager.WINDOW, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (key == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                    glfwSetWindowShouldClose(DisplayManager.WINDOW, true);
                }
            }
        });

        mouseX = mouseY = mouseDX = mouseDY = 0;
        glfwSetCursorPosCallback(DisplayManager.WINDOW, cursorPosCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                // Add delta of x and y mouse coordinates
                mouseDX += (int) xpos - mouseX;
                mouseDY += (int) xpos - mouseY;
                // Set new positions of x and y
                mouseX = (int) xpos;
                mouseY = (int) ypos;

            }
        });
    }

    public static void updateDisplay() {
        glfwPollEvents();
        glfwSwapBuffers(WINDOW);
        currentFrameTime = getCurrentMilis();
        timeDelta = (currentFrameTime - lastFrameTime) / 1000.0f;
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

    private static long getCurrentMilis() {
        return System.currentTimeMillis();
    }
}
