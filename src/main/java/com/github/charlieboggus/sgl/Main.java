package com.github.charlieboggus.sgl;

import com.github.charlieboggus.sgl.core.*;
import com.github.charlieboggus.sgl.graphics.g2d.*;
import com.github.charlieboggus.sgl.input.Keys;
import com.github.charlieboggus.sgl.utility.Color;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Main
{
    public static void main(String[] args)
    {
        new Application(new Configuration()).start(new TestGame());
    }

    public static class TestGame extends Game
    {
        ShapeBatch shape;
        SpriteBatch batch;
        Texture debugTexture;
        Texture loadedTexture;
        TextureRegion textureRegion;
        TrueTypeFont font;
        Camera camera;

        @Override
        public void initialize()
        {
            this.shape = new ShapeBatch();
            this.batch = new SpriteBatch();
            this.debugTexture = new Texture(128, 128);
            this.loadedTexture = new Texture("icon.png");
            this.textureRegion = new TextureRegion(this.loadedTexture, 16, 16);
            this.font = new TrueTypeFont("IBMPlexMono-Regular.ttf", 18);
            this.camera = new Camera();
            this.batch.setCamera(camera);
        }

        @Override
        public void shutdown()
        {
            this.font.dispose();
            this.debugTexture.dispose();
            this.loadedTexture.dispose();
            this.textureRegion.dispose();
            this.batch.dispose();
            this.shape.dispose();
        }

        @Override
        public void processInput()
        {
            if(Input.Keyboard().isKeyDown(Keys.W))
                this.camera.translateY(-5.0f);

            if(Input.Keyboard().isKeyDown(Keys.S))
                this.camera.translateY(5.0f);

            if(Input.Keyboard().isKeyDown(Keys.A))
                this.camera.translateX(-5.0f);

            if(Input.Keyboard().isKeyDown(Keys.D))
                this.camera.translateX(5.0f);

            if(Input.Keyboard().isKeyDown(Keys.Q))
                this.camera.zoom(-0.01f);

            if(Input.Keyboard().isKeyDown(Keys.E))
                this.camera.zoom(0.01f);

            if(Input.Keyboard().isKeyDown(Keys.Z))
                this.camera.rotate(-1.0f);

            if(Input.Keyboard().isKeyDown(Keys.X))
                this.camera.rotate(1.0f);

            if(Input.Keyboard().isKeyPressed(Keys.R))
                this.camera.resetPosition();

            if(Input.Keyboard().isKeyPressed(Keys.T))
                this.camera.resetZoom();

            if(Input.Keyboard().isKeyPressed(Keys.Y))
                this.camera.resetRotation();

            if(Input.Keyboard().isKeyPressed(Keys.U))
            {
                this.camera.resetPosition();
                this.camera.resetZoom();
                this.camera.resetRotation();
            }

            if(Input.Keyboard().isKeyPressed(Keys.M))
                Display.screenshot();
        }

        @Override
        public void update(float delta)
        {
        }

        @Override
        public void render()
        {
            // Sprite Batch Drawing
            this.batch.begin();
            this.batch.drawTexture(this.debugTexture, 500, 500, 0.0f, 1.0f, Color.White);
            this.batch.drawTexture(this.loadedTexture, 128, 128, (float) Math.toDegrees(Math.sin(glfwGetTime() * Math.PI) * 2), 1.0f, Color.White, false);
            this.batch.drawTexture(this.textureRegion, 600, 25, 0.0f, 1.0f, Color.White);

            this.batch.drawText("Test String", 5, 100, Color.Blue);
            this.batch.drawText("FPS: " + this.getFPS(), 5, 0, 0.0f, 1.0f, Color.White, false);
            this.batch.drawText("UPS: " + this.getUPS(), 5, this.font.getTextHeight("UPS"), 0.0f, 1.0f, Color.White, false);
            this.batch.end();

            this.shape.begin();
            this.shape.drawRectangle(700, 400, 50, 50, (float) Math.toDegrees(Math.sin(glfwGetTime() * Math.PI ) * 2), 1.0f, Color.Red, Color.Green, Color.Blue, Color.Chartreuse);
            this.shape.end();
        }
    }
}
