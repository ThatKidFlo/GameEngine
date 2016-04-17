package utils;

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
        matrix.translate(translation);
        matrix.rotationXYZ(rx, ry, rz);
        matrix.scale(scale, scale, scale);
        return matrix;
    }
}
