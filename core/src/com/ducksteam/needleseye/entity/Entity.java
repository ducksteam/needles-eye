package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.ducksteam.needleseye.Config;

/**
 * @author thechiefpotatopeeler
 * This class is the base for all objects with models in the game world
 * */
public abstract class Entity extends btMotionState {

    //TODO: Add numeric IDs for each object
    public static String id;

    public Boolean isRenderable;
    private ModelInstance modelInstance;
    private Vector3 modelOffset;

    private Vector3 velocity;

    public Matrix4 transform;
    public btRigidBody collider;

    /**
     * @param position the initial position
     * @param rotation the initial rotation
     * Constructor for entity class
     * takes a position and rotation offset
     * */
    public Entity(Vector3 position, Quaternion rotation){
        this(position, rotation, new Vector3(1, 1, 1), 1);
    }

    public Entity(Vector3 position, Quaternion rotation, Vector3 scale){
        this(position, rotation, scale, 1);
    }
    public Entity(Vector3 position, Quaternion rotation, Vector3 scale, float mass){
        velocity = new Vector3();
        transform = new Matrix4();
        transform.idt()
                .translate(position)
                .rotate(rotation)
                .scale(scale.x, scale.y, scale.z);

        setModelOffset(Vector3.Zero);
        if (modelInstance != null) collider = new btRigidBody(mass, this, Bullet.obtainStaticNodeShape(getModelInstance().model.nodes));
    }

    public abstract String getModelAddress();

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelOffset(Vector3 offset){
        this.modelOffset = offset;
    }
    public Vector3 getModelOffset() {
        return modelOffset;
    }
    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }
    public Vector3 getPosition() {
        return transform.getTranslation(new Vector3());
    }
    public void setPosition(Vector3 position) {
        transform.setTranslation(position);
    }
    public Quaternion getRotation() {
        return transform.getRotation(new Quaternion());
    }
    public void rotate(Quaternion rotation) {
        transform.rotate(rotation);
    }

    public void setRotation(Vector3 axis, float angle){
        transform.rotateRad(axis, angle);
    }

    public Vector3 getScale() {
        return transform.getScale(new Vector3());
    }
    public void setScale(Vector3 scale) {
        transform.scale(scale.x, scale.y, scale.z);
    }
    public Vector3 getVelocity() {
        return velocity;
    }
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    public void update(float delta){
        transform.translate(velocity.cpy().scl(delta));
        if (collider != null) collider.setWorldTransform(transform);
        modelInstance.transform.set(transform);
    }

    public void collisionResponse(Vector3 contactNormal){
        Vector3 norVel = this.velocity.cpy().sub(contactNormal.scl(contactNormal.cpy().dot(velocity.cpy())));
        Vector3 tanVel = this.velocity.cpy().sub(norVel);

        norVel.scl(-0.1f);
        tanVel.scl(0.9f);
        Vector3 newVel = norVel.cpy().add(tanVel);

        Vector3 newPos = getPosition().cpy().add(contactNormal.cpy().scl(Config.COLLISION_PENETRATION));

        Gdx.app.debug("Collision", "Normal: " + contactNormal + " Velocity: " + velocity + " New Velocity: " + newVel + " New Position: " + newPos);

        setVelocity(newVel);
        setPosition(newPos);
    }


    @Override
    public void setWorldTransform(Matrix4 worldTrans) {
        Gdx.app.debug("Entity", "Setting world transform");
        worldTrans.set(transform);
    }

    @Override
    public void getWorldTransform(Matrix4 worldTrans) {
        Gdx.app.debug("Entity", "Getting world transform");
        transform.set(worldTrans);
    }

    public static Vector3 sphericalToEuler(Vector2 spherical){
        return new Vector3((float) (spherical.x * Math.cos(spherical.y)), (float) (spherical.x * Math.sin(spherical.y)), 0);
    }

    public static Vector2 eulerToSpherical(Vector3 euler){
        return new Vector2((float) Math.sqrt(euler.x * euler.x + euler.y * euler.y), (float) Math.atan2(euler.y, euler.x));
    }

    public static Vector3 quatToEuler(Quaternion quat){
        return new Vector3(quat.getPitch(), quat.getYaw(), quat.getRoll());
    }

    public void destroy() {
        collider.dispose();
        this.dispose();
    }
}
