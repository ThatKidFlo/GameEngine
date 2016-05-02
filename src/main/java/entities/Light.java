package entities;

import org.joml.Vector3f;

/**
 * <p>
 * Diffuse lighting - an object's lighting depends on how much the object faces the light (i.e. per-pixel lighting, as
 * a function of dot product between a vertex normal, and the direction towards the light source.)
 * </p>
 * <p>
 * Specular lighting - the reflected light, as a function of the damping factor of a surface, its shininess factor,
 * and the direction of the camera
 * </p>
 *
 * Created by ThatKidFlo on 02.05.2016.
 */
public class Light {

    private Vector3f position;
    private Vector3f colour;

    public Light(Vector3f position, Vector3f colour) {
        this.position = position;
        this.colour = colour;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getColour() {
        return colour;
    }

    public void setColour(Vector3f colour) {
        this.colour = colour;
    }
}
