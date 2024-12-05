package com.ducksteam.needleseye.entity.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Almost identical to the xoppa version {@link com.badlogic.gdx.physics.bullet.DebugDrawer DebugDrawer} but with an updated ShapeRenderer for OpenGL ES 3.2. <br> <br> <br> <br> <br> <br> The NE stands for needles eye
 * @author skysourced
 */
public class NEDebugDrawer extends btIDebugDraw implements Disposable {
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private boolean ownsShapeRenderer = true;
    private boolean ownsSpriteBatch = true;
    private boolean ownsFont = true;
    private Camera camera;
    private Viewport viewport;
    private int debugMode = 0;

    public NEDebugDrawer() {
        ShaderProgram shader = createDefaultShader(false, true, 0);

        shapeRenderer = new ShapeRenderer(5000, shader);
    }

    private static String createVertexShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
        StringBuilder shader = new StringBuilder("#version 150\nin vec4 a_position;\n" + (hasNormals ? "in vec3 a_normal;\n" : "") + (hasColors ? "in vec4 a_color;\n" : ""));

        for(int i = 0; i < numTexCoords; ++i) {
            shader.append("in vec2 a_texCoord").append(i).append(";\n");
        }

        shader.append("uniform mat4 u_projModelView;\n").append(hasColors ? "out vec4 v_col;\n" : "");

        for(int i = 0; i < numTexCoords; ++i) {
            shader.append("out vec2 v_tex").append(i).append(";\n");
        }

        shader.append("void main() {\n   gl_Position = u_projModelView * a_position;\n");
        if (hasColors) {
            shader.append("   v_col = a_color;\n   v_col.a *= 255.0 / 254.0;\n");
        }

        for(int i = 0; i < numTexCoords; ++i) {
            shader.append("   v_tex").append(i).append(" = ").append("a_texCoord").append(i).append(";\n");
        }

        shader.append("   gl_PointSize = 1.0;\n}\n");
        return shader.toString();
    }

    private static String createFragmentShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
        StringBuilder shader = new StringBuilder("#version 150\n#ifdef GL_ES\nprecision mediump float;\n#endif\n");
        if (hasColors) {
            shader.append("in vec4 v_col;\n");
        }

        for(int i = 0; i < numTexCoords; ++i) {
            shader.append("in vec2 v_tex").append(i).append(";\n");
            shader.append("uniform sampler2D u_sampler").append(i).append(";\n");
        }

        shader.append("out vec4 FragColor;\n");

        shader.append("void main() {\n   FragColor = ").append(hasColors ? "v_col" : "vec4(1, 1, 1, 1)");
        if (numTexCoords > 0) {
            shader.append(" * ");
        }

        for(int i = 0; i < numTexCoords; ++i) {
            if (i == numTexCoords - 1) {
                shader.append(" texture2D(u_sampler").append(i).append(",  v_tex").append(i).append(")");
            } else {
                shader.append(" texture2D(u_sampler").append(i).append(",  v_tex").append(i).append(") *");
            }
        }

        shader.append(";\n}");
        return shader.toString();
    }

    public static ShaderProgram createDefaultShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
        String vertexShader = createVertexShader(hasNormals, hasColors, numTexCoords);
        String fragmentShader = createFragmentShader(hasNormals, hasColors, numTexCoords);
        ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
        if (!program.isCompiled()) {
            Gdx.app.error("Shaders", "Debug drawer shader failed to compile: " + program.getLog());
        } else {
            Gdx.app.error("Shaders", "Debug drawer shader compiled: " + program.getLog());
        }
        return program;
    }

    public void drawLine(Vector3 from, Vector3 to, Vector3 color) {
        this.shapeRenderer.setColor(color.x, color.y, color.z, 1.0F);
        this.shapeRenderer.line(from, to);
    }

    public void drawContactPoint(Vector3 pointOnB, Vector3 normalOnB, float distance, int lifeTime, Vector3 color) {
        this.shapeRenderer.setColor(color.x, color.y, color.z, 1.0F);
        this.shapeRenderer.point(pointOnB.x, pointOnB.y, pointOnB.z);
        this.shapeRenderer.line(pointOnB, normalOnB.scl(distance).add(pointOnB));
    }

    public void drawTriangle(Vector3 v0, Vector3 v1, Vector3 v2, Vector3 color, float a) {
        this.shapeRenderer.setColor(color.x, color.y, color.z, a);
        this.shapeRenderer.line(v0, v1);
        this.shapeRenderer.line(v1, v2);
        this.shapeRenderer.line(v2, v0);
    }

    public void reportErrorWarning(String warningString) {
        Gdx.app.error("Bullet", warningString);
    }

    public void draw3dText(Vector3 location, String textString) {
        if (this.spriteBatch == null) {
            this.spriteBatch = new SpriteBatch();
        }

        if (this.font == null) {
            this.font = new BitmapFont();
        }

        if (this.camera.frustum.pointInFrustum(location)) {
            if (this.viewport != null) {
                this.camera.project(location, (float)this.viewport.getScreenX(), (float)this.viewport.getScreenY(), (float)this.viewport.getScreenWidth(), (float)this.viewport.getScreenHeight());
            } else {
                this.camera.project(location);
            }

            this.shapeRenderer.end();
            this.spriteBatch.begin();
            this.font.draw(this.spriteBatch, textString, location.x, location.y, 0, textString.length(), 0.0F, 1, false);
            this.spriteBatch.end();
            this.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        }

    }

    public void setDebugMode(int debugMode) {
        this.debugMode = debugMode;
    }

    public int getDebugMode() {
        return this.debugMode;
    }

    public void begin(Camera camera) {
        this.camera = camera;
        this.shapeRenderer.setProjectionMatrix(camera.combined);
        this.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
    }

    public void begin(Viewport viewport) {
        this.viewport = viewport;
        this.begin(viewport.getCamera());
    }

    public void end() {
        this.shapeRenderer.end();
    }

    public ShapeRenderer getShapeRenderer() {
        return this.shapeRenderer;
    }

    public void setShapeRenderer(ShapeRenderer shapeRenderer) {
        if (this.ownsShapeRenderer) {
            this.shapeRenderer.dispose();
        }

        this.shapeRenderer = shapeRenderer;
        this.ownsShapeRenderer = false;
    }

    public SpriteBatch getSpriteBatch() {
        return this.spriteBatch;
    }

    public void setSpriteBatch(SpriteBatch spriteBatch) {
        if (this.ownsSpriteBatch && this.spriteBatch != null) {
            this.spriteBatch.dispose();
        }

        this.spriteBatch = spriteBatch;
        this.ownsSpriteBatch = false;
    }

    public BitmapFont getFont() {
        return this.font;
    }

    public void setFont(BitmapFont font) {
        if (this.ownsFont && this.font != null) {
            this.font.dispose();
        }

        this.font = font;
        this.ownsFont = false;
    }

    public void dispose() {
        super.dispose();
        if (this.ownsShapeRenderer) {
            this.shapeRenderer.dispose();
        }

        if (this.ownsSpriteBatch && this.spriteBatch != null) {
            this.spriteBatch.dispose();
        }

        if (this.ownsFont && this.font != null) {
            this.font.dispose();
        }

    }
}
