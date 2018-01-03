package utils;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by ThatKidFlo on 17.04.2016.
 */
public final class Maths {

    /**
     * Will create a {@link Matrix4f}, describing the transformations passed as parameters.
     *
     * @param translation - a {@link Vector3f}, describing translation about XYZ.
     * @param rx          - float, representing rotation about the X axis.
     * @param ry          - float, representing rotation about the Y axis.
     * @param rz          - float, representing rotation about the Z axis.
     * @param scale       - float, representing the scale of the transformation.
     * @return - a {@link Matrix4f}, describing the transformations to be applied.
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        // yields an identity matrix, which is column-major, in order to match OpenGL's internal representation.
        return new Matrix4f()
                .translate(translation)
                .rotateXYZ(toRadiansf(rx), toRadiansf(ry), toRadiansf(rz))
                .scale(scale);
    }

    /**
     * Creates the view matrix corresponding to the position, and orientation of the camera passed in as a parameter.
     *
     * @param camera - the target {@link Camera}, whose view matrix will be returned.
     * @return - the view matrix, as a {@link Matrix4f}.
     */
    public static Matrix4f createViewMatrix(Camera camera) {
        Vector3f cameraPosition = camera.getPosition();
        Vector3f negativeCameraPosition = new Vector3f(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        return new Matrix4f()
                .rotateXYZ(toRadiansf(camera.getPitch()), toRadiansf(camera.getYaw()), 0.0f)
                .translate(negativeCameraPosition);
    }

    /**
     * Returns the value of the angle provided as input in radians, as a float value.
     *
     * @param angle - float value for angle.
     * @return - a float, representing the equivalent radians value.
     */
    public static float toRadiansf(float angle) {
        return (float) Math.toRadians(angle);
    }

    /**
     * Returns the value of sin(angle) in radians, as a float.
     *
     * @param angle - float value for angle.
     * @return - a float, representing the sinus of angle.
     */
    public static float sinf(float angle) {
        return (float) Math.sin(angle);
    }

    /**
     * Returns the value of cos(angle) in radians, as a float.
     *
     * @param angle - float value for angle.
     * @return - a float, representing the cosine of angle.
     */
    public static float cosf(float angle) {
        return (float) Math.cos(angle);
    }
}
