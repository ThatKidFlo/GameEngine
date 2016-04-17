package entities;

import org.joml.Vector3f;

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

    public Camera() {
    }

    public void move() {
        //TODO: must deal with the new I/O API, and update camera according to input
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
