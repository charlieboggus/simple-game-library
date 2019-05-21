package com.github.charlieboggus.sgl.graphics.g2d;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera
{
    private static class Transformation
    {
        Matrix4f matrix = new Matrix4f();
        Vector2f lastPosition = new Vector2f();
        Vector2f lastOrigin = new Vector2f();
        float lastZoom = 1.0f;
        float lastRotation = 0.0f;

        void update(Vector2f pos, Vector2f origin, float zoom, float rotation)
        {
            this.lastPosition.set(pos.x, pos.y);
            this.lastOrigin.set(origin.x, origin.y);
            this.lastZoom = zoom;
            this.lastRotation = rotation;
        }
    }

    private final Transformation transform;
    private final Vector2f position;
    private final Vector2f origin;
    private float zoom;
    private float rotation;

    public Camera()
    {
        this.transform = new Transformation();
        this.position = new Vector2f(0.0f, 0.0f);
        this.origin = new Vector2f(0.0f, 0.0f);
        this.zoom = 1.0f;
        this.rotation = 0.0f;
    }

    public float getPositionX()
    {
        return this.position.x;
    }

    public float getPositionY()
    {
        return this.position.y;
    }

    public Vector2f getPosition()
    {
        return this.position;
    }

    public void setPosition(float x, float y)
    {
        this.position.x = x;
        this.position.y = y;
    }

    public void resetPosition()
    {
        this.position.x = this.origin.x;
        this.position.y = this.origin.y;
    }

    public void translateX(float xoffset)
    {
        this.position.x += xoffset;
    }

    public void translateY(float yoffset)
    {
        this.position.y += yoffset;
    }

    public void translate(float xoffset, float yoffset)
    {
        this.position.x += xoffset;
        this.position.y += yoffset;
    }

    public float getOriginX()
    {
        return this.origin.x;
    }

    public float getOriginY()
    {
        return this.origin.y;
    }

    public Vector2f getOrigin()
    {
        return this.origin;
    }

    public void setOrigin(float x, float y)
    {
        this.origin.x = x;
        this.origin.y = y;
    }

    public void resetOrigin()
    {
        this.origin.x = 0.0f;
        this.origin.y = 0.0f;
    }

    public float getZoom()
    {
        return this.zoom;
    }

    public void setZoom(float zoom)
    {
        this.zoom = zoom;
        if(this.zoom <= 0.0f)
            this.zoom = 0.01f;
    }

    public void zoom(float amt)
    {
        this.zoom += amt;
        if(this.zoom <= 0.0f)
            this.zoom = 0.01f;
    }

    public void resetZoom()
    {
        this.zoom = 1.0f;
    }

    public float getRotation()
    {
        return this.rotation;
    }

    public void setRotation(float angle)
    {
        this.rotation = angle;
    }

    public void rotate(float amt)
    {
        this.rotation += amt;
    }

    public void resetRotation()
    {
        this.rotation = 0.0f;
    }

    Matrix4f getViewMatrix()
    {
        boolean dirty = false;
        if(this.transform.lastPosition.x != this.position.x || this.transform.lastPosition.y != this.position.y)
            dirty = true;
        else if(this.transform.lastOrigin.x != this.origin.x || this.transform.lastOrigin.y != this.origin.y)
            dirty = true;
        else if(this.transform.lastZoom != this.zoom)
            dirty = true;
        else if(this.transform.lastRotation != this.rotation)
            dirty = true;

        if(dirty)
            this.recreateMatrix();

        return this.transform.matrix;
    }

    private void recreateMatrix()
    {
        this.transform.update(this.position, this.origin, this.zoom, this.rotation);
        this.transform.matrix.identity();
        this.transform.matrix.translate(new Vector3f(this.position.x, this.position.y, 0.0f));
        this.transform.matrix.scale(new Vector3f(this.zoom, this.zoom, 1.0f));
        this.transform.matrix.rotateZ((float) Math.toRadians(this.rotation));
        this.transform.matrix.translate(this.origin.x, this.origin.y, 0.0f);
    }
}
