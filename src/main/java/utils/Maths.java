package utils;

import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Created by ThatKidFlo on 17.04.2016.
 */
public final class Maths {

    /**
     * Will create a 4x4 matrix of floats, describing the transformations passed as parameters.
     *
     * @param translation - a Vector3f describing translation about XYZ.
     * @param rx          - float, representing rotation about the X axis.
     * @param ry          - float, representing rotation about the Y axis.
     * @param rz          - float, representing rotation about the Z axis.
     * @param scale       - float, representing the scale of the transformation.
     * @return - a Matrix4f, describing the transformations to be applied.
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        // yields an identity matrix, which is column-major, in order to match OpenGL's internal representation.
        Matrix4f matrix = new Matrix4f();
        matrix.translate(translation)
                .scale(scale)
                .rotateXYZ(rx, ry, rz);

        return matrix;
    }

    /**
     * Creates the view matrix corresponding to the position, and orientation of the camera passed in as a parameter.
     *
     * @param camera - the destination camera, whose view matrix will be returned
     * @return - the view matrix, as a Matrix4f
     */
    public static Matrix4f createViewMatrix(Camera camera) {
        Vector3f cameraPosition = camera.getPosition();
        Vector3f negativeCameraPosition = new Vector3f(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);

        Matrix4f viewMatrix = new Matrix4f().identity();
        viewMatrix.rotate((float) Math.toRadians(camera.getPitch()), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(camera.getYaw()), new Vector3f(0, 1, 0))
                .translate(negativeCameraPosition);
        return viewMatrix;
    }
}
