package com.ducksteam.needleseye.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.ducksteam.needleseye.entity.bullet.EntityMotionState;
import com.ducksteam.needleseye.entity.bullet.WorldTrigger;
import com.ducksteam.needleseye.map.DecoTemplate;
import net.mgsx.gltf.scene3d.scene.Scene;

import java.util.ArrayList;

import static com.ducksteam.needleseye.Main.dynamicsWorld;

public class DecoInstance extends Entity {
    public DecoTemplate template;
    public RoomInstance parentRoom;
    public boolean shattered = false;
    private WorldTrigger shatterTrigger;
    private final ArrayList<ShatterNode> shatterNodes = new ArrayList<>();

    /**
     * Creates a new deco
     * @param template the template of the deco being created
     * @param parentRoom the room the deco is placed in
     * @param position the relative position as found in {@link com.ducksteam.needleseye.map.RoomTemplate.DecoTagPosition DecoTagPosition}
     * @param rotation the rotation of the object
     */
    public DecoInstance(DecoTemplate template, RoomInstance parentRoom, Vector3 position, Quaternion rotation) {
        super(parentRoom.getPosition().cpy().add(position), rotation, 0, template.getScene(), btCollisionObject.CollisionFlags.CF_STATIC_OBJECT | DECO_GROUP);

        this.template = template;
        this.parentRoom = parentRoom;

        tryCreateWorldTrigger();
    }

    private void tryCreateWorldTrigger() {
        if (isRenderable && template.isDestructible()) {
            shatterTrigger = new WorldTrigger(collisionShape, transform, (Entity e) -> this.shatter(), ATTACK_GROUP, WorldTrigger.TriggerType.ONCE_ON_ENTER);
        }
    }

    @Override
    public void setScene(Scene scene) {
        this.scene = scene;
        if (isRenderable) {
            collisionShape = Bullet.obtainStaticNodeShape(scene.modelInstance.nodes);
            motionState = new EntityMotionState(this, transform);

            Vector3 inertia = new Vector3();
            collisionShape.calculateLocalInertia(0, inertia);

            collider = new btRigidBody(0, motionState, collisionShape, inertia);
            collider.obtain();
            collider.setActivationState(Collision.DISABLE_DEACTIVATION);
            collider.setUserValue(id);
            dynamicsWorld.addRigidBody(collider);
        }
    }

    public void shatter() {
        Gdx.app.debug("DecoInstance", "Shattered deco " + id);
        shatterTrigger.destroy();
        shatterTrigger = null;
        shattered = true;
        dynamicsWorld.removeRigidBody(collider);
        motionState.release();
        collider.release();
        for (Node node : scene.modelInstance.nodes) {
            shatterNodes.add(new ShatterNode(node, this));
        }
    }

    public static class ShatterNode extends btMotionState {
        Node node;
        btRigidBody collider;
        btCollisionShape shape;
        DecoInstance parent;

        private static final int SHATTER_OBJECT_MASS = 0;

        public ShatterNode(Node node, DecoInstance deco) {
            this.parent = deco;

            this.node = node;
            shape = Bullet.obtainStaticNodeShape(node, true);

            Vector3 inertia = new Vector3();
            shape.calculateLocalInertia(SHATTER_OBJECT_MASS, inertia);

            collider = new btRigidBody(SHATTER_OBJECT_MASS, this, shape);
            collider.obtain();
            collider.setActivationState(Collision.DISABLE_DEACTIVATION);

            setWorldTransform(deco.transform.cpy().mul(node.globalTransform));
            dynamicsWorld.addRigidBody(collider);
        }

        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
            node.globalTransform.set(worldTrans);
        }

        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
            worldTrans.set(node.globalTransform);
        }
    }

    @Override
    public void update(float delta) {
        if (!shattered) {
            motionState.setWorldTransform(transform);
        }
    }

    @Override
    public String getModelAddress() {
        return template.getModelPath();
    }

    @Override
    public synchronized void destroy() {
        if (shattered) {
            for (ShatterNode shatterNode : shatterNodes) {
                shatterNode.collider.release();
                shatterNode.collider = null;
                shatterNode.shape.release();
                shatterNode.shape = null;
                shatterNode.release();
            }
            shatterNodes.clear();
        } else {
            super.destroy();
            shatterTrigger.destroy();
        }
    }
}
